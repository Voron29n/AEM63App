package com.aem.community.core.models;

import com.aem.community.core.service.SearchService;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;


@Model(adaptables = Resource.class)
public class SearchModel {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    private SearchService searchService;

    @Inject
    @Optional
    @Named(value = "path")
    private String searchPath;

    @Inject
    @Optional
    @Named(value = "text")
    private String searchText;

    @Inject
    @Optional
    @Named(value = "typeOfSearch")
    private String typeSearch;

    @Inject
    @Optional
    private List<String> foundLinks;

    @PostConstruct
    protected void init(){
        if (searchText == null || searchPath == null){
            return;
        }
        foundLinks = searchService.getResults(searchText , searchPath , typeSearch);
    }

    public String getSearchPath() {
        return searchPath;
    }

    public String getSearchText() {
        return searchText;
    }

    public String getTypeSearch() {
        return typeSearch;
    }

    public List<String> getFoundLinks() {
        return foundLinks;
    }
}
