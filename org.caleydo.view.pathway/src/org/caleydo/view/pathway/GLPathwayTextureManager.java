package org.caleydo.view.pathway;

import java.util.HashMap;
import java.util.LinkedList;

import javax.media.opengl.GL;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.manager.EPathwayDatabaseType;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;

/**
 * OpenGL pathway texture manager
 * 
 * @author Marc Streit
 */
public class GLPathwayTextureManager {

	private GeneralManager generalManager;

	private HashMap<PathwayGraph, Texture> hashPathwayToTexture;

	/**
	 * Constructor.
	 */
	public GLPathwayTextureManager() {
		this.generalManager = GeneralManager.get();

		hashPathwayToTexture = new HashMap<PathwayGraph, Texture>();
	}

	public Texture loadPathwayTexture(PathwayGraph pathway) {
		if (hashPathwayToTexture.containsKey(pathway))
			return hashPathwayToTexture.get(pathway);

		Texture pathwayTexture = null;

		String sPathwayTexturePath = pathway.getImageLink();
		EPathwayDatabaseType type = pathway.getType();

		sPathwayTexturePath = PathwayManager.get().getPathwayDatabaseByType(type)
				.getImagePath()
				+ sPathwayTexturePath;

		Logger.log(new Status(IStatus.INFO, this.toString(),
				"Load pathway texture with ID: " + pathway.getID()));

		if (type == EPathwayDatabaseType.BIOCARTA) {
			pathwayTexture = PathwayManager.get()
					.getPathwayResourceLoader(EPathwayDatabaseType.BIOCARTA)
					.getTexture(sPathwayTexturePath);
		} else if (type == EPathwayDatabaseType.KEGG) {
			pathwayTexture = PathwayManager.get()
					.getPathwayResourceLoader(EPathwayDatabaseType.KEGG)
					.getTexture(sPathwayTexturePath);

		} else {
			throw new IllegalStateException("Unknown pathway database " + type);
		}

		hashPathwayToTexture.put(pathway, pathwayTexture);

		return pathwayTexture;
	}

	public void renderPathway(final GL gl, final AGLView containingView,
			final PathwayGraph pathway, final float fTextureTransparency,
			final boolean bHighlight) {

		Texture tmpPathwayTexture = loadPathwayTexture(pathway);

		tmpPathwayTexture.enable();
		tmpPathwayTexture.bind();

		if (bHighlight) {
			gl.glColor4f(1f, 0.85f, 0.85f, fTextureTransparency);
		} else {
			gl.glColor4f(1f, 1f, 1f, fTextureTransparency);
		}

		TextureCoords texCoords = tmpPathwayTexture.getImageTexCoords();

		float fTextureWidth = PathwayRenderStyle.SCALING_FACTOR_X * pathway.getWidth();
		float fTextureHeight = PathwayRenderStyle.SCALING_FACTOR_Y * pathway.getHeight();

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

		if (bHighlight) {
			gl.glColor4f(1, 0, 0, 1);
			gl.glLineWidth(3);
		} else {
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
	 * @param PathwayGraph
	 * @return Pathway texture
	 */
	public Texture getTextureByPathway(final PathwayGraph pathway) {

		if (hashPathwayToTexture.containsKey(pathway))
			return hashPathwayToTexture.get(pathway);

		loadPathwayTexture(pathway);
		return hashPathwayToTexture.get(pathway);
	}

	public void unloadUnusedTextures(LinkedList<Integer> iLLVisiblePathways) {

		int iTmpPathwayId = 0;
		Integer[] iArPathwayId = hashPathwayToTexture.keySet().toArray(
				new Integer[hashPathwayToTexture.size()]);

		for (Integer element : iArPathwayId) {
			iTmpPathwayId = element;

			if (!iLLVisiblePathways.contains(iTmpPathwayId)) {
				// Remove and dispose texture
				hashPathwayToTexture.remove(iTmpPathwayId).dispose();

				// generalManager.logMsg(
				// this.getClass().getSimpleName()
				// +": unloadUnusedTextures(): Unloading pathway texture with ID "
				// + iTmpPathwayId,
				// LoggerType.VERBOSE);
			}
		}
	}
}
