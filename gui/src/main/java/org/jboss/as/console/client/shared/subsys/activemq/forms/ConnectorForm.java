package org.jboss.as.console.client.shared.subsys.activemq.forms;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.layout.FormLayout;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.activemq.connections.MsgConnectionsPresenter;
import org.jboss.as.console.client.shared.subsys.activemq.model.ActivemqConnector;
import org.jboss.as.console.client.shared.subsys.activemq.model.ConnectorType;
import org.jboss.as.console.client.v3.widgets.SuggestionResource;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormItem;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextAreaItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.dmr.client.ModelNode;

import static org.jboss.as.console.client.meta.CoreCapabilitiesRegister.NETWORK_SOCKET_BINDING;

/**
 * @author Heiko Braun
 * @date 4/3/12
 */
public class ConnectorForm {

    Form<ActivemqConnector> form = new Form<>(ActivemqConnector.class);
    private ConnectorType type = null;
    boolean isCreate = false;
    private final MsgConnectionsPresenter presenter;
    private FormToolStrip.FormCallback<ActivemqConnector> callback;

    public ConnectorForm(MsgConnectionsPresenter presenter,
            FormToolStrip.FormCallback<ActivemqConnector> callback, ConnectorType type) {
        this.presenter = presenter;
        this.callback = callback;
        this.type = type;
    }

    public Widget asWidget() {
        switch (type) {
            case GENERIC:
                buildGenericForm();
                break;
            case REMOTE:
                buildRemoteForm();
                break;
            case INVM:
                buildInvmForm();
        }

        if (isCreate) {
            form.setNumColumns(1);
        } else {
            form.setNumColumns(2);
            form.setEnabled(false);
        }

        FormHelpPanel helpPanel = new FormHelpPanel(() -> {
            ModelNode address = Baseadress.get();
            address.add("subsystem", "messaging-activemq");
            address.add("server", presenter.getCurrentServer());
            address.add(type.getResource(), "*");
            return address;
        }, form);

        FormLayout formLayout = new FormLayout()
                .setForm(form)
                .setHelp(helpPanel);

        if (!isCreate) {
            FormToolStrip<ActivemqConnector> formTools = new FormToolStrip<>(form, callback);
            formLayout.setTools(formTools);
        }

        return formLayout.build();
    }

    private void buildInvmForm() {
        FormItem name;

        if (isCreate) { name = new TextBoxItem("name", "Name"); } else { name = new TextItem("name", "Name"); }
        NumberBoxItem server = new NumberBoxItem("serverId", "Server ID");
        form.setFields(name, server);
    }

    private void buildRemoteForm() {
        FormItem name;

        if (isCreate) { name = new TextBoxItem("name", "Name"); } else { name = new TextItem("name", "Name"); }
        SuggestionResource suggestionResource = new SuggestionResource("socketBinding", "Socket Binding", true,
                Console.MODULES.getCapabilities().lookup(NETWORK_SOCKET_BINDING));
        FormItem socket = suggestionResource.buildFormItem();


        form.setFields(name, socket);
    }

    private void buildGenericForm() {
        FormItem name;

        if (isCreate) { name = new TextBoxItem("name", "Name"); } else { name = new TextItem("name", "Name"); }
        TextAreaItem factory = new TextAreaItem("factoryClass", "Factory Class");
        SuggestionResource suggestionResource = new SuggestionResource("socketBinding", "Socket Binding", true,
                Console.MODULES.getCapabilities().lookup(NETWORK_SOCKET_BINDING));
        FormItem socket = suggestionResource.buildFormItem();

        form.setFields(name, socket, factory);
    }

    public Form<ActivemqConnector> getForm() {
        return form;
    }

    public void setIsCreate(boolean create) {
        isCreate = create;
    }

}
