/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.heatmap.heatmap.renderer.texture;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GLProfile;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.color.mapping.ColorMapper;
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

	private final static int MAX_ITEMS_PER_TEXTURE = 500;

	private int numTexturesY = 0;
	private int numTexturesX = 0;

	private int numRecords = 0;
	private int numDims = 0;

	private int recordsPerTexture = 0;
	private int dimsPerTexture = 0;

	/** array of textures for holding the data samples */
	private ArrayList<Texture> textures = new ArrayList<Texture>();

	private ArrayList<Integer> numberSamples = new ArrayList<Integer>();

	private PickingManager pickingManager = GeneralManager.get().getViewManager().getPickingManager();

	private int groupIndex = -1;

	private int viewID;

	private boolean isInitialized = false;

	public HeatMapTextureRenderer(GLHeatMap heatMap) {
		super(heatMap);
	}

	@Override
	public void updateSpacing() {

		// if (heatMap == null)
		// return;
		//
		// AHeatMapTemplate heatMapTemplate = heatMap.getTemplate();
		//
		// VirtualArray recordVA =
		// heatMap.getTablePerspective().getRecordPerspective()
		// .getVirtualArray();
		//
		// int recordElements = recordVA.size();
		//
		// RecordSelectionManager selectionManager =
		// heatMap.getRecordSelectionManager();
		// if (heatMap.isHideElements()) {
		// recordElements -= selectionManager
		// .getNumberOfElements(GLHeatMap.SELECTION_HIDDEN);
		// }
		//
		// recordSpacing.calculateRecordSpacing(recordElements,
		// heatMap.getTablePerspective()
		// .getDimensionPerspective().getVirtualArray().size(),
		// parameters.getSizeScaledX(), parameters.getSizeScaledY(),
		// heatMapTemplate.getMinSelectedFieldHeight());
		// heatMapTemplate.setContentSpacing(recordSpacing);
		//
		// float yPosition = parameters.getSizeScaledY();
		// recordSpacing.getYDistances().clear();
		// for (Integer recordID : recordVA) {
		//
		// float fieldHeight = recordSpacing.getFieldHeight(recordID);
		// yPosition -= fieldHeight;
		// recordSpacing.getYDistances().add(yPosition);
		// }
	}

	/**
	 * Init textures, build array of textures used for holding the whole samples
	 */
	public void initialize(GL2 gl) {
		int totalPixelY = numRecords = heatMap.getTablePerspective().getRecordPerspective().getVirtualArray()
				.size();
		int totalPixelX = numDims = MAX_ITEMS_PER_TEXTURE;// heatMap.getTablePerspective().getDimensionPerspective().getVirtualArray().size();

		numTexturesX = (int) Math.ceil((double) numDims / MAX_ITEMS_PER_TEXTURE);
		numTexturesY = (int) Math.ceil((double) numRecords / MAX_ITEMS_PER_TEXTURE);

		if (numTexturesX <= 1)
			dimsPerTexture = numDims;
		else
			dimsPerTexture = MAX_ITEMS_PER_TEXTURE;

		if (numTexturesY <= 1)
			recordsPerTexture = numRecords;
		else
			recordsPerTexture = MAX_ITEMS_PER_TEXTURE;

		textures.clear();
		numberSamples.clear();

		Texture tempTexture;

		recordsPerTexture = (int) Math.ceil((double) totalPixelY / numTexturesY);

		float lookupValue = 0;

		FloatBuffer[] floatBuffer = new FloatBuffer[numTexturesY];

		for (int texIndex = 0; texIndex < numTexturesY; texIndex++) {

			if (texIndex == numTexturesY - 1) {
				numberSamples.add(totalPixelY % recordsPerTexture);
				floatBuffer[texIndex] = FloatBuffer.allocate((totalPixelY % recordsPerTexture)
						* totalPixelX * 4);
			} else {
				numberSamples.add(recordsPerTexture);
				floatBuffer[texIndex] = FloatBuffer.allocate(recordsPerTexture * totalPixelX * 4);
			}
		}

		int recordCount = 0;
		int textureCounter = 0;
		float opacity = 1;

		ColorMapper colorMapper = heatMap.getDataDomain().getColorMapper();
		for (Integer recordID : heatMap.getTablePerspective().getRecordPerspective().getVirtualArray()) {

			recordCount++;

			int dimCount = 0;
			for (Integer dimensionID : heatMap.getTablePerspective().getDimensionPerspective().getVirtualArray()) {

				if (++dimCount > MAX_ITEMS_PER_TEXTURE)
					break;

				lookupValue = heatMap.getDataDomain().getTable().getNormalizedValue(dimensionID, recordID);

				float[] mappingColor = colorMapper.getColor(lookupValue);
				float[] rgba = { mappingColor[0], mappingColor[1], mappingColor[2], opacity };

				floatBuffer[textureCounter].put(rgba);
			}

			if (recordCount >= numberSamples.get(textureCounter)) {
				floatBuffer[textureCounter].rewind();

				TextureData texData = new TextureData(GLProfile.getDefault(), GL.GL_RGBA /* internalFormat */,
						totalPixelX /* height */, numberSamples.get(textureCounter) /* width */, 0 /* border */,
						GL.GL_RGBA /* pixelFormat */, GL.GL_FLOAT /* pixelType */, false /* mipmap */,
						false /* dataIsCompressed */, false /* mustFlipVertically */, floatBuffer[textureCounter], null);

				tempTexture = TextureIO.newTexture(0);
				tempTexture.updateImage(gl, texData);

				textures.add(tempTexture);

				textureCounter++;
				recordCount = 0;
			}
		}
		isInitialized = true;
	}

	@Override
	public void renderContent(GL2 gl) {

		if (!isInitialized) {
			initialize(gl);
		}

		float yOffset = 0;
		float yPosition = 0;
		float itemHeight = y / numRecords;

		gl.glColor4f(1f, 1f, 0f, 1f);

		for (int textureIndex = 0; textureIndex < numTexturesY; textureIndex++) {

			yPosition = itemHeight * numberSamples.get(numTexturesY - textureIndex - 1);
			renderTexture(gl, textures.get(numTexturesY - textureIndex - 1), 0, yOffset, x, yOffset + yPosition);

			yOffset += yPosition;
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

		gl.glBegin(GL2.GL_QUADS);
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
