package org.jboss.as.console.client.widgets.pages;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.ballroom.client.widgets.InlineLink;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 12/6/11
 */
public class LinkBar {

    private VerticalPanel bar;
    private int numLinks = 0;
    private List<HTML> links = new LinkedList<HTML>();
    private final boolean navOnFirstPage;

    public LinkBar() {
        this(false);
    }

    public LinkBar(boolean navOnFirstPage) {
        this.bar = new VerticalPanel();
        this.navOnFirstPage = navOnFirstPage;
    }

    public Widget asWidget() {

        // last element to the right
        HTML placeHolder = new HTML();
        this.bar.add(placeHolder);
        placeHolder.getElement().getParentElement().setAttribute("width", "100%");

        return bar;
    }

    public void addLink(String text, ClickHandler handler) {

        InlineLink html = new InlineLink(text);
        //html.setHTML("<a href='javascript:void(0)' tabindex='0'>"+text+"</a>");

        html.addClickHandler(handler);
        html.addStyleName("link-bar");

        SafeHtmlBuilder builder = new SafeHtmlBuilder();

        if(numLinks==0)
        {
            if(!navOnFirstPage)
            {
                html.addStyleName("link-bar-first");
                builder.appendHtmlConstant("<i class='icon-chevron-left'></i>");
                builder.appendHtmlConstant("&nbsp;");
            }
        }

        builder.appendHtmlConstant("<a href='javascript:void(0)' tabindex='0'>"+text+"</a>");
        html.setHTML(builder.toSafeHtml());
        links.add(html);

        bar.add(html);

        numLinks++;

    }

    public void setActive(int index) {
        for(int i=0; i<links.size(); i++)
        {
            if(i==index)
                links.get(i).getElement().addClassName("link-bar-active");
            else
                links.get(i).getElement().removeClassName("link-bar-active");
        }
    }
}
