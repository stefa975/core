/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jboss.as.console.client.core;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import org.jboss.as.console.client.ProductConfig;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.rbac.StandardRole;

import javax.inject.Inject;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Heiko Braun
 * @date 2/11/11
 */
public class BootstrapContext implements ApplicationProperties {

    private Map<String,String> ctx = new HashMap<String,String>();
    private String initialPlace = null;
    private Throwable lastError;
    private String serverName;
    private String productName;
    private String productVersion;
    private String principal;
    private boolean hostManagementDisabled;
    private boolean groupManagementDisabled;
    private Set<String> roles;

    private Set<String> addressableHosts = Collections.emptySet();
    private Set<String> addressableGroups = Collections.emptySet();
    private String runAs;
    private List<ProfileRecord> initialProfiles;
    private ServerInstance initialServer;

    @Inject
    public BootstrapContext(ProductConfig productConfig) {

        // Default values
        this.productName = "Management Console";
        this.productVersion = "";

        String devHost = productConfig.getDevHost();

        String domainApi = GWT.isScript() ? getBaseUrl() + "management" : "http://" + devHost + ":8888/app/proxy";
        setProperty(DOMAIN_API, domainApi);

        String deploymentApi = GWT.isScript() ? getBaseUrl() + "management/add-content" : "http://" + devHost + ":8888/app/upload";
        setProperty(DEPLOYMENT_API, deploymentApi);

        String patchApi = GWT.isScript() ? getBaseUrl() + "management-upload" : "http://" + devHost + ":8888/app/patch";
        setProperty(PATCH_API, patchApi);

        String logoutApi = GWT.isScript() ? getBaseUrl() + "logout" : "http://" + devHost + ":8888/app/logout";
        setProperty(LOGOUT_API, logoutApi);

        System.out.println("Domain API Endpoint: " + domainApi);
    }

    private String getBaseUrl() {
        // extract host
        String base = GWT.getHostPageBaseURL();
        return extractHttpEndpointUrl(base);

    }

    public static String extractHttpEndpointUrl(String base) {
        String protocol = base.substring(0, base.indexOf("//")+2);
        String remainder = base.substring(base.indexOf(protocol)+protocol.length(), base.length());

        String host;
        String port;

        int portDelim = remainder.indexOf(":");
        if(portDelim !=-1 )
        {
            host = remainder.substring(0, portDelim);
            String portRemainder = remainder.substring(portDelim+1, remainder.length());
            if(portRemainder.indexOf("/")!=-1)
            {
                port = portRemainder.substring(0, portRemainder.indexOf("/"));
            }
            else
            {
                port = portRemainder;
            }
        }
        else
        {
            host = remainder.substring(0, remainder.indexOf("/"));
            if ("https://".equalsIgnoreCase(protocol)) {
                port = "443";
            } else {
                port = "80";
            }
        }

        // default url
        return protocol + host + ":" + port + "/";
    }

    @Override
    public void setProperty(String key, String value)
    {
        ctx.put(key, value);
    }

    @Override
    public String getProperty(String key)
    {
        return ctx.get(key);
    }

    @Override
    public boolean hasProperty(String key)
    {
        return getProperty(key)!=null;
    }

    public PlaceRequest getDefaultPlace() {
        PlaceRequest.Builder builder = new PlaceRequest.Builder().nameToken(NameTokens.HomepagePresenter);
        return builder.build();
    }

    @Override
    public void removeProperty(String key) {

        ctx.remove(key);
    }

    @Override
    public boolean isStandalone() {
        return getProperty(BootstrapContext.STANDALONE).equals("true");
    }

    public void setInitialPlace(String nameToken) {
        this.initialPlace = nameToken;
    }

    public String getInitialPlace() {
        return initialPlace;
    }

    public String getLogoutUrl() {
        String url = getProperty(LOGOUT_API);

        if(!GWT.isScript())
            url += "?gwt.codesvr=" + Window.Location.getParameter("gwt.codesvr");
        return url;
    }

    public void setlastError(Throwable caught) {
        this.lastError = caught;
    }

    public Throwable getLastError() {
        return lastError;
    }

    public String getProductName()
    {
        return productName;
    }

    public void setProductName(final String productName)
    {
        this.productName = productName;
    }

    public String getProductVersion()
    {
        return productVersion;
    }

    public void setProductVersion(final String productVersion)
    {
        this.productVersion = productVersion;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setHostManagementDisabled(boolean b) {
        this.hostManagementDisabled = b;
    }

    public boolean isHostManagementDisabled() {
        return hostManagementDisabled;
    }

    public void setGroupManagementDisabled(boolean b) {
        this.groupManagementDisabled = b;
    }

    public boolean isGroupManagementDisabled() {
        return groupManagementDisabled;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public boolean isSuperUser() {
        boolean match = false;
        for(String role : roles)
        {
            if(StandardRole.SUPER_USER.equalsIgnoreCase(role))
            {
                match = true;
                break;
            }
        }
        return match;
    }

    public boolean isAdmin() {
        boolean match = false;
        for(String role : roles)
        {
            if(StandardRole.ADMINISTRATOR.equalsIgnoreCase(role))
            {
                match = true;
                break;
            }
        }
        return match;
    }

    public void setAddressableHosts(Set<String> hosts) {
        this.addressableHosts = hosts;
    }

    public Set<String> getAddressableHosts() {
        return addressableHosts;
    }

    public void setAdressableGroups(Set<String> groups) {
        this.addressableGroups = groups;
    }

    public Set<String> getAddressableGroups() {
        return addressableGroups;
    }

    public void setRunAs(final String runAs) {
        this.runAs = runAs;
    }

    public String getRunAs() {
        return runAs;
    }

    public void setInitialProfiles(final List<ProfileRecord> initialProfiles) {
        this.initialProfiles = initialProfiles;
    }

    public List<ProfileRecord> getInitialProfiles() {
        return initialProfiles;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
}
