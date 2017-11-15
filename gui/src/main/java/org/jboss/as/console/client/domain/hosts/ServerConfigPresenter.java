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

package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.user.client.Command;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.CircuitPresenter;
import org.jboss.as.console.client.core.MainLayoutPresenter;
import org.jboss.as.console.client.core.MultiView;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.model.HostInformationStore;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.domain.model.ServerGroupDAO;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.jvm.JvmManagement;
import org.jboss.as.console.client.shared.properties.CreatePropertyCmd;
import org.jboss.as.console.client.shared.properties.DeletePropertyCmd;
import org.jboss.as.console.client.shared.properties.NewPropertyWizard;
import org.jboss.as.console.client.shared.properties.PropertyManagement;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.v3.behaviour.CrudOperationDelegate;
import org.jboss.as.console.client.v3.dmr.AddressTemplate;
import org.jboss.as.console.client.v3.stores.domain.HostStore;
import org.jboss.as.console.client.v3.stores.domain.ServerStore;
import org.jboss.as.console.client.v3.stores.domain.actions.RefreshServer;
import org.jboss.as.console.client.v3.stores.domain.actions.UpdateServer;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.nav.v3.CloseApplicationEvent;
import org.jboss.as.console.mbui.behaviour.CoreGUIContext;
import org.jboss.as.console.spi.OperationMode;
import org.jboss.as.console.spi.RequiredResources;
import org.jboss.as.console.spi.SearchIndex;
import org.jboss.ballroom.client.rbac.SecurityContextChangedEvent;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;
import org.jboss.dmr.client.dispatch.DispatchAsync;
import org.jboss.gwt.circuit.Action;
import org.jboss.gwt.circuit.Dispatcher;
import org.useware.kernel.gui.behaviour.StatementContext;

import java.util.List;
import java.util.Map;

import static org.jboss.as.console.spi.OperationMode.Mode.DOMAIN;
import static org.jboss.dmr.client.ModelDescriptionConstants.NAME;

/**
 * @author Heiko Braun
 * @date 3/3/11
 *
 */
