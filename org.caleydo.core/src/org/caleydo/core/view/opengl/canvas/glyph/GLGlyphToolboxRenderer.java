package org.caleydo.core.view.opengl.canvas.glyph;

import gleem.linalg.Vec3f;
import javax.media.opengl.GL;
import org.caleydo.core.data.view.rep.renderstyle.GeneralRenderStyle;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.view.opengl.util.EIconTextures;
import org.caleydo.core.view.opengl.util.GLToolboxRenderer;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteHierarchyLayer;

/**
 * Extends GLToolboxRenderer by the icons relevant for the PCs
 * 
 * @author Alexander Lex
 */

public class GLGlyphToolboxRenderer
	extends GLToolboxRenderer
{

	/**
	 * Constructor - use this one when you only work locally
	 * 
	 * @param generalManager
	 * @param iContainingViewID
	 * @param vecLeftPoint
	 * @param bRenderLeftToRight
	 */
	public GLGlyphToolboxRenderer(final GL gl, final IGeneralManager generalManager,
			final int iContainingViewID, final Vec3f vecLeftPoint,
			final boolean bRenderLeftToRight, final GeneralRenderStyle renderStyle)
	{

		super(gl, generalManager, iContainingViewID, vecLeftPoint, bRenderLeftToRight,
				renderStyle);
	}

	/**
	 * Constructor - use this one when you want remote and local icons in the
	 * same toolbox
	 * 
	 * @param generalManager
	 * @param iContainingViewID
	 * @param iRemoteViewID
	 * @param vecLeftPoint
	 * @param layer
	 * @param bRenderLeftToRight
	 */
	public GLGlyphToolboxRenderer(final GL gl, final IGeneralManager generalManager,
			final int iContainingViewID, final int iRemoteViewID, final Vec3f vecLeftPoint,
			final RemoteHierarchyLayer layer, final boolean bRenderLeftToRight,
			final GeneralRenderStyle renderStyle)
	{

		super(gl, generalManager, iContainingViewID, iRemoteViewID, vecLeftPoint, layer,
				bRenderLeftToRight, renderStyle);
	}

	/**
	 * Does the actual rendering
	 * 
	 * @param gl
	 */

	public void render(GL gl)
	{

		super.render(gl);
		fRenderLenght = fOverallRenderLength;
		addIcon(gl, iContainingViewID, EPickingType.PC_ICON_SELECTION, EIconIDs.DISPLAY_CIRCLE
				.ordinal(), EIconTextures.GLYPH_SORT_CIRCLE);

		addIcon(gl, iContainingViewID, EPickingType.PC_ICON_SELECTION,
				EIconIDs.DISPLAY_RECTANGLE.ordinal(), EIconTextures.GLYPH_SORT_RECTANGLE);

		addIcon(gl, iContainingViewID, EPickingType.PC_ICON_SELECTION, EIconIDs.DISPLAY_RANDOM
				.ordinal(), EIconTextures.GLYPH_SORT_RANDOM);

		addIcon(gl, iContainingViewID, EPickingType.PC_ICON_SELECTION,
				EIconIDs.DISPLAY_SCATTERPLOT.ordinal(), EIconTextures.GLYPH_SORT_RANDOM);

		fOverallRenderLength = fRenderLenght;
		fRenderLenght = 0;
	}

}
