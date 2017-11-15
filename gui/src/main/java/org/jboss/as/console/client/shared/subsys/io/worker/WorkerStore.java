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
package org.jboss.as.console.client.shared.subsys.io.worker;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.v3.behaviour.CrudOperationDelegate;
import org.jboss.as.console.client.v3.dmr.AddressTemplate;
import org.jboss.as.console.mbui.behaviour.CoreGUIContext;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;
import org.jboss.dmr.client.dispatch.DispatchAsync;
import org.jboss.dmr.client.dispatch.impl.DMRAction;
import org.jboss.dmr.client.dispatch.impl.DMRResponse;
import org.jboss.gwt.circuit.ChangeSupport;
import org.jboss.gwt.circuit.Dispatcher;
import org.jboss.gwt.circuit.meta.Process;
import org.jboss.gwt.circuit.meta.Store;
import org.useware.kernel.gui.behaviour.StatementContext;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Harald Pehl
 */
@Store
public class WorkerStore extends ChangeSupport {

    private static final AddressTemplate RESOURCE_ADDRESS = AddressTemplate.of("{selected.profile}/subsystem=io/worker=*");

    private final DispatchAsync dispatcher;
    private final CoreGUIContext statementContext;
    private final CrudOperationDelegate operationDelegate;
    private final List<Property> workers;

    @Inject
    public WorkerStore(DispatchAsync dispatcher, CoreGUIContext statementContext) {
        this.dispatcher = dispatcher;
        this.statementContext = statementContext;
        this.operationDelegate = new CrudOperationDelegate(statementContext, dispatcher);
        this.workers = new ArrayList<>();
    }


    // ------------------------------------------------------ process methods

    @Process(actionType = AddWorker.class)
    public void add(final AddWorker action, final Dispatcher.Channel channel) {
        String name = action.getWorker().get(NAME).asString();
        operationDelegate.onCreateResource(RESOURCE_ADDRESS, name, action.getWorker(), new RefreshCallback(channel));
    }

    @Process(actionType = ModifyWorker.class)
    public void modify(final ModifyWorker action, final Dispatcher.Channel channel) {
        operationDelegate.onSaveResource(RESOURCE_ADDRESS, action.getName(), action.getChangedValues(),
                new RefreshCallback(channel));
    }

    @Process(actionType = RefreshWorkers.class)
    public void refresh(final Dispatcher.Channel channel) {
        final AddressTemplate address = AddressTemplate.of("{selected.profile}/subsystem=io/");
        ModelNode op = new ModelNode();
        op.get(ADDRESS).set(address.resolve(statementContext));
        op.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        op.get(CHILD_TYPE).set("worker");

        dispatcher.execute(new DMRAction(op), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                channel.nack(caught);
            }

            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode response = dmrResponse.get();
                if (response.isFailure()) {
                    channel.nack(new RuntimeException("Failed to read workers using " + op + ": " +
                            response.getFailureDescription()));
                } else {
                    workers.clear();
                    workers.addAll(response.get(RESULT).asPropertyList());
                    channel.ack();
                }
            }
        });
    }

    @Process(actionType = RemoveWorker.class)
    public void remove(final RemoveWorker action, final Dispatcher.Channel channel) {
        operationDelegate.onRemoveResource(RESOURCE_ADDRESS, action.getName(), new RefreshCallback(channel));
    }


    // ------------------------------------------------------ state access

    public List<Property> getWorkers() {
        return workers;
    }

    public StatementContext getStatementContext() {
        return statementContext;
    }


    // ------------------------------------------------------ inner classes

    private class RefreshCallback implements CrudOperationDelegate.Callback {
        private final Dispatcher.Channel channel;

        public RefreshCallback(Dispatcher.Channel channel) {
            this.channel = channel;
        }

        @Override
        public void onSuccess(AddressTemplate addressTemplate, String name) {
            Console.info(Console.MESSAGES.successfullyModifiedResource(addressTemplate.resolve(statementContext, name).toString()));
            refresh(channel);
        }

        @Override
        public void onFailure(AddressTemplate addressTemplate, String name, Throwable t) {
            Console.error(Console.MESSAGES
                    .failedToLoadResource(addressTemplate.resolve(statementContext, name).toString()), t.getMessage());
            channel.nack(t);
        }

    }
}
