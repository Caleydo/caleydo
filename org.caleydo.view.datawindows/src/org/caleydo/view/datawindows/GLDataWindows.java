package org.caleydo.view.datawindows;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.open.Transform;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

import javax.media.opengl.GL;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.view.opengl.CmdCreateView;
import org.caleydo.core.data.graph.tree.DefaultNode;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.util.tracking.TrackDataProvider;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevel;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevelElement;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.parcoords.PCRenderStyle;
import org.caleydo.view.pathway.GLPathway;
import org.caleydo.view.pathway.SerializedPathwayView;

/**
 * Rendering the Datawindow
 * 
 * @author Hannes Plank
 * @author Marc Streit
 */
@SuppressWarnings("unused")
public class GLDataWindows extends AGLView {

	public final static String VIEW_ID = "org.caleydo.view.datawindows";

	private double mouseCoordX = 0;
	private double mouseCoordY = 0;

	private Point mousePoint = new Point(0,0);
	int viewport[] = new int[4];

	private float canvasWidth = 7;
	private float canvasHeight = 5;

	private TrackDataProvider tracker;
	private float[] receivedEyeData;

	private ArrayList<AGLView> containedGLViews;
	private ArrayList<ASerializedView> newViews;

	private RemoteLevel testLevel;



	private DataWindowsDisk disk;
	
	//properties of the circle
	private double circleRadius=2;
	
	private org.eclipse.swt.graphics.Point upperLeftScreenPos = new org.eclipse.swt.graphics.Point(
			0, 0);
	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLDataWindows(GLCaleydoCanvas glCanvas, final String sLabel,
			final IViewFrustum viewFrustum) {

		super(glCanvas, sLabel, viewFrustum, true);
		viewType = GLDataWindows.VIEW_ID;

		containedGLViews = new ArrayList<AGLView>();
		newViews = new ArrayList<ASerializedView>();

		//preparing the eyetracker
		//this.tracker = new TrackDataProvider();
		//tracker.startTracking();

		//remote test
		//testLevel = new RemoteLevel(1, "testview", testLevel, testLevel);
		//Transform transform = new Transform();
		//transform.setTranslation(new Vec3f(0, 0, 0));
		//transform.setScale(new Vec3f(0.5f, 0.5f, 1));
		//testLevel.getElementByPositionIndex(0).setTransform(transform);
        //end remote test
		

		//debug
	
	    disk = new DataWindowsDisk(2);
		disk.loadTree();
		disk.scaleTree(2);
		//disk.translateTree(new Point2D.Double(3,3));
		
		// ASerializedView serView = getSerializableRepresentation();
		// newViews.add(serView);

		
		
	}

	@Override
	public void initLocal(GL gl) {

		iGLDisplayListIndexLocal = gl.glGenLists(5);

		iGLDisplayListToCall = iGLDisplayListIndexLocal;
		init(gl);

	}

	@Override
	public void initRemote(final GL gl, final AGLView glParentView,
			final GLMouseListener glMouseListener,
			GLInfoAreaManager infoAreaManager) {

		this.glMouseListener = glMouseListener;

		iGLDisplayListIndexRemote = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexRemote;

		init(gl);
	}

	@Override
	public void setDetailLevel(EDetailLevel detailLevel) {
		super.setDetailLevel(detailLevel);
	}

	@Override
	public void displayLocal(GL gl) {
		processEvents();

		// if (!isVisible())
		// return;
		// if (set == null)
		// return;

		pickingManager.handlePicking(this, gl);
		
		if (bIsDisplayListDirtyLocal) {

			buildDisplayList(gl, iGLDisplayListIndexLocal);
			bIsDisplayListDirtyLocal = false;

		}
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		display(gl);
		checkForHits(gl);

		if (eBusyModeState != EBusyModeState.OFF)
			renderBusyMode(gl);

	}
	@Override
	public void displayRemote(GL gl) {
		display(gl);
	}

