/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.heatmap.v2.internal;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLProfile;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.view.heatmap.v2.IBlockColorer;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

public class HeatMapTextureRenderer {
	private final static int MAX_SAMPLES_PER_TEXTURE = 2048;

	private int numberOfRecords = 0;

	/** array of textures for holding the data samples */
	private List<Pair<Texture, Integer>> textures = new ArrayList<>();

	private final TablePerspective tablePerspective;
	private final IBlockColorer blockColorer;

	/**
	 * @param tablePerspective
	 * @param blockColorer
	 */
	public HeatMapTextureRenderer(TablePerspective tablePerspective, IBlockColorer blockColorer) {
		this.tablePerspective = tablePerspective;
		this.blockColorer = blockColorer;
	}

	/**
	 * @param context
	 */
	public void init(IGLElementContext context) {
		final GL gl = GLContext.getCurrentGL();

		final VirtualArray recordVA = tablePerspective.getRecordPerspective().getVirtualArray();
		final VirtualArray dimVA = tablePerspective.getDimensionPerspective().getVirtualArray();
		final ATableBasedDataDomain dataDomain = tablePerspective.getDataDomain();

		numberOfRecords = recordVA.size();
		final int numberOfDimensions = dimVA.size();

		int bufferSize = Math.min(numberOfRecords, MAX_SAMPLES_PER_TEXTURE);
		FloatBuffer buffer = FloatBuffer.allocate(numberOfDimensions * bufferSize * 4); //w*h*rgba

		int actTexture = 0;
		int actRecordInTexture = 0;

		for (Integer recordID : recordVA) {
			actRecordInTexture++;
			//add row
			for (Integer dimensionID : dimVA) {
				Color color = blockColorer.apply(recordID, dimensionID, dataDomain, false);
				buffer.put(color.getRGBA());
			}

			if (actRecordInTexture == MAX_SAMPLES_PER_TEXTURE) { // flush intermediate
				createTexture(gl, numberOfDimensions, buffer, actTexture, actRecordInTexture, false);
				actTexture++;
				actRecordInTexture = 0;
			}
		}
		if (actRecordInTexture > 0) { // flush last
			assert (actRecordInTexture < MAX_SAMPLES_PER_TEXTURE);
			createTexture(gl, numberOfDimensions, buffer, actTexture, actRecordInTexture, true);

			actTexture++;
			actRecordInTexture = 0;
		}

		// remove all texture that are not needed anymore
		if (textures.size() > actTexture) {
			List<Pair<Texture, Integer>> toremove = textures.subList(actTexture, textures.size());
			for (Pair<Texture, Integer> entry : toremove) {
				entry.getFirst().destroy(gl);
			}
			toremove.clear();
		}
	}

	protected void createTexture(final GL gl, final int width, FloatBuffer buffer, int actTexture, int height,
			boolean exactMatch) {
		buffer.rewind();
		Texture texture;
		if (textures.size() > actTexture) { //try to reuse it
			Pair<Texture, Integer> entry = textures.get(actTexture);
			texture = entry.getFirst();
			textures.set(actTexture, Pair.make(texture, height));
		} else {
			texture = TextureIO.newTexture(GL.GL_TEXTURE_2D);
			textures.add(Pair.make(texture, height));
		}

		TextureData texData = asTextureData(buffer, width, height);
		texture.updateImage(gl, texData);
		gl.glFlush();
		texData.destroy();
	}

	protected TextureData asTextureData(FloatBuffer buffer, int width, int height) {
		TextureData texData = new TextureData(GLProfile.getDefault(), GL.GL_RGBA /* internalFormat */, width, height,
				0 /* border */,
				GL.GL_RGBA /* pixelFormat */, GL.GL_FLOAT /* pixelType */, false /* mipmap */,
				false /* dataIsCompressed */, false /* mustFlipVertically */, buffer, null);
		return texData;
	}

	public void takeDown() {
		GL gl = GLContext.getCurrentGL();
		for (Pair<Texture, Integer> entry : textures) {
			entry.getFirst().destroy(gl);
		}
		textures.clear();
		numberOfRecords = 0;
	}

	public void render(GLGraphics g, float w, float h) {
		float y = 0.0f;
		float elementHeight = h / numberOfRecords;

		g.color(1f, 1f, 0f, 1f);

		g.gl.glEnable(GL.GL_TEXTURE_2D);
		g.gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
		g.gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
		g.gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
		g.gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);

		for (Pair<Texture, Integer> entry : textures) {
			Texture tex = entry.getFirst();
			int samples = entry.getSecond();
			float hi = elementHeight * samples;
			g.fillImage(tex, 0, y, w, hi);
			y += hi;
		}
	}
}
