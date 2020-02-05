package com.aem.community.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

@Model(adaptables = Resource.class)
public class MyModel {

    @Inject
    private ResourceResolver resourceResolver ;

    @Inject
    @Optional
    private String text;

    @Inject
    @Optional
    @Named("textIsRich")
    private boolean isRich;

    @PostConstruct
    public void init(){
//        isRich = false;
    }

    private String userId;

    public String getText() {
        return text;
    }

    public boolean isRich() {
        return isRich;
    }
}
