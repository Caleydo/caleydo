package org.caleydo.view.bookmark;

import javax.media.opengl.GL2;

import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;
import org.caleydo.core.view.opengl.util.text.MinSizeTextRenderer;

/**
 * A bookmark for a gene. The id used here is DAVID
 * 
 * @author Alexander Lex
 */
class ContentBookmark extends ABookmark {

	private ElementLayout layoutParameters;

	/**
	 * Constructor taking a textRenderer
	 * 
	 * @param textRenderer
	 * @param id
	 */
	public ContentBookmark(GLBookmarkView manager,
			ContentBookmarkContainer parentContainer, IDType idType, Integer id,
			MinSizeTextRenderer textRenderer) {
		super(manager, parentContainer, idType, textRenderer);
		this.id = id;

		layoutParameters = new ElementLayout();
		layoutParameters.setRatioSizeX(1);
	

		layoutParameters.setRenderer(this);
		layoutParameters.setPixelGLConverter(manager.getParentGLCanvas().getPixelGLConverter());
//		float height = (float) textRenderer.getBounds("Text").getHeight();

		layoutParameters.setPixelSizeY(20);

	}

	@Override
	public ElementLayout getLayout() {
		return layoutParameters;
	}

	@Override
	public void render(GL2 gl) {

		super.render(gl);
		// String sContent = GeneralManager.get().getIDMappingManager().getID(
		// manager.getDataDomain().getPrimaryContentMappingType(),
		// EIDType.GENE_SYMBOL, id);
		//
		// float yOrigin = bookmarkDimensions.getYOrigin() - 0.08f;
		String sContent = manager.getDataDomain().getContentLabel(idType, id);
		// RenderingHelpers.renderText(gl, textRenderer, sContent,
		// bookmarkDimensions.getXOrigin() + BookmarkRenderStyle.SIDE_SPACING,
		// yOrigin, GeneralRenderStyle.SMALL_FONT_SCALING_FACTOR);


		float height = (layoutParameters.getSizeScaledY() - (float)textRenderer.getBounds("Bla").getHeight())/2;
		
		
		
		RenderingHelpers.renderText(gl, textRenderer, sContent,
				BookmarkRenderStyle.SIDE_SPACING * 2, height,
				GeneralRenderStyle.SMALL_FONT_SCALING_FACTOR);

	}

}
