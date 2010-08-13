package org.caleydo.view.bookmarking;

import javax.media.opengl.GL;

import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;

import com.sun.opengl.util.j2d.TextRenderer;

/**
 * A bookmark for a gene. The id used here is DAVID
 * 
 * @author Alexander Lex
 */
class StorageBookmark extends ABookmark {

	/**
	 * Constructor taking a textRenderer
	 * 
	 * @param textRenderer
	 * @param davidID
	 */
	public StorageBookmark(GLBookmarkManager manager, IDType idType,
			Integer experimentIndex, TextRenderer textRenderer) {
		super(manager, idType, textRenderer);
		this.id = experimentIndex;
		dimensions.setHeight(0.1f);
	}

	@Override
	public void render(GL gl) {
		String sContent = manager.getDataDomain().getStorageLabel(id);

		// ((ISetBasedDataDomain) DataDomainManager.getInstance()
		// .getDataDomain("org.caleydo.datadomain.genetic")).getSet().get(id).getLabel();
		//
		// GeneralManager.get().getIDMappingManager().getID(EIDType.DAVID,
		// EIDType.GENE_SYMBOL, id);
		//
		float yOrigin = dimensions.getYOrigin() - 0.08f;
		RenderingHelpers.renderText(gl, textRenderer, sContent, dimensions.getXOrigin()
				+ BookmarkRenderStyle.SIDE_SPACING * 2, yOrigin,
				GeneralRenderStyle.SMALL_FONT_SCALING_FACTOR);

	}

}
