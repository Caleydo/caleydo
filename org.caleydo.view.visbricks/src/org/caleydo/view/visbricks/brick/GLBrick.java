package org.caleydo.view.visbricks.brick;

import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.EVAOperation;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.event.data.StartClusteringEvent;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.clusterer.ClusterState;
import org.caleydo.core.view.IDataDomainSetBasedView;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.TemplateRenderer;
import org.caleydo.core.view.opengl.layout.ViewRenderer;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.rcp.dialog.cluster.StartClusteringDialog;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.heatmap.template.BrickHeatMapTemplate;
import org.eclipse.swt.widgets.Shell;

/**
 * Individual Brick for VisBricks
 * 
 * @author Alexander Lex
 * 
 */
public class GLBrick extends AGLView implements IDataDomainSetBasedView,
		IGLRemoteRenderingView {

	public final static String VIEW_ID = "org.caleydo.view.brick";

	private TemplateRenderer templateRenderer;
	private BrickLayoutTemplate brickLayout;

	private ElementLayout wrappingLayout;

	private int baseDisplayListIndex;
	private boolean isBaseDisplayListDirty = true;
	private ISet set;
	private GLHeatMap heatMap;
	private ASetBasedDataDomain dataDomain;

	public GLBrick(GLCaleydoCanvas glCanvas, ViewFrustum viewFrustum) {
		super(glCanvas, viewFrustum, true);
		viewType = GLBrick.VIEW_ID;

	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearAllSelections() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(GL2 gl) {
		baseDisplayListIndex = gl.glGenLists(1);

		if (heatMap == null) {
			templateRenderer = new TemplateRenderer(viewFrustum);
			brickLayout = new BrickLayoutTemplate(this);

			brickLayout.setPixelGLConverter(parentGLCanvas.getPixelGLConverter());

			heatMap = (GLHeatMap) GeneralManager
					.get()
					.getViewGLCanvasManager()
					.createGLView(
							GLHeatMap.class,
							getParentGLCanvas(),
							new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, 1, 0,
									1, -1, 1));
			heatMap.setRemoteRenderingGLView(this);
			heatMap.setSet(set);
			heatMap.setDataDomain(dataDomain);
			heatMap.setRenderTemplate(new BrickHeatMapTemplate(heatMap));
			heatMap.initialize();
			heatMap.initRemote(gl, this, glMouseListener);
			if (this.contentVA != null)
				heatMap.setContentVA(contentVA);
			brickLayout.setViewRenderer(new ViewRenderer(heatMap));
			templateRenderer.setTemplate(brickLayout);
			templateRenderer.updateLayout();
		}

	}

	@Override
	protected void initLocal(GL2 gl) {
		init(gl);

	}

	@Override
	public void initRemote(GL2 gl, AGLView glParentView, GLMouseListener glMouseListener) {
		init(gl);

	}

	@Override
	public void display(GL2 gl) {
		heatMap.processEvents();
		// GLHelperFunctions.drawViewFrustum(gl, viewFrustum);

		if (isBaseDisplayListDirty)
			buildBaseDisplayList(gl);

		templateRenderer.render(gl);

		gl.glCallList(baseDisplayListIndex);
	}

	@Override
	protected void displayLocal(GL2 gl) {
		pickingManager.handlePicking(this, gl);
		display(gl);
		checkForHits(gl);
	}

	@Override
	public void displayRemote(GL2 gl) {
		display(gl);
		checkForHits(gl);
	}

	private void buildBaseDisplayList(GL2 gl) {
		gl.glNewList(baseDisplayListIndex, GL2.GL_COMPILE);
		// templateRenderer.updateLayout();

		gl.glEndList();
		isBaseDisplayListDirty = false;
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

		super.reshape(drawable, x, y, width, height);
		if (templateRenderer != null)
			templateRenderer.updateLayout();
	}

	@Override
	protected void handlePickingEvents(EPickingType pickingType,
			EPickingMode pickingMode, int pickingID, Pick pick) {

		switch (pickingType) {
		case BRICK_CLUSTER:
			switch (pickingMode) {
			case CLICKED:
				// set.cluster(clusterState);
				System.out.println("cluster");

				getParentGLCanvas().getParentComposite().getDisplay()
						.asyncExec(new Runnable() {
							@Override
							public void run() {
								StartClusteringDialog dialog = new StartClusteringDialog(
										new Shell(), dataDomain);
								dialog.open();
								ClusterState clusterState = dialog.getClusterState();

								StartClusteringEvent event = null;
								// if (clusterState != null && set != null)

								event = new StartClusteringEvent(clusterState, set
										.getID());
								event.setDataDomainType(dataDomain.getDataDomainType());
								GeneralManager.get().getEventPublisher()
										.triggerEvent(event);
							}
						});

			}
		}

	}

	@Override
	public String getShortInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDetailedInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void broadcastElements(EVAOperation type) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getNumberOfSelections(SelectionType SelectionType) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setDataDomain(ASetBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	@Override
	public ASetBasedDataDomain getDataDomain() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setSet(ISet set) {
		this.set = set;
	}

	public void setContentVA(ContentVirtualArray contentVA) {
		this.contentVA = contentVA;
	}

	@Override
	public List<AGLView> getRemoteRenderedViews() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFrustum(ViewFrustum viewFrustum) {
		super.setFrustum(viewFrustum);
		if (templateRenderer != null)
			templateRenderer.updateLayout();
	}

	PickingManager getPickingManager() {
		return pickingManager;
	}
}
