/*
 * Copyright 2015-2016 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.console.client.shared.subsys.elytron.ui.mapper;

import java.util.List;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.subsys.elytron.store.ElytronStore;
import org.jboss.as.console.client.shared.subsys.elytron.ui.ElytronGenericResourceView;
import org.jboss.as.console.client.v3.dmr.ResourceDescription;
import org.jboss.as.console.client.widgets.pages.PagedView;
import org.jboss.ballroom.client.rbac.SecurityContext;
import org.jboss.dmr.client.Property;
import org.jboss.gwt.circuit.Dispatcher;

/**
 * @author Claudio Miranda <claudio@redhat.com>
 */
public class PermissionMapperView {

    private Dispatcher circuit;
    private ResourceDescription rootDescription;
    private SecurityContext securityContext;

    private SimplePermissionMapperView simplePermissionMapperEditor;
    private ElytronGenericResourceView logicalPermissionMapperEditor;
    private ElytronGenericResourceView customPermissionMapperEditor;
    private ConstantPermissionMapperView constantPermissionMapperEditor;

    public PermissionMapperView(final Dispatcher circuit, final ResourceDescription rootDescription,
            final SecurityContext securityFramework) {
        this.circuit = circuit;
        this.rootDescription = rootDescription;
        this.securityContext = securityFramework;
    }
    public Widget asWidget() {

        simplePermissionMapperEditor = new SimplePermissionMapperView(circuit,
                rootDescription.getChildDescription("simple-permission-mapper"), securityContext,
                "Simple Permission Mapper",
                ElytronStore.SIMPLE_PERMISSION_MAPPER_ADDRESS);

        logicalPermissionMapperEditor = new ElytronGenericResourceView(circuit,
                rootDescription.getChildDescription("logical-permission-mapper"),
                securityContext, "Logical Permission Mapper", ElytronStore.LOGICAL_PERMISSION_MAPPER_ADDRESS);

        customPermissionMapperEditor = new ElytronGenericResourceView(circuit,
                rootDescription.getChildDescription("custom-permission-mapper"), securityContext,
                "Custom Permission Mapper", ElytronStore.CUSTOM_PERMISSION_MAPPER_ADDRESS);

        constantPermissionMapperEditor = new ConstantPermissionMapperView(circuit,
                rootDescription.getChildDescription("constant-permission-mapper"), securityContext,
                "Constant Permission Mapper", ElytronStore.CONSTANT_PERMISSION_MAPPER_ADDRESS);

        PagedView panel = new PagedView(true);
        panel.addPage("Simple Permission Mapper", simplePermissionMapperEditor.asWidget());
        panel.addPage("Logical Permission Mapper", logicalPermissionMapperEditor.asWidget());
        panel.addPage("Custom Permission Mapper", customPermissionMapperEditor.asWidget());
        panel.addPage("Constant Permission Mapper", constantPermissionMapperEditor.asWidget());
        // default page
        panel.showPage(0);

        return panel.asWidget();
    }

    public void updateCustomPermissionMapper(List<Property> models) {
        this.customPermissionMapperEditor.update(models);
    }

    public void updateLogicalPermissionMapper(List<Property> models) {
        this.logicalPermissionMapperEditor.update(models);
    }

    public void updateSimplePermissionMapper(List<Property> models) {
        this.simplePermissionMapperEditor.update(models);
    }
    
    public void updateConstantPermissionMapper(List<Property> models) {
        this.constantPermissionMapperEditor.update(models);
    }
}
