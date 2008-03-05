package org.geneview.core.view.opengl.util;

import gleem.linalg.Vec3f;

import java.util.EnumMap;

import javax.media.opengl.GL;

import org.geneview.core.data.GeneralRenderStyle;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.view.EPickingType;
import org.geneview.core.manager.view.PickingManager;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;

/**
 * 
 * @author Alexander Lex
 * @author Marc Streit
 *
 */

public class GLToolboxRenderer 
{	
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
	
	EnumMap<EIconTextures, Texture> mapIconTextures;
	
	protected GLIconTextureManager iconTextureManager;
	protected GeneralRenderStyle renderStyle;
	
	
	/**
	 * Constructor
	 * 
	 * @param vecLeftPoint is the bottom left point if bRenderLeftToRight
	 * 			is true, else the top left point
	 * @param layer 
	 * @param bRenderLeftToRight true if it should be rendered left to right,
	 * 			false if top to bottom
	 */
	public GLToolboxRenderer(final GL gl,
			final IGeneralManager generalManager,
			final int iContainingViewID,
			final Vec3f vecLeftPoint,			
			final boolean bRenderLeftToRight,
			final GeneralRenderStyle renderStyle)
	{
		this.generalManager = generalManager;
		pickingManager = generalManager.getSingelton().getViewGLCanvasManager().getPickingManager();
		this.iContainingViewID = iContainingViewID;
		this.vecLeftPoint = vecLeftPoint;

		this.bRenderLeftToRight = bRenderLeftToRight;
		this.renderStyle = renderStyle;
		
		this.layer = null;
		this.iRemoteViewID = -1;
		iconTextureManager = new GLIconTextureManager(gl);
	}
	
	
	public GLToolboxRenderer(final GL gl,
			final IGeneralManager generalManager,
			final int iContainingViewID,
			final int iRemoteViewID,
			final Vec3f vecLeftPoint,			
			final JukeboxHierarchyLayer layer,
			final boolean bRenderLeftToRight,
			final GeneralRenderStyle renderStyle)
	{
		
		this(gl, generalManager, iContainingViewID, 
				vecLeftPoint, bRenderLeftToRight, renderStyle);
		
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
			addIcon(gl, iRemoteViewID, EPickingType.BUCKET_ICON_SELECTION,
					iContainingViewID, EIconTextures.ARROW_LEFT);
		}
		fOverallRenderLength = fRenderLenght;
		fRenderLenght = 0;
		
	}
	
	protected void addIcon(final GL gl, 
			int iContainingViewID, 
			EPickingType ePickingType, 
			int iIconID,
			EIconTextures eIconTexture)
	{		
		
		
		Texture tempTexture = iconTextureManager.getIconTexture(eIconTexture);
		tempTexture.enable();
		tempTexture.bind();
		
		TextureCoords texCoords = tempTexture.getImageTexCoords();
		
		gl.glColor4f(1, 1, 1, 1);
		gl.glPushName(pickingManager.getPickingID(iContainingViewID, ePickingType, iIconID));	
		gl.glBegin(GL.GL_POLYGON);
		if(bRenderLeftToRight)
		{
			gl.glTexCoord2f(texCoords.left(), texCoords.bottom()); 
			gl.glVertex3f(fRenderLenght + vecLeftPoint.x(), vecLeftPoint.y(), vecLeftPoint.z());
			gl.glTexCoord2f(texCoords.right(), texCoords.bottom()); 
			gl.glVertex3f(fRenderLenght + vecLeftPoint.x() + renderStyle.getButtonWidht(), vecLeftPoint.y(), vecLeftPoint.z());
			gl.glTexCoord2f(texCoords.right(), texCoords.top()); 
			gl.glVertex3f(fRenderLenght + vecLeftPoint.x() + renderStyle.getButtonWidht(), vecLeftPoint.y() + renderStyle.getButtonWidht(), vecLeftPoint.z());
			gl.glTexCoord2f(texCoords.left(), texCoords.top()); 
			gl.glVertex3f(fRenderLenght + vecLeftPoint.x(), vecLeftPoint.y() + renderStyle.getButtonWidht(), vecLeftPoint.z());		
		}
		else
		{
			gl.glVertex3f(vecLeftPoint.x(), fRenderLenght + vecLeftPoint.y(), vecLeftPoint.z());
			gl.glVertex3f(vecLeftPoint.x() + renderStyle.getButtonWidht(), fRenderLenght + vecLeftPoint.y(), vecLeftPoint.z());
			gl.glVertex3f(vecLeftPoint.x() + renderStyle.getButtonWidht(), fRenderLenght + vecLeftPoint.y() + renderStyle.getButtonWidht(), vecLeftPoint.z());
			gl.glVertex3f(vecLeftPoint.x(), fRenderLenght + vecLeftPoint.y() + renderStyle.getButtonWidht(), vecLeftPoint.z());	
		
		}
		gl.glEnd();	
		gl.glPopName();
		tempTexture.disable();
		fRenderLenght = fRenderLenght + renderStyle.getButtonWidht() + renderStyle.getButtonSpacing();
	}
	

}
