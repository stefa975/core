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

package org.jboss.as.console.client.core.gin;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.inject.Provider;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.annotations.DefaultGatekeeper;
import com.gwtplatform.mvp.client.googleanalytics.GoogleAnalytics;
import com.gwtplatform.mvp.client.proxy.Gatekeeper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.shared.proxy.TokenFormatter;
import org.jboss.as.console.client.administration.AdministrationPresenter;
import org.jboss.as.console.client.administration.audit.AuditLogPresenter;
import org.jboss.as.console.client.administration.role.RoleAssignmentPresenter;
import org.jboss.as.console.client.analytics.NavigationTracker;
import org.jboss.as.console.client.auth.CurrentUser;
import org.jboss.as.console.client.auth.SignInPagePresenter;
import org.jboss.as.console.client.core.*;
import org.jboss.as.console.client.core.message.MessageBar;
import org.jboss.as.console.client.core.message.MessageCenter;
import org.jboss.as.console.client.core.message.MessageCenterView;
import org.jboss.as.console.client.core.settings.ModelVersions;
import org.jboss.as.console.client.core.settings.SettingsPresenter;
import org.jboss.as.console.client.core.settings.SettingsPresenterWidget;
import org.jboss.as.console.client.domain.groups.ServerGroupPresenter;
import org.jboss.as.console.client.domain.groups.deployment.DomainDeploymentPresenter;
import org.jboss.as.console.client.domain.hosts.HostMgmtPresenter;
import org.jboss.as.console.client.domain.hosts.HostVMMetricPresenter;
import org.jboss.as.console.client.domain.hosts.ServerConfigPresenter;
import org.jboss.as.console.client.domain.hosts.general.HostInterfacesPresenter;
import org.jboss.as.console.client.domain.hosts.general.HostJVMPresenter;
import org.jboss.as.console.client.domain.hosts.general.HostPropertiesPresenter;
import org.jboss.as.console.client.domain.model.HostInformationStore;
import org.jboss.as.console.client.domain.model.ProfileStore;
import org.jboss.as.console.client.domain.model.ServerGroupStore;
import org.jboss.as.console.client.domain.profiles.CurrentProfileSelection;
import org.jboss.as.console.client.domain.profiles.ProfileMgmtPresenter;
import org.jboss.as.console.client.domain.runtime.DomainRuntimePresenter;
import org.jboss.as.console.client.domain.runtime.DomainRuntimegateKeeper;
import org.jboss.as.console.client.domain.runtime.NoServerPresenter;
import org.jboss.as.console.client.domain.topology.TopologyPresenter;
import org.jboss.as.console.client.plugins.AccessControlRegistry;
import org.jboss.as.console.client.plugins.RuntimeExtensionRegistry;
import org.jboss.as.console.client.plugins.SearchIndexRegistry;
import org.jboss.as.console.client.plugins.SubsystemRegistry;
import org.jboss.as.console.client.rbac.HostManagementGatekeeper;
import org.jboss.as.console.client.rbac.PlaceRequestSecurityFramework;
import org.jboss.as.console.client.rbac.SecurityFramework;
import org.jboss.as.console.client.rbac.UnauthorisedPresenter;
import org.jboss.as.console.client.search.Harvest;
import org.jboss.as.console.client.search.Index;
import org.jboss.as.console.client.shared.DialogPresenter;
import org.jboss.as.console.client.shared.deployment.DeploymentStore;
import org.jboss.as.console.client.shared.expr.ExpressionResolver;
import org.jboss.as.console.client.shared.general.InterfacePresenter;
import org.jboss.as.console.client.shared.general.PathManagementPresenter;
import org.jboss.as.console.client.shared.general.PropertiesPresenter;
import org.jboss.as.console.client.shared.general.SocketBindingPresenter;
import org.jboss.as.console.client.shared.help.HelpSystem;
import org.jboss.as.console.client.shared.homepage.HomepagePresenter;
import org.jboss.as.console.client.shared.model.PerspectiveStoreAdapter;
import org.jboss.as.console.client.shared.model.SubsystemLoader;
import org.jboss.as.console.client.shared.model.SubsystemStoreAdapter;
import org.jboss.as.console.client.shared.patching.PatchManagementPresenter;
import org.jboss.as.console.client.shared.runtime.RuntimeBaseAddress;
import org.jboss.as.console.client.shared.runtime.ds.DataSourceMetricPresenter;
import org.jboss.as.console.client.shared.runtime.env.EnvironmentPresenter;
import org.jboss.as.console.client.shared.runtime.jms.JMSMetricPresenter;
import org.jboss.as.console.client.shared.runtime.jpa.JPAMetricPresenter;
import org.jboss.as.console.client.shared.runtime.logging.files.LogFilesPresenter;
import org.jboss.as.console.client.shared.runtime.logging.store.LogStore;
import org.jboss.as.console.client.shared.runtime.logging.store.LogStoreAdapter;
import org.jboss.as.console.client.shared.runtime.naming.JndiPresenter;
import org.jboss.as.console.client.shared.runtime.tx.TXLogPresenter;
import org.jboss.as.console.client.shared.runtime.tx.TXMetricPresenter;
import org.jboss.as.console.client.shared.runtime.web.WebMetricPresenter;
import org.jboss.as.console.client.shared.runtime.ws.WebServiceRuntimePresenter;
import org.jboss.as.console.client.shared.state.ReloadState;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.batch.BatchPresenter;
import org.jboss.as.console.client.shared.subsys.batch.store.BatchStore;
import org.jboss.as.console.client.shared.subsys.batch.store.BatchStoreAdapter;
import org.jboss.as.console.client.shared.subsys.configadmin.ConfigAdminPresenter;
import org.jboss.as.console.client.shared.subsys.deploymentscanner.ScannerPresenter;
import org.jboss.as.console.client.shared.subsys.ejb3.EEPresenter;
import org.jboss.as.console.client.shared.subsys.ejb3.EJB3Presenter;
import org.jboss.as.console.client.shared.subsys.infinispan.*;
import org.jboss.as.console.client.shared.subsys.infinispan.model.CacheContainerStore;
import org.jboss.as.console.client.shared.subsys.infinispan.model.LocalCacheStore;
import org.jboss.as.console.client.shared.subsys.io.IOPresenter;
import org.jboss.as.console.client.shared.subsys.io.bufferpool.BufferPoolStore;
import org.jboss.as.console.client.shared.subsys.io.bufferpool.BufferPoolStoreAdapter;
import org.jboss.as.console.client.shared.subsys.io.worker.WorkerStore;
import org.jboss.as.console.client.shared.subsys.io.worker.WorkerStoreAdapter;
import org.jboss.as.console.client.shared.subsys.jacorb.JacOrbPresenter;
import org.jboss.as.console.client.shared.subsys.jca.DataSourcePresenter;
import org.jboss.as.console.client.shared.subsys.jca.JcaPresenter;
import org.jboss.as.console.client.shared.subsys.jca.ResourceAdapterPresenter;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSourceStore;
import org.jboss.as.console.client.shared.subsys.jca.model.DomainDriverStrategy;
import org.jboss.as.console.client.shared.subsys.jca.model.DriverRegistry;
import org.jboss.as.console.client.shared.subsys.jca.model.StandaloneDriverStrategy;
import org.jboss.as.console.client.shared.subsys.jgroups.JGroupsPresenter;
import org.jboss.as.console.client.shared.subsys.jmx.JMXPresenter;
import org.jboss.as.console.client.shared.subsys.jpa.JpaPresenter;
import org.jboss.as.console.client.shared.subsys.logging.HandlerListManager;
import org.jboss.as.console.client.shared.subsys.logging.LoggingPresenter;
import org.jboss.as.console.client.shared.subsys.mail.MailPresenter;
import org.jboss.as.console.client.shared.subsys.messaging.MsgDestinationsPresenter;
import org.jboss.as.console.client.shared.subsys.messaging.cluster.MsgClusteringPresenter;
import org.jboss.as.console.client.shared.subsys.messaging.connections.MsgConnectionsPresenter;
import org.jboss.as.console.client.shared.subsys.modcluster.ModclusterPresenter;
import org.jboss.as.console.client.shared.subsys.security.SecurityDomainsPresenter;
import org.jboss.as.console.client.shared.subsys.security.SecuritySubsystemPresenter;
import org.jboss.as.console.client.shared.subsys.threads.ThreadsPresenter;
import org.jboss.as.console.client.shared.subsys.undertow.HttpPresenter;
import org.jboss.as.console.client.shared.subsys.undertow.ServletPresenter;
import org.jboss.as.console.client.shared.subsys.undertow.UndertowPresenter;
import org.jboss.as.console.client.shared.subsys.web.WebPresenter;
import org.jboss.as.console.client.shared.subsys.ws.DomainEndpointStrategy;
import org.jboss.as.console.client.shared.subsys.ws.EndpointRegistry;
import org.jboss.as.console.client.shared.subsys.ws.StandaloneEndpointStrategy;
import org.jboss.as.console.client.shared.subsys.ws.WebServicePresenter;
import org.jboss.as.console.client.standalone.ServerMgmtApplicationPresenter;
import org.jboss.as.console.client.standalone.StandaloneServerPresenter;
import org.jboss.as.console.client.standalone.deployment.StandaloneDeploymentPresenter;
import org.jboss.as.console.client.standalone.runtime.StandaloneRuntimePresenter;
import org.jboss.as.console.client.standalone.runtime.VMMetricsPresenter;
import org.jboss.as.console.client.tools.BrowserPresenter;
import org.jboss.as.console.client.tools.ToolsPresenter;
import org.jboss.as.console.client.tools.modelling.workbench.repository.RepositoryPresenter;
import org.jboss.as.console.client.v3.stores.domain.HostStore;
import org.jboss.as.console.client.v3.stores.domain.HostStoreAdapter;
import org.jboss.as.console.client.v3.stores.domain.ServerStore;
import org.jboss.as.console.client.v3.stores.domain.ServerStoreAdapter;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.mbui.behaviour.CoreGUIContext;
import org.jboss.as.console.spi.GinExtension;
import org.jboss.dmr.client.dispatch.DispatchAsync;
import org.jboss.dmr.client.dispatch.HandlerMapping;
import org.jboss.dmr.client.dispatch.impl.DMRHandler;
import org.jboss.gwt.circuit.Dispatcher;
import org.jboss.dmr.client.dispatch.impl.UploadHandler;


