package org.jboss.as.console.client.shared.general.model;

import org.jboss.as.console.client.widgets.forms.Binding;

/**
 * @author Heiko Braun
 * @date 10/15/12
 */
public interface Path {

    @Binding(key = true)
    String getName();
    void setName(String name);

    String getPath();
    void setPath(String path);

    @Binding(detypedName = "relative-to")
    String getRelativeTo();
    void setRelativeTo(String Rel);

    @Binding(detypedName = "read-only")
    boolean isReadOnly();
    void setReadOnly(boolean b);

}