	@Override
	public void display(GL gl) {
		// processEvents();

		// GLHelperFunctions.drawAxis(gl);
		// GLHelperFunctions.drawViewFrustum(gl, viewFrustum);
		// gl.glEnable(GL.GL_DEPTH_TEST);
		// clipToFrustum(gl);

		 gl.glMatrixMode(GL.GL_PROJECTION);
		 gl.glLoadIdentity();
		//
		 gl.glOrtho(0.0f, canvasWidth,canvasHeight, 0.0f, -1.0f, 1.0f);
		//
		 gl.glMatrixMode(GL.GL_MODELVIEW);
		 gl.glLoadIdentity();
		//
		 


		
		Vec3f lowerLeftCorner = new Vec3f(-1 + canvasWidth / 2, -1
				+ canvasHeight / 2, 0);

		Vec3f lowerRightCorner = new Vec3f(1 + canvasWidth / 2, -1
				+ canvasHeight / 2, 0);
		Vec3f upperRightCorner = new Vec3f(1 + canvasWidth / 2,
				1 + canvasHeight / 2, 0);
		Vec3f upperLeftCorner = new Vec3f(-1 + canvasWidth / 2,
				1 + canvasHeight / 2, 0);
		Vec3f scalingPivot = new Vec3f(1, 1, 0);

		int iPickingID = pickingManager.getPickingID(iUniqueID,
				EPickingType.DATAW_NODE, 1);
		
		gl.glPushName(iPickingID);
		//displaying test texture for picking
		textureManager.renderGUITexture(gl, EIconTextures.PATHWAY_SYMBOL,
				lowerLeftCorner, lowerRightCorner, upperRightCorner,
				upperLeftCorner, scalingPivot, 1, 1, 1, 1, 100);

		gl.glPopName();
			
		 //System.out.println("mouseZeiger:"+mousePoint.getX()+"|"+mousePoint.getY());
		 
		 //
		 gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
		 
		 canvasWidth=canvasHeight*(float)viewport[2]/(float)viewport[3];
		
		 
		
		
		//
		// }
		//
		// receivedEyeData = tracker.getEyeTrackData();
		//
		// int offsetX = upperLeftScreenPos.x;
		// int offsetY = upperLeftScreenPos.y;
		//
		// receivedEyeData[0] = receivedEyeData[0] - (float) offsetX;
		// receivedEyeData[1] = receivedEyeData[1] - (float) offsetY;
		//
		// // System.out.println("Eye position korrigiert: " +
		// receivedEyeData[0]
		// // + " / " + receivedEyeData[1]);
		// float factorX = canvasWidth / (float) viewport[2];
		// float factorY = canvasHeight / (float) viewport[3];
		//
		// // visualisation of the eyecursor
		// gl.glBegin(GL.GL_LINE);
		// gl.glVertex3f(receivedEyeData[0] * factorX, receivedEyeData[1]
		// * factorY, 0);
		// gl.glVertex3f(2, 2, 0);
		// gl.glEnd();

		
		//remote test
		//initNewView(gl);

		//renderRemoteLevel(gl, testLevel);
		
		
		disk.renderTree(gl, textureManager,canvasWidth, canvasHeight);
	
		
		
		
		if (glMouseListener.getPickedPoint() != null) {
			mousePoint = glMouseListener.getPickedPoint();

			gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
			double factorX = (double) canvasWidth / (double) viewport[2];
			double factorY = (double) canvasHeight / (double) viewport[3];

			mouseCoordX = (float) (mousePoint.getX() * factorX);
			mouseCoordY = (float) (mousePoint.getY() * factorY);
			
			
			
		}
		
		
		
		
	
		 if (glMouseListener.wasRightMouseButtonPressed()){

			 disk.translateTree(new Point2D.Double(mouseCoordX-canvasWidth/2,mouseCoordY-canvasHeight/2));
		 }
		 if (glMouseListener.wasLeftMouseButtonPressed()){

			 disk.translateTree(new Point2D.Double(-0.3,-0.3));
		 }
		 
		// if (!containedGLViews.isEmpty()) {
		//
		// containedGLViews.get(0).displayRemote(gl);
		// // renderRemoteLevelElement(gl,
		// // testLevel.getElementByPositionIndex(0));
		//
		

		// buildDisplayList(gl, iGLDisplayListIndexRemote);
		// if (!isRenderedRemote())
		// contextMenu.render(gl, this);
	}

	private void buildDisplayList(final GL gl, int iGLDisplayListIndex) {

		if (bHasFrustumChanged) {
			bHasFrustumChanged = false;
		}

		gl.glNewList(iGLDisplayListIndex, GL.GL_COMPILE);
		gl.glEndList();
	}

	private void renderRemoteLevel(final GL gl, final RemoteLevel level) {
		for (RemoteLevelElement element : level.getAllElements()) {
			renderRemoteLevelElement(gl, element, level);
		}
	}

