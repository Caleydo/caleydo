package org.geneview.core.view.opengl.util;

import gleem.linalg.Vec3f;
import gleem.linalg.Vec4f;

import javax.media.opengl.GL;

import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.view.EPickingType;
import org.geneview.core.manager.view.PickingManager;

/**
 * 
 * @author Alexander Lex
 * @author Marc Streit
 *
 */

public class GLToolboxRenderer 
{
	protected final static float ELEMENT_LENGTH = 0.1f;
	protected final static float ELEMENT_SPACING = 0.02f;
	
	protected Vec3f vecLeftPoint;
	protected JukeboxHierarchyLayer layer;
	protected boolean bIsCalledLocally;
	protected boolean bRenderLeftToRight;
	
	protected IGeneralManager generalManager;
	protected PickingManager pickingManager; 
	protected int iRemoteViewID;
	protected int iContainingViewID;
	
	protected float fRenderLenght;
	protected float fOverallRenderLength;
	
	
	/**
	 * Constructor
	 * 
	 * @param vecLeftPoint is the bottom left point if bRenderLeftToRight
	 * 			is true, else the top left point
	 * @param layer 
	 * @param bRenderLeftToRight true if it should be rendered left to right,
	 * 			false if top to bottom
	 */
	public GLToolboxRenderer(final IGeneralManager generalManager,
			final int iContainingViewID,
			final Vec3f vecLeftPoint,			
			final boolean bRenderLeftToRight)
	{
		this.generalManager = generalManager;
		pickingManager = generalManager.getSingelton().getViewGLCanvasManager().getPickingManager();
		this.iContainingViewID = iContainingViewID;
		this.vecLeftPoint = vecLeftPoint;

		this.bRenderLeftToRight = bRenderLeftToRight;
		
		this.layer = null;
		this.iRemoteViewID = -1;
	}
	
	
	public GLToolboxRenderer(final IGeneralManager generalManager,
			final int iContainingViewID,
			final int iRemoteViewID,
			final Vec3f vecLeftPoint,			
			final JukeboxHierarchyLayer layer,
			final boolean bRenderLeftToRight)
	{
		
		this(generalManager, iContainingViewID, 
				vecLeftPoint, bRenderLeftToRight);
		
		this.layer = layer;
		this.iRemoteViewID = iRemoteViewID;
	}
	/**
	 * 	
	 * @param gl the gl of the context, remote gl when called remote
	 */
	public void render(final GL gl)
	{
		if(layer != null)
		{
			addIcon(gl, iRemoteViewID, EPickingType.BUCKET_ICON_SELECTION, iContainingViewID, new Vec4f(1, 0, 0, 1));
		}
		fOverallRenderLength = fRenderLenght;
		fRenderLenght = 0;
		
	}
	
	protected void addIcon(final GL gl, 
			int iContainingViewID, 
			EPickingType ePickingType, 
			int iIconID, Vec4f vecColor)
	{		
		gl.glColor4f(vecColor.x(), vecColor.y(), vecColor.z(), vecColor.w());
		gl.glPushName(pickingManager.getPickingID(iContainingViewID, ePickingType, iIconID));	
		gl.glBegin(GL.GL_POLYGON);
		if(bRenderLeftToRight)
		{
			gl.glVertex3f(fRenderLenght + vecLeftPoint.x(), vecLeftPoint.y(), vecLeftPoint.z());
			gl.glVertex3f(fRenderLenght + vecLeftPoint.x() + ELEMENT_LENGTH, vecLeftPoint.y(), vecLeftPoint.z());
			gl.glVertex3f(fRenderLenght + vecLeftPoint.x() + ELEMENT_LENGTH, vecLeftPoint.y() + ELEMENT_LENGTH, vecLeftPoint.z());
			gl.glVertex3f(fRenderLenght + vecLeftPoint.x(), vecLeftPoint.y() + ELEMENT_LENGTH, vecLeftPoint.z());		
		}
		else
		{
			gl.glVertex3f(vecLeftPoint.x(), fRenderLenght + vecLeftPoint.y(), vecLeftPoint.z());
			gl.glVertex3f(vecLeftPoint.x() + ELEMENT_LENGTH, fRenderLenght + vecLeftPoint.y(), vecLeftPoint.z());
			gl.glVertex3f(vecLeftPoint.x() + ELEMENT_LENGTH, fRenderLenght + vecLeftPoint.y() + ELEMENT_LENGTH, vecLeftPoint.z());
			gl.glVertex3f(vecLeftPoint.x(), fRenderLenght + vecLeftPoint.y() + ELEMENT_LENGTH, vecLeftPoint.z());	
		
		}
		gl.glEnd();	
		gl.glPopName();
		fRenderLenght = fRenderLenght + ELEMENT_LENGTH + ELEMENT_SPACING;
	}
}
