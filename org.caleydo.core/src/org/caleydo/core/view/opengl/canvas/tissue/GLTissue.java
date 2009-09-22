package org.caleydo.core.view.opengl.canvas.tissue;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.serialize.SerializedDummyView;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;

/**
 * Single OpenGL pathway view
 * 
 * @author Marc Streit
 */
public class GLTissue
	extends AGLEventListener {

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
	public void initRemote(GL gl, AGLEventListener glParentView, GLMouseListener glMouseListener,
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

		// GLHelperFunctions.drawViewFrustum(gl, viewFrustum);

		textureManager.renderTexture(gl, EIconTextures.TISSUE_SAMPLE, new Vec3f(0, 0, 0), new Vec3f(8, 0, 0),
			new Vec3f(8, 8, 0), new Vec3f(0, 8, 0), 1, 1, 1, 1);

	}

	@Override
	protected void handlePickingEvents(EPickingType ePickingType, EPickingMode pickingMode, int iExternalID,
		Pick pick) {
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

}