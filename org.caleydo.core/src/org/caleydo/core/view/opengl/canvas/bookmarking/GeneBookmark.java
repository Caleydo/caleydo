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
 *
 */
class GeneBookmark
	extends ABookmark {

	
	/**
	 * Constructor taking a textRenderer
	 * 
	 * @param textRenderer
	 * @param davidID
	 */
	public GeneBookmark(TextRenderer textRenderer, Integer davidID) {
		super(textRenderer);
		this.id = davidID;
		dimensions.setHeight(0.1f);
	}

	@Override
	public void render(GL gl) {
		String sContent = GeneralManager.get().getIDMappingManager().getID(EIDType.DAVID, EIDType.GENE_SYMBOL, id);
		
		
		renderCaption(gl, sContent, dimensions.getXOrigin(), dimensions.getYOrigin() ,
			GeneralRenderStyle.SMALL_FONT_SCALING_FACTOR);

	}
	
}
