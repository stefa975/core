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

package org.jboss.as.console.client.shared.subsys.activemq;

import java.util.List;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.subsys.activemq.model.ActivemqJMSEndpoint;
import org.jboss.as.console.client.shared.subsys.activemq.model.ActivemqJMSQueue;
import org.jboss.as.console.client.widgets.ContentDescription;

/**
 * @author Heiko Braun
 * @date 3/29/11
 */
public class JMSEditor {

    private MsgDestinationsPresenter presenter;
    private JMSTopicList topicList;
    private JMSQueueList queueList;

    private HTML serverName;

    public JMSEditor(MsgDestinationsPresenter presenter) {
        this.presenter = presenter;
    }

    public Widget asWidget() {
        LayoutPanel layout = new LayoutPanel();

        VerticalPanel panel = new VerticalPanel();
        panel.setStyleName("rhs-content-panel");

        ScrollPanel scroll = new ScrollPanel(panel);
        layout.add(scroll);
        layout.setWidgetTopHeight(scroll, 0, Style.Unit.PX, 100, Style.Unit.PCT);

        serverName = new HTML("Replace me");
        serverName.setStyleName("content-header-label");

        panel.add(serverName);
        panel.add(new ContentDescription("Configuration for JMS queues and topics."));

        TabPanel bottomLayout = new TabPanel();
        bottomLayout.addStyleName("default-tabpanel");
        bottomLayout.addStyleName("master_detail-detail");

        queueList = new JMSQueueList(presenter);
        bottomLayout.add(queueList.asWidget(), "Queues");

        topicList = new JMSTopicList(presenter);
        bottomLayout.add(topicList.asWidget(), "Topics");

        bottomLayout.selectTab(0);

        panel.add(bottomLayout);

        return layout;
    }


    public void setTopics(List<ActivemqJMSEndpoint> topics) {
        topicList.setTopics(topics);
    }

    public void setQueues(List<ActivemqJMSQueue> queues) {
        queueList.setQueues(queues);
        serverName.setText("JMS Endpoints: Provider " + presenter.getCurrentServer());
    }

    public void enableEditQueue(boolean b) {
        queueList.setEnabled(b);
    }

    public void enableEditTopic(boolean b) {
        topicList.setEnabled(b);
    }
}