/**
 * Overall module configuration.
 *
 * @see CoreUIModule
 *
 * @author Heiko Braun
 * @date 1/31/11
 */
@GinExtension("org.jboss.as.console.App")
public interface CoreUI {


    SubsystemRegistry getSubsystemRegistry();
    RuntimeExtensionRegistry getRuntimeLHSItemExtensionRegistry();

    PlaceManager getPlaceManager();
    EventBus getEventBus();
    //ProxyFailureHandler getProxyFailureHandler();
    TokenFormatter getTokenFormatter();

    @DefaultGatekeeper
    Gatekeeper getRBACGatekeeper();

    HostManagementGatekeeper getHostManagementGatekeeper();
    DomainRuntimegateKeeper getDomainRuntimegateKeeper();

    CurrentUser getCurrentUser();
    BootstrapContext getBootstrapContext();
    ApplicationProperties getAppProperties();

    GoogleAnalytics getAnalytics();
    NavigationTracker getTracker();

    Harvest getHarvest();
    Index getIndex();
    FeatureSet getFeatureSet();

    Scheduler getScheduler();

    // ----------------------------------------------------------------------

    Header getHeader();
    Footer getFooter();

    MessageBar getMessageBar();
    MessageCenter getMessageCenter();
    MessageCenterView getMessageCenterView();

