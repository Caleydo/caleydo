package org.caleydo.view.texture;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.manager.IDataDomain;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.datadomain.IDataDomainBasedView;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.serialize.SerializedDummyView;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;

/**
 * Single OpenGL tissue view
 * 
 * @author Marc Streit
 */
public class GLTexture extends AGLView implements IDataDomainBasedView<IDataDomain> {

	public final static String VIEW_ID = "org.caleydo.view.texture";

	private String texturePath;
	private int experimentIndex;

	private IDataDomain dataDomain;
	
	private boolean updateTexture = false;

	/**
	 * Constructor.
	 */
	public GLTexture(GLCaleydoCanvas glCanvas, final String sLabel,
			final IViewFrustum viewFrustum) {
		super(glCanvas, sLabel, viewFrustum, false);

		viewType = VIEW_ID;

	}

	@Override
	public void initLocal(final GL gl) {
		init(gl);
	}

	@Override
	public void initRemote(GL gl, AGLView glParentView, GLMouseListener glMouseListener,
			GLInfoAreaManager infoAreaManager) {

		this.glMouseListener = glMouseListener;
		init(gl);
	}

	@Override
	public void init(final GL gl) {
	}

	@Override
	public void displayLocal(final GL gl) {

		pickingManager.handlePicking(this, gl);
		if (bIsDisplayListDirtyLocal) {
			// rebuildPathwayDisplayList(gl);
			bIsDisplayListDirtyLocal = false;
		}
		display(gl);
	}

	@Override
	public void displayRemote(final GL gl) {
		if (bIsDisplayListDirtyRemote) {
			// rebuildPathwayDisplayList(gl);
			bIsDisplayListDirtyRemote = false;
		}

		display(gl);
	}

	@Override
	public void display(final GL gl) {
		// processEvents();
		checkForHits(gl);
		renderScene(gl);
	}

