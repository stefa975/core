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
package org.jboss.as.console.client.shared.runtime.logviewer;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.Proxy;
import org.jboss.as.console.client.core.CircuitPresenter;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.shared.runtime.logviewer.actions.ChangePageSize;
import org.jboss.as.console.client.shared.runtime.logviewer.actions.NavigateInLogFile;
import org.jboss.as.console.client.shared.runtime.logviewer.actions.SelectLogFile;
import org.jboss.as.console.client.shared.runtime.logviewer.actions.ReadLogFiles;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.spi.AccessControl;
import org.jboss.dmr.client.ModelNode;
import org.jboss.gwt.circuit.Dispatcher;

import java.util.List;

public class LogViewerPresenter extends CircuitPresenter<LogViewerPresenter.MyView, LogViewerPresenter.MyProxy> {

    @ProxyCodeSplit
    @NameToken(NameTokens.LogViewer)
    @AccessControl(resources = "/{selected.host}/{selected.server}/subsystem=logging")
    public interface MyProxy extends Proxy<LogViewerPresenter>, Place {

    }

    public interface MyView extends View {
        void list(List<ModelNode> logFiles);

        void select(LogState logState);

        void refresh(LogState logState);
    }

    private final RevealStrategy revealStrategy;
    private final Dispatcher circuit;
    private final LogStore logStore;

    @Inject
    public LogViewerPresenter(EventBus eventBus, MyView view, MyProxy proxy, RevealStrategy revealStrategy,
                              Dispatcher circuit, LogStore logStore) {
        super(eventBus, view, proxy, logStore);
        this.revealStrategy = revealStrategy;
        this.circuit = circuit;
        this.logStore = logStore;
    }

    @Override
    public void onAction(Class<?> actionType) {
        if (actionType.equals(ReadLogFiles.class)) {
            getView().list(logStore.getLogFiles());

        } else if (actionType.equals(SelectLogFile.class)) {
            getView().select(logStore.getActiveState());
        }

        else if (actionType.equals(NavigateInLogFile.class)) {
            getView().refresh(logStore.getActiveState());
        }

        else if (actionType.equals(ChangePageSize.class)) {
            getView().refresh(logStore.getActiveState());
        }
    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInRuntimeParent(this);
    }

    @Override
    protected void onReset() {
        super.onReset();
        circuit.dispatch(new ReadLogFiles());
    }
}
