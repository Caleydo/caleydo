/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.heatmap.heatmap.renderer.texture;

import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.GLProfile;

import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.view.ViewManager;
import org.caleydo.core.view.opengl.picking.PickingManager;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.heatmap.renderer.AHeatMapRenderer;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 *
 * TODO: needs to take care of tiled textures in both directions
 *
 * @author Marc Streit
 * @author Alexander Lex
 *
 */

public class HeatMapTextureRenderer extends AHeatMapRenderer {

	private final static int MAX_ITEMS_PER_TEXTURE = 1024;

	private int numTexturesY = 0;
	private int numTexturesX = 0;

	private int numRecords = 0;
	private int numDims = 0;

	/** array of textures for holding the data samples */
	private Texture[][] textures;

	private PickingManager pickingManager = ViewManager.get().getPickingManager();

	private int groupIndex = -1;

	private int viewID;

	private boolean isInitialized = false;

	public HeatMapTextureRenderer(GLHeatMap heatMap) {
		super(heatMap);
	}

	@Override
	public void updateSpacing() {

	}

	/**
	 * Init textures, build array of textures used for holding the whole samples
	 */
	public void initialize(GL2 gl) {

		int totalPixelX = numDims = heatMap.getTablePerspective().getDimensionPerspective().getVirtualArray().size();
		numTexturesX = (int) Math.ceil((double) numDims / MAX_ITEMS_PER_TEXTURE);

		int totalPixelY = numRecords = heatMap.getTablePerspective().getRecordPerspective().getVirtualArray().size();
		numTexturesY = (int) Math.ceil((double) numRecords / MAX_ITEMS_PER_TEXTURE);

		textures = new Texture[numTexturesX][numTexturesY];

		int texPixelX = totalPixelX / numTexturesX;
		int texPixelY = totalPixelY / numTexturesY;

		int dimStartIndex = 0;
		for (int dimTexIndex = 0; dimTexIndex < numTexturesX; dimTexIndex++) {

			int recordStartIndex = 0;
			for (int recordTexIndex = 0; recordTexIndex < numTexturesY; recordTexIndex++) {

				int recordEndIndex = recordStartIndex + texPixelY;
				if (recordEndIndex > totalPixelY)
					recordEndIndex = totalPixelY - 1;

				int dimEndIndex = dimStartIndex + texPixelX;
				if (dimEndIndex > totalPixelX)
					dimEndIndex = totalPixelX - 1;

				initializeSingleTexture(gl, recordTexIndex, recordStartIndex, recordEndIndex, dimTexIndex, dimStartIndex,
						dimEndIndex);

				recordStartIndex += texPixelY;
			}

			dimStartIndex += texPixelX;
		}

		isInitialized = true;
	}

	private void initializeSingleTexture(GL2 gl, int recordTexIndex, int recordStartIndex, int recordEndIndex,
			int dimTexIndex, int dimStartIndex, int dimEndIndex) {

		int recordPixels = recordEndIndex - recordStartIndex;
		int dimPixels = dimEndIndex - dimStartIndex;

		Texture texture;
		FloatBuffer floatBuffer = FloatBuffer.allocate(recordPixels * dimPixels * 4);
		// float lookupValue = 0;


		VirtualArray recordVA = heatMap.getTablePerspective().getRecordPerspective().getVirtualArray();
		for (int recordIndex = recordStartIndex; recordIndex < recordEndIndex; recordIndex++) {

			VirtualArray dimVA = heatMap.getTablePerspective().getDimensionPerspective().getVirtualArray();
			for (int dimIndex = dimStartIndex; dimIndex < dimEndIndex; dimIndex++) {

				float[] mappingColor = heatMap.getDataDomain().getTable()
						.getColor(dimVA.get(dimIndex), recordVA.get(recordIndex));

				// float[] mappingColor = colorMapper.getColor(lookupValue);
				floatBuffer.put(mappingColor);
			}
		}

		floatBuffer.rewind();

		TextureData texData = new TextureData(GLProfile.getDefault(), GL.GL_RGBA /* internalFormat */,
				dimPixels /* width */, recordPixels /* height */, 0 /* border */, GL.GL_RGBA /* pixelFormat */,
				GL.GL_FLOAT /* pixelType */, false /* mipmap */, false /* dataIsCompressed */,
				false /* mustFlipVertically */, floatBuffer, null);

		texture = TextureIO.newTexture(0);
		texture.updateImage(gl, texData);

		// System.out.println("created new texture" + dimTexIndex + " " + recordTexIndex);

		textures[dimTexIndex][recordTexIndex] = texture;
	}

	@Override
	public void renderContent(GL2 gl) {

		if (!isInitialized) {
			initialize(gl);
		}

		float xTexPos = 0;

		float xStep = x / numTexturesX;
		float yStep = y / numTexturesY;

		for (int texIndexX = 0; texIndexX < numTexturesX; texIndexX++) {

			float yTexPos = 0;
			for (int texIndexY = 0; texIndexY < numTexturesY; texIndexY++) {

				renderTexture(gl, textures[texIndexX][numTexturesY - texIndexY - 1], xTexPos, yTexPos, xStep, yStep);

				// GLHelperFunctions.drawSmallPointAt(gl, xTexPos, yTexPos, 0);
				yTexPos += yStep;
			}

			xTexPos += xStep;
		}
	}

	private void renderTexture(GL2 gl, Texture texture, float x, float y, float width, float height) {

		texture.enable(gl);
		texture.bind(gl);

		gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
		TextureCoords texCoords = texture.getImageTexCoords();

		if (groupIndex != -1)
			gl.glPushName(pickingManager.getPickingID(viewID, PickingType.HEAT_MAP_RECORD_GROUP, groupIndex));

		gl.glBegin(GL2GL3.GL_QUADS);
		gl.glTexCoord2d(texCoords.left(), texCoords.top());
		gl.glVertex3f(x, y, 0);
		gl.glTexCoord2d(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(x, y + height, 0);
		gl.glTexCoord2d(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(x + width, y + height, 0);
		gl.glTexCoord2d(texCoords.right(), texCoords.top());
		gl.glVertex3f(x + width, y, 0);
		gl.glEnd();

		if (groupIndex != -1)
			gl.glPopName();

		texture.disable(gl);
	}

	public void setGroupIndex(int groupIndex) {
		this.groupIndex = groupIndex;
	}

	/**
	 * @param isInitialized
	 *            setter, see {@link #isInitialized}
	 */
	public void setInitialized(boolean isInitialized) {
		this.isInitialized = isInitialized;
	}

	/**
	 * @return the isInitialized, see {@link #isInitialized}
	 */
	public boolean isInitialized() {
		return isInitialized;
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}
}
