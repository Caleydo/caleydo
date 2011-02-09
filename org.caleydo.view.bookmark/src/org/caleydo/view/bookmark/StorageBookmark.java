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
			Integer experimentIndex, MinSizeTextRenderer textRenderer) {
		super(manager, partentContainer, idType, textRenderer);
		this.id = experimentIndex;

	
		layout = new ElementLayout();
		layout.setRatioSizeX(1);
		layout.setRenderer(this);
		layout.setPixelGLConverter(manager.getPixelGLConverter());
		layout.setPixelSizeY(20);

	}

	@Override
	public ElementLayout getLayout() {
		return layout;
	}

	@Override
	public void render(GL2 gl) {

		super.render(gl);
		String sContent = manager.getDataDomain().getStorageLabel(id);

		// ((ISetBasedDataDomain) DataDomainManager.getInstance()
		// .getDataDomain("org.caleydo.datadomain.genetic")).getSet().get(id).getLabel();
		//
		// GeneralManager.get().getIDMappingManager().getID(EIDType.DAVID,
		// EIDType.GENE_SYMBOL, id);
		//
		// float yOrigin = bookmarkDimensions.getYOrigin() - 0.08f;
		float height = (layout.getSizeScaledY() - (float)textRenderer.getBounds("Bla").getHeight())/2;
		
		
		
		RenderingHelpers.renderText(gl, textRenderer, sContent,
				BookmarkRenderStyle.SIDE_SPACING * 2, height,
				GeneralRenderStyle.SMALL_FONT_SCALING_FACTOR);

	}

}
