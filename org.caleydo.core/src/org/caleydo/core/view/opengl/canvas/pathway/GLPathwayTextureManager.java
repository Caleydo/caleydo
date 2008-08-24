package org.caleydo.core.view.opengl.canvas.pathway;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import javax.media.opengl.GL;
import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.data.view.rep.renderstyle.PathwayRenderStyle;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.specialized.genome.pathway.EPathwayDatabaseType;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;
import com.sun.opengl.util.texture.TextureIO;

/**
 * OpenGL pathway texture manager
 * 
 * @author Marc Streit
 */
public class GLPathwayTextureManager
{

	private IGeneralManager generalManager;

	private HashMap<Integer, Texture> hashPathwayIdToTexture;

	/**
	 * Constructor.
	 */
	public GLPathwayTextureManager()
	{
		this.generalManager = GeneralManager.get();		

		hashPathwayIdToTexture = new HashMap<Integer, Texture>();
	}

	public Texture loadPathwayTextureById(int iPathwayId)
	{
		if (hashPathwayIdToTexture.containsKey(iPathwayId))
			return hashPathwayIdToTexture.get(iPathwayId);

		Texture pathwayTexture = null;

		String sPathwayTexturePath = ((PathwayGraph) generalManager.getPathwayManager()
				.getItem(iPathwayId)).getImageLink();

		EPathwayDatabaseType type = ((PathwayGraph) generalManager.getPathwayManager()
				.getItem(iPathwayId)).getType();

		sPathwayTexturePath = generalManager.getPathwayManager()
				.getPathwayDatabaseByType(type).getImagePath()
				+ sPathwayTexturePath;

		generalManager.getLogger().log(Level.INFO,
				"Load pathway texture with ID: " + iPathwayId);

		try
		{
			if (this.getClass().getClassLoader().getResource(sPathwayTexturePath) != null)
			{
				pathwayTexture = TextureIO.newTexture(TextureIO.newTextureData(this.getClass()
						.getClassLoader().getResourceAsStream(sPathwayTexturePath), true,
						"GIF"));
			}
			else
			{
				pathwayTexture = TextureIO.newTexture(TextureIO.newTextureData(new File(
						sPathwayTexturePath), true, "GIF"));
			}

			// pathwayTexture.setTexParameteri(GL.GL_TEXTURE_MIN_FILTER,
			// GL.GL_LINEAR);
			// pathwayTexture.setTexParameteri(GL.GL_TEXTURE_MAG_FILTER,
			// GL.GL_LINEAR);

			hashPathwayIdToTexture.put(iPathwayId, pathwayTexture);

			return pathwayTexture;

		}
		catch (Exception e)
		{
			generalManager.getLogger().log(Level.SEVERE,
					"Error loading pathway texture: " + sPathwayTexturePath);
			e.printStackTrace();
		}

		return null;
	}

	public void renderPathway(final GL gl, final AGLEventListener containingView,
			final int iPathwayId, final float fTextureTransparency, final boolean bHighlight)
	{

		Texture tmpPathwayTexture = loadPathwayTextureById(iPathwayId);

		tmpPathwayTexture.enable();
		tmpPathwayTexture.bind();

		if (bHighlight)
			gl.glColor4f(1f, 0.85f, 0.85f, fTextureTransparency);
		else
			gl.glColor4f(1f, 1f, 1f, fTextureTransparency);

		TextureCoords texCoords = tmpPathwayTexture.getImageTexCoords();

		float fTextureWidth = PathwayRenderStyle.SCALING_FACTOR_X
				* ((PathwayGraph) generalManager.getPathwayManager().getItem(iPathwayId))
						.getWidth();
		float fTextureHeight = PathwayRenderStyle.SCALING_FACTOR_Y
				* ((PathwayGraph) generalManager.getPathwayManager().getItem(iPathwayId))
						.getHeight();

		// gl.glPushName(generalManager.getSingelton().getViewGLCanvasManager().
		// getPickingManager()
		// .getPickingID(containingView.getId(),
		// EPickingType.PATHWAY_TEXTURE_SELECTION, iPathwayId));

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

		tmpPathwayTexture.disable();

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
		gl.glVertex3f(0.0f, 0.0f, 0.0f);
		;
		gl.glVertex3f(fTextureWidth, 0.0f, 0.0f);
		gl.glVertex3f(fTextureWidth, fTextureHeight, 0.0f);
		gl.glVertex3f(0.0f, fTextureHeight, 0.0f);
		gl.glVertex3f(0.0f, 0.0f, 0.0f);
		;
		gl.glEnd();

		// gl.glPopName();
	}

	/**
	 * Method supports lazy loading of pathway textures if they are not present
	 * at that time.
	 * 
	 * @param iPathwayId
	 * @return Pathway texture
	 */
	public Texture getTextureByPathwayId(final int iPathwayId)
	{

		if (hashPathwayIdToTexture.containsKey(iPathwayId))
			return hashPathwayIdToTexture.get(iPathwayId);

		loadPathwayTextureById(iPathwayId);
		return hashPathwayIdToTexture.get(iPathwayId);
	}

	public void unloadUnusedTextures(LinkedList<Integer> iLLVisiblePathways)
	{

		int iTmpPathwayId = 0;
		Integer[] iArPathwayId = hashPathwayIdToTexture.keySet().toArray(
				new Integer[hashPathwayIdToTexture.size()]);

		for (int iPathwayIndex = 0; iPathwayIndex < iArPathwayId.length; iPathwayIndex++)
		{
			iTmpPathwayId = iArPathwayId[iPathwayIndex];

			if (!iLLVisiblePathways.contains(iTmpPathwayId))
			{
				// Remove and dispose texture
				hashPathwayIdToTexture.remove(iTmpPathwayId).dispose();

				// generalManager.logMsg(
				// this.getClass().getSimpleName()
				//+": unloadUnusedTextures(): Unloading pathway texture with ID "
				// + iTmpPathwayId,
				// LoggerType.VERBOSE);
			}
		}
	}
}
