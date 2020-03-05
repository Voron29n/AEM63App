package com.aem.community.core.listeners.removeNodeExercise;

import org.apache.jackrabbit.oak.commons.PathUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.HashMap;
import java.util.Map;


@Component(
        immediate = true,
        service = JobConsumer.class,
        property = {
                JobConsumer.PROPERTY_TOPICS + "=" + RemoveNodeListener.JOB_TOPIC
        }
)
public class CreatePropertyForRemoveNodeJob implements JobConsumer {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String PATH_TO_REMOVE_NODES = "/var/log/removedProperties";
    private int counter;

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Override
    public JobResult process(Job job) {

        try {
            createNoteWithSetsProperty((String) job.getProperty("eventPath"));
            logger.info("execute");
            counter++;
            return JobResult.OK;
        } catch (ItemExistsException e){
            logger.error("Property not create");
            return JobResult.FAILED;
        } catch (LoginException | RepositoryException e) {
            e.printStackTrace();
            logger.error(e.toString());
            return JobResult.FAILED;
        }
    }

    private void createNoteWithSetsProperty(String eventPath) throws LoginException, RepositoryException {
        ResourceResolver resourceResolver = getResourceResolver();
        Session session_1 = resourceResolver.adaptTo(Session.class);

        Resource resource = resourceResolver.getResource(PATH_TO_REMOVE_NODES);

        String removedNodeName = PathUtils.getName(eventPath);

        Node newNode = resource.adaptTo(Node.class);
        Node node = newNode.addNode(("removeNode_" + counter), "cq:Component");
        node.setProperty("propertyName", removedNodeName);
        node.setProperty("propertyPath", eventPath);
        session_1.save();
        session_1.logout();

        logger.info("something has been remove : {}", eventPath);
    }

    private ResourceResolver getResourceResolver() throws LoginException {

        Map<String, Object> param = new HashMap<String, Object>();
        param.put(ResourceResolverFactory.SUBSERVICE, RemoveNodeListener.SERVICE_USER_NAME);

        ResourceResolver resolver = null;
        resolver = resolverFactory.getServiceResourceResolver(param);

        return resolver;
    }

}
