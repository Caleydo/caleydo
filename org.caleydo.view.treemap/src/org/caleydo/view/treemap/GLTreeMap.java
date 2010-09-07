package org.caleydo.view.treemap;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.ISetBasedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.GLHelperFunctions;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;

/**
 * TODO
 * @author TODO
 *
 */
public class GLTreeMap extends AGLView implements ISetBasedView {

	public final static String VIEW_ID = "org.caleydo.view.treemap";
	
	private ASetBasedDataDomain dataDomain;
	
	public GLTreeMap(GLCaleydoCanvas glCanvas, ViewFrustum viewFrustum) {
		super(glCanvas, viewFrustum, true);
		
		viewType = GLTreeMap.VIEW_ID;
	}

	@Override
	public ASerializedView getSerializableRepresentation() {

		throw new IllegalStateException();
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
	protected void initLocal(GL gl) {
		throw new IllegalStateException();
	}

	@Override
	public void initRemote(GL gl, AGLView glParentView, GLMouseListener glMouseListener, GLInfoAreaManager infoAreaManager) {
		this.glMouseListener = glMouseListener;

		iGLDisplayListIndexRemote = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexRemote;
		init(gl);
	}

	@Override
	public void display(GL gl) {
		GLHelperFunctions.drawAxis(gl);
	}

	@Override
	protected void displayLocal(GL gl) {
		throw new IllegalStateException();
	}

	@Override
	public void displayRemote(GL gl) {
//		if (bIsDisplayListDirtyRemote) {
//			buildDisplayList(gl, iGLDisplayListIndexRemote);
//			bIsDisplayListDirtyRemote = false;
//		}
//		iGLDisplayListToCall = iGLDisplayListIndexRemote;
		display(gl);
	}

	@Override
	protected void handlePickingEvents(EPickingType ePickingType, EPickingMode ePickingMode, int externalPickingID, Pick pick) {
		// TODO Auto-generated method stub

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
		return dataDomain;
	}

	@Override
	public void setSet(ISet set) {
		throw new IllegalStateException("Should not be used");
	}

}
