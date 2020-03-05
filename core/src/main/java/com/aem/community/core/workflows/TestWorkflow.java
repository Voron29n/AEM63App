package com.aem.community.core.workflows;

import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = WorkflowProcess.class,
        property = {
                "process.label = EPAM Training WF Process"
        })
public class TestWorkflow implements WorkflowProcess {

    private Logger log = LoggerFactory.getLogger(this.getClass());


    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
        log.info((String) metaDataMap.get("path"));
        String move = workItem.getWorkflow().getInitiator();
        log.info(move);
        final WorkflowData workflowData = workItem.getWorkflowData();

        // Get the path to the JCR resource from the payload
        final String path = workflowData.getPayload().toString();

    }
}
