package org.caleydo.core.view.opengl.canvas.tissue;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.ESelectionCommandType;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.manager.event.view.SelectionCommandEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.view.ConnectedElementRepresentationManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.serialize.SerializedDummyView;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.remote.viewbrowser.GLTissueViewBrowser;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;

/**
 * Single OpenGL tissue view
 * 
 * @author Marc Streit
 */
public class GLTissue
	extends AGLView {

	private String texturePath;
	private int experimentIndex;

	/**
	 * Constructor.
	 */
	public GLTissue(GLCaleydoCanvas glCanvas, final String sLabel, final IViewFrustum viewFrustum) {
		super(glCanvas, sLabel, viewFrustum, false);
		viewType = EManagedObjectType.GL_TISSUE;

		// initialize internal gene selection manager
		ArrayList<ESelectionType> alSelectionType = new ArrayList<ESelectionType>();
		for (ESelectionType selectionType : ESelectionType.values()) {
			alSelectionType.add(selectionType);
		}
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
		processEvents();
		checkForHits(gl);
		renderScene(gl);
	}

	private void renderScene(final GL gl) {

		gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.TISSUE_SELECTION, experimentIndex));
		if (texturePath != null && !texturePath.isEmpty()) {
			textureManager.renderTexture(gl, texturePath, new Vec3f(0, 0, 0), new Vec3f(8, 0, 0), new Vec3f(
				8, 8, 0), new Vec3f(0, 8, 0), 1, 1, 1, 1);
		}
		gl.glPopName();

		float[] color = null;

		float z = 0.005f;
		ESelectionType selectionType =
			((GLTissueViewBrowser) glRemoteRenderingView).getSelectionManager().getSelectionType(
				experimentIndex);

		if (selectionType == ESelectionType.SELECTION)
			color = GeneralRenderStyle.SELECTED_COLOR;
		else if (selectionType == ESelectionType.MOUSE_OVER)
			color = GeneralRenderStyle.MOUSE_OVER_COLOR;
		else if (selectionType == ESelectionType.DESELECTED) {
			gl.glColor4f(1f, 1f, 1f, 0.7f);
			gl.glBegin(GL.GL_POLYGON);
			gl.glVertex3f(viewFrustum.getLeft(), viewFrustum.getBottom(), z);
			gl.glVertex3f(viewFrustum.getRight() - viewFrustum.getLeft(), viewFrustum.getBottom(), z);
			gl.glVertex3f(viewFrustum.getRight() - viewFrustum.getLeft(), viewFrustum.getTop()
				- viewFrustum.getBottom(), z);
			gl.glVertex3f(viewFrustum.getLeft(), viewFrustum.getTop() - viewFrustum.getBottom(), z);
			gl.glEnd();
		}

		if (color != null) {
			gl.glColor4fv(color, 0);
			gl.glLineWidth(3);
			gl.glBegin(GL.GL_LINE_LOOP);
			gl.glVertex3f(viewFrustum.getLeft(), viewFrustum.getBottom(), z);
			gl.glVertex3f(viewFrustum.getRight() - viewFrustum.getLeft(), viewFrustum.getBottom(), z);
			gl.glVertex3f(viewFrustum.getRight() - viewFrustum.getLeft(), viewFrustum.getTop()
				- viewFrustum.getBottom(), z);
			gl.glVertex3f(viewFrustum.getLeft(), viewFrustum.getTop() - viewFrustum.getBottom(), z);
			gl.glEnd();
		}
	}

	@Override
	protected void handlePickingEvents(EPickingType ePickingType, EPickingMode pickingMode, int iExternalID,
		Pick pick) {
		// if (detailLevel == EDetailLevel.VERY_LOW) {
		// return;
		// }

		switch (ePickingType) {
			case TISSUE_SELECTION:
				switch (pickingMode) {
					case MOUSE_OVER: {
						SelectionManager selectionManager =
							((GLTissueViewBrowser) glRemoteRenderingView).getSelectionManager();
						selectionManager.clearSelections();
						selectionManager.addToType(ESelectionType.MOUSE_OVER, experimentIndex);

						SelectedElementRep selectedElementRep =
							new SelectedElementRep(EIDType.EXPERIMENT_INDEX, iUniqueID, 1f, 1f, 0);

						ConnectedElementRepresentationManager connectedElementRepresentationManager =
							generalManager.getViewGLCanvasManager()
								.getConnectedElementRepresentationManager();
						connectedElementRepresentationManager.clear(EIDType.EXPERIMENT_INDEX);

						int connectionID =
							generalManager.getIDManager().createID(EManagedObjectType.CONNECTION);

						selectionManager.addConnectionID(connectionID, experimentIndex);

						connectedElementRepresentationManager.addSelection(connectionID, selectedElementRep);

						// triggerSelectionUpdate(EMediatorType.SELECTION_MEDIATOR,
						// axisSelectionManager
						// .getDelta(), null);

						SelectionCommand command =
							new SelectionCommand(ESelectionCommandType.CLEAR, ESelectionType.MOUSE_OVER);

						SelectionCommandEvent event = new SelectionCommandEvent();
						event.setSender(this);
						event.setCategory(EIDCategory.EXPERIMENT);
						event.setSelectionCommand(command);
						eventPublisher.triggerEvent(event);

						ISelectionDelta selectionDelta = selectionManager.getDelta();
						// if (eAxisDataType == EIDType.EXPRESSION_INDEX
						// || eAxisDataType == EIDType.EXPERIMENT_INDEX) {
						// handleConnectedElementRep(selectionDelta);
						// }
						SelectionUpdateEvent selectionEvent = new SelectionUpdateEvent();
						selectionEvent.setSender(this);
						selectionEvent.setSelectionDelta((SelectionDelta) selectionDelta);
						eventPublisher.triggerEvent(selectionEvent);

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

		return "Tissue Viewer";
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

		return "Tissuew Viewer";
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

	public void setExperimentIndex(int experimentIndex) {
		this.experimentIndex = experimentIndex;
	}

	public int getExperimentIndex() {
		return experimentIndex;
	}

}