    HelpSystem getHelpSystem();

    ExpressionResolver getExpressionManager();
    Baseadress getBaseadress();
    RuntimeBaseAddress getRuntimeBaseAddress();

    ModelVersions modelVersions();

    // ----------------------------------------------------------------------

    DispatchAsync getDispatchAsync();
    HandlerMapping getDispatcherHandlerRegistry();
    DMRHandler getDMRHandler();
    UploadHandler getUploadHHandler();

    ApplicationMetaData getApplicationMetaData();

    // ----------------------------------------------------------------------
    AsyncProvider<HomepagePresenter> getHomepagePresenter();

    Provider<SignInPagePresenter> getSignInPagePresenter();
    AsyncProvider<MainLayoutPresenter> getMainLayoutPresenter();
    AsyncProvider<ToolsPresenter> getToolsPresenter();


    AsyncProvider<BrowserPresenter> getBrowserPresenter();
    //AsyncProvider<DebugPresenter> getDebugPresenter();

    AsyncProvider<SettingsPresenter> getSettingsPresenter();
    AsyncProvider<SettingsPresenterWidget> getSettingsPresenterWidget();


    // ----------------------------------------------------------------------
    AsyncProvider<ServerMgmtApplicationPresenter> getServerManagementAppPresenter();
    AsyncProvider<StandaloneDeploymentPresenter> getDeploymentBrowserPresenter();

