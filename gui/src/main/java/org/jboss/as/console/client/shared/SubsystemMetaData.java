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

package org.jboss.as.console.client.shared;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.plugins.SubsystemExtensionMetaData;
import org.jboss.as.console.client.plugins.SubsystemRegistry;
import org.jboss.as.console.client.shared.model.SubsystemRecord;

/**
 * @author Heiko Braun
 * @date 3/29/11
 */

public class SubsystemMetaData {

    static Map<String, SubsystemGroup> groups = new LinkedHashMap<String, SubsystemGroup>();

    private static final String CONNECTOR = "Connector";

    private static final String MESSAGING = "Messaging";

    private static final String CORE = "Core";

    private static final String CONTAINER = "Container";

    private static final String OSGI = "OSGi";

    private static final String INFINISPAN = "Infinispan";

    private static final String SECURITY = "Security";

    private static final String WEB = "Web";

    private static final String OTHER = "Other";

    static {

        // specify groups and the order they appear
        groups.put(CORE, new SubsystemGroup(CORE));
        groups.put(CONNECTOR, new SubsystemGroup(CONNECTOR));
        groups.put(MESSAGING, new SubsystemGroup(MESSAGING));
        groups.put(CONTAINER, new SubsystemGroup(CONTAINER));
        groups.put(SECURITY, new SubsystemGroup(SECURITY));
        groups.put(WEB, new SubsystemGroup(WEB));
        groups.put(OSGI, new SubsystemGroup(OSGI));
        groups.put(INFINISPAN, new SubsystemGroup(INFINISPAN));
        groups.put(OTHER, new SubsystemGroup(OTHER));

        // assign actual subsystems
        groups.get(CONNECTOR).getItems().add(new SubsystemGroupItem("JCA", "jca"));
        groups.get(CONNECTOR).getItems().add(new SubsystemGroupItem("Datasources", "datasources", NameTokens.DataSourceFinder));
        groups.get(CONNECTOR).getItems().add(new SubsystemGroupItem("Resource Adapters", "resource-adapters", NameTokens.ResourceAdapterFinder));
        groups.get(CONNECTOR).getItems().add(new SubsystemGroupItem("Mail", "mail", NameTokens.MailFinder));

        groups.get(WEB).getItems().add(new SubsystemGroupItem("Servlet/HTTP", "web"));
        groups.get(WEB).getItems().add(new SubsystemGroupItem("Web Services", "webservices"));
        groups.get(WEB).getItems().add(new SubsystemGroupItem("ModCluster", "modcluster", NameTokens.ModclusterPresenter));

        groups.get(WEB).getItems().add(new SubsystemGroupItem("Web/HTTP - Undertow", "undertow", NameTokens.UndertowFinder));
        /*groups.get(WEB).getItems().add(new SubsystemGroupItem("Servlets", "undertow", NameTokens.ServletPresenter));
        groups.get(WEB).getItems().add(new SubsystemGroupItem("HTTP", "undertow", NameTokens.HttpPresenter));*/
        //groups.get(WEB).getItems().add(new SubsystemGroupItem("Undertow Core", "undertow", NameTokens.UndertowPresenter));

        //groups.get(CORE).getItems().add(new SubsystemGroupItem("Threads", "threads", Boolean.TRUE));
        groups.get(CORE).getItems().add(new SubsystemGroupItem("IO", "io", NameTokens.IO));
        groups.get(CORE).getItems().add(new SubsystemGroupItem("Logging", "logging", "logging"));
        groups.get(CORE).getItems().add(new SubsystemGroupItem("Deployment Scanners", NameTokens.DeploymentScanner));
        groups.get(CORE).getItems().add(new SubsystemGroupItem("Threads", NameTokens.BoundedQueueThreadPoolPresenter));
        groups.get(CORE).getItems().add(new SubsystemGroupItem("JMX", "jmx"));
        groups.get(CORE).getItems().add(new SubsystemGroupItem("Remoting", NameTokens.Remoting));
        groups.get(CORE).getItems().add(new SubsystemGroupItem("Config Admin", "osgi", NameTokens.ConfigAdminPresenter));
        groups.get(CORE).getItems().add(new SubsystemGroupItem("JGroups", NameTokens.JGroupsPresenter));

        //groups.get(CONTAINER).getItems().add(new SubsystemGroupItem("Naming", "naming", !Console.getBootstrapContext().isStandalone()));
        groups.get(CONTAINER).getItems().add(new SubsystemGroupItem("EJB 3", "ejb3"));
        groups.get(CONTAINER).getItems().add(new SubsystemGroupItem("EE", "ee", NameTokens.EEPresenter));
        //groups.get(CONTAINER).getItems().add(new SubsystemGroupItem("Transactions", "transactions"));

        groups.get(CONTAINER).getItems().add(new SubsystemGroupItem("JPA", "jpa"));
        groups.get(CONTAINER).getItems().add(new SubsystemGroupItem("JacORB", "jacorb"));
        groups.get(CONTAINER).getItems().add(new SubsystemGroupItem("IIOP", NameTokens.IiopOpenJdk));
        groups.get(CONTAINER).getItems().add(new SubsystemGroupItem("Batch", "batch", NameTokens.Batch));
        groups.get(CONTAINER).getItems().add(new SubsystemGroupItem("Batch", "batch-jberet", NameTokens.BatchJberet));

        groups.get(SECURITY).getItems().add(new SubsystemGroupItem("Security", "security", NameTokens.SecDomains));
        groups.get(SECURITY).getItems().add(new SubsystemGroupItem("Security - Elytron", "elytron", NameTokens.ElytronFinder));
        groups.get(SECURITY).getItems().add(new SubsystemGroupItem("PicketLink", "picketlink-federation", NameTokens.PicketLinkFinder));

        groups.get(INFINISPAN).getItems().add(new SubsystemGroupItem("Infinispan", NameTokens.Infinispan, NameTokens.CacheFinderPresenter));
        groups.get(MESSAGING).getItems().add(new SubsystemGroupItem("Messaging - HornetQ", "messaging", NameTokens.HornetqFinder));
        groups.get(MESSAGING).getItems().add(new SubsystemGroupItem("Messaging - ActiveMQ", "messaging-activemq", NameTokens.ActivemqFinder));
    }

