package org.caleydo.core.view.opengl.canvas.pathway;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

import javax.media.opengl.GL;

import org.caleydo.core.data.graph.core.PathwayGraph;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.ILoggerManager.LoggerType;
import org.caleydo.core.manager.data.pathway.EPathwayDatabaseType;
import org.caleydo.core.manager.view.EPickingType;
import org.caleydo.core.view.opengl.canvas.AGLCanvasUser;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;
import com.sun.opengl.util.texture.TextureIO;


/**
 * @author Marc Streit
 *
 */
public class GLPathwayTextureManager {
	
	private IGeneralManager generalManager;
	
	private HashMap<Integer, Texture> hashPathwayIdToTexture;
	
	/**
	 * Constructor.
	 */
	public GLPathwayTextureManager(final IGeneralManager generalManager) {
		
		this.generalManager = generalManager;
		
		hashPathwayIdToTexture = new HashMap<Integer, Texture>();

	}
	
	public Texture loadPathwayTextureById(int iPathwayId) {
			
		if (hashPathwayIdToTexture.containsKey(iPathwayId))
			return hashPathwayIdToTexture.get(iPathwayId);
		
		Texture refPathwayTexture = null;

		String sPathwayTexturePath = ((PathwayGraph)generalManager
				.getPathwayManager().getItem(iPathwayId)).getImageLink();

		sPathwayTexturePath = sPathwayTexturePath.substring(
				sPathwayTexturePath.lastIndexOf('/') + 1, 
				sPathwayTexturePath.length());
		
		EPathwayDatabaseType type = ((PathwayGraph)generalManager
				.getPathwayManager().getItem(iPathwayId)).getType();
		
		sPathwayTexturePath = generalManager.getPathwayManager()
				.getPathwayDatabaseByType(type).getImagePath() + sPathwayTexturePath;	
		
		try
		{			
			if (this.getClass().getClassLoader().getResource(sPathwayTexturePath) != null)
			{
				refPathwayTexture = TextureIO.newTexture(TextureIO.newTextureData(
						this.getClass().getClassLoader().getResourceAsStream(sPathwayTexturePath), false, "GIF"));
			}
			else
			{
				refPathwayTexture = TextureIO.newTexture(TextureIO.newTextureData(
						new File(sPathwayTexturePath), true, "GIF"));			
			}
		
//			refPathwayTexture.setTexParameteri(GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR); 
//			refPathwayTexture.setTexParameteri(GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
			
			hashPathwayIdToTexture.put(iPathwayId, refPathwayTexture);
			
			generalManager.logMsg(
					this.getClass().getSimpleName() + 
					": loadPathwayTexture(): Loaded Texture for Pathway with ID: " +iPathwayId,
					LoggerType.VERBOSE );
			
			return refPathwayTexture;
			
		} catch (Exception e)
		{
			System.out.println("Error loading texture " + sPathwayTexturePath);
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void renderPathway(final GL gl,
			final AGLCanvasUser containingView,
			final int iPathwayId, 
			final float fTextureTransparency,
			final boolean bHighlight) {

		Texture refTmpPathwayTexture = loadPathwayTextureById(iPathwayId);
		
		refTmpPathwayTexture.enable();
		refTmpPathwayTexture.bind();

		if (bHighlight)
			gl.glColor4f(1f, 0.85f, 0.85f, fTextureTransparency);
		else
			gl.glColor4f(1f, 1f, 1f, fTextureTransparency);
		
		TextureCoords texCoords = refTmpPathwayTexture.getImageTexCoords();

		float fTextureWidth = GLPathwayManager.SCALING_FACTOR_X * 
			((PathwayGraph)generalManager.getPathwayManager().getItem(iPathwayId)).getWidth();
		float fTextureHeight = GLPathwayManager.SCALING_FACTOR_Y * 
			((PathwayGraph)generalManager.getPathwayManager().getItem(iPathwayId)).getHeight();
		
//		gl.glPushName(generalManager.getSingelton().getViewGLCanvasManager().getPickingManager()
//				.getPickingID(containingView.getId(), EPickingType.PATHWAY_TEXTURE_SELECTION, iPathwayId));
				
		gl.glBegin(GL.GL_QUADS);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom()); 
		gl.glVertex3f(0.0f, 0.0f, 0.0f);			  
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom()); 
		gl.glVertex3f(fTextureWidth, 0.0f, 0.0f);			 
		gl.glTexCoord2f(texCoords.right(), texCoords.top()); 
		gl.glVertex3f(fTextureWidth, fTextureHeight, 0.0f);
		gl.glTexCoord2f(texCoords.left(), texCoords.top()); 
		gl.glVertex3f(0.0f, fTextureHeight, 0.0f);
		gl.glEnd();	

		refTmpPathwayTexture.disable();
		
		if (bHighlight)
		{
			gl.glColor4f(1, 0, 0, 1);
			gl.glLineWidth(3);			
		}
		else
		{
			gl.glColor4f(0.5f, 0.5f, 0.5f, 1.0f);
			gl.glLineWidth(1);	
		}
		
		gl.glBegin(GL.GL_LINE_STRIP); 
		gl.glVertex3f(0.0f, 0.0f, 0.0f);; 
		gl.glVertex3f(fTextureWidth, 0.0f, 0.0f);
		gl.glVertex3f(fTextureWidth, fTextureHeight, 0.0f);
		gl.glVertex3f(0.0f, fTextureHeight, 0.0f);
		gl.glVertex3f(0.0f, 0.0f, 0.0f);; 				
		gl.glEnd();
		
//		gl.glPopName();
	}
	
	/**
	 * Method supports lazy loading of pathway textures
	 * if they are not present at that time.
	 * 
	 * @param iPathwayId
	 * @return Pathway texture
	 */
	public Texture getTextureByPathwayId(final int iPathwayId) {
		
		if (hashPathwayIdToTexture.containsKey(iPathwayId))
			return hashPathwayIdToTexture.get(iPathwayId);
		
		loadPathwayTextureById(iPathwayId);
		return hashPathwayIdToTexture.get(iPathwayId);
	}
	
	public void unloadUnusedTextures(LinkedList<Integer> iLLVisiblePathways) {

		int iTmpPathwayId = 0;
		Integer[] iArPathwayId = hashPathwayIdToTexture.keySet()
			.toArray(new Integer[hashPathwayIdToTexture.size()]);
		
		for (int iPathwayIndex = 0; iPathwayIndex < iArPathwayId.length; iPathwayIndex++)
		{
			iTmpPathwayId = iArPathwayId[iPathwayIndex];
			
			if (!iLLVisiblePathways.contains(iTmpPathwayId))
			{
				// Remove and dispose texture
				hashPathwayIdToTexture.remove(iTmpPathwayId).dispose();

				generalManager.logMsg(
						this.getClass().getSimpleName() 
						+": unloadUnusedTextures(): Unloading pathway texture with ID " + iTmpPathwayId,
						LoggerType.VERBOSE);
			}
		}
	}
}
