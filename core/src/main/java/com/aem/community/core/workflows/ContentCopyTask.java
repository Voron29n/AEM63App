package com.aem.community.core.workflows;

import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.resource.JcrResourceConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component(
        immediate = true,
        service = {WorkflowProcess.class},
        property = {
                "process.label = Experience AEM Content Copy Task"
        }
)
public class ContentCopyTask implements WorkflowProcess {
    private static final String DIALOG_PARTICIPANT_NODE_ID = "node1";
    private static final String PATH_TO_MOVE_PROPERTY = "pathToMove";
    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    private final String SERVICE_USER_NAME = "testuser";

    @Reference
    private ResourceResolverFactory resolverFactory;

    private PageManager pageManager;

    @Override
    public void execute(WorkItem item, WorkflowSession wfSession, MetaDataMap args) throws WorkflowException {

        String pathToMove;
        String pagePath;
        ResourceResolver resourceResolver;

        try {

            final WorkflowData workflowData = item.getWorkflowData();
            pagePath = workflowData.getPayload().toString();

            resourceResolver = getResourceResolver(wfSession);
            pageManager = getPageManager(resourceResolver);

            Page page = pageManager.getContainingPage(resourceResolver.getResource(pagePath));
            Node jscContentOfPage = page.getContentResource().adaptTo(Node.class);

            try {
                pathToMove = String.valueOf(jscContentOfPage.getProperty(PATH_TO_MOVE_PROPERTY).getValue());
            } catch (PathNotFoundException e) {
                log.error("Property 'pathToMove' not found");
                return;
            }

            if (!isPathToMoveCorrect(pathToMove, pagePath, resourceResolver)){
                log.info("PathToMove Incorrect");
                return;
            }

            log.info("PathToMove is Correct");

            if (!pageMoved(resourceResolver, pagePath , pathToMove)){
                log.info("Page wasn't moved");
            }

            log.info("Page was moved");

        } catch (Exception e) {
            log.error(e.toString());
            e.printStackTrace();
        }
    }

    private boolean pageMoved(ResourceResolver resourceResolver, String pagePath, String pathToMove) {
        Session session = resourceResolver.adaptTo(Session.class);
        Workspace workspace = session.getWorkspace();
        pathToMove = pathToMove + pagePath.substring(pagePath.lastIndexOf("/"));
        try {
            workspace.move(pagePath, pathToMove);
            session.save();
            return true;
        } catch (ItemExistsException e) {
            log.info("Some problems with destAbsPath, a node already exists!");
            e.printStackTrace();
        } catch (RepositoryException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return false;
    }

    /**
     * This method check field pathToMove
     */
    private boolean isPathToMoveCorrect(String pathToMove, String pagePath, ResourceResolver resourceResolver) throws RepositoryException {
        if (pathToMove.isEmpty()){
            return false;
        } else if (pathToMove.equals(pagePath.substring(0 , pagePath.lastIndexOf("/")))){
            return false;
        } else if (!isPathToMoveValid(pathToMove, resourceResolver)){
            return false;
        }
        return true;
    }

    private boolean isPathToMoveValid(String pathToMove, ResourceResolver resourceResolver) throws RepositoryException {
        Pattern pattern = Pattern.compile("^(\\/content)");
        Matcher matcher = pattern.matcher(pathToMove);
        if (!matcher.find()){
            return false;
        } else if (pathToMove.equals("/content")){
            return true;
        }
        Resource resourceOfPathToMove = resourceResolver.getResource(pathToMove);
        Node nodeOfPathToMove = resourceOfPathToMove.adaptTo(Node.class);
        return nodeOfPathToMove.isNodeType(NameConstants.NT_PAGE);
    }

    private ResourceResolver getResourceResolver(WorkflowSession wfSession) throws LoginException {
        final Map<String, Object> authInfo = new HashMap<String, Object>();
        authInfo.put(JcrResourceConstants.AUTHENTICATION_INFO_SESSION, wfSession.getSession());
        return resolverFactory.getResourceResolver(authInfo);
    }

    private PageManager getPageManager(ResourceResolver resourceResolver) {
        return pageManager = resourceResolver.adaptTo(PageManager.class);
    }

}