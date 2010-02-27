package org.caleydo.view.heatmap;

import gleem.linalg.Vec3f;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.selection.ContentGroupList;
import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.data.selection.Group;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.StorageVirtualArray;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.opengl.util.texture.TextureManager;

import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;
import com.sun.opengl.util.texture.TextureData;
import com.sun.opengl.util.texture.TextureIO;

public class HeatMapUtil {

	public static int MAX_SAMPLES_PER_TEXTURE = 2000;

	public static ArrayList<Texture> createHeatMapTextures(ISet set,
			ContentVirtualArray contentVA, StorageVirtualArray storageVA,
			ContentSelectionManager contentSelectionManager) {

		int numSamples = contentVA.size();
		int numStorages = storageVA.size();

		ArrayList<Texture> textures = new ArrayList<Texture>();
		ColorMapping colorMapping = ColorMappingManager.get().getColorMapping(
				EColorMappingType.GENE_EXPRESSION);

		int numSamplesProcessed = 0;
		boolean isNewTexture = true;
		FloatBuffer textureBuffer = null;
		int numSamplesInTexture = 0;

		for (Integer contentIndex : contentVA) {

			if (isNewTexture) {
				numSamplesInTexture = Math.min(MAX_SAMPLES_PER_TEXTURE,
						numSamples - numSamplesProcessed);
				textureBuffer = BufferUtil.newFloatBuffer(numSamplesInTexture
						* numStorages * 4);
				isNewTexture = false;
			}

			for (Integer storageIndex : storageVA) {

				float fOpacity = 1.0f;

				if (contentSelectionManager != null
						&& contentSelectionManager.checkStatus(
								SelectionType.DESELECTED, contentIndex)) {
					fOpacity = 0.3f;
				}
				IStorage storage = set.get(storageIndex);
				float fLookupValue = storage.getFloat(
						EDataRepresentation.NORMALIZED, contentIndex);

				float[] fArMappingColor = colorMapping.getColor(fLookupValue);

				float[] fArRgba = { fArMappingColor[0], fArMappingColor[1],
						fArMappingColor[2], fOpacity };

				textureBuffer.put(fArRgba);
				if (!textureBuffer.hasRemaining()) {
					textureBuffer.rewind();

					TextureData texData = new TextureData(
							GL.GL_RGBA /* internalFormat */,
							numStorages /* height */,
							numSamplesInTexture /* width */, 0 /* border */,
							GL.GL_RGBA /* pixelFormat */,
							GL.GL_FLOAT /* pixelType */, false /* mipmap */,
							false /* dataIsCompressed */,
							false /* mustFlipVertically */, textureBuffer, null);

					Texture texture = TextureIO.newTexture(0);
					texture.updateImage(texData);

					textures.add(texture);
					isNewTexture = true;
				}
			}
			numSamplesProcessed++;
		}

		return textures;
	}

	public static void renderHeatmapTextures(GL gl,
			ArrayList<Texture> textures, float height, float width) {

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
			texture.enable();
			texture.bind();
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S,
					GL.GL_CLAMP);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T,
					GL.GL_CLAMP);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
					GL.GL_NEAREST);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
					GL.GL_NEAREST);
			TextureCoords texCoords = texture.getImageTexCoords();
			gl.glBegin(GL.GL_QUADS);
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
			texture.disable();
		}
	}

	public static void renderGroupBar(GL gl, ContentVirtualArray contentVA,
			float totalHeight, float groupWidth, PickingManager pickingManager,
			int viewID, EPickingType pickingType, TextureManager textureManager) {

		ContentGroupList contentGroupList = contentVA.getGroupList();

		if (contentGroupList != null) {
			float sampleHeight = totalHeight / ((float) contentVA.size());
			float groupPositionY = totalHeight;

			gl.glColor4f(1, 1, 1, 1);
			gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);
			int groupIndex = 0;
			for (Group group : contentGroupList) {
				int numSamplesGroup = group.getNrElements();
				float groupHeight = numSamplesGroup * sampleHeight;
				EIconTextures iconTextures = (group.getSelectionType() == SelectionType.SELECTION) ? EIconTextures.HEAT_MAP_GROUP_SELECTED
						: EIconTextures.HEAT_MAP_GROUP_NORMAL;

				gl.glPushName(pickingManager.getPickingID(viewID, pickingType,
						groupIndex));
				Vec3f lowerLeftCorner = new Vec3f(0.0f, groupPositionY
						- groupHeight, 0.0f);
				Vec3f lowerRightCorner = new Vec3f(0.0f + groupWidth,
						groupPositionY - groupHeight, 0.0f);
				Vec3f upperRightCorner = new Vec3f(0.0f + groupWidth,
						groupPositionY, 0.0f);
				Vec3f upperLeftCorner = new Vec3f(0.0f, groupPositionY, 0.0f);

				textureManager.renderTexture(gl, iconTextures, lowerLeftCorner,
						lowerRightCorner, upperRightCorner, upperLeftCorner, 1,
						1, 1, 1);

				gl.glPopName();
				if (groupIndex < contentGroupList.size() - 1) {
					gl.glBegin(GL.GL_LINES);
					gl.glVertex3f(lowerLeftCorner.x(), lowerLeftCorner.y(),
							lowerLeftCorner.z());
					gl.glVertex3f(lowerRightCorner.x(), lowerRightCorner.y(),
							lowerRightCorner.z());
					gl.glEnd();
				}

				groupIndex++;
				groupPositionY -= groupHeight;
			}
		}
	}
}
