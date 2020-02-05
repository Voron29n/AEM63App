package com.aem.community.core.service;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.QueryManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(immediate = true, service = SearchService.class)
public class SearchService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final String QUERY_BUILDER = "queryBuilder";

    private final String QUERY_MANAGER = "queryManager";

    private final String SERVICE_USER_NAME = "testuser";

    @Reference
    private QueryBuilder queryBuilder;

    @Reference
    private ResourceResolverFactory resolverFactory;

    private Session session;

    public List<String> getResults(String searchText, String searchPath, String typeSearch) {

        List<String> resultPaths = null ;

        try {
        
            resultPaths = typeSearch.equals(QUERY_BUILDER) ? queryBuilderSearch(searchText , searchPath) :
                queryManagerSearch(searchText , searchPath);
            
        } catch (RepositoryException | LoginException e) {
            logger.error(e.toString());
        } finally {
            if (session != null) {
                session.logout();
            }
            return resultPaths;
        }
    }

    private List<String> queryManagerSearch(String searchText, String searchPath) throws RepositoryException, LoginException {

        List<String> foundLinks = new ArrayList<>();

        session = getSession();

        QueryManager queryManager = session.getWorkspace().getQueryManager();

        String sqlStatement= "SELECT * FROM [dam:Asset] AS base WHERE ISDESCENDANTNODE(base,'" + searchPath + "') and contains(base.*, '" + searchText + "')";

        javax.jcr.query.Query query = queryManager.createQuery(sqlStatement,"JCR-SQL2");
        //Execute the query and get the results ...
        javax.jcr.query.QueryResult result = query.execute();

        javax.jcr.NodeIterator nodeItem = result.getNodes();

        logger.info("now");

        while ( nodeItem.hasNext() ) {
            javax.jcr.Node node = nodeItem.nextNode();
            foundLinks.add(node.getPath());
            logger.info(foundLinks.get(0));

        }
        return foundLinks;
    }

    private List<String> queryBuilderSearch(String searchText, String searchPath) throws RepositoryException, LoginException {

        session = getSession();

        List<String> foundLinks = new ArrayList<>();

        Map<String, String> predicate = new HashMap<String, String>(){{
            put("path", searchPath);
            put("fulltext", searchText);
        }};

        Query query = queryBuilder.createQuery(PredicateGroup.create(predicate), this.session);

        query.setStart(0);
        query.setHitsPerPage(20);

        SearchResult result = query.getResult();
        logger.info("now");
        for (Hit hit : result.getHits()) {
            String path = hit.getPath();
            foundLinks.add(path);
        }

        return foundLinks;
    };

    private Session getSession() throws LoginException {
        Session session = null;

        Map<String, Object> param = new HashMap<String, Object>();
        param.put(ResourceResolverFactory.SUBSERVICE, SERVICE_USER_NAME);

        ResourceResolver resolver = null;
        resolver = resolverFactory.getServiceResourceResolver(param);

        session = resolver.adaptTo(Session.class);

        return session;
    }
}