    public static void bootstrap(SubsystemRegistry registry) {

        List<SubsystemExtensionMetaData> defaults = new ArrayList<SubsystemExtensionMetaData>();

        for(String groupName : groups.keySet())
        {
            SubsystemGroup group = groups.get(groupName);
            for(SubsystemGroupItem item : group.getItems())
            {
                if(!item.isDisabled())
                {
                    SubsystemExtensionMetaData meta = new SubsystemExtensionMetaData(
                            item.getName(), item.getToken(),
                            group.getName(), item.getKey()
                    );

                    meta.setMajor(item.getMajor());
                    meta.setMinor(item.getMinor());
                    meta.setMicro(item.getMicro());

                    defaults.add(meta);
                }
            }
        }

        registry.getExtensions().addAll(defaults);

    }

    public static Map<String, SubsystemGroup> getGroups() {
        return groups;
    }

    public static SubsystemGroup getGroupForKey(String subsysKey)
    {
        SubsystemGroup matchingGroup = null;

        for(String groupName : groups.keySet())
        {
            SubsystemGroup group = groups.get(groupName);
            for(SubsystemGroupItem item : group.getItems())
            {
                if(item.getKey().equals(subsysKey)
                        && item.isDisabled() == false)
                {
                    matchingGroup =  group;
                    break;
                }
            }

            if(matchingGroup!=null)
                break;
        }

        // found one?
        if(null==matchingGroup)
            matchingGroup = groups.get(OTHER);

        return matchingGroup;
    }

    public static String[] getDefaultSubsystem(String preferred, List<SubsystemRecord> existing)
    {
        if(existing.isEmpty())
            throw new RuntimeException("No subsystem provided!");

        SubsystemRecord chosen = null;
        SubsystemRegistry subsystemRegistry = Console.MODULES.getSubsystemRegistry();
        for (SubsystemExtensionMetaData subsys : subsystemRegistry.getExtensions()) {
            if (preferred.equals(subsys.getToken()))
            {
                for (SubsystemRecord subsystemRecord : existing) {
                    if(subsystemRecord.getKey().equals(subsys.getKey()))
                    {
                        chosen = subsystemRecord;
                        break;
                    }
                }

            }
        }

        if(null==chosen)
            chosen = firstAvailable(existing);

        return resolveTokens(chosen.getKey());
    }

    private static SubsystemRecord firstAvailable(List<SubsystemRecord> existing) {

        SubsystemRecord match =  null;

        for(SubsystemRecord candidate : existing)
        {
            final SubsystemRegistry subsystemRegistry = Console.MODULES.getSubsystemRegistry();
            for(SubsystemExtensionMetaData ext : subsystemRegistry.getExtensions())
            {
                if(candidate.getKey().equals(ext.getKey()))
                {
                    match = candidate;
                    break;
                }
            }
        }

        if(null==match)
            throw new RuntimeException("Failed to resolve default subsystem selection");

        return match;
    }


    public static String[] resolveTokens(String key) {
        String[] token = new String[2];

        final SubsystemRegistry subsystemRegistry = Console.MODULES.getSubsystemRegistry();
        for(SubsystemExtensionMetaData ext : subsystemRegistry.getExtensions())
        {
            if(ext.getKey().equals(key))
            {
                token[0] = ext.getName();
                token[1] = ext.getToken();
                break;
            }
        }

        return token;
    }
}