	private void renderRemoteLevelElement(final GL gl,
			RemoteLevelElement element, RemoteLevel level) {

		AGLView glView = element.getGLView();

		if (glView == null) {
			return;
		}

		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.REMOTE_LEVEL_ELEMENT, element.getID()));
		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.VIEW_SELECTION, glView.getID()));

		gl.glPushMatrix();

		Transform transform = element.getTransform();
		Vec3f translation = transform.getTranslation();
		Rotf rot = transform.getRotation();
		Vec3f scale = transform.getScale();
		Vec3f axis = new Vec3f();
		float fAngle = rot.get(axis);

		gl.glTranslatef(translation.x(), translation.y(), translation.z());
		gl.glRotatef(Vec3f.convertRadiant2Grad(fAngle), axis.x(), axis.y(),
				axis.z());
		gl.glScalef(scale.x(), scale.y(), scale.z());

		glView.displayRemote(gl);

		gl.glPopMatrix();

		gl.glPopName();
		gl.glPopName();
	}

	@Override
	public String getShortInfo() {
		if (contentVA == null)
			return "Scatterplot - 0 " + useCase.getContentLabel(false, true)
					+ " / 0 experiments";

		return "Scatterplot - " + contentVA.size() + " "
				+ useCase.getContentLabel(false, true) + " / "
				+ storageVA.size() + " experiments";
	}

	@Override
	public String getDetailedInfo() {
		StringBuffer sInfoText = new StringBuffer();
		sInfoText.append("<b>Type:</b> Scatter Plot\n");
		// TODO Everything

		// return sInfoText.toString();
		return "TODO: ScatterploT Deatil Info";
	}

	@Override
	protected void handlePickingEvents(EPickingType ePickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick) {
		if (detailLevel == EDetailLevel.VERY_LOW) {
			return;
		}
		
		SelectionType selectionType;
		switch (ePickingType) {

		case DATAW_NODE:
			switch (pickingMode) {

			case CLICKED:
				if (iExternalID==1)
				{
				disk.scaleTree(0.5);
				System.out.println("CLICKED!!!");
				}
			}
		}
		
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedDataWindowsView serializedForm = new SerializedDataWindowsView(
				dataDomain);
		serializedForm.setViewID(this.getID());

		ArrayList<ASerializedView> remoteViews = new ArrayList<ASerializedView>(
				testLevel.getAllElements().size());
		for (RemoteLevelElement rle : testLevel.getAllElements()) {
			if (rle.getGLView() != null) {
				AGLView remoteView = rle.getGLView();
				remoteViews.add(remoteView.getSerializableRepresentation());
			}
		}
		serializedForm.setTestViews(remoteViews);

		return serializedForm;
	}

	@Override
	public String toString() {
		return "Standalone Scatterplot, rendered remote: " + isRenderedRemote()
				+ ", contentSize: " + contentVA.size() + ", storageSize: "
				+ storageVA.size() + ", contentVAType: " + contentVAType
				+ ", remoteRenderer:" + getRemoteRenderingGLCanvas();
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();

	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();
	}

	@Override
	public void broadcastElements(EVAOperation type) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearAllSelections() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(GL gl) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getNumberOfSelections(SelectionType SelectionType) {
		// TODO Auto-generated method stub
		return 0;
	}

	private void initNewView(GL gl) {
		if (!newViews.isEmpty()) {
			ASerializedView serView = newViews.remove(0);
			AGLView view = createView(gl, serView);
			containedGLViews.add(view);
			testLevel.getNextFree().setGLView(view);
		}
	}

	@Override
	public void initFromSerializableRepresentation(ASerializedView ser) {

		SerializedDataWindowsView serializedView = (SerializedDataWindowsView) ser;

		for (ASerializedView remoteSerializedView : serializedView
				.getTestViews()) {
			newViews.add(remoteSerializedView);
		}

		setDisplayListDirty();
	}

	private AGLView createView(GL gl, ASerializedView serView) {

		ICommandManager cm = generalManager.getCommandManager();
		CmdCreateView cmdView = (CmdCreateView) cm
				.createCommandByType(ECommandType.CREATE_GL_VIEW);
		cmdView.setViewID(serView.getViewType());
		cmdView.setAttributesFromSerializedForm(serView);
		cmdView.doCommand();

		AGLView glView = cmdView.getCreatedObject();
		glView.setUseCase(useCase);
		// glView.setRemoteRenderingGLView(this);
		glView.setSet(set);

		if (glView instanceof GLPathway) {
			GLPathway glPathway = (GLPathway) glView;

			glPathway.setPathway(((SerializedPathwayView) serView)
					.getPathwayID());
			glPathway.enablePathwayTextures(true);
			glPathway.enableNeighborhood(false);
			glPathway.enableGeneMapping(false);
		}

		glView.initRemote(gl, this, glMouseListener, null);

		return glView;
	}
	
}