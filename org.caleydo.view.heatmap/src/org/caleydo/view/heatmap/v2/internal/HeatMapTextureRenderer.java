/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.heatmap.v2.internal;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLProfile;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.view.heatmap.v2.IBlockColorer;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * helper class for generating a texture heat map
 *
 * TODO support tiling in X and Y direction
 *
 * @author Samuel Gratzl
 *
 */
public class HeatMapTextureRenderer {
	private final static int MAX_SAMPLES_PER_TEXTURE = 2048;

	private Dimension dimension = null;

	private List<Tile> tiles = new ArrayList<>();

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

		final int numberOfRecords = tablePerspective.getRecordPerspective().getVirtualArray().size();
		final int numberOfDimensions = tablePerspective.getDimensionPerspective().getVirtualArray().size();
		dimension = new Dimension(numberOfDimensions, numberOfRecords);

		final int maxSize = resolveMaxSize(gl);

		boolean needXTiling = numberOfDimensions > maxSize;
		boolean needYTiling = numberOfRecords > maxSize;

		if (!needXTiling && !needYTiling) {
			//single tile
			FloatBuffer buffer = FloatBuffer.allocate(numberOfDimensions * numberOfRecords * 4); //w*h*rgba
			Rectangle tile = new Rectangle(0,0,numberOfDimensions,numberOfRecords);
			tiles.add(createTile(buffer,tile, gl));
		} else if (needXTiling && !needYTiling){
			//tile in x direction only
			FloatBuffer buffer = FloatBuffer.allocate(maxSize * numberOfRecords * 4); //w*h*rgba
			//fill full
			int lastTile = numberOfDimensions - maxSize;
			for (int i = 0; i < lastTile; i += maxSize) {
				Rectangle tile = new Rectangle(i,0,maxSize,numberOfRecords);
				tiles.add(createTile(buffer,tile, gl));
			}
			{//create rest
				int remaining = numberOfDimensions % maxSize;
				Rectangle tile = new Rectangle(numberOfDimensions-remaining,0,remaining,numberOfRecords);
				tiles.add(createTile(buffer,tile, gl));
			}
		} else if (!needXTiling && needYTiling) {
			//tile in y direction only
			FloatBuffer buffer = FloatBuffer.allocate(numberOfDimensions * maxSize * 4); //w*h*rgba
			//fill full
			int lastTile = numberOfRecords - maxSize;
			for (int i = 0; i < lastTile; i += maxSize) {
				Rectangle tile = new Rectangle(0,i,numberOfDimensions,maxSize);
				tiles.add(createTile(buffer,tile, gl));
			}
			{//create rest
				int remaining = numberOfRecords % maxSize;
				Rectangle tile = new Rectangle(0,numberOfRecords-remaining,numberOfDimensions,remaining);
				tiles.add(createTile(buffer,tile, gl));
			}
		} else {
			//tile in both directions
			//tile in y direction only
			FloatBuffer buffer = FloatBuffer.allocate(maxSize * maxSize * 4); //w*h*rgba
			//fill full
			int lastTileR = numberOfRecords - maxSize;
			int lastTileD = numberOfDimensions - maxSize;
			for (int i = 0; i < lastTileR; i += maxSize) {
				for (int j = 0; j < lastTileD; j += maxSize) {
					Rectangle tile = new Rectangle(j,i,maxSize,maxSize);
					tiles.add(createTile(buffer,tile, gl));
				}
				{//create rest
					int remaining = numberOfDimensions % maxSize;
					Rectangle tile = new Rectangle(numberOfDimensions-remaining,i,remaining,maxSize);
					tiles.add(createTile(buffer,tile, gl));
				}
			}
			{//last line
				int iremaining = numberOfRecords % maxSize;
				int i = numberOfRecords - iremaining;
				for (int j = 0; j < lastTileD; j += maxSize) {
					Rectangle tile = new Rectangle(j,i,maxSize,iremaining);
					tiles.add(createTile(buffer,tile, gl));
				}
				{//create rest
					int remaining = numberOfDimensions % maxSize;
					Rectangle tile = new Rectangle(numberOfDimensions-remaining,i,remaining,iremaining);
					tiles.add(createTile(buffer,tile, gl));
				}
			}
		}
	}

	/**
	 * @param buffer
	 * @param tile
	 * @return
	 */
	private Tile createTile(FloatBuffer buffer, Rectangle tile, GL gl) {
		final VirtualArray recordVA = tablePerspective.getRecordPerspective().getVirtualArray();
		final VirtualArray dimVA = tablePerspective.getDimensionPerspective().getVirtualArray();
		final ATableBasedDataDomain dataDomain = tablePerspective.getDataDomain();
		final int ilast = tile.y+tile.height;
		final int jlast = tile.x+tile.width;

		//fill buffer
		buffer.rewind();
		for(int i = tile.y; i < ilast; ++i) {
			int recordID = recordVA.get(i);
			for(int j = tile.x; j < jlast; ++j) {
				int dimensionID = dimVA.get(j);
				Color color = blockColorer.apply(recordID, dimensionID, dataDomain, false);
				buffer.put(color.getRGBA());
			}
		}

		//load to texture
		buffer.rewind();
		Texture texture = TextureIO.newTexture(GL.GL_TEXTURE_2D);
		TextureData texData = asTextureData(buffer, tile.width, tile.height);
		texture.updateImage(gl, texData);
		gl.glFlush();
		texData.destroy();

		return new Tile(tile, texture);
	}

	/**
	 * compute the max texture size to use
	 *
	 * @param gl
	 * @return
	 */
	private static int resolveMaxSize(GL gl) {
		int[] result = new int[1];
		gl.glGetIntegerv(GL.GL_MAX_TEXTURE_SIZE, result, 0);
		int maxTexSize = result[0];
		return Math.min(maxTexSize, MAX_SAMPLES_PER_TEXTURE);
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
		for (Tile tile : tiles) {
			tile.texture.destroy(gl);
		}
		tiles.clear();
		dimension = null;
	}

	public void render(GLGraphics g, float w, float h) {
		float wScale = w / dimension.width;
		float hScale = h / dimension.height;

		g.save();
		//scale to be able to use pixel rendering
		g.gl.glScalef(wScale, hScale, 1.0f);

		g.gl.glEnable(GL.GL_TEXTURE_2D);
		for(Tile tile : tiles)
			tile.render(g);
		g.restore();
	}

	private static class Tile {
		private final Rectangle tile;
		private final Texture texture;

		public Tile(Rectangle tile, Texture texture) {
			this.tile = tile;
			this.texture = texture;
		}
		public void render(GLGraphics g) {

			texture.bind(g.gl);
			g.gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
			g.gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
			g.gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
			g.gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);

			g.fillImage(texture, tile.x, tile.y, tile.width, tile.height);
		}
	}

	// public static void main(String[] args) {
	// MockDataDomain d = MockDataDomain.createNumerical(100, 50, new Random());
	// GLSandBox.main(args, new HeatMapElement(d.getDefaultTablePerspective(), BasicBlockColorer.INSTANCE,
	// EDetailLevel.LOW));
	// }
}
