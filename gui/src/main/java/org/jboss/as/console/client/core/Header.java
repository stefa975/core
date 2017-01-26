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

package org.jboss.as.console.client.core;

import com.google.common.base.CharMatcher;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.layout.client.Layout;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

import java.util.Set;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.ProductConfig;
import org.jboss.as.console.client.core.message.MessageCenter;
import org.jboss.as.console.client.core.message.MessageCenterView;
import org.jboss.as.console.client.rbac.RBACContextView;
import org.jboss.as.console.client.search.Harvest;
import org.jboss.as.console.client.search.Index;
import org.jboss.as.console.client.search.SearchTool;
import org.jboss.as.console.client.shared.model.PerspectiveStore;
import org.jboss.as.console.client.widgets.popups.DefaultPopup;
import org.jboss.ballroom.client.widgets.window.Feedback;

import static org.jboss.as.console.client.StringUtils.ELLIPSIS;

/**
 * Top level header, gives access to main applications.
 *
 * @author Heiko Braun
 * @date 1/28/11
 */
public class Header implements ValueChangeHandler<String> {

    private final FeatureSet featureSet;
    private final ToplevelTabs toplevelTabs;
    private final ProductConfig productConfig;
    private final BootstrapContext bootstrap;
    private final MessageCenter messageCenter;
    private final PlaceManager placeManager;
    private final Harvest harvest;
    private final Index index;
    private final PerspectiveStore perspectiveStore;

    private HTMLPanel linksPane;
    private String currentHighlightedSection = null;
    private SearchTool searchTool;

    @Inject
    public Header(final FeatureSet featureSet, final ToplevelTabs toplevelTabs, MessageCenter messageCenter,
            ProductConfig productConfig, BootstrapContext bootstrap, PlaceManager placeManager, Harvest harvest, Index index,
            PerspectiveStore perspectiveStore) {
        this.featureSet = featureSet;
        this.toplevelTabs = toplevelTabs;
        this.messageCenter = messageCenter;
        this.productConfig = productConfig;
        this.bootstrap = bootstrap;
        this.placeManager = placeManager;
        this.harvest = harvest;
        this.index = index;
        this.perspectiveStore = perspectiveStore;
        History.addValueChangeHandler(this);
    }

