/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.aem.community.core.listeners;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.SlingConstants;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import java.util.HashMap;
import java.util.Map;

/**
 * A service to demonstrate how changes in the resource tree
 * can be listened for. It registers an event handler service.
 * The component is activated immediately after the bundle is
 * started through the immediate flag.
 * Please note, that apart from EventHandler services,
 * the immediate flag should not be set on a service.
 */
//@Component(service = EventHandler.class,
//           immediate = true,
//           property = {
//                   Constants.SERVICE_DESCRIPTION + "=Demo to listen on changes in the resource tree",
//                   EventConstants.EVENT_TOPIC + "=org/apache/sling/api/resource/Resource/*",
//
//           })
public class SimpleResourceListener implements EventHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final String SERVICE_USER_NAME = "testuser";

//    @Reference
    private ResourceResolverFactory resolverFactory;

    private ResourceResolver resourceResolver;

    public void handleEvent(final Event event) {
        String path = (String) event.getProperty("path");

//        try {
//            getSession();
//
//           Resource resource = resourceResolver.getResource(path);
//           Page page = resource.adaptTo(Page.class);
//
//
//
//        } catch (LoginException e) {
//            e.printStackTrace();
//        }

        logger.debug("Resource event: {} at: {}", event.getTopic(), event.getProperty(SlingConstants.PROPERTY_PATH));
    }

    private Session getSession() throws LoginException {
        Session session = null;

        Map<String, Object> param = new HashMap<String, Object>();
        param.put(ResourceResolverFactory.SUBSERVICE, SERVICE_USER_NAME);

        resourceResolver = resolverFactory.getServiceResourceResolver(param);

        session = resourceResolver.adaptTo(Session.class);

        return session;
    }

//    private Page createPage(Page page){
//
//        Session session = resourceResolver.adaptTo(Session.class);
//        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
//
//        Page newPage = pageManager.create()
//
//
//
//    }
}

