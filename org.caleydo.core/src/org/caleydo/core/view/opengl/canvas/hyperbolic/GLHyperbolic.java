package org.caleydo.core.view.opengl.canvas.hyperbolic;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.manager.event.view.storagebased.RedrawViewEvent;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.ADrawAbleNode;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.TestNode;
import org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters.ITreeLayouter;
import org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters.LinearTreeLayouter;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.RedrawViewListener;
import org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;

/**
 * Rendering the hyperbolic view.
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class GLHyperbolic
	extends AGLEventListener
	implements IViewCommandHandler {

	// private Tree<DefaultNode> tree;
	public Vec3f[] vec;

	boolean bIsInListMode = false;

	boolean bUseDetailLevel = true;
	ISet set;

	Tree<ADrawAbleNode> tree = null;

	ITreeLayouter layouter = null;

	HyperbolicRenderStyle renderStyle = null;

	private ColorMappingManager colorMappingManager;

	private RedrawViewListener redrawViewListener = null;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLHyperbolic(GLCaleydoCanvas glCanvas, final String sLabel, final IViewFrustum viewFrustum) {
		super(glCanvas, sLabel, viewFrustum, true);

		viewType = EManagedObjectType.GL_HYPERBOLIC;
		colorMappingManager = ColorMappingManager.get();
		renderStyle = new HyperbolicRenderStyle(viewFrustum);

		ArrayList<ESelectionType> alSelectionTypes = new ArrayList<ESelectionType>();
		alSelectionTypes.add(ESelectionType.NORMAL);
		alSelectionTypes.add(ESelectionType.MOUSE_OVER);
		alSelectionTypes.add(ESelectionType.SELECTION);

		// Build the Test Tree in Constructor
		// TreeTester tester = new TreeTester();
		// tree = tester;
		// tree.runTest();

		tree = new Tree<ADrawAbleNode>();
		ADrawAbleNode test = new TestNode("first Test", 0);
		tree.setRootNode(test);
		for (int i = 1; i <= 6; ++i) {
			ADrawAbleNode test2 = new TestNode("childs", i);
			tree.addChild(test, test2);
			test = test2;
		}
		
		test = tree.getRoot();
		ADrawAbleNode test2 = new TestNode("childs", 500);
		tree.addChild(test, test2);
//		blabla(test);
//		int lala = tree.getDepth();
//		lala = tree.getDepth(test);
//		for(ADrawAbleNode node : tree.getChildren(test))
//			lala = tree.getDepth(node);
//		ADrawAbleNode test2 = new TestNode("childs", 500);
//		tree.addChild(test, test2);
//		lala = tree.getDepth(test2);
//		lala = tree.getDepth(test);
//		lala = tree.getDepth();
		
		// ADrawAbleObject obj = DrawAbleObjectsFactory.getDrawAbleObject("Polygon");
		// obj.setAlpha(0.8f);
		// obj.setBgColor3f(0.4f, 0.3f, 0.5f);
		// test.setDetailLevel(EDrawAbleNodeDetailLevel.VeryHigh, obj);
		// obj = DrawAbleObjectsFactory.getDrawAbleObject("Polygon");
		// obj.setAlpha(0.2f);
		// obj.setBgColor3f(0.2f, 0.7f, 0.3f);
		// test.setDetailLevel(EDrawAbleNodeDetailLevel.High, obj);

		// tree.addChild(test, test2);
		layouter = new LinearTreeLayouter(viewFrustum);
	}

	@Override
	public void init(GL gl) {

		if (set == null)
			return;
	}

	@Override
	public void initLocal(GL gl) {
		iGLDisplayListIndexLocal = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexLocal;
		init(gl);
	}

	@Override
	public void initRemote(final GL gl, final AGLEventListener glParentView,
		final GLMouseListener glMouseListener, final IGLCanvasRemoteRendering remoteRenderingGLCanvas,
		GLInfoAreaManager infoAreaManager) {

		this.remoteRenderingGLView = remoteRenderingGLCanvas;

		this.glMouseListener = glMouseListener;

		iGLDisplayListIndexRemote = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexRemote;
		init(gl);

	}

	public void setToListMode(boolean bSetToListMode) {
		this.bIsInListMode = bSetToListMode;
		super.setDetailLevel(EDetailLevel.HIGH);
		bUseDetailLevel = false;
		setDisplayListDirty();
	}

	@Override
	public void setDetailLevel(EDetailLevel detailLevel) {
		if (bUseDetailLevel) {
			super.setDetailLevel(detailLevel);
			// renderStyle.setDetailLevel(detailLevel);
		}

	}

	@Override
	public void displayLocal(GL gl) {
		pickingManager.handlePicking(this, gl);

		if (bIsDisplayListDirtyLocal) {
			buildDisplayList(gl, iGLDisplayListIndexLocal);
			bIsDisplayListDirtyLocal = false;
		}
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		display(gl);
		checkForHits(gl);

		if (eBusyModeState != EBusyModeState.OFF) {
			renderBusyMode(gl);
		}
	}

	@Override
	public void displayRemote(GL gl) {
		if (bIsDisplayListDirtyRemote) {
			buildDisplayList(gl, iGLDisplayListIndexRemote);
			bIsDisplayListDirtyRemote = false;
		}
		iGLDisplayListToCall = iGLDisplayListIndexRemote;

		display(gl);
		checkForHits(gl);

		// glMouseListener.resetEvents();
	}

	@Override
	public void display(GL gl) {
		processEvents();
		gl.glCallList(iGLDisplayListToCall);
		// GLHelperFunctions.drawAxis(gl);
		// render(gl);
		// clipToFrustum(gl);
		//
		// gl.glCallList(iGLDisplayListToCall);

		// buildDisplayList(gl, iGLDisplayListIndexRemote);

		// if (iPathwayID != -1)
		// {
		// PathwayGraph pathwayGraph = generalManager.getPathwayManager().getItem(iPathwayID);
		// for (IGraphItem node : pathwayGraph.getAllItemsByKind(EGraphItemKind.NODE))
		// {
		// System.out.println("Node:" + node);
		// }
		// }

	}

	private void buildDisplayList(final GL gl, int iGLDisplayListIndex) {
		gl.glNewList(iGLDisplayListIndex, GL.GL_COMPILE);
		layouter.renderTreeLayout(gl, tree);
		// renderHistogram(gl);
		// renderColorBars(gl);
		gl.glEndList();
	}

	private void render(GL gl) {
		// layouter.renderTreeLayout(gl);

		// TestLayout layout = new TestLayout(gl, viewFrustum, tree.getTree());
		// layout.drawGraph(gl);
		// gl.glFlush();

		// gl.glColor4f(1, 0, 0, 1);
		// gl.glBegin(GL.GL_POLYGON);
		// gl.glVertex3f(0, 0, 0);
		// gl.glVertex3f(0, 1, 0);
		// gl.glVertex3f(1, 1, 0);
		// gl.glVertex3f(1, 0, 0);
		// gl.glEnd();

		// gl.glColor4f(1, 1, 0, 1);

		// gl.glBegin(GL.GL_POLYGON);
		// gl.glVertex2f(0.0f, 0.0f);
		// gl.glVertex2f(0.0f, 3.0f);
		// gl.glVertex2f(1.0f, 0.0f);
		// gl.glVertex2f(1.0f, 3.0f);
		// gl.glVertex2f(0.0f, 1.5f);
		// gl.glVertex2f(1.0f, 1.5f);
		// gl.glVertex2f(2.0f, 0.0f);
		// gl.glVertex2f(2.0f, 3.0f);
		// gl.glVertex2f(2.0f, 1.5f);
		// gl.glVertex2f(3.0f, 1.5f);
		// gl.glVertex2f(2.0f, 3.0f);
		// gl.glVertex2f(0.0f, 5.0f);
		// // glVertex2f(4.0, 3.0);
		// // glVertex2f(6.0, 1.5);
		// // glVertex2f(4.0, 0.0);
		// gl.glEnd();

		// gl.glBegin(GL.GL_LINES);
		// gl.glVertex3f(0.0f, 10.0f, 0.0f); // origin of the line
		// gl.glVertex3f(200.0f, 14.0f, 0.0f); // ending point of the line
		// gl.glEnd( );

		// gl.glColor4f(1, 1, 0, 1);
		//		
		// gl.glBegin(GL.GL_LINE);
		//
		// gl.glVertex2f(0.0f, 0.0f);
		// gl.glVertex2f(5.0f, 3.0f);
		// // gl.glVertex3f(20F, 1F, 0F);
		// // gl.glVertex3f(30F, 2F, 0F);
		// // gl.glVertex3f(40F, 3F, 0F);
		// gl.glEnd();

		// gl.glPointSize(5.0f);
		// gl.glColor4f(0,0,1,1);
		// gl.glBegin(GL.GL_POINTS);
		//		
		// gl.glVertex3f(5.0f, 3.0f, 0.0f);
		//		
		// gl.glEnd();
		// gl.glFlush();

		// vec[0] = new Vec3f();
		// vec[1] = new Vec3f();
		// vec[2] = new Vec3f();
		//		
		// vec[0].set(3.0f, 3.0f, 0);
		// vec[1].set(2.0f, 2.0f, 0);
		// vec[2].set(1.0f, 1.0f, 0);

		// Spline3D spline = new Spline3D(vec, 1.0F, 1.0F);

	}

	@Override
	public String getDetailedInfo() {
		return new String("");
	}

	@Override
	protected void handleEvents(EPickingType ePickingType, EPickingMode pickingMode, int iExternalID,
		Pick pick) {
		if (detailLevel == EDetailLevel.VERY_LOW) {
			return;
		}
		switch (ePickingType) {

			case HEAT_MAP_STORAGE_SELECTION:

				switch (pickingMode) {
					case CLICKED:
						break;
					case MOUSE_OVER:

						break;
					default:
						return;
				}

				setDisplayListDirty();
				break;
		}
	}

	public boolean isInListMode() {
		return bIsInListMode;
	}

	@Override
	public void broadcastElements(EVAOperation type) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getNumberOfSelections(ESelectionType selectionType) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getShortInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearAllSelections() {
		// TODO Auto-generated method stub

	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedHyperbolicView serializedForm = new SerializedHyperbolicView();
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();
		redrawViewListener = new RedrawViewListener();
		redrawViewListener.setHandler(this);
		eventPublisher.addListener(RedrawViewEvent.class, redrawViewListener);
	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();
		if (redrawViewListener != null) {
			eventPublisher.removeListener(redrawViewListener);
			redrawViewListener = null;
		}
	}

	@Override
	public void handleClearSelections() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleRedrawView() {
		setDisplayListDirty();
	}

	@Override
	public void handleUpdateView() {
		// TODO Auto-generated method stub

	}

}
