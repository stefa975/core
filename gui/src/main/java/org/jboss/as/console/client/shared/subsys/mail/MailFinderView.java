package org.jboss.as.console.client.shared.subsys.mail;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.widgets.nav.v3.ColumnManager;
import org.jboss.as.console.client.widgets.nav.v3.ContextualCommand;
import org.jboss.as.console.client.widgets.nav.v3.FinderColumn;
import org.jboss.as.console.client.widgets.nav.v3.MenuDelegate;
import org.jboss.as.console.client.widgets.nav.v3.PreviewFactory;

import java.util.List;

/**
 * @author Heiko Braun
 * @since 27/02/15
 */
public class MailFinderView extends SuspendableViewImpl implements MailFinder.MyView {

    private final PlaceManager placeManager;
    private LayoutPanel previewCanvas;
    private SplitLayoutPanel layout;
    private FinderColumn<MailSession> mailSessions;
    private MailFinder presenter;
    private ColumnManager columnManager;
    private Widget mailSessCol;

    interface Template extends SafeHtmlTemplates {
        @Template("<div class=\"{0}\">{1}</div>")
        SafeHtml item(String cssClass, String title, String jndiName);

        @Template("<div class=\"preview-content\"><h1>{0}</h1><p>The mail session is bound to {1}.</p></div>")
        SafeHtml mailSessionPreview(String name, String jndi);
    }

    private static final Template TEMPLATE = GWT.create(Template.class);

    @Inject
    public MailFinderView(PlaceManager placeManager) {

        this.placeManager = placeManager;
    }

    @Override
    public void setPresenter(MailFinder presenter) {

        this.presenter = presenter;
    }

    @Override
    public void updateFrom(List<MailSession> list) {
        mailSessions.updateFrom(list);
    }

    @Override
    public Widget createWidget() {

        previewCanvas = new LayoutPanel();

        layout = new SplitLayoutPanel(2);
        columnManager = new ColumnManager(layout, FinderColumn.FinderId.CONFIGURATION);

        mailSessions = new FinderColumn<MailSession>(
                FinderColumn.FinderId.CONFIGURATION,
                "Mail Session",
                new FinderColumn.Display<MailSession>() {

                    @Override
                    public boolean isFolder(MailSession data) {
                        return false;
                    }

                    @Override
                    public SafeHtml render(String baseCss, MailSession data) {
                        return TEMPLATE.item(baseCss, data.getName(), data.getJndiName());
                    }

                    @Override
                    public String rowCss(MailSession data) {
                        return "";
                    }
                },
                new ProvidesKey<MailSession>() {
                    @Override
                    public Object getKey(MailSession item) {
                        return item.getName();
                    }
                }, presenter.getProxy().getNameToken())
        ;

        mailSessions.setPreviewFactory(new PreviewFactory<MailSession>() {
            @Override
            public void createPreview(final MailSession data, final AsyncCallback<SafeHtml> callback) {
                callback.onSuccess(TEMPLATE.mailSessionPreview(data.getName(), data.getJndiName()));
            }
        });

        mailSessions.setTopMenuItems(
                new MenuDelegate<MailSession>(
                        Console.CONSTANTS.common_label_add(), new ContextualCommand<MailSession>() {
                    @Override
                    public void executeOn(MailSession mailSession) {
                        presenter.launchNewSessionWizard();
                    }
                }, MenuDelegate.Role.Operation)
        );


        mailSessions.setMenuItems(
                new MenuDelegate<MailSession>(
                        Console.CONSTANTS.common_label_view(), new ContextualCommand<MailSession>() {
                    @Override
                    public void executeOn(MailSession mailSession) {
                        placeManager.revealRelativePlace(
                                new PlaceRequest(NameTokens.MailPresenter).with("name", mailSession.getName())
                        );
                    }
                }),
                new MenuDelegate<MailSession>(
                        Console.CONSTANTS.common_label_attributes(), new ContextualCommand<MailSession>() {
                    @Override
                    public void executeOn(MailSession mailSession) {
                        presenter.onLauchAttributesWizard(mailSession);
                    }
                }),
                new MenuDelegate<MailSession>(
                        Console.CONSTANTS.common_label_delete(), new ContextualCommand<MailSession>() {
                    @Override
                    public void executeOn(MailSession mailSession) {
                        presenter.onDelete(mailSession);
                    }
                }, MenuDelegate.Role.Operation)
        );

        mailSessions.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                if(mailSessions.hasSelectedItem())
                {
                    MailSession item = mailSessions.getSelectedItem();
                    columnManager.updateActiveSelection(mailSessCol);
                }
            }
        });

        mailSessCol = mailSessions.asWidget();

        columnManager.addWest(mailSessCol);
        columnManager.add(previewCanvas);

        columnManager.setInitialVisible(1);

        return layout;
    }

    @Override
    public void setPreview(final SafeHtml html) {

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                previewCanvas.clear();
                previewCanvas.add(new ScrollPanel(new HTML(html)));
            }
        });

    }
}
