package org.caleydo.core.view.opengl.canvas.bookmarking;

import javax.media.opengl.GL;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;

import com.sun.opengl.util.j2d.TextRenderer;

/**
 * A bookmark for a gene. The id used here is DAVID
 * 
 * @author Alexander Lex
 */
class ExperimentBookmark
	extends ABookmark {

	/**
	 * Constructor taking a textRenderer
	 * 
	 * @param textRenderer
	 * @param davidID
	 */
	public ExperimentBookmark(TextRenderer textRenderer, Integer experimentIndex) {
		super(textRenderer);
		this.id = experimentIndex;
		dimensions.setHeight(0.1f);
	}

	@Override
	public void render(GL gl) {
		String sContent = GeneralManager.get().getUseCase().getSet().get(id).getLabel();
		GeneralManager.get().getIDMappingManager().getID(EIDType.DAVID, EIDType.GENE_SYMBOL, id);

		float yOrigin = dimensions.getYOrigin() - 0.08f;
		RenderingHelpers.renderText(gl, textRenderer, sContent, dimensions.getXOrigin()
			+ BookmarkRenderStyle.SIDE_SPACING * 2, yOrigin, GeneralRenderStyle.SMALL_FONT_SCALING_FACTOR);

	}

}
