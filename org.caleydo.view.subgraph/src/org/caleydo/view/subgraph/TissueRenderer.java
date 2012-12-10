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
package org.caleydo.view.subgraph;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.util.texture.TextureManager;

import com.jogamp.opengl.util.texture.Texture;

/**
 * Renderer for tissue images that consist of multiple texture layers, one being the actual tissue image, the others
 * being marked areas within the image.
 *
 * @author Christian Partl
 *
 */
public class TissueRenderer extends LayoutRenderer {

	/**
	 * View that displays this renderer.
	 */
	private final AGLView view;

	/**
	 * Path of the base tissue image.
	 */
	private final String baseImagePath;

	/**
	 * Paths of all images that mark areas within the tissue image.
	 */
	private final List<String> markedAreaImagePaths;

	/**
	 * Texture for base tissue image.
	 */
	private Texture baseImage;

	/**
	 * List of textures for marked areas.
	 */
	private List<Texture> areaImages;

	/**
	 * Constructor for the tissue renderer. The resolution of all provided image files are assumed to be the same.
	 *
	 * @param view
	 *            see {@link #view}
	 * @param baseImagePath
	 *            see {@link #baseImagePath}
	 * @param markedAreaImagePaths
	 *            see {@link #markedAreaImagePaths}
	 */
	public TissueRenderer(AGLView view, String baseImagePath, List<String> markedAreaImagePaths) {
		this.view = view;
		this.baseImagePath = baseImagePath;
		this.markedAreaImagePaths = markedAreaImagePaths;
		areaImages = new ArrayList<>(this.markedAreaImagePaths.size());
	}

	public TissueRenderer(AGLView view, Texture baseImage, List<Texture> markedAreaImages) {
		this.view = view;
		this.baseImage = baseImage;
		this.areaImages = markedAreaImages;
		baseImagePath = null;
		markedAreaImagePaths = null;
		areaImages = new ArrayList<>(this.markedAreaImagePaths.size());
	}

	@Override
	protected void renderContent(GL2 gl) {
		TextureManager textureManager = view.getTextureManager();
		if (baseImage == null) {
			baseImage = textureManager.getIconTexture(baseImagePath);
			for (String areaImagePath : markedAreaImagePaths) {
				areaImages.add(textureManager.getIconTexture(areaImagePath));
			}
		}

		float rendererAspectRatio = x / y;
		float imageAspectRatio = (float) baseImage.getWidth() / (float) baseImage.getHeight();
		float renderWidth;
		float renderHeight;
		if (rendererAspectRatio > imageAspectRatio) {
			renderWidth = (y / baseImage.getHeight()) * baseImage.getWidth();
			renderHeight = y;
		} else {
			renderWidth = x;
			renderHeight = (x / baseImage.getWidth()) * baseImage.getHeight();
		}

		float left = x / 2.0f - renderWidth / 2.0f;
		float right = x / 2.0f + renderWidth / 2.0f;
		float bottom = y / 2.0f - renderHeight / 2.0f;
		float top = y / 2.0f + renderHeight / 2.0f;

		Vec3f lowerLeftCorner = new Vec3f(left, bottom, 0);
		Vec3f lowerRightCorner = new Vec3f(right, bottom, 0);
		Vec3f upperRightCorner = new Vec3f(right, top, 0);
		Vec3f upperLeftCorner = new Vec3f(left, top, 0);

		textureManager.renderTexture(gl, baseImage, lowerLeftCorner, lowerRightCorner, upperRightCorner,
				upperLeftCorner, 1, 1, 1, 1);
		for (Texture areaImage : areaImages) {
			textureManager.renderTexture(gl, areaImage, lowerLeftCorner, lowerRightCorner, upperRightCorner,
					upperLeftCorner, 1, 1, 1, 1);
		}

	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return true;
	}

	@Override
	public void destroy(GL2 gl) {
		baseImage.destroy(gl);
		for (Texture texture : areaImages) {
			texture.destroy(gl);
		}
		super.destroy(gl);
	}

}
