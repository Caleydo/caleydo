package org.caleydo.view.visbricks.brick;

import javax.media.opengl.GL2;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.EVAOperation;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.TemplateRenderer;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;

/**
 * Individual Brick for VisBricks
 * 
 * @author Alexander Lex
 * 
 */
public class GLBrick extends AGLView {

	private TemplateRenderer templateRenderer;
	private BrickLayoutTemplate brickLayout;
	
	private ElementLayout wrappingLayout;

	private int baseDisplayListIndex = 1;
	private boolean isBaseDisplayListDirty = true;

	public GLBrick(GLCaleydoCanvas glCanvas, ViewFrustum viewFrustum) {
		super(glCanvas, viewFrustum, true);
		templateRenderer = new TemplateRenderer(viewFrustum);
		brickLayout = new BrickLayoutTemplate();

		brickLayout.setPixelGLConverter(pixelGLConverter);

		templateRenderer.setTemplate(brickLayout);

	}
	
	public void setWrappingLayout(ElementLayout wrappingLayout) {
		this.wrappingLayout = wrappingLayout;
	}
	
	public ElementLayout getWrappingLayout() {
		return wrappingLayout;
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
		pixelGLConverter = ((AGLView) getRemoteRenderingGLCanvas()).getPixelGLConverter();
		// TODO Auto-generated method stub

	}

	@Override
	protected void initLocal(GL2 gl) {
		init(gl);

	}

	@Override
	public void initRemote(GL2 gl, AGLView glParentView, GLMouseListener glMouseListener,
			GLInfoAreaManager infoAreaManager) {
		init(gl);

	}

	@Override
	public void display(GL2 gl) {

//		GLHelperFunctions.drawViewFrustum(gl, viewFrustum);

		if (isBaseDisplayListDirty)
			buildBaseDisplayList(gl);

		gl.glCallList(baseDisplayListIndex);
	}

	@Override
	protected void displayLocal(GL2 gl) {
//		display(gl);
	}

	@Override
	public void displayRemote(GL2 gl) {
		display(gl);
	}

	private void buildBaseDisplayList(GL2 gl) {
		gl.glNewList(baseDisplayListIndex, GL2.GL_COMPILE);
		templateRenderer.updateLayout();
		templateRenderer.render(gl);
		gl.glEndList();
		isBaseDisplayListDirty = false;
	}

	@Override
	protected void handlePickingEvents(EPickingType pickingType,
			EPickingMode pickingMode, int pickingID, Pick pick) {
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

}
