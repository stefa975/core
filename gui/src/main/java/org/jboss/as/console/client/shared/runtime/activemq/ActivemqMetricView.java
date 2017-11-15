package org.jboss.as.console.client.shared.runtime.activemq;

import java.util.List;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.layout.SimpleLayout;
import org.jboss.as.console.client.shared.subsys.activemq.model.PreparedTransaction;
import org.jboss.as.console.client.shared.subsys.messaging.model.JMSEndpoint;
import org.jboss.as.console.client.shared.subsys.messaging.model.Queue;
import org.jboss.as.console.client.widgets.pages.PagedView;
import org.jboss.as.console.client.widgets.tables.ViewLinkCell;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tabs.FakeTabPanel;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

/**
 * @author Heiko Braun
 * @date 12/10/11
 */
public class ActivemqMetricView extends SuspendableViewImpl implements ActivemqMetricPresenter.MyView{

    private ActivemqMetricPresenter presenter;
    private TopicMetrics topicMetrics;
    private QueueMetrics queueMetrics;
    private PooledConnectionFactoryRuntimeView pooledConnectionFactoryRuntimeView;
    private PreparedTransactionManagement preparedTransactions;
    private PagedView panel;
    private DefaultCellTable table;
    private ListDataProvider<Property> dataProvider;

    @Override
    public Widget createWidget() {

        this.topicMetrics = new TopicMetrics(presenter);
        this.queueMetrics= new QueueMetrics(presenter);
        pooledConnectionFactoryRuntimeView = new PooledConnectionFactoryRuntimeView(presenter);
        this.preparedTransactions = new PreparedTransactionManagement(presenter);

        LayoutPanel layout = new LayoutPanel();

        FakeTabPanel titleBar = new FakeTabPanel("Messaging Statistics");
        layout.add(titleBar);

        panel = new PagedView();

        this.table = new DefaultCellTable(5);
        this.dataProvider = new ListDataProvider<>();
        this.dataProvider.addDataDisplay(table);
        this.table.setSelectionModel(new SingleSelectionModel<Property>());

        TextColumn<Property> nameColumn = new TextColumn<Property>() {
            @Override
            public String getValue(Property node) {
                return node.getName();
            }
        };
        TextColumn<Property> statsColumn = new TextColumn<Property>() {
            @Override
            public String getValue(Property node) {
                return node.getValue().get("statistics-enabled").asString();
            }
        };

        Column<Property, String> option = new Column<Property, String>(
                new ViewLinkCell<String>(Console.CONSTANTS.common_label_view(), new ActionCell.Delegate<String>() {
                    @Override
                    public void execute(String selection) {
                        presenter.getPlaceManager().revealPlace(
                                new PlaceRequest(NameTokens.ActivemqMetricPresenter).with("name", selection)
                        );
                    }
                })
        ) {
            @Override
            public String getValue(Property node) {
                return node.getName();
            }
        };

        table.addColumn(nameColumn, "Name");
        table.addColumn(statsColumn, "Statistics enabled");
        table.addColumn(option, "Option");

        statsColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        option.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        nameColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

        Widget frontPage = new SimpleLayout()
                .setPlain(true)
                .setHeadline("JMS Messaging Provider")
                .setDescription(Console.CONSTANTS.pleaseChoseMessagingProvider())
                .addContent("", table.asWidget())
                .build();

        panel.addPage("JMS Server", frontPage);


        panel.addPage("Queues", queueMetrics.asWidget()) ;
        panel.addPage("Topics", topicMetrics.asWidget()) ;
        panel.addPage("Pooled Connection Factory", pooledConnectionFactoryRuntimeView.asWidget()) ;
        panel.addPage("Prepared Transactions", preparedTransactions.asWidget()) ;

        // default page
        panel.showPage(0);

        Widget panelWidget = panel.asWidget();
        layout.add(panelWidget);

        layout.setWidgetTopHeight(titleBar, 0, Style.Unit.PX, 40, Style.Unit.PX);
        layout.setWidgetTopHeight(panelWidget, 40, Style.Unit.PX, 100, Style.Unit.PCT);

        return layout;
    }

    @Override
    public void setPresenter(ActivemqMetricPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void updateProvider(List<Property> provider) {
        dataProvider.setList(provider);
        table.selectDefaultEntity();
    }

    @Override
    public void setSelectedProvider(String selectedProvider) {
        if(null==selectedProvider)
        {
            panel.showPage(0);
        }
        else{

            queueMetrics.setProviderName(selectedProvider);
            topicMetrics.setProviderName(selectedProvider);
            presenter.refreshResources(selectedProvider);

            // move to first page if still showing topology
            if(0==panel.getPage())
                panel.showPage(1);
        }
    }

    @Override
    public void clearSamples() {
        topicMetrics.clearSamples();
    }

    @Override
    public void setTopics(List<JMSEndpoint> topics) {
        topicMetrics.setTopics(topics);
    }

    @Override
    public void setQueues(List<Queue> queues) {
        queueMetrics.setQueues(queues);
    }

    @Override
    public void updateQueueMetrics(ModelNode result) {
        queueMetrics.updateFrom(result);
    }

    @Override
    public void updateTopicMetrics(ModelNode result) {
        topicMetrics.updateFrom(result);
    }

    @Override
    public void setPooledConnectionFactoryModel(List<Property> model) {
        pooledConnectionFactoryRuntimeView.setModel(model);
    }

    public void setTransactions(List<PreparedTransaction> transactions) {
        preparedTransactions.setTransactions(transactions);
    }
}
