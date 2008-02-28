package org.geneview.core.view.opengl.canvas.parcoords;

import gleem.linalg.Vec3f;
import gleem.linalg.Vec4f;

import javax.media.opengl.GL;

import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.view.EPickingType;
import org.geneview.core.view.opengl.util.GLToolboxRenderer;
import org.geneview.core.view.opengl.util.JukeboxHierarchyLayer;

/**
 * Extends GLToolboxRenderer by the icons relevant for the PCs
 * 
 * @author Alexander Lex
 *
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
	public GLParCoordsToolboxRenderer(final IGeneralManager generalManager,
			final int iContainingViewID,
			final Vec3f vecLeftPoint,			
			final boolean bRenderLeftToRight)
	{
		super(generalManager, iContainingViewID,
				vecLeftPoint, bRenderLeftToRight);
	}
	
	/**
	 * Constructor - use this one when you want remote and local icons in the same toolbox
	 * 
	 * @param generalManager
	 * @param iContainingViewID
	 * @param iRemoteViewID
	 * @param vecLeftPoint
	 * @param layer
	 * @param bRenderLeftToRight
	 */
	public GLParCoordsToolboxRenderer(final IGeneralManager generalManager,
			final int iContainingViewID,
			final int iRemoteViewID,
			final Vec3f vecLeftPoint,			
			final JukeboxHierarchyLayer layer,
			final boolean bRenderLeftToRight)
	{
		super(generalManager, iContainingViewID, 
				iRemoteViewID, vecLeftPoint, 
				layer, bRenderLeftToRight);
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
		addIcon(gl, iContainingViewID, EPickingType.PC_ICON_SELECTION, EIconIDs.TOGGLE_RENDER_ARRAY_AS_POLYLINE.ordinal(), new Vec4f(0, 0, 1, 1));
		addIcon(gl, iContainingViewID, EPickingType.PC_ICON_SELECTION, EIconIDs.TOGGLE_PREVENT_OCCLUSION.ordinal(), new Vec4f(0, 1, 0, 1));
		addIcon(gl, iContainingViewID, EPickingType.PC_ICON_SELECTION, EIconIDs.TOGGLE_RENDER_SELECTION.ordinal(), new Vec4f(0, 1, 1, 1));
		addIcon(gl, iContainingViewID, EPickingType.PC_ICON_SELECTION, EIconIDs.RESET_SELECTIONS.ordinal(), new Vec4f(1, 1, 0, 1));
		
		fOverallRenderLength = fRenderLenght;
		fRenderLenght = 0;
		
	}
	
	
}
