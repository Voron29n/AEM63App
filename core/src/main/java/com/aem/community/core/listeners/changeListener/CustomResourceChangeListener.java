package com.aem.community.core.listeners.changeListener;

import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(
        immediate = true,
        service = ResourceChangeListener.class,
        property = {
                ResourceChangeListener.PATHS + "=" + "/content/AEM63App",
                ResourceChangeListener.CHANGES + "= ADDED",
                ResourceChangeListener.CHANGES + "= REMOVED",
                ResourceChangeListener.CHANGES + "= CHANGED"
        }
)
public class CustomResourceChangeListener implements ResourceChangeListener {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Reference
    JobManager jobManager;

    @Override
    public void onChange(List<ResourceChange> list) {
        logger.debug("at Change Listener");
    }
}
