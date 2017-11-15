/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.console.client.shared.subsys.io;

import java.util.List;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.v3.dmr.AddressTemplate;
import org.jboss.as.console.client.v3.dmr.ResourceDescription;
import org.jboss.as.console.mbui.widgets.ModelNodeFormBuilder;
import org.jboss.ballroom.client.rbac.SecurityContext;
import org.jboss.ballroom.client.widgets.forms.FormCallback;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.dmr.client.Property;

/**
 * @author Harald Pehl
 */
public abstract class IOPanel extends SuspendableViewImpl {

    protected final IOPresenter presenter;
    protected final DefaultCellTable<Property> table;
    protected final ProvidesKey<Property> providesKey;
    protected final ListDataProvider<Property> dataProvider;
    protected final SingleSelectionModel<Property> selectionModel;
    protected final AddressTemplate address;

    @SuppressWarnings("unchecked")
    public IOPanel(AddressTemplate address, IOPresenter presenter) {
        this.address = address;
        this.presenter = presenter;
        this.providesKey = new ProvidesKey<Property>() {
            @Override
            public Object getKey(Property item) {
                return item.getName();
            }
        };
        this.table = new DefaultCellTable<>(5, providesKey);
        this.dataProvider = new ListDataProvider<Property>(providesKey);
        this.selectionModel = new SingleSelectionModel<Property>(providesKey);

        dataProvider.addDataDisplay(table);
        table.setSelectionModel(selectionModel);
    }


    // ------------------------------------------------------ builder / setup methods

    protected ToolStrip buildTools() {
        ToolStrip tools = new ToolStrip();
        tools.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_add(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                onAdd();
            }
        }));
        tools.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_delete(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                onRemove(selectionModel.getSelectedObject().getName());
            }
        }));
        return tools;
    }

    @SuppressWarnings("unchecked")
    protected DefaultCellTable<Property> setupTable() {
        TextColumn<Property> nameColumn = new TextColumn<Property>() {
            @Override
            public String getValue(Property node) {
                return node.getName();
            }
        };
        table.addColumn(nameColumn, "Name");
        return table;
    }

    protected Widget buildFormPanel(final ResourceDescription definition, SecurityContext securityContext) {
        final ModelNodeFormBuilder.FormAssets formAssets = new ModelNodeFormBuilder()
                .setConfigOnly()
                .setResourceDescription(definition)
                .setSecurityContext(securityContext).build();
        formAssets.getForm().setToolsCallback(new FormCallback() {
            @Override
            public void onSave(Map changeSet) {
                IOPanel.this.onModify(selectionModel.getSelectedObject().getName(),
                        formAssets.getForm().getChangedValues());
            }

            @Override
            public void onCancel(Object entity) {
                formAssets.getForm().cancel();
            }
        });

        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                Property worker = selectionModel.getSelectedObject();
                if (worker != null) {
                    formAssets.getForm().edit(worker.getValue());
                } else {
                    formAssets.getForm().clearValues();
                }
            }
        });

        VerticalPanel formPanel = new VerticalPanel();
        formPanel.setStyleName("fill-layout-width");
        formPanel.add(formAssets.getHelp().asWidget());
        formPanel.add(formAssets.getForm().asWidget());

        return formPanel;
    }


    // ------------------------------------------------------ abstract methods

    protected abstract void onAdd();

    protected abstract void onRemove(final String name);

    protected abstract void onModify(final String name, final Map<String, Object> changedValues);


    // ------------------------------------------------------ select & update

    public void select(String key) {
        for (Property property : dataProvider.getList()) {
            if (property.getName().equals(key)) {
                selectionModel.setSelected(property, true);
                break;
            }
        }
    }

    public void update(final List<Property> models) {
        dataProvider.setList(models);
        if (models.isEmpty()) {
            selectionModel.clear();
        } else {
            table.selectDefaultEntity();
        }
        SelectionChangeEvent.fire(selectionModel); // updates ModelNodeForm's editedEntity with current value
    }
}
