package org.jboss.as.console.client.shared.runtime.jpa;

import java.util.List;
import java.util.Map;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SingleSelectionModel;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.layout.MultipleToOneLayout;
import org.jboss.as.console.client.shared.help.StaticHelpPanel;
import org.jboss.as.console.client.shared.runtime.jpa.model.JPADeployment;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.as.console.client.widgets.tables.ViewLinkCell;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.icons.Icons;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;

/**
 * @author Heiko Braun
 * @date 1/19/12
 */
public class PersistenceUnitList {


    private DefaultCellTable<JPADeployment> table;
    private ListDataProvider<JPADeployment> dataProvider;

    private JPAMetricPresenter presenter;

    public PersistenceUnitList(JPAMetricPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {


        table = new DefaultCellTable<JPADeployment>(8, new ProvidesKey<JPADeployment>() {
            @Override
            public Object getKey(JPADeployment item) {
                return item.getDeploymentName()+"_"+item.getPersistenceUnit();
            }
        });

        TextColumn<JPADeployment> name = new TextColumn<JPADeployment>() {

            @Override
            public String getValue(JPADeployment record) {
                return record.getDeploymentName();
            }
        };

        TextColumn<JPADeployment> unit = new TextColumn<JPADeployment>() {

            @Override
            public String getValue(JPADeployment record) {
                return record.getPersistenceUnit();
            }
        };

        Column<JPADeployment, ImageResource> statusColumn =
                new Column<JPADeployment, ImageResource>(new ImageResourceCell()) {
                    @Override
                    public ImageResource getValue(JPADeployment jpa) {

                        ImageResource res = null;

                        if(jpa.isMetricEnabled())
                            res = Icons.INSTANCE.status_good();
                        else
                            res = Icons.INSTANCE.status_bad();

                        return res;
                    }
                };


        Column<JPADeployment, JPADeployment> option = new Column<JPADeployment, JPADeployment>(
                new ViewLinkCell<JPADeployment>(Console.CONSTANTS.common_label_view(), new ActionCell.Delegate<JPADeployment>() {
                    @Override
                    public void execute(JPADeployment selection) {
                        presenter.getPlaceManager().revealPlace(
                                new PlaceRequest(NameTokens.JPAMetricPresenter)
                                        .with("dpl", selection.getDeploymentName())
                                        .with("unit", selection.getPersistenceUnit())
                                        .with("custom", String.valueOf(selection.isCustomDeployment()))
                        );
                    }
                })
        ) {
            @Override
            public JPADeployment getValue(JPADeployment manager) {
                return manager;
            }
        };


        table.addColumn(unit, "Persistence Unit");
        table.addColumn(name, "Deployment");
        table.addColumn(statusColumn, "Metrics Enabled?");
        table.addColumn(option, "Option");
        
        name.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        statusColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

        table.setSelectionModel(
                new SingleSelectionModel<JPADeployment>(

                        // TODO: https://issues.jboss.org/browse/AS7-3441
                        /*new ProvidesKey<JPADeployment>() {
                            @Override
                            public Object getKey(JPADeployment item) {
                                return item.getDeploymentName()+"#"+item.getPersistenceUnit();
                            }
                        } */
                )
        );

        dataProvider = new ListDataProvider<JPADeployment>();
        dataProvider.addDataDisplay(table);


        // ---


        final Form<JPADeployment> form = new Form<JPADeployment>(JPADeployment.class);
        form.setNumColumns(2);
        form.setEnabled(false);

        TextItem deployment = new TextItem("deploymentName", "Deployment");
        TextItem persistenceUnit = new TextItem("persistenceUnit", "Unit");
        CheckBoxItem enabledField = new CheckBoxItem("metricEnabled", "Metrics Enabled?");

        form.setFields(deployment, persistenceUnit, enabledField);


         final StaticHelpPanel helpPanel = new StaticHelpPanel(
                 Console.CONSTANTS.subsys_jpa_deployment_desc()
         );

        form.bind(table);



        FormToolStrip<JPADeployment> formTools = new FormToolStrip<JPADeployment>(
                form, new FormToolStrip.FormCallback<JPADeployment>() {
            @Override
            public void onSave(Map<String, Object> changeset) {
                presenter.onSaveJPADeployment(form.getEditedEntity(), changeset);
            }

            @Override
            public void onDelete(JPADeployment entity) {
                // not provided
            }
        }
        );
        formTools.providesDeleteOp(false);

        VerticalPanel formPanel = new VerticalPanel();
        formPanel.add(formTools.asWidget());
        formPanel.add(helpPanel.asWidget());
        formPanel.add(form.asWidget());

        // ---

        MultipleToOneLayout layout = new MultipleToOneLayout()
                .setPlain(true)
                .setTitle("JPA Metrics")
                .setHeadline("Persistence Units")
                .setDescription(Console.CONSTANTS.subsys_jpa_puList_desc())
                .setMaster(Console.MESSAGES.available("Persistence Units"), table)
                .addDetail("Persistence Unit", formPanel);


        return layout.build();

    }

    public void setUnits(List<JPADeployment> jpaUnits) {
        dataProvider.setList(jpaUnits);

        table.selectDefaultEntity();
    }
}
