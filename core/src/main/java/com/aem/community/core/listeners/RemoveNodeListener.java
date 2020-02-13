package com.aem.community.core.listeners;


import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import java.util.HashMap;
import java.util.Map;

//@Component(
//        service = EventListener.class,
//        immediate = true
//)
public class RemoveNodeListener implements EventListener {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final String SERVICE_USER_NAME = "testuser";

    private final String PATH_TO_REMOVE_NODES = "/var/log/removedProperties";

    private static int counter;

    private Session session;

    @Reference
    private SlingRepository repository;
    @Reference
    private ResourceResolverFactory resolverFactory;

    @Activate
    public void activate(ComponentContext context) {
        log.info("activating ExampleObservation");
        try {

            session = repository.loginService(SERVICE_USER_NAME,null);
            Workspace workspace = session.getWorkspace();
            session.getWorkspace().getObservationManager().addEventListener(
                    this, //handler
                    Event.NODE_REMOVED , //binary combination of event types
                    "/content/AEM63App", //path
                    true, //is Deep?
                    null, //uuids filter
                    null, //nodetypes filter
                    false);
            log.debug("Hello");
        } catch (RepositoryException | NullPointerException e) {
            log.error("unable to register session", e);
            e.printStackTrace();
        }
    }

    @Deactivate
    public void deactivate() {
        if (session != null) {
            session.logout();
        }
    }

    public void onEvent(EventIterator eventIterator) {
        ResourceResolver resourceResolver  = null;

        try {
            while (eventIterator.hasNext()) {
                Event event = eventIterator.nextEvent();
                String eventPath = event.getPath();

                log.info("something has been remove : {}", eventPath);

                resourceResolver = getResourceResolver();

                Session session_1 = resourceResolver.adaptTo(Session.class);

                Resource resource = resourceResolver.getResource(PATH_TO_REMOVE_NODES);

                String removedNodeName = eventPath.substring((eventPath.lastIndexOf("/") + 1));

                Node newNode = resource.adaptTo(Node.class);
                Node node = newNode.addNode(("removeNode_" + counter), "cq:Component");
                node.setProperty("propertyName" , removedNodeName);
                node.setProperty("propertyPath" , eventPath);
                session_1.save();
                session_1.logout();
                log.info("something has been remove : {}", event.getPath());

            }
        } catch (RepositoryException | LoginException | NullPointerException e) {
            log.error("Error while treating events", e);
        }
    }

    private ResourceResolver getResourceResolver() throws LoginException {

        Map<String, Object> param = new HashMap<String, Object>();
        param.put(ResourceResolverFactory.SUBSERVICE, SERVICE_USER_NAME);

        ResourceResolver resolver = null;
        resolver = resolverFactory.getServiceResourceResolver(param);

        return resolver;
    }
}
