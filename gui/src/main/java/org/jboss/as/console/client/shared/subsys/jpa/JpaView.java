package org.jboss.as.console.client.shared.subsys.jpa;

import java.util.Map;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.layout.FormLayout;
import org.jboss.as.console.client.layout.OneToOneLayout;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.jpa.model.JpaSubsystem;
import org.jboss.as.console.client.v3.widgets.SuggestionResource;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormItem;
import org.jboss.dmr.client.ModelNode;

import static org.jboss.as.console.client.meta.CoreCapabilitiesRegister.DATASOURCE;

/**
 * @author Heiko Braun
 * @date 11/28/11
 */
public class JpaView extends DisposableViewImpl implements JpaPresenter.MyView {

    private JpaPresenter presenter;
    private Form<JpaSubsystem> form;

    @Override
    public Widget createWidget() {

        form = new Form<JpaSubsystem>(JpaSubsystem.class);
        form.setNumColumns(2);


        SuggestionResource suggestionResource = new SuggestionResource("defaultDataSource", "Default Datasource", false,
                Console.MODULES.getCapabilities().lookup(DATASOURCE));
        FormItem defaultDs = suggestionResource.buildFormItem();

        ComboBoxItem inheritance = new ComboBoxItem("inheritance", "Persistence Inheritance")
        {
            @Override
            public boolean isRequired() {
                return false;
            }
        };

        inheritance.setValueMap(new String[] {"DEEP", "SHALLOW"});

        //CheckBoxItem vfs = new CheckBoxItem("defaultVfs", "Enable VFS?");

        form.setFields(defaultDs, inheritance);
        form.setEnabled(false);

        FormToolStrip<JpaSubsystem> formToolStrip = new FormToolStrip<JpaSubsystem>(
                form, new FormToolStrip.FormCallback<JpaSubsystem>() {
            @Override
            public void onSave(Map<String, Object> changeset) {
                presenter.onSave(form.getEditedEntity(), changeset);
            }

            @Override
            public void onDelete(JpaSubsystem entity) {
                // cannot be removed
            }
        });
        formToolStrip.providesDeleteOp(false);

        FormHelpPanel helpPanel = new FormHelpPanel(new FormHelpPanel.AddressCallback() {
            @Override
            public ModelNode getAddress() {
                ModelNode address = Baseadress.get();
                address.add("subsystem", "jpa");
                return address;
            }
        }, form);

        Widget detail = new FormLayout()
                .setForm(form)
                .setHelp(helpPanel).build();

        Widget panel = new OneToOneLayout()
                .setTitle("JPA")
                .setHeadline("JPA Subsystem")
                .setDescription(Console.CONSTANTS.subsys_jpa_desc())
                .setMaster("Details", detail)
                .setMasterTools(formToolStrip.asWidget()).build();



        return panel;
    }

    @Override
    public void setPresenter(JpaPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void updateFrom(JpaSubsystem jpaSubsystem) {
        form.edit(jpaSubsystem);
    }
}
