package org.caleydo.view.subgraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.media.opengl.GL2;

import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.datadomain.IDataSupportDefinition;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.IMultiTablePerspectiveBasedView;
import org.caleydo.core.view.ViewManager;
import org.caleydo.core.view.listener.AddTablePerspectivesEvent;
import org.caleydo.core.view.listener.AddTablePerspectivesListener;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.layout.MultiFormRenderer;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.eclipse.swt.widgets.Composite;

public class GLSubGraph extends AGLView implements IMultiTablePerspectiveBasedView, IGLRemoteRenderingView {

	public static String VIEW_TYPE = "org.caleydo.view.subgraph";

	public static String VIEW_NAME = "SubGraph";

	private LayoutManager layoutManager;

	private MultiFormRenderer multiFormRenderer;

	private List<TablePerspective> tablePerspectives = new ArrayList<>();

	private Set<String> remoteRenderedViewIDs;

	private ElementLayout baseElementLayout;

	private AddTablePerspectivesListener addTablePerspectivesListener;

	/**
	 * Constructor.
	 *
	 * @param glCanvas
	 * @param viewLabel
	 * @param viewFrustum
	 */
	public GLSubGraph(IGLCanvas glCanvas, Composite parentComposite, ViewFrustum viewFrustum) {

		super(glCanvas, parentComposite, viewFrustum, VIEW_TYPE, VIEW_NAME);
	}

	@Override
	public void init(GL2 gl) {
		displayListIndex = gl.glGenLists(1);
		detailLevel = EDetailLevel.HIGH;
		multiFormRenderer = new MultiFormRenderer(this, true);

		layoutManager = new LayoutManager(viewFrustum, pixelGLConverter);

		baseElementLayout = new ElementLayout();
		baseElementLayout.setRatioSizeX(1);
		baseElementLayout.setRatioSizeY(1);
		layoutManager.setBaseElementLayout(baseElementLayout);

	}

	@Override
	public void initLocal(GL2 gl) {
		init(gl);
	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView, final GLMouseListener glMouseListener) {

		// Register keyboard listener to GL2 canvas
		glParentView.getParentComposite().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				glParentView.getParentComposite().addKeyListener(glKeyListener);
			}
		});

		this.glMouseListener = glMouseListener;

		init(gl);
	}

	@Override
	public void displayLocal(GL2 gl) {
		pickingManager.handlePicking(this, gl);
		display(gl);
		if (busyState != EBusyState.OFF) {
			renderBusyMode(gl);
		}

	}

	@Override
	public void displayRemote(GL2 gl) {
		display(gl);
	}

	@Override
	public void display(GL2 gl) {

		if (isDisplayListDirty) {
			layoutManager.updateLayout();
			isDisplayListDirty = false;
		}
		layoutManager.render(gl);
		checkForHits(gl);
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedSubGraphView serializedForm = new SerializedSubGraphView();
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public String toString() {
		return "Subgraph view";
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();

		addTablePerspectivesListener = new AddTablePerspectivesListener();
		addTablePerspectivesListener.setHandler(this);
		eventPublisher.addListener(AddTablePerspectivesEvent.class, addTablePerspectivesListener);

	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

		if (addTablePerspectivesListener != null) {
			eventPublisher.removeListener(addTablePerspectivesListener);
			addTablePerspectivesListener = null;
		}
	}

	@Override
	protected void destroyViewSpecificContent(GL2 gl) {
		gl.glDeleteLists(displayListIndex, 1);
		layoutManager.destroy(gl);
	}

	@Override
	public IDataSupportDefinition getDataSupportDefinition() {
		return DataSupportDefinitions.tableBased;
	}

	@Override
	public void addTablePerspective(TablePerspective newTablePerspective) {
		if (newTablePerspective != null) {
			tablePerspectives.add(newTablePerspective);
		}
		createRemoteRenderedViews();
	}

	@Override
	public void addTablePerspectives(List<TablePerspective> newTablePerspectives) {
		if (newTablePerspectives != null) {
			for (TablePerspective tablePerspective : newTablePerspectives) {
				if (tablePerspective != null) {
					tablePerspectives.add(tablePerspective);
				}
			}
		}
		createRemoteRenderedViews();
	}

	private void createRemoteRenderedViews() {
		if (remoteRenderedViewIDs == null) {
			remoteRenderedViewIDs = ViewManager.get().getRemotePlugInViewIDs(VIEW_TYPE, "test");
			for (String viewID : remoteRenderedViewIDs) {
				multiFormRenderer.addView(viewID, "test", tablePerspectives);
			}
			baseElementLayout.setRenderer(multiFormRenderer);
			setDisplayListDirty();
		}
	}

	@Override
	public List<TablePerspective> getTablePerspectives() {
		return new ArrayList<>(tablePerspectives);
	}

	@Override
	public void removeTablePerspective(int tablePerspectiveID) {
		// TODO: implement
	}

	@Override
	public List<AGLView> getRemoteRenderedViews() {
		// TODO: implement
		return null;
	}

	@Override
	public boolean isDataView() {
		return true;
	}

}
