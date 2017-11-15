package org.jboss.as.console.client.shared.subsys.modcluster;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.layout.OneToOneLayout;
import org.jboss.as.console.client.shared.subsys.modcluster.model.Modcluster;
import org.jboss.as.console.client.v3.widgets.SuggestionResource;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.FormItem;
import org.jboss.ballroom.client.widgets.forms.ListItem;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextAreaItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;

import static org.jboss.as.console.client.meta.CoreCapabilitiesRegister.NETWORK_SOCKET_BINDING;
import static org.jboss.as.console.client.meta.CoreCapabilitiesRegister.SECURITY_SSL_CONTEXT;
import static org.jboss.as.console.client.meta.CoreCapabilitiesRegister.UNDERTOW_LISTENER;

/**
 * @author Pavel Slegr
 * @date 02/16/12
 */
public class ModclusterView extends DisposableViewImpl implements ModclusterPresenter.MyView{

    private ModclusterPresenter presenter;
    private ModclusterForm form;
    private ModclusterForm contextForm;
    private ModclusterForm proxyForm;
    private ModclusterForm sessionForm;
    private ModclusterForm networkingForm;

    private SSLEditor sslEditor;

    @Override
    public Widget createWidget() {


        form = new ModclusterForm(presenter);

        CheckBoxItem advertise = new CheckBoxItem("advertise", "Advertise");
        FormItem advertiseSocket = new SuggestionResource("advertiseSocket", "Advertise Socket", true,
            Console.MODULES.getCapabilities().lookup(NETWORK_SOCKET_BINDING))
            .buildFormItem();

        TextBoxItem advertiseKey= new TextBoxItem("advertiseKey", "Advertise Key", false);
        TextBoxItem balancer = new TextBoxItem("balancer", "Balancer", false);
        TextBoxItem loadBalancingGroup = new TextBoxItem("loadBalancingGroup", "Load Balancing Group", false);
        FormItem connector = new SuggestionResource("connector", "Connector", true,
            Console.MODULES.getCapabilities().lookup(UNDERTOW_LISTENER))
            .buildFormItem();
        FormItem sslContext = new SuggestionResource("sslContext", "SSL Context", false,
                Console.MODULES.getCapabilities().lookup(SECURITY_SSL_CONTEXT))
                .buildFormItem();

        form.setFields(connector, loadBalancingGroup, balancer, advertiseSocket, advertiseKey, advertise, sslContext);

        // ---

        contextForm = new ModclusterForm(presenter);

        TextAreaItem excludedContexts = new TextAreaItem("excludedContexts", "Excluded Contexts");
        excludedContexts.setRequired(false);
        CheckBoxItem autoEnableContexts = new CheckBoxItem("autoEnableContexts", "Auto Enable Contexts");

        contextForm.setFields(autoEnableContexts, excludedContexts);


        // ---

        proxyForm = new ModclusterForm(presenter);

        TextAreaItem proxyList = new TextAreaItem("proxyList", "Proxy List");
        proxyList.setRequired(false);
        TextBoxItem proxyUrl = new TextBoxItem("proxyUrl", "Proxy Url");

        ListItem proxies = new ListItem("proxies", "Proxies");
        proxies.setRequired(false);

        proxyForm.setFields(proxyUrl, proxyList, proxies);


        //---
        sessionForm = new ModclusterForm(presenter);

        CheckBoxItem stickySession = new CheckBoxItem("stickySession", "Sticky Session");
        CheckBoxItem stickySessionForce = new CheckBoxItem("stickySessionForce", "Sticky Session Force");
        CheckBoxItem stickySessionRemove = new CheckBoxItem("stickySessionRemove", "Sticky Session Remove");

        sessionForm.setFields(stickySession, stickySessionForce, stickySessionRemove);

        // --

        networkingForm = new ModclusterForm(presenter);

        NumberBoxItem nodeTimeout = new NumberBoxItem("nodeTimeout", "Node Timeout", -1, Integer.MAX_VALUE);
        NumberBoxItem socketTimeout = new NumberBoxItem("socketTimeout", "Socket Timeout", 1, Integer.MAX_VALUE);
        NumberBoxItem stopContextTimeout = new NumberBoxItem("stopContextTimeout", "Stop Context Timeout", 1, Integer.MAX_VALUE);

        NumberBoxItem maxAttemps = new NumberBoxItem("maxAttemps", "Max Attempts", 1, Integer.MAX_VALUE  );
        CheckBoxItem flushPackets = new CheckBoxItem("flushPackets", "Flush Packets");
        NumberBoxItem flushWait = new NumberBoxItem("flushWait", "Flush Wait", -1, Integer.MAX_VALUE);
        NumberBoxItem ping = new NumberBoxItem("ping", "Ping");
        NumberBoxItem workerTimeout = new NumberBoxItem("workerTimeout", "Worker Timeout", -1, Integer.MAX_VALUE);
        NumberBoxItem ttl = new NumberBoxItem("ttl", "TTL", -1, Integer.MAX_VALUE);

        networkingForm.setFields(nodeTimeout, socketTimeout, stopContextTimeout, maxAttemps, flushPackets, flushWait, ping, ttl, workerTimeout);

        //  --

        sslEditor = new SSLEditor(presenter);

        // --

        OneToOneLayout layout = new OneToOneLayout()
                .setTitle("mod_cluster")
                .setHeadline("mod_cluster Subsystem")
                .setDescription(Console.CONSTANTS.subsys_modcluster_desc())
                .addDetail("Advertising", form.asWidget())
                .addDetail("Sessions", sessionForm.asWidget())
                .addDetail("Web Contexts", contextForm.asWidget())
                .addDetail("Proxies", proxyForm.asWidget())
                .addDetail("SSL", sslEditor.asWidget())
                .addDetail("Networking", networkingForm.asWidget());


        return layout.build();
    }

    @Override
    public void setPresenter(ModclusterPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void updateFrom(Modcluster modcluster) {
        form.updateFrom(modcluster);
        sessionForm.updateFrom(modcluster);
        contextForm.updateFrom(modcluster);
        proxyForm.updateFrom(modcluster);
        networkingForm.updateFrom(modcluster);

        sslEditor.edit(modcluster.getSSLConfig());
    }
}
