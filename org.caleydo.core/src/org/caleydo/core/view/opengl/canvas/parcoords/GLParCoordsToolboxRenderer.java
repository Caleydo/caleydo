package org.caleydo.core.view.opengl.canvas.parcoords;

import gleem.linalg.Vec3f;
import gleem.linalg.Vec4f;
import javax.media.opengl.GL;
import org.caleydo.core.data.view.rep.renderstyle.GeneralRenderStyle;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.view.EPickingType;
import org.caleydo.core.view.opengl.util.EIconTextures;
import org.caleydo.core.view.opengl.util.GLToolboxRenderer;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteHierarchyLayer;

/**
 * Extends GLToolboxRenderer by the icons relevant for the PCs
 * 
 * @author Alexander Lex
 */

public class GLParCoordsToolboxRenderer
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
	public GLParCoordsToolboxRenderer(final GL gl, final IGeneralManager generalManager,
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
	public GLParCoordsToolboxRenderer(final GL gl, final IGeneralManager generalManager,
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
		addIcon(gl, iContainingViewID, EPickingType.PC_ICON_SELECTION,
				EIconIDs.TOGGLE_RENDER_ARRAY_AS_POLYLINE.ordinal(),
				EIconTextures.POLYLINE_TO_AXIS);
		addIcon(gl, iContainingViewID, EPickingType.PC_ICON_SELECTION,
				EIconIDs.TOGGLE_PREVENT_OCCLUSION.ordinal(), EIconTextures.PREVENT_OCCLUSION);
		addIcon(gl, iContainingViewID, EPickingType.PC_ICON_SELECTION,
				EIconIDs.TOGGLE_RENDER_SELECTION.ordinal(), EIconTextures.RENDER_SELECTION);
		addIcon(gl, iContainingViewID, EPickingType.PC_ICON_SELECTION,
				EIconIDs.RESET_SELECTIONS.ordinal(), EIconTextures.RESET_SELECTIONS);
		addIcon(gl, iContainingViewID, EPickingType.PC_ICON_SELECTION,
				EIconIDs.SAVE_SELECTIONS.ordinal(), EIconTextures.SAVE_SELECTIONS);
		addIcon(gl, iContainingViewID, EPickingType.PC_ICON_SELECTION,
				EIconIDs.ANGULAR_BRUSHING.ordinal(), EIconTextures.ANGULAR_BRUSHING);

		fOverallRenderLength = fRenderLenght;
		fRenderLenght = 0;

	}

}
