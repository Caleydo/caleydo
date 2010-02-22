package org.caleydo.view.heatmap;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.StorageVirtualArray;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;

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

		// iSamplesPerTexture = (int) Math.ceil((double) iTextureHeight
		// / iNrTextures);

		// FloatBuffer[] FbTemp = new FloatBuffer[iNrTextures];

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

				float fOpacity = 0;

				if (contentSelectionManager.checkStatus(
						SelectionType.DESELECTED, contentIndex)) {
					fOpacity = 0.3f;
				} else {
					fOpacity = 1.0f;
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

		// for (int itextures = 0; itextures < iNrTextures; itextures++) {
		//
		// if (itextures == iNrTextures - 1) {
		// iAlNumberSamples.add(iTextureHeight - iSamplesPerTexture
		// * itextures);
		// FbTemp[itextures] = BufferUtil
		// .newFloatBuffer((iTextureHeight - iSamplesPerTexture
		// * itextures)
		// * iTextureWidth * 4);
		// } else {
		// iAlNumberSamples.add(iSamplesPerTexture);
		// FbTemp[itextures] = BufferUtil
		// .newFloatBuffer(iSamplesPerTexture * iTextureWidth * 4);
		// }
		// }
		//
		// int iCount = 0;
		// int iTextureCounter = 0;
		//
		// for (Integer iContentIndex : contentVA) {
		// iCount++;
		// for (Integer iStorageIndex : storageVA) {
		// if (contentSelectionManager.checkStatus(
		// SelectionType.DESELECTED, iContentIndex)) {
		// fOpacity = 0.3f;
		// } else {
		// fOpacity = 1.0f;
		// }
		//
		// fLookupValue = set.get(iStorageIndex).getFloat(
		// EDataRepresentation.NORMALIZED, iContentIndex);
		//
		// float[] fArMappingColor = colorMapper.getColor(fLookupValue);
		//
		// float[] fArRgba = { fArMappingColor[0], fArMappingColor[1],
		// fArMappingColor[2], fOpacity };
		//
		// FbTemp[iTextureCounter].put(fArRgba);
		// }
		// if (iCount >= iAlNumberSamples.get(iTextureCounter)) {
		// FbTemp[iTextureCounter].rewind();
		//
		// TextureData texData = new TextureData(
		// GL.GL_RGBA /* internalFormat */,
		// iTextureWidth /* height */, iAlNumberSamples
		// .get(iTextureCounter) /* width */,
		// 0 /* border */, GL.GL_RGBA /* pixelFormat */,
		// GL.GL_FLOAT /* pixelType */, false /* mipmap */,
		// false /* dataIsCompressed */,
		// false /* mustFlipVertically */, FbTemp[iTextureCounter],
		// null);
		//
		// tempTextur = TextureIO.newTexture(0);
		// tempTextur.updateImage(texData);
		//
		// AlTextures.add(tempTextur);
		//
		// iTextureCounter++;
		// iCount = 0;
		// }
		// }
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
}