    DeploymentStore getDeploymentStore();


    // ----------------------------------------------------------------------
    // domain config below
    AsyncProvider<ProfileMgmtPresenter> getProfileMgmtPresenter();
    CurrentProfileSelection getCurrentSelectedProfile();
    ReloadState getReloadState();

    AsyncProvider<TopologyPresenter> getServerGroupHostMatrixPresenter();
    AsyncProvider<ServerGroupPresenter> getServerGroupsPresenter();

    ProfileStore getProfileStore();
    SubsystemLoader getSubsystemStore();
    ServerGroupStore getServerGroupStore();
    HostInformationStore getHostInfoStore();


    AsyncProvider<DomainDeploymentPresenter> getDeploymentsPresenter();

    AsyncProvider<HostMgmtPresenter> getHostMgmtPresenter();
    AsyncProvider<ServerConfigPresenter> getServerPresenter();

    // ----------------------------------------------------------------------
    // shared subsystems
    AsyncProvider<DataSourcePresenter> getDataSourcePresenter();
    DataSourceStore getDataSourceStore();

    DomainDriverStrategy getDomainDriverStrategy();
    StandaloneDriverStrategy getStandloneDriverStrategy();
    DriverRegistry getDriverRegistry();

    AsyncProvider<EJB3Presenter> getEJB3Presenter();
    AsyncProvider<MsgDestinationsPresenter> getMsgDestinationsPresenter();
    AsyncProvider<MsgConnectionsPresenter> getMsgConnectionsPresenter();
    AsyncProvider<MsgClusteringPresenter> getMsgClusteringPresenter();

    AsyncProvider<LoggingPresenter> getLoggingPresenter();
    AsyncProvider<LogFilesPresenter> getLogFilesPresenter();

    HandlerListManager getHandlerListManager();

    AsyncProvider<ScannerPresenter> getScannerPresenter();
    AsyncProvider<ConfigAdminPresenter> getConfigAdminPresenter();
    AsyncProvider<SocketBindingPresenter> getSocketBindingPresenter();

    // Infinispan
    AsyncProvider<CacheContainerPresenter> getCacheContainerPresenter();
    CacheContainerStore getCacheContainerStore();
    AsyncProvider<LocalCachePresenter> getLocalCachePresenter();
    LocalCacheStore getLocalCacheStore();
    AsyncProvider<InvalidationCachePresenter> getInvalidationCachePresenter();
    AsyncProvider<DistributedCachePresenter> getDistributedCachePresenter();
    AsyncProvider<ReplicatedCachePresenter> getReplicatedCachePresenter();

    AsyncProvider<ThreadsPresenter> getBoundedQueueThreadPoolPresenter();

    AsyncProvider<WebPresenter> getWebPresenter();

    AsyncProvider<InterfacePresenter> getInterfacePresenter();
    AsyncProvider<PropertiesPresenter> getDomainPropertiesPresenter();

    AsyncProvider<HostPropertiesPresenter> getHostPropertiesPresenter();
    AsyncProvider<HostJVMPresenter> getHostJVMPresenter();
    AsyncProvider<HostInterfacesPresenter> getHostInterfacesPresenter();

    AsyncProvider<StandaloneServerPresenter> getStandaloneServerPresenter();

    AsyncProvider<WebServicePresenter> getWebServicePresenter();
    AsyncProvider<WebServiceRuntimePresenter> getWebServiceRuntimePresenter();

