package org.jboss.as.console.client.shared.subsys.activemq.connections;

import java.util.Map;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.subsys.activemq.forms.DefaultCFForm;
import org.jboss.as.console.client.shared.subsys.activemq.model.ActivemqConnectionFactory;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormValidation;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;

/**
 * @author Heiko Braun
 * @date 4/3/12
 */
public class NewCFWizard {

    MsgConnectionsPresenter presenter;

    public NewCFWizard(MsgConnectionsPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.addStyleName("window-content");

        DefaultCFForm defaultAttributes = new DefaultCFForm(presenter,
                new FormToolStrip.FormCallback<ActivemqConnectionFactory>() {
                    @Override
                    public void onSave(Map<String, Object> changeset) {}

                    @Override
                    public void onDelete(ActivemqConnectionFactory entity) {}
                }, false
        );

        defaultAttributes.getForm().setNumColumns(1);
        defaultAttributes.getForm().setEnabled(true);
        defaultAttributes.setIsCreate(true);

        layout.add(defaultAttributes.asWidget());

        DialogueOptions options = new DialogueOptions(
                event -> {
                    Form<ActivemqConnectionFactory> form = defaultAttributes.getForm();
                    FormValidation validation = form.validate();
                    if (!validation.hasErrors()) { presenter.onCreateCF(form.getUpdatedEntity()); }
                },
                event -> presenter.closeDialogue()
        );

        return new WindowContentBuilder(layout, options).build();
    }
}