	private void renderScene(final GL gl) {

		if (updateTexture)
			renewTextureInCache();
		
		float topMargin = 0.07f;
		
		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.TISSUE_SELECTION, experimentIndex));
		if (texturePath != null && !texturePath.isEmpty()) {
		
			try {
				textureManager.renderTexture(gl, texturePath, new Vec3f(viewFrustum.getLeft(), viewFrustum.getBottom(), 0), new Vec3f(
						viewFrustum.getRight(), viewFrustum.getBottom(), 0), new Vec3f(viewFrustum.getRight(), viewFrustum.getTop()- topMargin, 0), new Vec3f(viewFrustum.getLeft(), viewFrustum.getTop() - topMargin, 0), 1, 1, 1, 1);				
			} catch (IllegalStateException e) {
				// Render nothing if texture does not exist
			}
		}
		gl.glPopName();

		float[] color = null;

		float z = 0.005f;

		// FIXME: after view plugin reorganization
		// SelectionType selectionType = ((GLTissueViewBrowser)
		// glRemoteRenderingView)
		// .getSelectionManager().getSelectionType(experimentIndex);
		// if (selectionType == SelectionType.SELECTION)
		// color = GeneralRenderStyle.SELECTED_COLOR;
		// else if (selectionType == SelectionType.MOUSE_OVER)
		// color = GeneralRenderStyle.MOUSE_OVER_COLOR;
		// else if (selectionType == SelectionType.DESELECTED) {
		// gl.glColor4f(1f, 1f, 1f, 0.7f);
		// gl.glBegin(GL.GL_POLYGON);
		// gl.glVertex3f(viewFrustum.getLeft(), viewFrustum.getBottom(), z);
		// gl.glVertex3f(viewFrustum.getRight() - viewFrustum.getLeft(),
		// viewFrustum.getBottom(), z);
		// gl.glVertex3f(viewFrustum.getRight() - viewFrustum.getLeft(),
		// viewFrustum.getTop() - viewFrustum.getBottom(), z);
		// gl.glVertex3f(viewFrustum.getLeft(), viewFrustum.getTop()
		// - viewFrustum.getBottom(), z);
		// gl.glEnd();
		// }

		if (color != null) {
			gl.glColor4fv(color, 0);
			gl.glLineWidth(3);
			gl.glBegin(GL.GL_LINE_LOOP);
			gl.glVertex3f(viewFrustum.getLeft(), viewFrustum.getBottom(), z);
			gl.glVertex3f(viewFrustum.getRight() - viewFrustum.getLeft(), viewFrustum
					.getBottom(), z);
			gl.glVertex3f(viewFrustum.getRight() - viewFrustum.getLeft(), viewFrustum
					.getTop()-topMargin
					- viewFrustum.getBottom(), z);
			gl.glVertex3f(viewFrustum.getLeft(), viewFrustum.getTop()-topMargin
					- viewFrustum.getBottom(), z);
			gl.glEnd();
		}
	}

	@Override
	protected void handlePickingEvents(EPickingType ePickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick) {
		// if (detailLevel == EDetailLevel.VERY_LOW) {
		// return;
		// }

		switch (ePickingType) {
		case TISSUE_SELECTION:
			switch (pickingMode) {
			case MOUSE_OVER: {

				// FIXME: after view plugin reorganization
				// SelectionManager selectionManager =
				// ((GLTissueViewBrowser)
				// glRemoteRenderingView).getSelectionManager();
				// selectionManager.clearSelections();
				// selectionManager.addToType(SelectionType.MOUSE_OVER,
				// experimentIndex);
				//
				// SelectedElementRep selectedElementRep =
				// new SelectedElementRep(EIDType.EXPERIMENT_INDEX,
				// iUniqueID, 1f, 1f, 0);
				//
				// ConnectedElementRepresentationManager
				// connectedElementRepresentationManager =
				// generalManager.getViewGLCanvasManager()
				// .getConnectedElementRepresentationManager();
				// connectedElementRepresentationManager.clear(EIDType.EXPERIMENT_INDEX);
				//
				// int connectionID =
				// generalManager.getIDManager().createID(EManagedObjectType.CONNECTION);
				//
				// selectionManager.addConnectionID(connectionID,
				// experimentIndex);
				//
				// connectedElementRepresentationManager.addSelection(connectionID,
				// selectedElementRep);
				//
				// //
				// triggerSelectionUpdate(EMediatorType.SELECTION_MEDIATOR,
				// // axisSelectionManager
				// // .getDelta(), null);
				//
				// SelectionCommand command =
				// new SelectionCommand(ESelectionCommandType.CLEAR,
				// SelectionType.MOUSE_OVER);
				//
				// SelectionCommandEvent event = new
				// SelectionCommandEvent();
				// event.setSender(this);
				// event.setCategory(EIDCategory.EXPERIMENT);
				// event.setSelectionCommand(command);
				// eventPublisher.triggerEvent(event);
				//
				// ISelectionDelta selectionDelta =
				// selectionManager.getDelta();
				// // if (eAxisDataType == EIDType.EXPRESSION_INDEX
				// // || eAxisDataType == EIDType.EXPERIMENT_INDEX) {
				// // handleConnectedElementRep(selectionDelta);
				// // }
				// SelectionUpdateEvent selectionEvent = new
				// SelectionUpdateEvent();
				// selectionEvent.setSender(this);
				// selectionEvent.setSelectionDelta((SelectionDelta)
				// selectionDelta);
				// eventPublisher.triggerEvent(selectionEvent);

				break;
			}
			}
		}
	}

	@Override
	public String getShortInfo() {
		// PathwayGraph pathway =
		// (generalManager.getPathwayManager().getItem(iPathwayID));
		//		
		// return pathway.getTitle() + " (" +pathway.getType().getName() + ")";

		return "";
	}

	@Override
	public String getDetailedInfo() {
		// StringBuffer sInfoText = new StringBuffer();
		// PathwayGraph pathway =
		// (generalManager.getPathwayManager().getItem(iPathwayID));
		//
		// sInfoText.append("<b>Pathway</b>\n\n<b>Name:</b> "+
		// pathway.getTitle()
		// + "\n<b>Type:</b> "+pathway.getType().getName());
		//
		// // generalManager.getSWTGUIManager().setExternalRCPStatusLineMessage(
		// // pathway.getType().getName() + " Pathway: " + sPathwayTitle);
		//
		// return sInfoText.toString();

		return "";
	}

	@Override
	public void broadcastElements(EVAOperation type) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getNumberOfSelections(SelectionType selectionType) {
		// TODO Auto-generated method stub
		return 0;
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

	public void setTexturePath(String texturePath) {
		this.texturePath = texturePath;
	}
	
	public void updateTexture() {
		updateTexture = true;
	}
	
	private void renewTextureInCache() {
		
		updateTexture = false;
	
		try {
			textureManager.renewTexture(texturePath);			
		} catch (Exception e) {
			updateTexture = true;
			System.out.println("invalid!");
		}
	}
	
	public void setExperimentIndex(int experimentIndex) {
		this.experimentIndex = experimentIndex;
	}

	public int getExperimentIndex() {
		return experimentIndex;
	}

	@Override
	public IDataDomain getDataDomain() {
		return dataDomain;
	}

	@Override
	public void setDataDomain(IDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

}