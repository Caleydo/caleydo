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
import org.caleydo.datadomain.pathway.manager.EPathwayDatabaseType;
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

		String pathwayTexturePath = pathway.getImageLink();
		EPathwayDatabaseType type = pathway.getType();

		pathwayTexturePath = PathwayManager.get().getPathwayDatabaseByType(type).getImagePath() + pathwayTexturePath;

		Logger.log(new Status(IStatus.INFO, this.toString(), "Load pathway texture with ID: " + pathway.getID()));

		if (type == EPathwayDatabaseType.BIOCARTA) {
			pathwayTexture = PathwayManager.get().getPathwayResourceLoader(EPathwayDatabaseType.BIOCARTA)
					.getTexture(pathwayTexturePath);
		}
		else if (type == EPathwayDatabaseType.KEGG) {
			pathwayTexture = PathwayManager.get().getPathwayResourceLoader(EPathwayDatabaseType.KEGG)
					.getTexture(pathwayTexturePath);

		}
		else {
			throw new IllegalStateException("Unknown pathway database " + type);
		}

		hashPathwayToTexture.put(pathway, pathwayTexture);

		return pathwayTexture;
	}

	public void renderPathway(final GL2 gl, final AGLView containingView, final PathwayGraph pathway,
			final float fTextureTransparency, final boolean bHighlight) {

		Texture tmpPathwayTexture = loadPathwayTexture(pathway);

		tmpPathwayTexture.enable(gl);
		tmpPathwayTexture.bind(gl);

		if (bHighlight) {
			gl.glColor4f(1f, 0.85f, 0.85f, fTextureTransparency);
		}
		else {
			gl.glColor4f(1f, 1f, 1f, fTextureTransparency);
		}

		TextureCoords texCoords = tmpPathwayTexture.getImageTexCoords();

		float textureWidth = containingView.getPixelGLConverter().getGLWidthForPixelWidth(pathway.getWidth());
		float textureHeight = containingView.getPixelGLConverter().getGLHeightForPixelHeight(pathway.getHeight());

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
		}
		else {
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

	public void unloadUnusedTextures(GL2 gl, LinkedList<Integer> visiblePathways) {

		for (Integer element : hashPathwayToTexture.keySet().toArray(new Integer[hashPathwayToTexture.size()])) {
			int pathwayId = element;

			if (!visiblePathways.contains(pathwayId)) {
				// Remove and dispose texture
				hashPathwayToTexture.remove(pathwayId).dispose(gl);
			}
		}
	}
}
