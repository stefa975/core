package org.jboss.as.console.client.shared.subsys.jgroups;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.v3.widgets.SuggestionResource;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormItem;
import org.jboss.ballroom.client.widgets.forms.FormValidation;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;
import org.jboss.dmr.client.ModelNode;

import static org.jboss.as.console.client.meta.CoreCapabilitiesRegister.NETWORK_SOCKET_BINDING;

/**
 * @author Heiko Braun
 * @date 2/16/12
 */
public class NewProtocolWizard {


    private JGroupsPresenter presenter;

    public NewProtocolWizard(JGroupsPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("window-content");

        final Form<JGroupsProtocol> form = new Form<>(JGroupsProtocol.class);

        ComboBoxItem nameField = new ComboBoxItem("name", "Type");

        List<String> names = new ArrayList<>();
        for (Protocol element : Protocol.values()) {
            final String name = element.getLocalName();
            if (name!=null && !"TCP".equals(name) && !"UDP".equals(name))
                names.add(name);
        }

        nameField.setValueMap(names);

        FormItem socket = new SuggestionResource("socketBinding", "Socket Binding", false,
                Console.MODULES.getCapabilities().lookup(NETWORK_SOCKET_BINDING))
                .buildFormItem();

        form.setFields(nameField, socket);


        DialogueOptions options = new DialogueOptions(

                // save
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        // merge base

                        FormValidation validation = form.validate();
                        if(validation.hasErrors())
                            return;

                        presenter.onCreateProtocol(form.getUpdatedEntity());

                    }
                },

                // cancel
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        presenter.closeDialoge();
                    }
                }

        );

        // ----------------------------------------

        Widget formWidget = form.asWidget();

        FormHelpPanel helpPanel = new FormHelpPanel(new FormHelpPanel.AddressCallback() {
            @Override
            public ModelNode getAddress() {
                ModelNode address = Baseadress.get();
                address.add("subsystem", "jgroups");
                address.add("stack", "*");
                address.add("protocol", "*");
                return address;
            }
        }, form);

        layout.add(helpPanel.asWidget());

        layout.add(formWidget);

        return new WindowContentBuilder(layout, options).build();
    }
}
