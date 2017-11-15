package org.jboss.as.console.client.shared.runtime.ds;

import java.util.Collections;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.Proxy;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.CircuitPresenter;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.model.LoggingCallback;
import org.jboss.as.console.client.shared.model.ResponseWrapper;
import org.jboss.as.console.client.shared.runtime.Metric;
import org.jboss.as.console.client.shared.runtime.RuntimeBaseAddress;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.shared.subsys.jca.ConnectionWindow;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;
import org.jboss.as.console.client.v3.ResourceDescriptionRegistry;
import org.jboss.as.console.client.v3.stores.domain.ServerStore;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.as.console.spi.RequiredResources;
import org.jboss.as.console.spi.SearchIndex;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.dispatch.DispatchAsync;
import org.jboss.dmr.client.dispatch.impl.DMRAction;
import org.jboss.dmr.client.dispatch.impl.DMRResponse;
import org.jboss.gwt.circuit.Action;
import org.jboss.gwt.circuit.Dispatcher;
import org.useware.kernel.gui.behaviour.StatementContext;

import static org.jboss.as.console.client.shared.subsys.jca.VerifyConnectionOp.VerifyResult;
import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 12/19/11
 */
public class DataSourceMetricPresenter extends CircuitPresenter<DataSourceMetricPresenter.MyView,
        DataSourceMetricPresenter.MyProxy> {

    static final String DATASOURCE_ADDRESS = "/{implicit.host}/{selected.server}/subsystem=datasources/data-source=*"; 
    static final String XADATASOURCE_ADDRESS = "/{implicit.host}/{selected.server}/subsystem=datasources/xa-data-source=*"; 
    static final String DATASOURCE_POOL_ADDRESS = DATASOURCE_ADDRESS + "/statistics=pool"; 
    
    @ProxyCodeSplit
    @NameToken(NameTokens.DataSourceMetricPresenter)
    @RequiredResources(resources = {
            DATASOURCE_ADDRESS,
            XADATASOURCE_ADDRESS,
            DATASOURCE_POOL_ADDRESS
            })
    @SearchIndex(keywords = {"data-source", "pool", "pool-usage"})
    public interface MyProxy extends Proxy<DataSourceMetricPresenter>, Place {}

    public interface MyView extends View {

        void setPresenter(DataSourceMetricPresenter presenter);

        void clearSamples();

        void setDatasources(List<DataSource> datasources, boolean isXA);

        void setDSPoolMetric(ModelNode results, boolean isXA);

        void setDSCacheMetric(Metric metric, boolean isXA);
    }

    private DispatchAsync dispatcher;
    private RevealStrategy revealStrategy;
    private DataSource selectedDS;
    private EntityAdapter<DataSource> dataSourceAdapter;
    private ResourceDescriptionRegistry descriptionRegistry;

    private LoadDataSourceCmd loadDSCmd;
    private DataSource selectedXA;
    private final ServerStore serverStore;
    private StatementContext statementContext;

    @Inject
    public DataSourceMetricPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            DispatchAsync dispatcher, Dispatcher circuit,
            ApplicationMetaData metaData, RevealStrategy revealStrategy, StatementContext statementContext,
            ServerStore serverStore, ResourceDescriptionRegistry descriptionRegistry) {
        super(eventBus, view, proxy, circuit);

        this.dispatcher = dispatcher;
        this.revealStrategy = revealStrategy;
        this.serverStore = serverStore;
        this.descriptionRegistry = descriptionRegistry;
        this.statementContext = statementContext;

        this.loadDSCmd = new LoadDataSourceCmd(dispatcher, metaData);

    }

    public void refreshDatasources() {

        getView().clearSamples();
        getView().setDatasources(Collections.EMPTY_LIST, true);
        getView().setDatasources(Collections.EMPTY_LIST, false);

        // Regular Datasources
        loadDSCmd.execute(new LoggingCallback<List<DataSource>>() {

            @Override
            public void onFailure(Throwable caught) {
                Log.error(caught.getMessage());
            }

            @Override
            public void onSuccess(List<DataSource> result) {
                getView().setDatasources(result, false);
            }
        }, false);

        // XA Data Sources
        loadDSCmd.execute(new LoggingCallback<List<DataSource>>() {

            @Override
            public void onFailure(Throwable caught) {
                Log.error(caught.getMessage());
            }

            @Override
            public void onSuccess(List<DataSource> result) {
                getView().setDatasources(result, true);
            }
        }, true);
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
        addChangeHandler(serverStore);
    }

    @Override
    protected void onAction(Action action) {
        getView().clearSamples();

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                if (isVisible()) { refreshDatasources(); }
            }
        });
    }

    @Override
    protected void onReset() {
        super.onReset();
        refreshDatasources();
    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInRuntimeParent(this);
    }

    public void setSelectedDS(DataSource currentSelection, boolean xa) {

        if (!currentSelection.isEnabled()) {
            Console.error(Console.MESSAGES.subsys_jca_err_ds_notEnabled(currentSelection.getName()));
            getView().clearSamples();
            return;
        }

        if (xa) {
            this.selectedXA = currentSelection;
            if (selectedXA != null) { loadMetrics(true); }
        } else {
            this.selectedDS = currentSelection;
            if (selectedDS != null) { loadMetrics(false); }
        }
    }

    private void loadMetrics(boolean isXA) {
        loadDSPoolMetrics(isXA);
        loadDSCacheMetrics(isXA);
    }

    private void loadDSPoolMetrics(final boolean isXA) {

        DataSource target = isXA ? selectedXA : selectedDS;
        if (null == target) { throw new RuntimeException("DataSource selection is null!"); }

        getView().clearSamples();

        String subresource = isXA ? "xa-data-source" : "data-source";
        String name = target.getName();

        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).set(RuntimeBaseAddress.get());
        operation.get(ADDRESS).add("subsystem", "datasources");
        operation.get(ADDRESS).add(subresource, name);
        operation.get(ADDRESS).add("statistics", "pool");
        operation.get(OP).set(READ_RESOURCE_OPERATION);
        operation.get(INCLUDE_RUNTIME).set(true);

        dispatcher.execute(new DMRAction(operation), new LoggingCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode response = dmrResponse.get();

                if (response.isFailure()) {
                    Console.error(Console.MESSAGES.failed("Datasource Metrics"), response.getFailureDescription());
                } else {
                    ModelNode result = response.get(RESULT).asObject();
                    getView().setDSPoolMetric(result, isXA);
                }
            }
        });
    }
    
    private void loadDSCacheMetrics(final boolean isXA) {

        DataSource target = isXA ? selectedXA : selectedDS;
        if (null == target) { throw new RuntimeException("DataSource selection is null!"); }

        getView().clearSamples();

        String subresource = isXA ? "xa-data-source" : "data-source";
        String name = target.getName();

        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).set(RuntimeBaseAddress.get());
        operation.get(ADDRESS).add("subsystem", "datasources");
        operation.get(ADDRESS).add(subresource, name);
        operation.get(ADDRESS).add("statistics", "jdbc");

        operation.get(OP).set(READ_RESOURCE_OPERATION);
        operation.get(INCLUDE_RUNTIME).set(true);

        dispatcher.execute(new DMRAction(operation), new LoggingCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode response = dmrResponse.get();

                if (response.isFailure()) {
                    Console.error(Console.MESSAGES.failed("Datasource Metrics"), response.getFailureDescription());
                } else {
                    ModelNode result = response.get(RESULT).asObject();

                    long size = result.get("PreparedStatementCacheAccessCount").asLong();
                    long hit = result.get("PreparedStatementCacheHitCount").asLong();
                    long miss = result.get("PreparedStatementCacheMissCount").asLong();

                    Metric metric = new Metric(
                            size, hit, miss
                    );

                    getView().setDSCacheMetric(metric, isXA);
                }
            }
        });
    }

    public void verifyConnection(final String dsName, boolean isXA) {
        String subresource = isXA ? "xa-data-source" : "data-source";

        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).set(RuntimeBaseAddress.get());
        operation.get(ADDRESS).add("subsystem", "datasources");
        operation.get(ADDRESS).add(subresource, dsName);
        operation.get(OP).set("test-connection-in-pool");

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(final Throwable caught) {
                show(new VerifyResult(caught, false));
            }

            @Override
            public void onSuccess(DMRResponse response) {
                VerifyResult verifyResult;
                ModelNode result = response.get();
                ResponseWrapper<Boolean> wrapped = new ResponseWrapper<Boolean>(!result.isFailure(), result);

                if (wrapped.getUnderlying()) {
                    verifyResult = new VerifyResult(true, false,
                            Console.MESSAGES.verify_datasource_successful_message(dsName));
                } else {
                    verifyResult = new VerifyResult(false, false,
                            Console.MESSAGES.verify_datasource_failed_message(dsName), result.getFailureDescription());
                }
                show(verifyResult);
            }

            private void show(VerifyResult result) {
                new ConnectionWindow(dsName, result).show();
            }
        });
    }

    public void flush(final String dsName, final String flushOp, boolean isXA) {


        String subresource = isXA ? "xa-data-source" : "data-source";

        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).set(RuntimeBaseAddress.get());
        operation.get(ADDRESS).add("subsystem", "datasources");
        operation.get(ADDRESS).add(subresource, dsName);
        operation.get(OP).set(flushOp);

        dispatcher.execute(new DMRAction(operation), new LoggingCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode response = dmrResponse.get();

                if (response.isFailure()) {
                    Console.error(Console.MESSAGES.failed(
                            Console.MESSAGES.flushConnectionsError(dsName)), response.getFailureDescription());
                } else {
                    Log.info("Successfully executed flush operation ':" + flushOp + "'");
                    Console.info(Console.MESSAGES.successful(
                            Console.MESSAGES.flushConnectionsSuccess(dsName)));
                }
            }
        });

    }

    public ResourceDescriptionRegistry getDescriptionRegistry() {
        return descriptionRegistry;
    }

    public StatementContext getStatementContext() {
        return statementContext;
    }
}
