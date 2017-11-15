package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.layout.FormLayout;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.jca.model.XADataSource;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextAreaItem;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.dmr.client.ModelNode;

/**
 * @author Heiko Braun
 * @date 12/13/11
 */
public class XADataSourceConnection {


    private ModelNode helpAddress;
    private FormToolStrip.FormCallback<XADataSource> callback;
    private Form<XADataSource> form;
    private XADataSourcePresenter presenter;

    public XADataSourceConnection(XADataSourcePresenter presenter, FormToolStrip.FormCallback<XADataSource> callback) {

        ModelNode helpAddress = Baseadress.get();
        helpAddress.add("subsystem", "datasources");
        helpAddress.add("xa-data-source", "*");

        this.presenter = presenter;

        this.callback = callback;
        this.helpAddress = helpAddress;
        this.form = new Form<XADataSource>(XADataSource.class);
    }

    public Widget asWidget() {


        TextAreaItem connectionSql= new TextAreaItem("connectionSql", "New Connection Sql") {
            @Override
            public boolean isRequired() {
                return false;
            }
        };

        /*TextBoxItem urlItem = new TextBoxItem("connectionUrl", "Connection URL");
        CheckBoxItem jtaItem = new CheckBoxItem("jta", "Use JTA?");
        CheckBoxItem ccmItem = new CheckBoxItem("ccm", "Use CCM?");*/

        ComboBoxItem tx = new ComboBoxItem("transactionIsolation", "Transaction Isolation");
        tx.setValueMap(new String[]{
                "TRANSACTION_NONE",
                "TRANSACTION_READ_UNCOMMITTED",
                "TRANSACTION_READ_COMMITTED",
                "TRANSACTION_REPEATABLE_READ",
                "TRANSACTION_SERIALIZABLE"
        }
        );

        CheckBoxItem rmOverride = new CheckBoxItem("enableRMOverride", "IsSameRM Override");
        CheckBoxItem interleave = new CheckBoxItem("enableInterleave", "Interleaving");
        CheckBoxItem padXid = new CheckBoxItem("padXid", "Pad XID");
        CheckBoxItem wrap = new CheckBoxItem("wrapXaResource", "Wrap XAResource");


        ToolButton verifyBtn = new ToolButton(Console.CONSTANTS.subsys_jca_dataSource_verify(),
                clickEvent -> presenter.verifyConnection(form.getEditedEntity()));
        verifyBtn.ensureDebugId(Console.DEBUG_CONSTANTS.debug_label_verify_xADataSourceDetails());


        FormToolStrip<XADataSource> formTools = new FormToolStrip<XADataSource>(form, callback);
        formTools.providesDeleteOp(false);

        formTools.addToolButtonRight(verifyBtn);

        form.setFields(connectionSql, tx, rmOverride, interleave, padXid, wrap);
        form.setNumColumns(2);
        form.setEnabled(false);

        FormHelpPanel helpPanel = new FormHelpPanel(() -> helpAddress, form);

        VerticalPanel panel = new VerticalPanel();
        panel.setStyleName("fill-layout");

        panel.add(formTools.asWidget());
        panel.add(helpPanel.asWidget());
        panel.add(form.asWidget());
        return panel;
    }

    public Form<XADataSource> getForm() {
        return form;
    }
}