public class ServerConfigPresenter extends CircuitPresenter<ServerConfigPresenter.MyView, ServerConfigPresenter.MyProxy>
        implements JvmManagement, PropertyManagement {

    static final String JVM_ADDRESS = "opt://{implicit.host}/server-config=*/jvm=*";
    static final AddressTemplate JVM_ADDRESS_TEMPLATE = AddressTemplate.of(JVM_ADDRESS);

    @ProxyCodeSplit
    @NameToken(NameTokens.ServerPresenter)
    @OperationMode(DOMAIN)
    @RequiredResources(resources = {
            "/{implicit.host}/server-config=*",
            JVM_ADDRESS,
            "opt://{implicit.host}/server-config=*/system-property=*"},
            recursive = false)
    @SearchIndex(keywords = {"server", "server-config", "jvm", "socket-binding"})
    public interface MyProxy extends ProxyPlace<ServerConfigPresenter>, Place {}


    public interface MyView extends MultiView {
        void setPresenter(ServerConfigPresenter presenter);
        void updateSocketBindings(List<String> result);
        void setJvm(String reference, Property jvm);
        void setProperties(String reference, List<PropertyRecord> properties);
        void setGroups(List<ServerGroupRecord> result);
        void updateFrom(Server server);
    }

    private final ServerStore serverStore;
    private final Dispatcher circuit;
    private final StatementContext statementContext;
    private HostInformationStore hostInfoStore;
    private ServerGroupDAO serverGroupDAO;

    private DefaultWindow propertyWindow;
    private DispatchAsync dispatcher;
    private ApplicationMetaData propertyMetaData;
    private BeanFactory factory;
    private PlaceManager placeManager;


    private final HostStore hostStore;
    private CrudOperationDelegate operationDelegate;


    @Inject
    public ServerConfigPresenter(EventBus eventBus, MyView view, MyProxy proxy,
                                 HostInformationStore hostInfoStore, ServerGroupDAO serverGroupStore,
                                 DispatchAsync dispatcher, ApplicationMetaData propertyMetaData,
                                 BeanFactory factory, PlaceManager placeManager, HostStore hostStore,
                                 ServerStore serverStore, Dispatcher circuit, final CoreGUIContext delegateContext) {

        super(eventBus, view, proxy, circuit);

        this.hostInfoStore = hostInfoStore;
        this.serverGroupDAO = serverGroupStore;
        this.dispatcher = dispatcher;
        this.propertyMetaData = propertyMetaData;
        this.factory = factory;
        this.placeManager = placeManager;
        this.serverStore = serverStore;
        this.hostStore = hostStore;

        this.circuit = circuit;
        this.statementContext = delegateContext;
        this.operationDelegate = new CrudOperationDelegate(statementContext, dispatcher);
    }

    @Override
    public boolean useManualReveal() {
        return true;
    }

    @Override
    public void prepareFromRequest(PlaceRequest request) {
        Command cmd = () ->  {
            getProxy().manualReveal(ServerConfigPresenter.this);
        };

        SecurityContextChangedEvent.AddressResolver resolver = new SecurityContextChangedEvent.AddressResolver<AddressTemplate>() {
            @Override
            public String resolve(AddressTemplate template) {
                String resolved = template.resolveAsKey(statementContext, serverStore.getSelectedServer().getServerName());
                return resolved;
            }
        };

        // RBAC: context change propagation
        SecurityContextChangedEvent.fire(
                ServerConfigPresenter.this,
                cmd,
                resolver
        );

    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
        getEventBus().addHandler(ServerWizardEvent.TYPE, this);

        addChangeHandler(hostStore);
        addChangeHandler(serverStore);
    }

    @Override
    protected void onAction(Action action) {

        if(!isVisible()) return; // don't process anything when not visible


        if(action instanceof RefreshServer)
        {
            refreshView();
        }
    }

    @Override
    protected void onReset() {
        super.onReset();

        loadSocketBindings(new Command() {
            @Override
            public void execute() {

                if (serverStore.getSelectedServer() != null) {
                    refreshView();
                }

                getView().toggle(
                        placeManager.getCurrentPlaceRequest().getParameter("action", "none")
                );
            }
        });
    }

    private void refreshView() {
        Server server = serverStore.findServer(serverStore.getSelectedServer());
        getView().updateFrom(server);
    }

    public void onServerConfigSelectionChanged(final Server server) {
        if (server != null) {
            loadJVMConfiguration(server);
            loadProperties(server);
        }
    }

    private void loadSocketBindings(final Command cmd) {
        serverGroupDAO.loadSocketBindingGroupNames(new SimpleCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> result) {
                getView().updateSocketBindings(result);
                loadServerGroups(cmd);
            }
        });
    }

    private void loadServerGroups(final Command cmd) {
        serverGroupDAO.loadServerGroups(new SimpleCallback<List<ServerGroupRecord>>() {
            @Override
            public void onSuccess(List<ServerGroupRecord> serverGroups) {
                getView().setGroups(serverGroups);
                cmd.execute();
            }
        });
    }


    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(this, MainLayoutPresenter.TYPE_MainContent, this);
    }

    public void closeApplicationView() {
        CloseApplicationEvent.fire(this);
    }

    public void onSaveChanges(final Server entity, Map<String, Object> changedValues) {
        circuit.dispatch(new UpdateServer(entity, changedValues));
        circuit.dispatch(new RefreshServer());
    }

    public String getSelectedHost() {
        return hostStore.getSelectedHost();
    }


    @Override
    public void onCreateJvm(String reference, ModelNode jvm) {
        String name = jvm.get(NAME).asString();
        AddressTemplate address = JVM_ADDRESS_TEMPLATE.replaceWildcards(reference);
        operationDelegate.onCreateResource(address, name, jvm, new CrudOperationDelegate.Callback() {
            @Override
            public void onSuccess(AddressTemplate addressTemplate, String name) {
                Console.info(Console.MESSAGES.added("JVM Configuration"));
                refreshView();
            }

            @Override
            public void onFailure(AddressTemplate addressTemplate, String name, Throwable t) {
                Console.error(Console.MESSAGES.addingFailed("JVM Configuration"), t.getMessage());
            }
        });
    }

    @Override
    public void onDeleteJvm(String reference, String name) {
        AddressTemplate address = JVM_ADDRESS_TEMPLATE.replaceWildcards(reference);
        operationDelegate.onRemoveResource(address, name, new CrudOperationDelegate.Callback() {
            @Override
            public void onSuccess(AddressTemplate addressTemplate, String name) {
                Console.info(Console.MESSAGES.deleted("JVM Configuration"));
                refreshView();
            }

            @Override
            public void onFailure(AddressTemplate addressTemplate, String name, Throwable t) {
                Console.error(Console.MESSAGES.deletionFailed("JVM Configuration"), t.getMessage());
            }
        });
    }

    @Override
    public void onUpdateJvm(String reference, String jvmName, Map<String, Object> changedValues) {
        if (changedValues.size() > 0) {
            AddressTemplate address = JVM_ADDRESS_TEMPLATE.replaceWildcards(reference);

            operationDelegate.onSaveResource(address, jvmName, changedValues, new CrudOperationDelegate.Callback() {
                @Override
                public void onSuccess(AddressTemplate addressTemplate, String name) {
                    Console.info(Console.MESSAGES.modified("JVM Configuration"));
                    refreshView();
                }

                @Override
                public void onFailure(AddressTemplate addressTemplate, String name, Throwable t) {
                    Console.error(Console.MESSAGES.modificationFailed("JVM Configuration"), t.getMessage());
                }
            });
        }
    }

    @Override
    public void onCreateProperty(String reference, final PropertyRecord prop) {
        if (propertyWindow != null && propertyWindow.isShowing()) {
            propertyWindow.hide();
        }

        ModelNode address = new ModelNode();
        address.add("host", serverStore.getSelectedServer().getHostName());
        address.add("server-config", reference);
        address.add("system-property", prop.getKey());

        CreatePropertyCmd cmd = new CreatePropertyCmd(dispatcher, factory, address);
        cmd.execute(prop, new SimpleCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                circuit.dispatch(new RefreshServer());
            }
        });
    }

    @Override
    public void onDeleteProperty(String reference, final PropertyRecord prop) {

        ModelNode address = new ModelNode();
        address.add("host", serverStore.getSelectedServer().getHostName());
        address.add("server-config", reference);
        address.add("system-property", prop.getKey());

        DeletePropertyCmd cmd = new DeletePropertyCmd(dispatcher, factory, address);
        cmd.execute(prop, new SimpleCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                circuit.dispatch(new RefreshServer());
            }
        });
    }

    @Override
    public void onChangeProperty(String reference, PropertyRecord prop) {
        // do nothing
    }

    @Override
    public void launchNewPropertyDialoge(String reference) {
        propertyWindow = new DefaultWindow(Console.MESSAGES.createTitle("System Property"));
        propertyWindow.setWidth(480);
        propertyWindow.setHeight(360);

        propertyWindow.trapWidget(
                new NewPropertyWizard(this, reference, true).asWidget()
        );

        propertyWindow.setGlassEnabled(true);
        propertyWindow.center();
    }

    @Override
    public void closePropertyDialoge() {
        propertyWindow.hide();
    }

    public void loadJVMConfiguration(final Server server) {
        hostInfoStore.loadJVMConfiguration(server.getHostName(), server, new SimpleCallback<Property>() {
            @Override
            public void onSuccess(Property jvm) {
                getView().setJvm(server.getName(), jvm);
            }
        });
    }

    public void loadProperties(final Server server) {
        hostInfoStore
                .loadProperties(server.getHostName(), server, new SimpleCallback<List<PropertyRecord>>() {
                    @Override
                    public void onSuccess(List<PropertyRecord> properties) {
                        getView().setProperties(server.getName(), properties);
                    }
                });
    }

    public String getFilter() {
        return serverStore.getFilter();
    }

}