package org.jboss.as.console.client.standalone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.resources.client.ExternalTextResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
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
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.plugins.SubsystemExtensionMetaData;
import org.jboss.as.console.client.plugins.SubsystemRegistry;
import org.jboss.as.console.client.preview.PreviewContent;
import org.jboss.as.console.client.preview.PreviewContentFactory;
import org.jboss.as.console.client.shared.model.SubsystemRecord;
import org.jboss.as.console.client.widgets.nav.v3.ColumnManager;
import org.jboss.as.console.client.widgets.nav.v3.ContextualCommand;
import org.jboss.as.console.client.widgets.nav.v3.FinderColumn;
import org.jboss.as.console.client.widgets.nav.v3.FinderItem;
import org.jboss.as.console.client.widgets.nav.v3.MenuDelegate;
import org.jboss.as.console.client.widgets.nav.v3.PreviewFactory;
import org.jboss.as.console.client.widgets.nav.v3.ValueProvider;

import static com.google.common.base.CaseFormat.LOWER_HYPHEN;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;

/**
 * @author Heiko Braun
 * @since 09/01/15
 */
public class ColumnServerView extends SuspendableViewImpl
        implements ServerMgmtApplicationPresenter.ServerManagementView {

    /**
     * These items will reveal a nested finder contribution
     */
    private final static String[] subsystemFolders = new String[] {
            NameTokens.MailFinder,
            NameTokens.CacheFinderPresenter,
            NameTokens.ActivemqFinder,
            NameTokens.SecDomains,
            NameTokens.PicketLinkFinder,
            NameTokens.UndertowFinder,
            NameTokens.DataSourceFinder,
            NameTokens.ResourceAdapterFinder,
            NameTokens.ElytronFinder
    };

    private final FinderColumn<SubsystemLink> subsystems;
    private final FinderColumn<FinderItem> config;
    private final ArrayList<FinderItem> configLinks;
    private final ColumnManager columnManager;

    private final Widget subsystColWidget;
    private final Widget configColWidget;
    private final PlaceManager placeManager;
    private final PreviewContentFactory contentFactory;

    private SplitLayoutPanel splitlayout;
    private LayoutPanel contentCanvas;
    private ServerMgmtApplicationPresenter presenter;

    interface Template extends SafeHtmlTemplates {
        @Template("<div class=\"{0}\">{1}</div>")
        SafeHtml item(String cssClass, String title);
    }

    private static final Template TEMPLATE = GWT.create(Template.class);


    @Inject
    public ColumnServerView(final PlaceManager placeManager, PreviewContentFactory contentFactory) {
        super();
        this.placeManager = placeManager;
        this.contentFactory = contentFactory;

        contentCanvas = new LayoutPanel();

        splitlayout = new SplitLayoutPanel(2);
        columnManager = new ColumnManager(splitlayout, FinderColumn.FinderId.CONFIGURATION);

        config = new FinderColumn<FinderItem>(
                FinderColumn.FinderId.CONFIGURATION,
                "Configuration",
                new FinderColumn.Display<FinderItem>() {

                    @Override
                    public boolean isFolder(FinderItem data) {
                        return data.isFolder();
                    }

                    @Override
                    public SafeHtml render(String baseCss, FinderItem data) {
                        return TEMPLATE.item(baseCss, data.getTitle());
                    }

                    @Override
                    public String rowCss(FinderItem data) {
                        return data.getTitle().equals("Subsystems") ? "no-menu" : "";
                    }
                },
                new ProvidesKey<FinderItem>() {
                    @Override
                    public Object getKey(FinderItem item) {
                        return item.getTitle();
                    }
                }, NameTokens.ServerProfile);

        config.setPreviewFactory(new PreviewFactory<FinderItem>() {
            @Override
            public void createPreview(FinderItem data, AsyncCallback<SafeHtml> callback) {
                SafeHtmlBuilder html = new SafeHtmlBuilder();
                html.appendHtmlConstant("<div class='preview-content'><h2>").appendEscaped(data.getTitle()).appendHtmlConstant("</h2>");
                html.appendEscaped(resolveDescriptionFor(data.getTitle()));
                html.appendHtmlConstant("</div>");
                callback.onSuccess(html.toSafeHtml());
            }
        });

        config.setMenuItems(new MenuDelegate<FinderItem>("View", new ContextualCommand<FinderItem>() {
            @Override
            public void executeOn(FinderItem item) {
                item.getCmd().execute();
            }
        }));

        configLinks = new ArrayList<>();

        configLinks.add(
                new FinderItem("Subsystems",
                        new Command() {
                            @Override
                            public void execute() {
                                // noop
                            }
                        },
                        true
                )
        );

        configLinks.add(
                new FinderItem("Interfaces",
                        new Command() {
                            @Override
                            public void execute() {
                                placeManager.revealRelativePlace(new PlaceRequest(NameTokens.InterfacePresenter));
                            }
                        },
                        false
                )
        );

        configLinks.add(
                new FinderItem("Socket Binding",
                        new Command() {
                            @Override
                            public void execute() {
                                placeManager.revealRelativePlace(new PlaceRequest(NameTokens.SocketBindingPresenter));
                            }
                        },
                        false
                )
        );

        configLinks.add(
                new FinderItem("Paths",
                        new Command() {
                            @Override
                            public void execute() {
                                placeManager.revealRelativePlace(new PlaceRequest(NameTokens.PathManagementPresenter));
                            }
                        },
                        false
                )
        );

        configLinks.add(
                new FinderItem("System Properties",
                        new Command() {
                            @Override
                            public void execute() {
                                placeManager.revealRelativePlace(new PlaceRequest(NameTokens.PropertiesPresenter));
                            }
                        },
                        false
                )
        );

        subsystems = new FinderColumn<SubsystemLink>(
                FinderColumn.FinderId.CONFIGURATION,
                "Subsystem",
                new FinderColumn.Display<SubsystemLink>() {

                    @Override
                    public boolean isFolder(SubsystemLink data) {
                        return data.isFolder();
                    }

                    @Override
                    public SafeHtml render(String baseCss, SubsystemLink data) {
                        return TEMPLATE.item(baseCss, data.getTitle());
                    }

                    @Override
                    public String rowCss(SubsystemLink data) {
                        return data.isFolder() ? "no-menu" : "";
                    }
                },
                new ProvidesKey<SubsystemLink>() {
                    @Override
                    public Object getKey(SubsystemLink item) {
                        return item.getToken()+"_"+item.getKey();
                    }
                },NameTokens.ServerProfile);

        subsystems.setShowSize(true);
        subsystems.setFilter((item, token) -> item.getTitle().toLowerCase().contains(token.toLowerCase()));

        subsystems.setValueProvider(new ValueProvider<SubsystemLink>() {
            @Override
            public String get(SubsystemLink item) {
                return item.getTitle();
            }
        });

        subsystems.setMenuItems(new MenuDelegate<SubsystemLink>("View", new ContextualCommand<SubsystemLink>() {
            @Override
            public void executeOn(final SubsystemLink link) {
                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                    @Override
                    public void execute() {
                        PlaceRequest placeRequest = new PlaceRequest(link.getToken());
                        if(link.getKey()!=null)
                            placeRequest = placeRequest.with("key", link.getKey());

                        placeManager.revealRelativePlace(placeRequest);
                    }
                });
            }
        }));

        subsystems.setPreviewFactory(new PreviewFactory<SubsystemLink>() {
            @Override
            public void createPreview(SubsystemLink data, AsyncCallback<SafeHtml> callback) {
                PreviewContent content = PreviewContent.INSTANCE;
                ExternalTextResource resource = (ExternalTextResource)content.getResource(data.getToken().replace("-", "_"));
                if(resource!=null) {
                    contentFactory.createContent(resource, callback);
                }
                else
                {
                    SafeHtmlBuilder builder = new SafeHtmlBuilder();
                    String icon = "icon-folder-close-alt";
                    builder.appendHtmlConstant("<center><i class='" + icon + "' style='font-size:48px;top:100px;position:relative'></i></center>");
                    callback.onSuccess(builder.toSafeHtml());
                }

            }
        });

        subsystColWidget = subsystems.asWidget();

        configColWidget = config.asWidget();

        columnManager.addWest(configColWidget);
        columnManager.addWest(subsystColWidget);
        columnManager.add(contentCanvas);

        columnManager.setInitialVisible(1);

        // event handler

        config.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent selectionChangeEvent) {
                columnManager.reduceColumnsTo(1);
                if(config.hasSelectedItem())
                {
                    FinderItem item = config.getSelectedItem();
                    columnManager.updateActiveSelection(configColWidget);

                    clearNestedPresenter();

                    if("Subsystems".equals(item.getTitle())) {

                        columnManager.appendColumn(subsystColWidget);
                        presenter.loadSubsystems();
                    }
                } else {
                    startupContent();
                }
            }
        });


        subsystems.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {

                if(subsystems.hasSelectedItem()) {

                    final SubsystemLink link = subsystems.getSelectedItem();
                    columnManager.updateActiveSelection(subsystColWidget);

                    if(link.isFolder())
                    {
                        // hb: some trickery with regard to nested presenters
                        // it needs to be relative, but should not append to existing hirarchies
                        List<PlaceRequest> next = new ArrayList<PlaceRequest>(2);
                        next.add(placeManager.getCurrentPlaceHierarchy().get(0));
                        next.add(new PlaceRequest(link.getToken()));
                        placeManager.revealPlaceHierarchy(next);
                    }
                    else
                    {
                        clearNestedPresenter();
                    }

                }
            }
        });
    }

    @Override
    public Widget createWidget() {
        Widget widget = splitlayout.asWidget();
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                config.updateFrom(configLinks, false);
            }
        });
        return widget;
    }

    @Override
    public void setPresenter(ServerMgmtApplicationPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setInSlot(Object slot, IsWidget content) {

        if (slot == ServerMgmtApplicationPresenter.TYPE_MainContent) {
            if(content!=null) {
                Widget w = content.asWidget();
                w.getElement().setAttribute("presenter-view", "true");
                setContent(w);
            }
            else
                contentCanvas.clear();
        }
    }

    private void setContent(IsWidget newContent) {
        contentCanvas.clear();
        contentCanvas.add(newContent);
    }

    @Override
    public void updateFrom(List<SubsystemRecord> subsystemRecords) {

        subsystems.updateFrom(matchSubsystems(subsystemRecords), false);

    }

    @Override
    public void clearActiveSelection() {
        configColWidget.getElement().removeClassName("active");
        subsystColWidget.getElement().removeClassName("active");
    }

    private void clearNestedPresenter() {

        presenter.clearSlot(ServerMgmtApplicationPresenter.TYPE_MainContent);

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                if(placeManager.getHierarchyDepth()>1)
                    placeManager.revealRelativePlace(1);
            }
        });
    }

    class SubsystemLink
    {
        String title;
        String token;
        private final String key;
        private final boolean isFolder;

        public SubsystemLink(String title, String token, boolean isFolder) {
            this.title = title;
            this.token = token;
            this.isFolder = isFolder;
            this.key = null;
        }

        public SubsystemLink(String title, String token, String key) {
            this.title = title;
            this.token = token;
            this.key = key;
            this.isFolder = false;
        }

        public String getKey() {
            return key;
        }

        public String getTitle() {
            return title;
        }

        public String getToken() {
            return token;
        }

        public boolean isFolder() {
            return isFolder;
        }
    }

    private List<SubsystemLink> matchSubsystems(List<SubsystemRecord> subsystems)
    {
        Set<String> explicitSubsystems = new HashSet<>();
        List<SubsystemLink> matches = new ArrayList<>();

        SubsystemRegistry registry = Console.getSubsystemRegistry();

        Map<String, List<SubsystemExtensionMetaData>> grouped = new HashMap<String, List<SubsystemExtensionMetaData>>();
        List<String> groupNames = new ArrayList<String>();
        for(SubsystemExtensionMetaData ext : registry.getExtensions())
        {
            if(!grouped.containsKey(ext.getGroup()))
            {
                groupNames.add(ext.getGroup());
                grouped.put(ext.getGroup(), new ArrayList<SubsystemExtensionMetaData>());
            }

            grouped.get(ext.getGroup()).add(ext);
        }

        Collections.sort(groupNames);

        // build groups first
        for(String groupName : groupNames)
        {
            List<SubsystemExtensionMetaData> items = grouped.get(groupName);

            for(SubsystemExtensionMetaData candidate : items)
            {
                for(SubsystemRecord actual: subsystems)
                {
                    if(actual.getKey().equals(candidate.getKey()))
                    {

                        boolean isFolder = false;
                        for (String subsystemFolder : subsystemFolders) {
                            if(candidate.getToken().equals(subsystemFolder)) {
                                isFolder = true;
                                break;
                            }
                        }

                        matches.add(
                                new SubsystemLink(candidate.getName(), candidate.getToken(), isFolder)
                        );

                        explicitSubsystems.add(actual.getKey());
                    }
                }
            }

        }

        for (SubsystemRecord subsystem : subsystems) {
            if (!explicitSubsystems.contains(subsystem.getKey())) {
                String title = LOWER_HYPHEN.to(UPPER_CAMEL, subsystem.getKey());
                matches.add(
                        new SubsystemLink(title, NameTokens.GenericSubsystem, subsystem.getKey())
                );
            }
        }

        return matches;
    }

    @Override
    public void setPreview(final SafeHtml html) {

        if (
                (contentCanvas.getWidgetCount()>0  && !contentCanvas.getWidget(0).getElement().hasAttribute("presenter-view"))
                        || (contentCanvas.getWidgetCount() ==0)
                ) {
                    contentCanvas.clear();
                    contentCanvas.add(new ScrollPanel(new HTML(html)));
        }

    }

    private void startupContent() {
        contentFactory.createContent(PreviewContent.INSTANCE.profile_empty(),
                new SimpleCallback<SafeHtml>() {
                    @Override
                    public void onSuccess(SafeHtml previewContent) {
                        setPreview(previewContent);
                    }
                }
        );
    }


    private String resolveDescriptionFor(String title) {
        if("Paths".equals(title))
            return Console.CONSTANTS.pathsDescription();

        else if("Subsystems".equals(title))
            return Console.CONSTANTS.subsystemsDescription();

        else if("Socket Binding".equals(title))
            return Console.CONSTANTS.socketBindingDescription();

        else if("Interfaces".equals(title))
            return Console.CONSTANTS.interfacesDescription();

        else if("System Properties".equals(title))
                   return Console.CONSTANTS.systemPropertiesDescription();

        return "";
    }

    @Override
    public void toggleScrolling(boolean enforceScrolling, int requiredWidth) {

        columnManager.toogleScrolling(enforceScrolling, requiredWidth);
    }
}
