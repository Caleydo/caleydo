package org.caleydo.view.bookmark;

import javax.media.opengl.GL2;

import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;

/**
 * A bookmark for a gene. The id used here is DAVID
 * 
 * @author Alexander Lex
 */
class StorageBookmark extends ABookmark {

	ElementLayout layout;

	/**
	 * Constructor taking a textRenderer
	 * 
	 * @param textRenderer
	 * @param davidID
	 */
	public StorageBookmark(GLBookmarkView manager,
			StorageBookmarkContainer partentContainer, IDType idType,
			Integer experimentIndex, CaleydoTextRenderer textRenderer) {
		super(manager, partentContainer, idType, textRenderer);
		this.id = experimentIndex;

		layout = new ElementLayout();
		layout.setRatioSizeX(1);
		layout.setRenderer(this);
		layout.setPixelGLConverter(manager.getParentGLCanvas().getPixelGLConverter());
		layout.setPixelSizeY(20);

	}

	@Override
	public ElementLayout getLayout() {
		return layout;
	}

	@Override
	public void render(GL2 gl) {

		super.render(gl);
		String label = manager.getDataDomain().getStorageLabel(id);
		gl.glPushName(manager.getBookmarkPickingIDManager().getPickingID(parentContainer,
				id));
		manager.getTextRenderer().renderTextInBounds(gl, label, 0 + xSpacing,
				0 + ySpacing, 0, x - xSpacing, y - 2 * ySpacing);
		gl.glPopName();

	}

}
