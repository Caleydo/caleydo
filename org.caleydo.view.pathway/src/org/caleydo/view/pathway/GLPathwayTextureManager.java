/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.pathway;

import java.util.HashMap;
import java.util.LinkedList;

import javax.media.opengl.GL2;

import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.manager.PathwayDatabaseType;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;

/**
 * OpenGL2 pathway texture manager
 * 
 * @author Marc Streit
 */
public class GLPathwayTextureManager {

	private HashMap<PathwayGraph, Texture> hashPathwayToTexture;

	/**
	 * Constructor.
	 */
	public GLPathwayTextureManager() {
		hashPathwayToTexture = new HashMap<PathwayGraph, Texture>();
	}

	public Texture loadPathwayTexture(PathwayGraph pathway) {
		if (hashPathwayToTexture.containsKey(pathway))
			return hashPathwayToTexture.get(pathway);

		Texture pathwayTexture = null;

		String sPathwayTexturePath = pathway.getImageLink();
		PathwayDatabaseType type = pathway.getType();

		sPathwayTexturePath = PathwayManager.get().getPathwayDatabaseByType(type)
				.getImagePath()
				+ sPathwayTexturePath;

		Logger.log(new Status(IStatus.INFO, this.toString(),
				"Load pathway texture with ID: " + pathway.getID()));

		if (type == PathwayDatabaseType.BIOCARTA) {
			pathwayTexture = PathwayManager.get()
					.getPathwayResourceLoader(PathwayDatabaseType.BIOCARTA)
					.getTexture(sPathwayTexturePath);
		} else if (type == PathwayDatabaseType.KEGG) {
			pathwayTexture = PathwayManager.get()
					.getPathwayResourceLoader(PathwayDatabaseType.KEGG)
					.getTexture(sPathwayTexturePath);

		} else {
			throw new IllegalStateException("Unknown pathway database " + type);
		}

		hashPathwayToTexture.put(pathway, pathwayTexture);

		return pathwayTexture;
	}

	public void renderPathway(final GL2 gl, final AGLView containingView,
			final PathwayGraph pathway, final float fTextureTransparency,
			final boolean bHighlight) {

		Texture tmpPathwayTexture = loadPathwayTexture(pathway);

		tmpPathwayTexture.enable(gl);
		tmpPathwayTexture.bind(gl);

		if (bHighlight) {
			gl.glColor4f(1f, 0.85f, 0.85f, fTextureTransparency);
		} else {
			gl.glColor4f(1f, 1f, 1f, fTextureTransparency);
		}

		TextureCoords texCoords = tmpPathwayTexture.getImageTexCoords();

		float textureWidth = PathwayRenderStyle.SCALING_FACTOR_X * pathway.getWidth();
		float textureHeight = PathwayRenderStyle.SCALING_FACTOR_Y * pathway.getHeight();

		// gl.glPushName(generalManager.getSingelton().getViewGLCanvasManager().
		// getPickingManager()
		// .getPickingID(containingView.getId(),
		// EPickingType.PATHWAY_TEXTURE_SELECTION, iPathwayId));

		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(0.0f, 0.0f, 0.0f);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(textureWidth, 0.0f, 0.0f);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(textureWidth, textureHeight, 0.0f);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(0.0f, textureHeight, 0.0f);
		gl.glEnd();

		tmpPathwayTexture.disable(gl);

		if (bHighlight) {
			gl.glColor4f(1, 0, 0, 1);
			gl.glLineWidth(3);
		} else {
			gl.glColor4f(0.5f, 0.5f, 0.5f, 1.0f);
			gl.glLineWidth(1);
		}

		gl.glBegin(GL2.GL_LINE_STRIP);
		gl.glVertex3f(0.0f, 0.0f, 0.0f);
		gl.glVertex3f(textureWidth, 0.0f, 0.0f);
		gl.glVertex3f(textureWidth, textureHeight, 0.0f);
		gl.glVertex3f(0.0f, textureHeight, 0.0f);
		gl.glVertex3f(0.0f, 0.0f, 0.0f);
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

	public void unloadUnusedTextures(GL2 gl, LinkedList<Integer> iLLVisiblePathways) {

		int iTmpPathwayId = 0;
		Integer[] iArPathwayId = hashPathwayToTexture.keySet().toArray(
				new Integer[hashPathwayToTexture.size()]);

		for (Integer element : iArPathwayId) {
			iTmpPathwayId = element;

			if (!iLLVisiblePathways.contains(iTmpPathwayId)) {
				// Remove and dispose texture
				hashPathwayToTexture.remove(iTmpPathwayId).dispose(gl);

				// generalManager.logMsg(
				// this.getClass().getSimpleName()
				// +": unloadUnusedTextures(): Unloading pathway texture with ID "
				// + iTmpPathwayId,
				// LoggerType.VERBOSE);
			}
		}
	}
}