    EndpointRegistry getEndpointRegistry();
    DomainEndpointStrategy getDomainEndpointStrategy();
    StandaloneEndpointStrategy getStandaloneEndpointStrategy();

    AsyncProvider<ResourceAdapterPresenter> getResourceAdapterPresenter();
    AsyncProvider<JndiPresenter> getJndiPresenter();

    AsyncProvider<VMMetricsPresenter> getVMMetricsPresenter();
    AsyncProvider<HostVMMetricPresenter> getServerVMMetricPresenter();

    //AsyncProvider<TransactionPresenter> getTransactionPresenter();
    AsyncProvider<SecuritySubsystemPresenter> getSecuritySubsystemPresenter();
    AsyncProvider<SecurityDomainsPresenter> getSecurityDomainsPresenter();

    AsyncProvider<StandaloneRuntimePresenter> getRuntimePresenter();
    AsyncProvider<DomainRuntimePresenter> getDomainRuntimePresenter();
    AsyncProvider<TXMetricPresenter> getTXMetricPresenter();
    AsyncProvider<TXLogPresenter> getTXLogPresenter();

    AsyncProvider<JacOrbPresenter> getJacOrbPresenter();
    AsyncProvider<JpaPresenter> getJpaPresenter();
    AsyncProvider<MailPresenter> getMailPresenter();
    AsyncProvider<ModclusterPresenter> getModclusterPresenter();
    AsyncProvider<JMXPresenter> getJMXPresenter();
    AsyncProvider<EEPresenter> getEEPresenter();

    AsyncProvider<JcaPresenter> getJcaPresenter();

    AsyncProvider<WebMetricPresenter> WebMetricPresenter();

    AsyncProvider<JMSMetricPresenter> JMSMetricPresenter();

    AsyncProvider<DataSourceMetricPresenter> DataSourceMetricPresenter();

    AsyncProvider<JPAMetricPresenter> JPAMetricPresenter();

    AsyncProvider<JGroupsPresenter> JGroupsPresenter();

    AsyncProvider<PathManagementPresenter> PathManagementPresenter();

    AsyncProvider<EnvironmentPresenter> EnvironmentPresenter();

    AsyncProvider<PatchManagementPresenter> getPatchManagerProvider();

    AsyncProvider<BatchPresenter> getBatchPresenter();

    AsyncProvider<IOPresenter> getIOPresenter();

    // Administration
    AsyncProvider<AdministrationPresenter> getAdministrationPresenter();
    AsyncProvider<RoleAssignmentPresenter> getRoleAssignmentPresenter();
    AsyncProvider<AuditLogPresenter> getAuditLogPresenter();

    // mbui workbench
    Provider<RepositoryPresenter> getRepositoryPresenter();

    AccessControlRegistry getAccessControlRegistry();

    SearchIndexRegistry getSearchIndexRegistry();

    SecurityFramework getSecurityFramework();
    PlaceRequestSecurityFramework getPlaceRequestSecurityContext();

    UnauthorisedPresenter getUnauthorisedPresenter();

    AsyncProvider<DialogPresenter> getDialogPresenter();

    AsyncProvider<NoServerPresenter> getNoServerPresenter();

    Dispatcher getCircuitDispatcher();

    BufferPoolStore getBufferPoolStore();
    BufferPoolStoreAdapter getBufferPoolStoreAdapter();

    WorkerStore getWorkerStore();
    WorkerStoreAdapter getWorkerStoreAdapter();

    BatchStore getBatchStore();
    BatchStoreAdapter getBatchStoreAdapter();

    LogStore getLogStore();
    LogStoreAdapter getLogStoreAdapter();

    HostStore getHostStore();
    HostStoreAdapter getHostStoreAdapter();

    ServerStore getServerStore();
    ServerStoreAdapter getServerStoreAdapter();

    CoreGUIContext getCoreGUIContext();

    AsyncProvider<HttpPresenter> getHttpPresenter();
    AsyncProvider<ServletPresenter> getServletPresenter();
    AsyncProvider<UndertowPresenter> getUndertowPresenter();

    SubsystemStoreAdapter getSubsystemStoreAdapter();

    PerspectiveStoreAdapter getPerspectiveStoreAdapter();
}
