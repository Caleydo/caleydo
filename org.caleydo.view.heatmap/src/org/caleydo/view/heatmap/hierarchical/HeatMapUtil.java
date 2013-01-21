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
package org.caleydo.view.heatmap.hierarchical;

import gleem.linalg.Vec3f;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GLProfile;

import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.selection.RecordSelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.util.color.mapping.ColorMapper;
import org.caleydo.core.view.opengl.picking.PickingManager;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.opengl.util.texture.TextureManager;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

public class HeatMapUtil {

	public static int MAX_SAMPLES_PER_TEXTURE = 2000;

	public static ArrayList<Texture> createHeatMapTextures(GL2 gl, Table table,VirtualArray recordVA,
			VirtualArray dimensionVA, RecordSelectionManager contentSelectionManager) {

		int numSamples = recordVA.size();
		int numDimensions = dimensionVA.size();

		ArrayList<Texture> textures = new ArrayList<Texture>();
		ColorMapper colorMapping = table.getDataDomain().getColorMapper();

		int numSamplesProcessed = 0;
		boolean isNewTexture = true;
		FloatBuffer textureBuffer = null;
		int numSamplesInTexture = 0;

		for (Integer recordIndex : recordVA) {

			if (isNewTexture) {
				numSamplesInTexture = Math.min(MAX_SAMPLES_PER_TEXTURE, numSamples - numSamplesProcessed);
				textureBuffer = FloatBuffer.allocate(numSamplesInTexture * numDimensions * 4);
				isNewTexture = false;
			}

			for (Integer dimensionIndex : dimensionVA) {

				float fOpacity = 1.0f;

				if (contentSelectionManager != null
						&& contentSelectionManager.checkStatus(SelectionType.DESELECTED, recordIndex)) {
					fOpacity = 0.3f;
				}
				;
				float fLookupValue = table.getNormalizedValue(dimensionIndex, recordIndex);

				float[] fArMappingColor = colorMapping.getColor(fLookupValue);

				float[] fArRgba = { fArMappingColor[0], fArMappingColor[1], fArMappingColor[2], fOpacity };

				textureBuffer.put(fArRgba);
				if (!textureBuffer.hasRemaining()) {
					textureBuffer.rewind();

					TextureData texData = new TextureData(GLProfile.getDefault(), GL.GL_RGBA /* internalFormat */,
							numDimensions /* height */, numSamplesInTexture /* width */, 0 /* border */,
							GL.GL_RGBA /* pixelFormat */, GL2.GL_FLOAT /* pixelType */, false /* mipmap */,
							false /* dataIsCompressed */, false /* mustFlipVertically */, textureBuffer, null);

					Texture texture = TextureIO.newTexture(0);
					texture.updateImage(gl, texData);

					textures.add(texture);
					isNewTexture = true;
				}
			}
			numSamplesProcessed++;
		}

		return textures;
	}

	public static void renderHeatmapTextures(GL2 gl, ArrayList<Texture> textures, float height, float width) {

		int numElements = 0;

		for (Texture texture : textures) {
			numElements += texture.getHeight();
		}

		float yOffset = 0.0f;

		float elementHeight = height / numElements;

		float textureDrawingHeight = 0;

		gl.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);

		for (int i = 0; i < textures.size(); i++) {
			Texture texture = textures.get(textures.size() - i - 1);
			textureDrawingHeight = elementHeight * texture.getHeight();
			texture.enable(gl);
			texture.bind(gl);
			gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
			TextureCoords texCoords = texture.getImageTexCoords();
			gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2d(texCoords.left(), texCoords.top());
			gl.glVertex3f(0, yOffset, 0);
			gl.glTexCoord2d(texCoords.left(), texCoords.bottom());
			gl.glVertex3f(0, yOffset + textureDrawingHeight, 0);
			gl.glTexCoord2d(texCoords.right(), texCoords.bottom());
			gl.glVertex3f(width, yOffset + textureDrawingHeight, 0);
			gl.glTexCoord2d(texCoords.right(), texCoords.top());
			gl.glVertex3f(width, yOffset, 0);
			gl.glEnd();
			yOffset += textureDrawingHeight;
			texture.disable(gl);
		}
	}

	public static void renderGroupBar(GL2 gl, VirtualArray recordVA, float totalHeight, float groupWidth,
			PickingManager pickingManager, int viewID, PickingType pickingType, TextureManager textureManager) {

		GroupList contentGroupList = recordVA.getGroupList();

		if (contentGroupList != null) {
			float sampleHeight = totalHeight / (recordVA.size());
			float groupPositionY = totalHeight;

			gl.glColor4f(1, 1, 1, 1);
			gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);
			int groupIndex = 0;
			for (Group group : contentGroupList) {
				int numSamplesGroup = group.getSize();
				float groupHeight = numSamplesGroup * sampleHeight;
				EIconTextures iconTextures = (group.getSelectionType() == SelectionType.SELECTION) ? EIconTextures.HEAT_MAP_GROUP_SELECTED
						: EIconTextures.HEAT_MAP_GROUP_NORMAL;

				gl.glPushName(pickingManager.getPickingID(viewID, pickingType, groupIndex));
				Vec3f lowerLeftCorner = new Vec3f(0.0f, groupPositionY - groupHeight, 0.0f);
				Vec3f lowerRightCorner = new Vec3f(0.0f + groupWidth, groupPositionY - groupHeight, 0.0f);
				Vec3f upperRightCorner = new Vec3f(0.0f + groupWidth, groupPositionY, 0.0f);
				Vec3f upperLeftCorner = new Vec3f(0.0f, groupPositionY, 0.0f);

				textureManager.renderTexture(gl, iconTextures, lowerLeftCorner, lowerRightCorner, upperRightCorner,
						upperLeftCorner, 1, 1, 1, 1);

				gl.glPopName();
				if (groupIndex < contentGroupList.size() - 1) {
					gl.glBegin(GL.GL_LINES);
					gl.glVertex3f(lowerLeftCorner.x(), lowerLeftCorner.y(), 1.0f);
					gl.glVertex3f(lowerRightCorner.x(), lowerRightCorner.y(), 1.0f);
					gl.glEnd();
				}

				groupIndex++;
				groupPositionY -= groupHeight;
			}
		}
	}
}
