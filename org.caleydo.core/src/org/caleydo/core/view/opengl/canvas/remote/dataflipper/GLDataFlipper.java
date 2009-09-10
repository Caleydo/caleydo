package org.caleydo.core.view.opengl.canvas.remote.dataflipper;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.open.Transform;

import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.view.opengl.CmdCreateGLEventListener;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.GLHelperFunctions;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevelElement;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.eclipse.core.runtime.Status;

public class GLDataFlipper
	extends AGLEventListener
	implements IGLRemoteRenderingView{

	private ArrayList<ASerializedView> newViews;

	private ArrayList<Integer> containedViewIDs;

	private RemoteLevelElement focusElement;
	private ArrayList<RemoteLevelElement> remoteLevelElementsLeft;
	private ArrayList<RemoteLevelElement> remoteLevelElementsRight;
	
	private GLInfoAreaManager infoAreaManager;

	/**0
	 * Constructor.
	 */
	public GLDataFlipper(GLCaleydoCanvas glCanvas, final String sLabel, final IViewFrustum viewFrustum) {

		super(glCanvas, sLabel, viewFrustum, true);

		viewType = EManagedObjectType.GL_DATA_FLIPPER;

		// // Unregister standard mouse wheel listener
		// parentGLCanvas.removeMouseWheelListener(glMouseListener);
		// // Register specialized bucket mouse wheel listener
		// parentGLCanvas.addMouseWheelListener(bucketMouseWheelListener);
		// // parentGLCanvas.addMouseListener(bucketMouseWheelListener);

		glMouseListener.addGLCanvas(this);

		newViews = new ArrayList<ASerializedView>();
		containedViewIDs = new ArrayList<Integer>();
		remoteLevelElementsRight = new ArrayList<RemoteLevelElement>();
		remoteLevelElementsLeft = new ArrayList<RemoteLevelElement>();
		focusElement = new RemoteLevelElement(null);
		
		// TODO: Move to render style
		Transform transform = new Transform();
		transform.setTranslation(new Vec3f(0, 0, 0));
		transform.setScale(new Vec3f(1,1,1));
//		transform.setTranslation(new Vec3f(-2, -2, 0));
//			transform.setScale(new Vec3f(fScalingFactorFocusLevel, fScalingFactorFocusLevel,
//				fScalingFactorFocusLevel));

		focusElement.setTransform(transform);

	}

	@Override
	public void initLocal(final GL gl) {
		// iGLDisplayList = gl.glGenLists(1);
		init(gl);
	}

	@Override
	public void initRemote(final GL gl, final AGLEventListener glParentView,
		final GLMouseListener glMouseListener, final IGLRemoteRenderingView remoteRenderingGLCanvas,
		GLInfoAreaManager infoAreaManager) {

		throw new IllegalStateException("Not implemented to be rendered remote");
	}

	@Override
	public void init(final GL gl) {
		gl.glClearColor(1f, 1f, 1f, 1f);
		
		infoAreaManager = new GLInfoAreaManager();
		infoAreaManager.initInfoInPlace(viewFrustum);

	}

	@Override
	public void displayLocal(final GL gl) {

		pickingManager.handlePicking(this, gl);

		display(gl);

		if (eBusyModeState != EBusyModeState.OFF) {
			renderBusyMode(gl);
		}

		checkForHits(gl);

		// gl.glCallList(iGLDisplayListIndexLocal);
	}

	@Override
	public void displayRemote(final GL gl) {
		display(gl);
	}

	@Override
	public void display(final GL gl) {
		processEvents();
		// initNewView(gl);

		// renderRemoteLevel(gl, focusLevel);
		// renderHandles(gl);

		// gl.glCallList(iGLDisplayList);

		initNewView(gl);
		
		renderRemoteLevelElement(gl, focusElement);

		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(0, 2, 0);
		gl.glVertex3f(6, 4, 8);
		gl.glVertex3f(2, 3, 4);
		gl.glVertex3f(1, 6, 3);
		gl.glEnd();
		GLHelperFunctions.drawViewFrustum(gl, viewFrustum);

		float fZTranslation = 0;
		fZTranslation = 4f;

		gl.glTranslatef(0, 0, fZTranslation);
		contextMenu.render(gl, this);
		gl.glTranslatef(0, 0, -fZTranslation);
	}

	private void renderRemoteLevelElement(final GL gl, RemoteLevelElement element) {

		if (element.getContainedElementID() == -1)
			return;

		int iViewID = element.getContainedElementID();

		gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.REMOTE_LEVEL_ELEMENT, element
			.getID()));
		gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.VIEW_SELECTION, iViewID));

		AGLEventListener glEventListener =
			generalManager.getViewGLCanvasManager().getGLEventListener(iViewID);

		if (glEventListener == null) {
			generalManager.getLogger().log(
				new Status(Status.WARNING, GeneralManager.PLUGIN_ID,
					"Remote level element is null and cannot be rendered!"));
			return;
		}

		gl.glPushMatrix();

		Transform transform = element.getTransform();
		Vec3f translation = transform.getTranslation();
		Rotf rot = transform.getRotation();
		Vec3f scale = transform.getScale();
		Vec3f axis = new Vec3f();
		float fAngle = rot.get(axis);

		gl.glTranslatef(translation.x(), translation.y(), translation.z());
		gl.glRotatef(Vec3f.convertRadiant2Grad(fAngle), axis.x(), axis.y(), axis.z());
		gl.glScalef(scale.x(), scale.y(), scale.z());

		glEventListener.displayRemote(gl);

		gl.glPopMatrix();

		gl.glPopName();
		gl.glPopName();
	}

	
	/**
	 * Adds new remote-rendered-views that have been queued for displaying to this view. Only one view is
	 * taken from the list and added for remote rendering per call to this method.
	 * 
	 * @param GL
	 */
	private void initNewView(GL gl) {

		// if(arSlerpActions.isEmpty())
		// {
		if (!newViews.isEmpty()) {
			ASerializedView serView = newViews.remove(0);
			AGLEventListener view = createView(gl, serView);
			
			// addSlerpActionForView(gl, view);
			
			// TODO: remove when activating slerp
			view.initRemote(gl, this, glMouseListener, this, infoAreaManager);
			view.setDetailLevel(EDetailLevel.MEDIUM);
			
			containedViewIDs.add(view.getID());

			if (focusElement.isFree()) {
				focusElement.setContainedElementID(view.getID());
			}
			else {
				// iAl;
				// remoteLevelElementsLeft.add(newElement);
			}

			if (newViews.isEmpty()) {
				// triggerToolBarUpdate();
				enableUserInteraction();
			}
		}
	}

	@Override
	public void initFromSerializableRepresentation(ASerializedView ser) {
		// resetView(false);

		SerializedDataFlipperView serializedView = (SerializedDataFlipperView) ser;
		newViews.add(serializedView.getFocusView());

		// for (ASerializedView remoteSerializedView : serializedView.getStackViews()) {
		// newViews.add(remoteSerializedView);
		// }

		setDisplayListDirty();
	}

	/**
	 * Creates and initializes a new view based on its serialized form. The view is already added to the list
	 * of event receivers and senders.
	 * 
	 * @param gl
	 * @param serView
	 *            serialized form of the view to create
	 * @return the created view ready to be used within the application
	 */
	private AGLEventListener createView(GL gl, ASerializedView serView) {

		ICommandManager commandManager = generalManager.getCommandManager();
		ECommandType cmdType = serView.getCreationCommandType();
		CmdCreateGLEventListener cmdView =
			(CmdCreateGLEventListener) commandManager.createCommandByType(cmdType);
		cmdView.setAttributesFromSerializedForm(serView);
		// cmdView.setSet(set);
		cmdView.doCommand();

		AGLEventListener glView = cmdView.getCreatedObject();
		glView.setUseCase(useCase);
		glView.setRenderedRemote(true);
		glView.setSet(set);

		// if (glView instanceof GLPathway) {
		// initializePathwayView((GLPathway) glView);
		// }

		// triggerMostRecentDelta();

		return glView;
	}

	/**
	 * Disables picking and enables busy mode
	 */
	public void disableUserInteraction() {
		IViewManager canvasManager = generalManager.getViewGLCanvasManager();
		canvasManager.getPickingManager().enablePicking(false);
		canvasManager.requestBusyMode(this);
	}

	/**
	 * Enables picking and disables busy mode
	 */
	public void enableUserInteraction() {
		IViewManager canvasManager = generalManager.getViewGLCanvasManager();
		canvasManager.getPickingManager().enablePicking(true);
		canvasManager.releaseBusyMode(this);
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
	public String getDetailedInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumberOfSelections(ESelectionType eSelectionType) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getShortInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void handleEvents(EPickingType pickingType, EPickingMode pickingMode, int iExternalID, Pick pick) {

		// switch (pickingType) {
		// case BUCKET_DRAG_ICON_SELECTION:
		//
		// switch (pickingMode) {
		// case CLICKED:
		//
		// break;
		// }
		// break;
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		// TODO Auto-generated method stub
		return null;
	}

}
