package org.geneview.core.view.opengl.util;

import java.util.ArrayList;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL;

import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.view.Pick;
import org.geneview.core.manager.view.PickingManager;
import org.geneview.core.view.opengl.canvas.AGLCanvasUser;
import org.geneview.core.view.opengl.canvas.parcoords.GLCanvasParCoords3D;

/**
 * 
 * @author Alexander Lex
 * @author Marc Streit
 *
 */

public class GLToolboxRenderer 
{
	
	protected final static float ELEMENT_LENGTH = 0.1f;
	//TODO: centralize modes
	protected final static int ICON_SELECTION = 7;
	
	protected Vec3f vecLeftPoint;
	protected JukeboxHierarchyLayer layer;
	protected boolean bIsCalledLocally;
	protected boolean bRenderLeftToRight;
	
	protected IGeneralManager generalManager;
	protected PickingManager pickingManager; 
	protected int iContainingViewID;
	
	
	/**
	 * Constructor
	 * 
	 * @param vecLeftPoint is the bottom left point if bRenderLeftToRight
	 * 			is true, else the top left point
	 * @param layer 
	 * @param bIsCalledLocally true if called locally	  
	 * @param bRenderLeftToRight true if it should be rendered left to right,
	 * 			false if top to bottom
	 */
	public GLToolboxRenderer(final IGeneralManager generalManager,
			final int containingViewID,
			final Vec3f vecLeftPoint,			
			final JukeboxHierarchyLayer layer,
			final boolean bRenderLeftToRight)
	{
		this.generalManager = generalManager;
		pickingManager = generalManager.getSingelton().getViewGLCanvasManager().getPickingManager();
		this.iContainingViewID = containingViewID;
		this.vecLeftPoint = vecLeftPoint;
		this.layer = layer;
		this.bRenderLeftToRight = bRenderLeftToRight;
	}
	/**
	 * 	
	 * @param gl the gl of the context, remote gl when called remote
	 */
	public void render(final GL gl)
	{
		//pickingManager.handlePicking(iContainingViewID, gl, false);
		// Icon one
		gl.glColor3f(1, 0, 0);
		gl.glPushName(pickingManager.getPickingID(iContainingViewID, ICON_SELECTION, 1));	
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(vecLeftPoint.x(), vecLeftPoint.y(), vecLeftPoint.z());
		gl.glVertex3f(vecLeftPoint.x() + ELEMENT_LENGTH, vecLeftPoint.y(), vecLeftPoint.z());
		gl.glVertex3f(vecLeftPoint.x() + ELEMENT_LENGTH, vecLeftPoint.y() + ELEMENT_LENGTH, vecLeftPoint.z());
		gl.glVertex3f(vecLeftPoint.x(), vecLeftPoint.y() + ELEMENT_LENGTH, vecLeftPoint.z());		
		gl.glEnd();	
		gl.glPopName();
		
		
//		checkForHits();
	}
	
	public void checkForHits()
	{
		ArrayList<Pick> alHits = null;		
		
		alHits = pickingManager.getHits(iContainingViewID, ICON_SELECTION);		
		if(alHits != null)
		{			
			if (alHits.size() != 0 )
			{
//				boolean bSelectionCleared = false;
//				boolean bMouseOverCleared = false;					
				
				for (int iCount = 0; iCount < alHits.size(); iCount++)
				{
					Pick tempPick = alHits.get(iCount);
					int iPickingID = tempPick.getPickingID();
					int iExternalID = pickingManager.getExternalIDFromPickingID(iContainingViewID, iPickingID);
					//alNormalPolylines.remove(new Integer(iExternalID));
						
					switch (tempPick.getPickingMode())
					{						
						case CLICKED:	
							((GLCanvasParCoords3D)generalManager.getSingelton().getViewGLCanvasManager().getItem(iContainingViewID)).renderArrayAsPolyline(true);
						
					}
				}
			}
		}
		
	}
	
	
	
	

}
