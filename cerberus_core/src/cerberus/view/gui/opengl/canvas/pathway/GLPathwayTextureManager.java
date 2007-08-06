package cerberus.view.gui.opengl.canvas.pathway;

import java.io.File;
import java.util.HashMap;

import javax.media.opengl.GL;

import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;
import com.sun.opengl.util.texture.TextureIO;


/**
 * @author Marc Streit
 *
 */
public class GLPathwayTextureManager {

	protected IGeneralManager refGeneralManager;
	
	protected HashMap<Integer, Texture> refHashPathwayIdToTexture;
	
	/**
	 * Constructor.
	 */
	public GLPathwayTextureManager(IGeneralManager refGeneralManager) {
		
		this.refGeneralManager = refGeneralManager;
		
		refHashPathwayIdToTexture = new HashMap<Integer, Texture>();
	}
	
	public Texture loadPathwayTextureById(int iPathwayID) {
		
		if (refHashPathwayIdToTexture.containsKey(iPathwayID))
			return refHashPathwayIdToTexture.get(iPathwayID);
		
		String sPathwayTexturePath = "";
		Texture refPathwayTexture;
		
		if (iPathwayID < 10)
		{
			sPathwayTexturePath = "hsa0000" + Integer.toString(iPathwayID);
		}
		else if (iPathwayID < 100 && iPathwayID >= 10)
		{
			sPathwayTexturePath = "hsa000" + Integer.toString(iPathwayID);
		}
		else if (iPathwayID < 1000 && iPathwayID >= 100)
		{
			sPathwayTexturePath = "hsa00" + Integer.toString(iPathwayID);
		}
		else if (iPathwayID < 10000 && iPathwayID >= 1000)
		{
			sPathwayTexturePath = "hsa0" + Integer.toString(iPathwayID);
		}
		
		sPathwayTexturePath = refGeneralManager.getSingelton().getPathwayManager().getPathwayImagePath()
			+ sPathwayTexturePath +".gif";	
		
		try
		{
			refPathwayTexture = TextureIO.newTexture(new File(sPathwayTexturePath), false);
			refHashPathwayIdToTexture.put(iPathwayID, refPathwayTexture);
			
			refGeneralManager.getSingelton().logMsg(
					this.getClass().getSimpleName() + 
					": loadPathwayTexture(): Loaded Texture for Pathway with ID: " +iPathwayID,
					LoggerType.VERBOSE );
			
			return refPathwayTexture;
			
		} catch (Exception e)
		{
			System.out.println("Error loading texture " + sPathwayTexturePath);
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void renderPathway(final GL gl, int iPathwayID, 
			float fTextureTransparency,
			boolean bHighlight) {

		Texture refTmpPathwayTexture = loadPathwayTextureById(iPathwayID);
		
		refTmpPathwayTexture.enable();
		refTmpPathwayTexture.bind();

		if (bHighlight)
			gl.glColor4f(1f, 0.85f, 0.85f, fTextureTransparency);
		else
			gl.glColor4f(1f, 1f, 1f, fTextureTransparency);
		
		TextureCoords texCoords = refTmpPathwayTexture.getImageTexCoords();
		
		float fTextureWidth = GLPathwayManager.SCALING_FACTOR_X * refTmpPathwayTexture.getImageWidth();
		float fTextureHeight = GLPathwayManager.SCALING_FACTOR_Y * refTmpPathwayTexture.getImageHeight();				
		
//		int viewport[] = new int[4];
//		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
//
//		float h = (float) (float) (viewport[3]-viewport[1]) / 
//			(float) (viewport[2]-viewport[0]) * 4.0f;
		
		gl.glLoadName(GLCanvasJukeboxPathway3D.MAX_LOADED_PATHWAYS);
				
		gl.glBegin(GL.GL_QUADS);
		gl.glTexCoord2f(0, 0); 
		gl.glVertex3f(0.0f, fTextureHeight, 0.0f);			  
		gl.glTexCoord2f(texCoords.right(), 0); 
		gl.glVertex3f(fTextureWidth, fTextureHeight, 0.0f);			 
		gl.glTexCoord2f(texCoords.right(), texCoords.top()); 
		gl.glVertex3f(fTextureWidth, 0.0f, 0.0f);
		gl.glTexCoord2f(0, texCoords.top()); 
		gl.glVertex3f(0.0f, 0.0f, 0.0f);
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
	}
}
