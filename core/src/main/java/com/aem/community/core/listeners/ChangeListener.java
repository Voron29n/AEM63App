package com.aem.community.core.listeners;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Remember that you must set http://localhost:4502/system/console/configMgr 5000 into 	Timeout
 */

//@Component(service = EventHandler.class,
//        immediate = true,
//        property = {
//                Constants.SERVICE_DESCRIPTION + "=Demo to listen on changes in the resource tree",
//                EventConstants.EVENT_TOPIC + "=org/apache/sling/api/resource/Resource/*",
//                EventConstants.EVENT_FILTER + "=(&(path=/content/AEM63App/en/jcr:content)(resourceType=*/page))"
//        })
public class ChangeListener implements EventHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static int counter;
    private final String SERVICE_USER_NAME = "testuser";

    @Reference
    private ResourceResolverFactory resolverFactory;

    private Page newPage;
    private PageManager pageManager;

    @Override
    public void handleEvent(Event event) {
        counter++;
        logger.info(String.format("value of counter is %d", counter));
        ResourceResolver resourceResolver = null;
        try {
            String path = (String) event.getProperty("path");

            resourceResolver = getResourceResolver();

            Resource resource = resourceResolver.getResource(path);
            Node parentNode = resource.adaptTo(Node.class);
            Node parentParNode = parentNode.getNode("par");

            String templatePath = "/apps/AEM63App/templates/page-home";
            String pageName = "en_" + counter;
            String pageTitle = String.valueOf(parentNode.getProperty("jcr:title").getValue()) + " 1." + counter;
            String pagePath = path.substring(0, path.lastIndexOf("jcr:content"));


            //create a page manager instance
            pageManager = resourceResolver.adaptTo(PageManager.class);
            newPage = pageManager.create(pagePath, pageName, templatePath, pageTitle);

            Node newNode = newPage.adaptTo(Node.class);
            Node newParNode = newNode.getNode("jcr:content").getNode("par");
            String newParNodePath = newParNode.getPath();

            Session session = resourceResolver.adaptTo(Session.class);
            Workspace workspace = session.getWorkspace();

            NodeIterator nodeItem = parentParNode.getNodes();

            while (nodeItem.hasNext()) {
                javax.jcr.Node childNode = nodeItem.nextNode();
                String childNodePathPath = childNode.getPath();

                String nameCopyableNode = childNodePathPath.substring(childNodePathPath.lastIndexOf("/"), childNodePathPath.length());
                String pathToCopyableNode = newParNodePath + nameCopyableNode;

                if (!isNewPageHasCopyableNode(newParNode, nameCopyableNode)) {
                    workspace.copy(childNodePathPath, pathToCopyableNode);
                }
            }
            session.logout();
        } catch (LoginException | WCMException | RepositoryException e) {
            e.printStackTrace();
        }
    }

    private boolean isNewPageHasCopyableNode(Node newNode, String nameCopyableNode) throws RepositoryException {
        String nameOfNode = nameCopyableNode.substring(1);
        return newNode.hasNode(nameOfNode);
    }

    private ResourceResolver getResourceResolver() throws LoginException {

        Map<String, Object> param = new HashMap<String, Object>();
        param.put(ResourceResolverFactory.SUBSERVICE, SERVICE_USER_NAME);

        ResourceResolver resolver = null;
        resolver = resolverFactory.getServiceResourceResolver(param);

        return resolver;
    }
}
