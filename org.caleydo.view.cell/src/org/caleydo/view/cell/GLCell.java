package org.caleydo.view.cell;

import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.serialize.SerializedDummyView;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;

/**
 * Single OpenGL cell view
 * 
 * @author Marc Streit
 */
public class GLCell extends AGLView {
	// private ConnectedElementRepresentationManager
	// connectedElementRepresentationManager;

	// private GenericSelectionManager selectionManager;

	public final static String VIEW_ID = "org.caleydo.view.cell";

	/**
	 * Constructor.
	 */
	public GLCell(GLCaleydoCanvas glCanvas, final String sLabel,
			final IViewFrustum viewFrustum) {
		super(glCanvas, sLabel, viewFrustum, false);
		viewType = VIEW_ID;

		// connectedElementRepresentationManager =
		// generalManager.getViewGLCanvasManager().getConnectedElementRepresentationManager();

		// initialize internal gene selection manager
		ArrayList<ESelectionType> alSelectionType = new ArrayList<ESelectionType>();
		for (ESelectionType selectionType : ESelectionType.values()) {
			alSelectionType.add(selectionType);
		}

		// selectionManager = new
		// GenericSelectionManager.Builder(EIDType.PATHWAY_VERTEX).build();
	}

	@Override
	public void initLocal(final GL gl) {
		init(gl);
	}

	@Override
	public void initRemote(GL gl, AGLView glParentView,
			GLMouseListener glMouseListener, GLInfoAreaManager infoAreaManager) {
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

		// GLHelperFunctions.drawViewFrustum(gl, viewFrustum);

		Texture tempTexture = textureManager.getIconTexture(gl,
				EIconTextures.CELL_MODEL);
		tempTexture.enable();
		tempTexture.bind();

		TextureCoords texCoords = tempTexture.getImageTexCoords();
		gl.glColor3f(1, 1, 1);
		gl.glBegin(GL.GL_POLYGON);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(viewFrustum.getLeft(), viewFrustum.getBottom(), -0.025f);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(viewFrustum.getLeft(), viewFrustum.getTop()
				- viewFrustum.getBottom(), -0.025f);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(viewFrustum.getRight() - viewFrustum.getLeft(),
				viewFrustum.getTop() - viewFrustum.getBottom(), -0.025f);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(viewFrustum.getRight() - viewFrustum.getLeft(),
				viewFrustum.getBottom(), -0.025f);
		gl.glEnd();

		tempTexture.disable();

	}

	// private ISelectionDelta resolveExternalSelectionDelta(ISelectionDelta
	// selectionDelta) {
	// ISelectionDelta newSelectionDelta = new
	// SelectionDelta(EIDType.PATHWAY_VERTEX, EIDType.DAVID);
	//
	// int iDavidID = 0;
	//
	// for (SelectionDeltaItem item : selectionDelta) {
	// if (item.getSelectionType() != ESelectionType.MOUSE_OVER
	// && item.getSelectionType() != ESelectionType.SELECTION) {
	// continue;
	// }
	//
	// iDavidID = item.getPrimaryID();
	//
	// System.out.println("Cell component: "
	// +
	// GeneralManager.get().getIDMappingManager().getID(EMappingType.DAVID_2_CELL_COMPONENT,
	// iDavidID));
	// }
	// //
	// // iPathwayVertexGraphItemID = generalManager.getPathwayItemManager()
	// // .getPathwayVertexGraphItemIdByDavidId(iDavidID);
	// //
	// // // Ignore David IDs that do not exist in any pathway
	// // if (iPathwayVertexGraphItemID == -1)
	// // {
	// // continue;
	// // }
	// //
	// // // Convert DAVID ID to pathway graph item representation ID
	// // for (IGraphItem tmpGraphItemRep :
	// // generalManager.getPathwayItemManager().getItem(
	// // iPathwayVertexGraphItemID).getAllItemsByProp(
	// // EGraphItemProperty.ALIAS_CHILD))
	// // {
	// // if
	// // (!pathwayManager.getItem(iPathwayID).containsItem(tmpGraphItemRep))
	// // continue;
	// //
	// // newSelectionDelta.addSelection(tmpGraphItemRep.getId(), item
	// // .getSelectionType(), iDavidID);
	// // }
	// // }
	// //
	// return newSelectionDelta;
	// }

	@Override
	protected void handlePickingEvents(EPickingType ePickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick) {
		if (detailLevel == EDetailLevel.VERY_LOW) {
			return;
		}

		switch (ePickingType) {

		}
	}

	@Override
	public String getShortInfo() {
		// PathwayGraph pathway =
		// (generalManager.getPathwayManager().getItem(iPathwayID));
		//		
		// return pathway.getTitle() + " (" +pathway.getType().getName() + ")";

		return null;
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

		return null;
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
		SerializedDummyView serializedForm = new SerializedDummyView(dataDomain);
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

}