    public Widget asWidget() {

        LayoutPanel outerLayout = new LayoutPanel();
        outerLayout.addStyleName("page-header");

        Widget logo = getProductSection();
        Widget links = getLinksSection();

        LayoutPanel line = new LayoutPanel();
        line.setStyleName("header-line");
        LayoutPanel top = new LayoutPanel();
        top.setStyleName("header-top");
        LayoutPanel bottom = new LayoutPanel();
        bottom.setStyleName("header-bottom header-bottom-env");

        outerLayout.add(line);
        outerLayout.add(top);
        outerLayout.add(bottom);

        outerLayout.setWidgetTopHeight(line, 0, Style.Unit.PX, 4, Style.Unit.PX);
        outerLayout.setWidgetTopHeight(top, 4, Style.Unit.PX, 32, Style.Unit.PX);
        outerLayout.setWidgetTopHeight(bottom, 36, Style.Unit.PX, 44, Style.Unit.PX);

        top.add(logo);
        top.setWidgetLeftWidth(logo, 15, Style.Unit.PX, 700, Style.Unit.PX);
        top.setWidgetTopHeight(logo, 0, Style.Unit.PX, 32, Style.Unit.PX);

        bottom.add(links);

        // Debug tools
        VerticalPanel debugTools = new VerticalPanel();

        if(!GWT.isScript())
        {
            HTML rbac = new HTML("<i title='RBAC Diagnostics' style='cursor:pointer;color:#cecece;font-size:30px;font-weight:normal!important' class='icon-eye-open'></i>");
            debugTools.add(rbac);

            rbac.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    RBACContextView.launch();
                }
            });
        }

        bottom.add(debugTools);
        bottom.setWidgetLeftWidth(links, 0, Style.Unit.PX, 800, Style.Unit.PX);
        bottom.setWidgetTopHeight(links, 0, Style.Unit.PX, 44, Style.Unit.PX);

        bottom.setWidgetRightWidth(debugTools, 0, Style.Unit.PX, 50, Style.Unit.PX);
        bottom.setWidgetTopHeight(debugTools, 0, Style.Unit.PX, 44, Style.Unit.PX);


        HorizontalPanel tools = new HorizontalPanel();
        tools.setStyleName("top-level-tools");

        // messages
        MessageCenterView messageCenterView = new MessageCenterView(messageCenter);
        Widget messageCenter = messageCenterView.asWidget();
        tools.add(messageCenter);
        messageCenter.getElement().getParentElement().addClassName("first");

        // global search
        if (featureSet.isSearchEnabled()) {
            searchTool = new SearchTool(harvest, index, placeManager);
            tools.add(searchTool);
        }

        // user menu

        // roles
        Set<String> roles = bootstrap.getRoles();
        SafeHtmlBuilder sb = new SafeHtmlBuilder();
        sb.appendHtmlConstant("<div class='roles-menu'>");
        for(String role : roles)
        {
            sb.appendEscaped(role).appendHtmlConstant("<br/>");
        }
        sb.appendHtmlConstant("<div>");

        // current user
        String userHtml = "<i style='color:#cecece' class='icon-user'></i>&nbsp;"+bootstrap.getPrincipal()+"&nbsp;<i style='color:#cecece' class='icon-angle-down'></i>";

        SafeHtml principal = new SafeHtmlBuilder().appendHtmlConstant("<div class='header-textlink'>"+userHtml+"</div>").toSafeHtml();
        final HTML userButton = new HTML(principal);
        userButton.getElement().setAttribute("style", "cursor:pointer");
        tools.add(userButton);

        final DefaultPopup menuPopup = new DefaultPopup(DefaultPopup.Arrow.NONE);
        menuPopup.setAutoHideEnabled(true);
        ClickHandler clickHandler = new ClickHandler() {
            public void onClick(ClickEvent event) {

                int width = 120;
                int height = 50;

                menuPopup.setPopupPosition(
                        userButton.getAbsoluteLeft() ,
                        userButton.getAbsoluteTop() + 25
                );

                menuPopup.show();

                menuPopup.setWidth(width+"px");
                menuPopup.setHeight(height+"px");
            }
        };

        userButton.addClickHandler(clickHandler);
        HTML logoutHtml = new HTML(Console.CONSTANTS.common_label_logout());
        logoutHtml.setStyleName("menu-item");
        logoutHtml.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

                menuPopup.hide();

                Feedback.confirm(
                        Console.CONSTANTS.common_label_logout(),
                        Console.CONSTANTS.logout_confirm(),
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if (isConfirmed) {
                                    new LogoutCmd().execute();
                                }
                            }
                        }
                );
            }
        });


        VerticalPanel usermenu = new VerticalPanel();
        usermenu.setStyleName("fill-layout-width");
        usermenu.addStyleName("top-level-menu");

        usermenu.add(new HTML("Roles:"));
        usermenu.add(new HTML(sb.toSafeHtml()));


        if(bootstrap.isSuperUser())
        {
            usermenu.add(new HTML("<hr/>"));
            HTML runAsBtn = new HTML();
            runAsBtn.addStyleName("menu-item");

            SafeHtmlBuilder runAsRole = new SafeHtmlBuilder();
            runAsRole.appendEscaped("Run as");
            if (bootstrap.getRunAs()!=null) {
                runAsRole.appendHtmlConstant("&nbsp;").appendEscaped(bootstrap.getRunAs());
            } else {
                runAsRole.appendEscaped(ELLIPSIS);
            }

            runAsBtn.setHTML(runAsRole.toSafeHtml());
            runAsBtn.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {

                    menuPopup.hide();

                    placeManager.revealPlace(
                            new PlaceRequest(NameTokens.ToolsPresenter).with("name", "run-as-role"), false
                    );


                }
            });
            usermenu.add(runAsBtn);
        }

        usermenu.add(logoutHtml);
        menuPopup.setWidget(usermenu);

        top.add(tools);
        top.setWidgetRightWidth(tools, 15, Style.Unit.PX, 700, Style.Unit.PX);
        top.setWidgetTopHeight(tools, 2, Style.Unit.PX, 32, Style.Unit.PX);
        top.setWidgetHorizontalPosition(tools, Layout.Alignment.END);

        outerLayout.getElement().setAttribute("role", "navigation");
        outerLayout.getElement().setAttribute("aria-label", "Toplevel Categories");
        return outerLayout;
    }

    private Widget getProductSection() {

        final HorizontalPanel panel = new HorizontalPanel();
        panel.getElement().setAttribute("role", "presentation");
        panel.getElement().setAttribute("aria-hidden", "true");

        final Image logo = new Image();
        logo.setStyleName("logo");
        panel.add(logo);

        HTML productVersion = new HTML(productConfig.getProductVersion());
        productVersion.setStyleName("header-product-version");
        panel.add(productVersion);

        if (ProductConfig.Profile.PRODUCT.equals(productConfig.getProfile())) {
            logo.addErrorHandler(new ErrorHandler() {
                @Override
                public void onError(ErrorEvent event) {
                    panel.remove(logo);
                    Label productName = new Label(productConfig.getProductName());
                    productName.setStyleName("header-product-name");
                    panel.insert(productName, 0);
                }
            });
            logo.setUrl("images/logo/" + logoName(productConfig.getProductName()) + ".png");
            logo.setAltText(productConfig.getProductName());
        } else {
            logo.setUrl("images/logo/community_title.png");
            logo.setAltText("Wildlfy Application Server");
        }

        return panel;
    }

    private String logoName(String productName) {
        CharMatcher digits = CharMatcher.inRange('0', '9');
        CharMatcher alpha = CharMatcher.inRange('a', 'z');
        return digits.or(alpha).retainFrom(productName.toLowerCase());
    }

    private Widget getLinksSection() {
        linksPane = new HTMLPanel(createLinks());
        linksPane.getElement().setId("header-links-section");
        linksPane.getElement().setAttribute("role", "menubar");
        linksPane.getElement().setAttribute("aria-controls", "main-content-area");

        for (final ToplevelTabs.Config tlt : toplevelTabs) {
            final String id = "header-" + tlt.getToken();

            SafeHtmlBuilder html = new SafeHtmlBuilder();
            html.appendHtmlConstant("<div class='header-link-label'>");
            html.appendHtmlConstant("<span role='menuitem'>");
            html.appendHtmlConstant(tlt.getTitle());
            html.appendHtmlConstant("</span>");
            html.appendHtmlConstant("</div>");
            HTML widget = new HTML(html.toSafeHtml());
            widget.setStyleName("fill-layout");

            widget.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    // navigate either child directly or parent if revealed the first time
                    boolean hasChild = perspectiveStore.hasChild(tlt.getToken());
                    String token = hasChild ?
                            perspectiveStore.getChild(tlt.getToken()) : tlt.getToken();


                    boolean updateToken = hasChild ? true : tlt.isUpdateToken();
                    placeManager.revealPlace(
                            new PlaceRequest.Builder().nameToken(token).build(), updateToken);
                }
            });
            linksPane.add(widget, id);

        }

        //subnavigation = createSubnavigation();
        //linksPane.add(subnavigation, "subnavigation");

        return linksPane;
    }

    private String createLinks() {

        SafeHtmlBuilder headerString = new SafeHtmlBuilder();

        if (!toplevelTabs.isEmpty()) {
            headerString
                    .appendHtmlConstant("<table border=0 class='header-links' cellpadding=0 cellspacing=0 border=0>");
            headerString.appendHtmlConstant("<tr id='header-links-ref'>");

            headerString.appendHtmlConstant("<td><img src=\"images/blank.png\" width=1/></td>");
            for (ToplevelTabs.Config tlt : toplevelTabs) {
                final String id = "header-" + tlt.getToken();
                String styleClass = "header-link";
                String styleAtt = "vertical-align:middle; text-align:center";

                String td = "<td style='" + styleAtt + "' id='" + id + "' class='" + styleClass + "'></td>";

                headerString.appendHtmlConstant(td);
                //headerString.append(title);

                //headerString.appendHtmlConstant("<td ><img src=\"images/blank.png\" width=1 height=32/></td>");

            }

            headerString.appendHtmlConstant("</tr>");
            headerString.appendHtmlConstant("</table>");
            headerString.appendHtmlConstant("<div id='subnavigation' style='float:right;clear:right;'/>");
        }

        return headerString.toSafeHtml().asString();
    }

    @Override
    public void onValueChange(ValueChangeEvent<String> event) {
        String historyToken = event.getValue();
        if(historyToken.equals(currentHighlightedSection))
            return;
        else
            currentHighlightedSection = historyToken;

        if(historyToken.indexOf("/")!=-1)
        {
            highlight(historyToken.substring(0, historyToken.indexOf("/")));
        }
        else
        {
            highlight(historyToken);
        }
    }

    public void highlight(String name)
    {
        toggleSubnavigation(name);

        com.google.gwt.user.client.Element target = linksPane.getElementById("header-links-ref");
        if(target!=null) // TODO: i think this cannot happen, does it?
        {
            NodeList<Node> childNodes = target.getChildNodes();
            for(int i=0; i<childNodes.getLength(); i++)
            {
                Node n = childNodes.getItem(i);
                if(Node.ELEMENT_NODE == n.getNodeType())
                {
                    Element element = (Element) n;
                    if(element.getId().equals("header-"+name))
                    {
                        element.addClassName("header-link-selected");
                        element.setAttribute("aria-selected", "true");
                    }
                    else {
                        element.removeClassName("header-link-selected");
                        element.setAttribute("aria-selected", "false");
                    }
                }
            }
        }

    }

    private void toggleSubnavigation(String name) {

    }

    public DeckPanel createSubnavigation() {

        DeckPanel subnavigation = new DeckPanel();

        // TODO: fill in contents

        return subnavigation;
    }

    public SearchTool getSearchTool() {
        return searchTool;
    }
}
