package org.caleydo.core.view.opengl.canvas.hyperbolic;

import static org.caleydo.core.view.opengl.canvas.histogram.HistogramRenderStyle.SIDE_SPACING;

import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.GLHelperFunctions;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.core.view.serialize.ASerializedView;
import org.caleydo.core.view.serialize.SerializedDummyView;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.ADrawableNode;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.TestNode;
import org.caleydo.core.view.opengl.canvas.hyperbolic.lineartree.Tree;
import org.caleydo.core.view.opengl.canvas.hyperbolic.lineartree.TreeTester;
import gleem.linalg.Vec3f;
import org.caleydo.core.view.opengl.canvas.hyperbolic.lineartree.*;
import org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters.ATreeLayouter;
import org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters.LinearTreeLayouter;




/**
 * Rendering the hyperbolic view.
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class GLHyperbolic
	extends AGLEventListener {
	
	//private Tree<DefaultNode> tree;
	public Vec3f[] vec;
	
	boolean bIsInListMode = false;

	boolean bUseDetailLevel = true;
	ISet set;
	
	Tree<ADrawableNode> tree = null;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLHyperbolic(GLCaleydoCanvas glCanvas, final String sLabel,
		final IViewFrustum viewFrustum) {
		super(glCanvas, sLabel, viewFrustum, true);

		viewType = EManagedObjectType.GL_HYPERBOLIC;

		ArrayList<ESelectionType> alSelectionTypes = new ArrayList<ESelectionType>();
		alSelectionTypes.add(ESelectionType.NORMAL);
		alSelectionTypes.add(ESelectionType.MOUSE_OVER);
		alSelectionTypes.add(ESelectionType.SELECTION);
		
		//Build the Test Tree in Constructor
//		TreeTester tester = new TreeTester();
//		tree = tester;
//		tree.runTest();

		tree = new Tree<ADrawableNode>();
		tree.setRootNode(new TestNode("first Test", 1));
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
		final GLMouseListener glMouseListener,
		final IGLCanvasRemoteRendering remoteRenderingGLCanvas, GLInfoAreaManager infoAreaManager) {

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
		//GLHelperFunctions.drawAxis(gl);
		render(gl);
		// clipToFrustum(gl);
		//
		// gl.glCallList(iGLDisplayListToCall);

		// buildDisplayList(gl, iGLDisplayListIndexRemote);
		
//		if (iPathwayID != -1)
//		{
//			PathwayGraph pathwayGraph = generalManager.getPathwayManager().getItem(iPathwayID);
//			for (IGraphItem node : pathwayGraph.getAllItemsByKind(EGraphItemKind.NODE))
//			{
//				System.out.println("Node:" + node);
//			}
//		}
		
	}

	private void buildDisplayList(final GL gl, int iGLDisplayListIndex) {

	}

	private void render(GL gl) {
		
		ATreeLayouter layouter = new LinearTreeLayouter(gl, viewFrustum, tree);
		layouter.drawLayout();
		
//		TestLayout layout = new TestLayout(gl, viewFrustum, tree.getTree());
//		layout.drawGraph(gl);
//		gl.glFlush();

//		gl.glColor4f(1, 0, 0, 1);
//		gl.glBegin(GL.GL_POLYGON);
//		gl.glVertex3f(0, 0, 0);
//		gl.glVertex3f(0, 1, 0);
//		gl.glVertex3f(1, 1, 0);
//		gl.glVertex3f(1, 0, 0);
//		gl.glEnd();
	
//		gl.glColor4f(1, 1, 0, 1);

//		gl.glBegin(GL.GL_POLYGON);
//		gl.glVertex2f(0.0f, 0.0f);
//		gl.glVertex2f(0.0f, 3.0f);
//		gl.glVertex2f(1.0f, 0.0f);
//		gl.glVertex2f(1.0f, 3.0f);
//		gl.glVertex2f(0.0f, 1.5f);
//		gl.glVertex2f(1.0f, 1.5f);
//		gl.glVertex2f(2.0f, 0.0f);
//		gl.glVertex2f(2.0f, 3.0f);
//		gl.glVertex2f(2.0f, 1.5f);
//		gl.glVertex2f(3.0f, 1.5f);
//		gl.glVertex2f(2.0f, 3.0f);
//		gl.glVertex2f(0.0f, 5.0f);
////		 glVertex2f(4.0, 3.0);
////		 glVertex2f(6.0, 1.5);
////		 glVertex2f(4.0, 0.0);
//		gl.glEnd();
		
//		gl.glBegin(GL.GL_LINES);
//		gl.glVertex3f(0.0f, 10.0f, 0.0f); // origin of the line
//		gl.glVertex3f(200.0f, 14.0f, 0.0f); // ending point of the line
//		gl.glEnd( );
		
//		gl.glColor4f(1, 1, 0, 1);
//		
//		gl.glBegin(GL.GL_LINE);
//
//		gl.glVertex2f(0.0f, 0.0f);
//		gl.glVertex2f(5.0f, 3.0f);
////		gl.glVertex3f(20F, 1F, 0F);
////		gl.glVertex3f(30F, 2F, 0F);
////		gl.glVertex3f(40F, 3F, 0F);
//		gl.glEnd();
		
//		gl.glPointSize(5.0f);
//		gl.glColor4f(0,0,1,1);
//		gl.glBegin(GL.GL_POINTS);
//		
//		gl.glVertex3f(5.0f, 3.0f, 0.0f);
//		
//		gl.glEnd();
//		gl.glFlush();
		
		
//		vec[0] = new Vec3f();
//		vec[1] = new Vec3f();
//		vec[2] = new Vec3f();
//		
//		vec[0].set(3.0f, 3.0f, 0);
//		vec[1].set(2.0f, 2.0f, 0);
//		vec[2].set(1.0f, 1.0f, 0);


		
//		Spline3D spline = new Spline3D(vec, 1.0F, 1.0F);

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
		SerializedDummyView serializedForm = new SerializedDummyView();
		serializedForm.setViewID(this.getID());
		return serializedForm; 
	}

}
