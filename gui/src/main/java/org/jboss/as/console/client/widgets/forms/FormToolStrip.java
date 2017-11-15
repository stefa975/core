package org.jboss.as.console.client.widgets.forms;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Heiko Braun
 * @date 9/19/11
 */
public class FormToolStrip<T> {


    private FormAdapter<T> form = null;

    private List<Widget> additionalButtons = new LinkedList<Widget>();

    private Set<Command> cancelDelegates = new HashSet<Command>();
    private ToolStrip toolStrip;

    public void addCancelHandler(Command command) {
        cancelDelegates.add(command);
    }

    public interface PreValidation {
        boolean isValid();
    }

    public FormToolStrip(FormAdapter<T> form, final FormCallback<T> callback) {
        this.form = form;

        // API Changes (FormCallback has been moved and deprecated)
        form.setToolsCallback(new org.jboss.ballroom.client.widgets.forms.FormCallback<T>()
        {
            @Override
            public void onSave(Map<String, Object> changeset) {
                callback.onSave(changeset);
            }

            @Override
            public void onCancel(T entity) {
                for(Command cmd : cancelDelegates)
                    cmd.execute();

                callback.onDelete(entity);     // TODO: cleanup
            }
        });
    }


    /**
     * NOOP
     * @param b
     */
    @Deprecated
    public void providesDeleteOp(boolean b) {

    }

    public void setVisible(boolean visible) {
        toolStrip.setVisible(visible);
    }

    public Widget asWidget() {

        toolStrip = new ToolStrip();
        toolStrip.setStyleName("default-toolstrip-secondary");

        for(Widget btn : additionalButtons)
            if (btn instanceof ToolButton) {
                toolStrip.addToolButtonRight((ToolButton) btn);
            } else {
                toolStrip.addToolWidgetRight(btn);
            }


        // with the form API changes (self containment), it happens that we have many empty toolstrips
        // these will be suppressed
        if(additionalButtons.isEmpty())
           toolStrip.addStyleName("suppressed");

        return toolStrip;
    }

    public void addToolButtonRight(Widget btn) {
        additionalButtons.add(btn);
    }

    @Deprecated
    public interface FormCallback<T> {
        void onSave(Map<String, Object> changeset);

        /**
         * NOOP
         * @param entity
         */
        @Deprecated
        void onDelete(T entity);
    }

    public void doCancel() {
        form.setEnabled(false);
        form.cancel();
    }
}
