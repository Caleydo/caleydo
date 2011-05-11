package org.caleydo.view.bookmark;

import javax.media.opengl.GL2;

import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;
import org.caleydo.core.view.opengl.util.GLHelperFunctions;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;

/**
 * A bookmark for a gene. The id used here is DAVID
 * 
 * @author Alexander Lex
 */
class ContentBookmark extends ABookmark {

	private ElementLayout layout;

	/**
	 * Constructor taking a textRenderer
	 * 
	 * @param textRenderer
	 * @param id
	 */
	public ContentBookmark(GLBookmarkView manager,
			ContentBookmarkContainer parentContainer, IDType idType, Integer id,
			CaleydoTextRenderer textRenderer) {
		super(manager, parentContainer, idType, textRenderer);
		this.id = id;

		layout = new ElementLayout("ContentBookmark");
	
		layout.setRatioSizeX(1);

		layout.setRenderer(this);
		layout.setPixelGLConverter(manager.getParentGLCanvas().getPixelGLConverter());
		// float height = (float) textRenderer.getBounds("Text").getHeight();

		layout.setPixelSizeY(20);

	}

	@Override
	public ElementLayout getLayout() {
		return layout;
	}

	@Override
	public void render(GL2 gl) {

		super.render(gl);

		String label = manager.getDataDomain().getContentLabel(idType, id);
		textRenderer.setColor(0, 0, 0, 1);
		textRenderer.renderTextInBounds(gl, label, 0 + xSpacing, 0 + ySpacing, 0, x
				- xSpacing, y - 2 * ySpacing);

	
	}

}
