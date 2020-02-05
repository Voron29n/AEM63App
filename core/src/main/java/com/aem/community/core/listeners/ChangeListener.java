package com.aem.community.core.listeners;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import javax.jcr.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Remember that you must set http://localhost:4502/system/console/configMgr 5000 into 	Timeout
 */

@Component(service = EventHandler.class,
        immediate = true,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Demo to listen on changes in the resource tree",
                EventConstants.EVENT_TOPIC + "=org/apache/sling/api/resource/Resource/*",
                EventConstants.EVENT_FILTER + "=(&(path=/content/AEM63App/en/jcr:content)(resourceType=*/page))"
        })
public class ChangeListener implements EventHandler {

    private static int counter;
    private final String SERVICE_USER_NAME = "testuser";
    @Reference
    private ResourceResolverFactory resolverFactory;
    private Page newPage;
    private PageManager pageManager;

    @Override
    public void handleEvent(Event event) {
        String path = (String) event.getProperty("path");
        System.out.println(path);
        counter++;
        String templatePath = "/apps/AEM63App/templates/page-home";
        String pagePath = "/content/AEM63App/en/";
        String pageTitle = "AEM home page " + counter;
        String pageName = "test_" + counter;
        List<String> listPaths = new ArrayList<>();
        String user = null;


        ResourceResolver resourceResolver = null;
        try {
            resourceResolver = getResourceResolver();

            //create a page manager instance
            pageManager = resourceResolver.adaptTo(PageManager.class);

            newPage = pageManager.create(pagePath, pageName, templatePath, pageTitle);

            Node newNode = newPage.adaptTo(Node.class);
            Node newParNode = newNode.getNode("jcr:content").getNode("par");
            String newParNodePath = newParNode.getPath();

            Resource resource = resourceResolver.getResource(path);
            Node parentNode = resource.adaptTo(Node.class);
            Node oldParNode = parentNode.getNode("par");
            String oldParNodePath = oldParNode.getPath();

            Session session = resourceResolver.adaptTo(Session.class);

            Workspace workspace = session.getWorkspace();

            NodeIterator nodeItem = oldParNode.getNodes();

            while (nodeItem.hasNext()) {
                javax.jcr.Node node = nodeItem.nextNode();

                String[] parts = node.getPath().split("/");
                String destrelativepath = parts[(parts.length - 1)];
                String destpath = newParNodePath + "/" + parts[(parts.length - 1)];
                if (!isNewPageHasCopyableNode(newParNode , destrelativepath)) {
                    workspace.copy(node.getPath(), destpath);
                }
                listPaths.add(destpath);
            }
            session.logout();
        } catch (LoginException | WCMException | RepositoryException e) {
            e.printStackTrace();
        }

        System.out.println();
    }

    private boolean isNewPageHasCopyableNode(Node newNode, String destpath) throws RepositoryException {
        return newNode.hasNode(destpath) ;
    }

    private ResourceResolver getResourceResolver() throws LoginException {

        Map<String, Object> param = new HashMap<String, Object>();
        param.put(ResourceResolverFactory.SUBSERVICE, SERVICE_USER_NAME);

        ResourceResolver resolver = null;
        resolver = resolverFactory.getServiceResourceResolver(param);

        return resolver;
    }
}
