package org.jboss.as.console.client.shared.general;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.Proxy;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.general.model.Path;
import org.jboss.as.console.client.shared.general.wizard.NewPathWizard;
import org.jboss.as.console.client.shared.model.ModelAdapter;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.as.console.spi.RequiredResources;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.dispatch.DispatchAsync;
import org.jboss.dmr.client.dispatch.impl.DMRAction;
import org.jboss.dmr.client.dispatch.impl.DMRResponse;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 10/15/12
 */
public class PathManagementPresenter
        extends Presenter<PathManagementPresenter.MyView, PathManagementPresenter.MyProxy> {

    @ProxyCodeSplit
    @NameToken(NameTokens.PathManagementPresenter)
    @RequiredResources(resources = {"path=*"})
    public interface MyProxy extends Proxy<PathManagementPresenter>, Place {
    }


    public interface MyView extends View {
        void setPresenter(PathManagementPresenter presenter);
        void setPaths(List<Path> paths);
    }

    private DispatchAsync dispatcher;
    private RevealStrategy revealStrategy;
    private DefaultWindow window;
    private EntityAdapter<Path> entityAdapter;

    @Inject
    public PathManagementPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            DispatchAsync dispatcher, RevealStrategy revealStrategy,
            ApplicationMetaData propertyMetaData) {
        super(eventBus, view, proxy);

        this.dispatcher = dispatcher;
        this.revealStrategy = revealStrategy;
        this.entityAdapter = new EntityAdapter<Path>(Path.class, propertyMetaData);
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }


    @Override
    protected void onReset() {
        super.onReset();

        loadPathInformation();
    }

    private void loadPathInformation() {
        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).setEmptyList();
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(CHILD_TYPE).set("path");

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();

                if (response.isFailure()) {
                    Console.error(Console.MESSAGES.failed("Paths"), response.getFailureDescription());
                } else {
                    List<ModelNode> payload = response.get(RESULT).asList();

                    List<Path> paths = new ArrayList<Path>();
                    for (ModelNode item : payload) {
                        paths.add(entityAdapter.fromDMR(item));
                    }

                    getView().setPaths(paths);
                }
            }
        });
    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInParent(this);
    }

    public void launchNewPathDialogue() {
        window = new DefaultWindow(Console.MESSAGES.createTitle("Path"));
        window.setWidth(480);
        window.setHeight(360);

        window.trapWidget(
                new NewPathWizard(this).asWidget()
        );

        window.setGlassEnabled(true);
        window.center();
    }

    public void onDeletePath(String pathName) {
        ModelNode operation = new ModelNode();
        operation.get(OP).set(REMOVE);
        operation.get(ADDRESS).add("path", pathName);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();
                if (ModelAdapter.wasSuccess(response)) {
                    Console.info(Console.MESSAGES.deleted("Path " + pathName));
                } else {
                    Console.error(Console.MESSAGES.deletionFailed("Path " + pathName),
                            response.getFailureDescription());
                }

                loadPathInformation();
            }
        });
    }

    public void onSavePath(final String name, Map<String, Object> changedValues) {
        ModelNode address = new ModelNode();
        address.add("path", name);

        ModelNode addressNode = new ModelNode();
        addressNode.get(ADDRESS).set(address);

        ModelNode operation = entityAdapter.fromChangeset(changedValues, addressNode);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();

                if (response.isFailure()) {
                    Console.error(Console.MESSAGES.modificationFailed("Path " + name),
                            response.getFailureDescription());
                } else { Console.info(Console.MESSAGES.modified("Path " + name)); }

                loadPathInformation();
            }
        });
    }

    public void onCloseDialoge() {
        window.hide();
    }

    public void onCreatePath(final Path path) {
        onCloseDialoge();

        ModelNode operation = entityAdapter.fromEntity(path);
        operation.get(OP).set(ADD);
        operation.get(ADDRESS).add("path", path.getName());

        // TODO: workaround ....
        if (null == path.getRelativeTo() || path.getRelativeTo().equals("")) { operation.remove("relative-to"); }

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();
                if (!response.isFailure()) {
                    Console.info(Console.MESSAGES.added("Path " + path.getName()));
                } else {
                    Console.error(Console.MESSAGES.addingFailed("Path " + path.getName()),
                            response.getFailureDescription());
                }

                loadPathInformation();
            }

            @Override
            public void onFailure(Throwable caught) {
                Console.error(Console.MESSAGES.addingFailed("Path " + path.getName()), caught.getMessage());
            }
        });
    }

    public EntityAdapter<Path> getEntityAdapter() {
        return entityAdapter;
    }
}
