package org.caleydo.view.visbricks20;

import java.util.List;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.DetailLevel;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.ViewLayoutRenderer;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.view.datagraph.GLDataGraph;
import org.caleydo.view.visbricks.GLVisBricks;
import org.caleydo.view.visbricks.dimensiongroup.DimensionGroup;
import org.caleydo.view.visbricks20.renderstyle.VisBricks20RenderStyle;
import org.eclipse.swt.widgets.Composite;

/**
 * <p>
 * VisBricks 2.0 view.
 * </p>
 * TODO </p>
 * 
 * @author Marc Streit
 */

public class GLVisBricks20
	extends AGLView
	implements IGLRemoteRenderingView {

	public final static String VIEW_TYPE = "org.caleydo.view.visbricks20";

	private VisBricks20RenderStyle renderStyle;

	private LayoutManager layoutManager;

	private GLDataGraph dvi;

	private GLVisBricks visBricks;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param viewLabel
	 * @param viewFrustum
	 */
	public GLVisBricks20(GLCanvas glCanvas, Composite parentComposite, ViewFrustum viewFrustum) {

		super(glCanvas, parentComposite, viewFrustum);

		viewType = GLVisBricks20.VIEW_TYPE;
		viewLabel = "VisBricks 2.0";
	}

	@Override
	public void init(GL2 gl) {
		displayListIndex = gl.glGenLists(1);
		renderStyle = new VisBricks20RenderStyle(viewFrustum);

		super.renderStyle = renderStyle;
		detailLevel = DetailLevel.HIGH;

		initLayouts();

		dvi.initRemote(gl, this, glMouseListener);
		visBricks.initRemote(gl, this, glMouseListener);
	}

	@Override
	public void initLocal(GL2 gl) {
		init(gl);
	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView,
			final GLMouseListener glMouseListener) {

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

		dvi.processEvents();
		visBricks.processEvents();
		
		pickingManager.handlePicking(this, gl);

		display(gl);
		if (busyState != EBusyState.OFF) {
			renderBusyMode(gl);
		}

		checkForHits(gl);
	}

	private void initLayouts() {

		layoutManager = new LayoutManager(viewFrustum, pixelGLConverter);

		Column mainColumn = new Column("baseElementLayout");
		mainColumn.setDebug(true);
		// mainColumn.setBottomUp(false);
		layoutManager.setBaseElementLayout(mainColumn);

		Row dviElementLayout = new Row("dviElementLayoutRow");
		dviElementLayout.setDebug(true);
		createDVI(dviElementLayout);

		Row visBricksElementLayout = new Row("visBricksElementLayoutRow");
		visBricksElementLayout.setDebug(true);
		createVisBricks(visBricksElementLayout);

		mainColumn.append(dviElementLayout);
		mainColumn.append(visBricksElementLayout);

		layoutManager.updateLayout();
	}

	/**
	 * Creates DVI view
	 * 
	 * @param wrappingLayout
	 * @return
	 */
	private GLDataGraph createDVI(ElementLayout wrappingLayout) {
		ViewFrustum frustum = new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, 1, 0, 1,
				-4, 4);
		dvi = (GLDataGraph) GeneralManager.get().getViewManager()
				.createGLView(GLDataGraph.class, parentGLCanvas, parentComposite, frustum);

		dvi.setRemoteRenderingGLView(this);
		dvi.initialize();

		ViewLayoutRenderer dviRenderer = new ViewLayoutRenderer(dvi);
		wrappingLayout.setRenderer(dviRenderer);
		wrappingLayout.setPixelSizeY(300);

		return dvi;
	}

	/**
	 * Creates VisBricks view
	 * 
	 * @param wrappingLayout
	 * @return
	 */
	private GLVisBricks createVisBricks(ElementLayout wrappingLayout) {
		ViewFrustum frustum = new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, 1, 0, 1,
				-4, 4);
		visBricks = (GLVisBricks) GeneralManager.get().getViewManager()
				.createGLView(GLVisBricks.class, parentGLCanvas, parentComposite, frustum);

		visBricks.setRemoteRenderingGLView(this);
		visBricks.initialize();

		ViewLayoutRenderer visBricksRenderer = new ViewLayoutRenderer(visBricks);
		wrappingLayout.setRenderer(visBricksRenderer);

		return visBricks;
	}

	@Override
	public void displayRemote(GL2 gl) {
		display(gl);
	}

	@Override
	public void display(GL2 gl) {

		layoutManager.render(gl);
	}

	@Override
	protected void handlePickingEvents(PickingType pickingType, PickingMode pickingMode,
			int externalID, Pick pick) {

		// TODO: Implement picking processing here!
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedVisBricks20View serializedForm = new SerializedVisBricks20View();
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public String toString() {
		return "TODO: ADD INFO THAT APPEARS IN THE LOG";
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();

	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.view.opengl.canvas.AGLView#reshape(javax.media.opengl
	 * .GLAutoDrawable, int, int, int, int)
	 */
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		super.reshape(drawable, x, y, width, height);
		// initLayouts();

		layoutManager.updateLayout();
		visBricks.updateLayout();
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView#
	 * getRemoteRenderedViews()
	 */
	@Override
	public List<AGLView> getRemoteRenderedViews() {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.view.opengl.canvas.AGLView#getNumberOfSelections(org
	 * .caleydo.core.data.selection.SelectionType)
	 */
	@Override
	public int getNumberOfSelections(SelectionType SelectionType) {
		// TODO Auto-generated method stub
		return 0;
	}
}
