package com.aem.community.core.service;

import org.apache.jackrabbit.api.security.JackrabbitAccessControlList;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.security.AccessControlList;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.AccessControlPolicyIterator;
import javax.jcr.security.Privilege;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

//@Component(immediate = true, service = ModifyPermissions.class)
public class ModifyPermissions {

    private static final String CONTENT_SITE_FR = "/content/we-retail/fr";
    private static final Logger log = LoggerFactory.getLogger(ModifyPermissions.class);
    private static final String SERVICE_USER_NAME = "testuser";

    @Reference
    private ResourceResolverFactory resolverFactory;

    private AccessControlList acl;

    @Activate
    protected void activate() {
        log.info("ModifyPermissions activated");
        modifyPermissions();
    }

    private void modifyPermissions() {
        Session adminSession = null;

        try {
            //Don’t use loginAdministrative in real application, it’s deprecated
            //loginAdministrative requires whitelisting starting with AEM 6.3 https://sling.apache.org/documentation/the-sling-
            //engine/service-authentication.html#whitelisting-bundles-for-administrative-login
            adminSession = getSession();

            UserManager userMgr = ((org.apache.jackrabbit.api.JackrabbitSession) adminSession).getUserManager();
            AccessControlManager accessControlManager = adminSession.getAccessControlManager();
            Authorizable denyAccess = userMgr.getAuthorizable("deny-access");
            AccessControlPolicyIterator policyIterator =
                    accessControlManager.getApplicablePolicies(CONTENT_SITE_FR);

            try {
                acl = (AccessControlList) policyIterator.nextAccessControlPolicy();
                log.info("");
            } catch (NoSuchElementException nse) {
                acl = (JackrabbitAccessControlList) accessControlManager.getPolicies(CONTENT_SITE_FR)[0];
            }
            Privilege[] privileges =
                    {accessControlManager.privilegeFromName(Privilege.JCR_READ)};
            acl.addAccessControlEntry(denyAccess.getPrincipal(), privileges);
            accessControlManager.setPolicy(CONTENT_SITE_FR, acl);
            adminSession.save();
        } catch (RepositoryException | LoginException e) {
            log.error("**************************Repo Exception", e);
        } finally {
            if (adminSession != null)
                adminSession.logout();
        }
    }

    private Session getSession() throws LoginException {
        Session session = null;

        Map<String, Object> param = new HashMap<String, Object>();
        param.put(ResourceResolverFactory.SUBSERVICE, SERVICE_USER_NAME);

        ResourceResolver resolver = null;
        resolver = resolverFactory.getServiceResourceResolver(param);

        session = resolver.adaptTo(Session.class);

        return session;
    }
}
