package org.caleydo.view.heatmap.texture;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.GLProfile;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.StorageVirtualArray;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.util.mapping.color.ColorMapper;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.view.heatmap.uncertainty.GLUncertaintyHeatMap;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

public class HeatMapTextureRenderer extends LayoutRenderer{

	private final static int MAX_SAMPLES_PER_TEXTURE = 2000;
	
	private int numberOfTextures = 0;

	private int numberOfElements = 0;
	
	private int samplesPerTexture = 0;

	/** array of textures for holding the data samples */
	private ArrayList<Texture> textures = new ArrayList<Texture>();

	private ArrayList<Integer> numberSamples = new ArrayList<Integer>();
	
	private GLUncertaintyHeatMap uncertaintyHeatMap;
	
	private PickingManager pickingManager = GeneralManager.get().getViewGLCanvasManager().getPickingManager();

	private int groupIndex;
	
	
	/**
	 * Init textures, build array of textures used for holding the whole samples
	 */
	public void init(GLUncertaintyHeatMap uncertaintyHeatMap, ISet set, ContentVirtualArray contentVA,
			StorageVirtualArray storageVA, int groupIndex) {

		this.uncertaintyHeatMap = uncertaintyHeatMap;
		ColorMapper colorMapper = uncertaintyHeatMap.getColorMapper();
		this.groupIndex = groupIndex;
		
		int textureHeight = numberOfElements = contentVA.size();
		int textureWidth = storageVA.size();
		
		numberOfTextures = (int) Math.ceil((double) numberOfElements
				/ MAX_SAMPLES_PER_TEXTURE);
		
		if (numberOfTextures <= 1)
			samplesPerTexture = numberOfElements;
		else
			samplesPerTexture = MAX_SAMPLES_PER_TEXTURE;
		
		textures.clear();
		numberSamples.clear();

		Texture tempTexture;

		samplesPerTexture = (int) Math.ceil((double) textureHeight / numberOfTextures);

		float fLookupValue = 0;

		FloatBuffer[] floatBuffer = new FloatBuffer[numberOfTextures];

		for (int itextures = 0; itextures < numberOfTextures; itextures++) {

			if (itextures == numberOfTextures - 1) {
				numberSamples.add(textureHeight - samplesPerTexture * itextures);
				floatBuffer[itextures] = FloatBuffer
						.allocate((textureHeight - samplesPerTexture * itextures)
								* textureWidth * 4);
			} else {
				numberSamples.add(samplesPerTexture);
				floatBuffer[itextures] = FloatBuffer.allocate(samplesPerTexture
						* textureWidth * 4);
			}
		}

		int contentCount = 0;
		int textureCounter = 0;
		float opacity = 1;

		for (Integer contentIndex : contentVA) {
			contentCount++;
			for (Integer storageIndex : storageVA) {
				// if
				// (contentSelectionManager.checkStatus(SelectionType.DESELECTED,
				// iContentIndex)) {
				// fOpacity = 0.3f;
				// } else {
				// fOpacity = 1.0f;
				// }

				fLookupValue = set.get(storageIndex).getFloat(
						EDataRepresentation.NORMALIZED, contentIndex);

				float[] mappingColor = colorMapper.getColor(fLookupValue);

				float[] rgba = { mappingColor[0], mappingColor[1], mappingColor[2],
						opacity };

				floatBuffer[textureCounter].put(rgba);
			}
			if (contentCount >= numberSamples.get(textureCounter)) {
				floatBuffer[textureCounter].rewind();

				TextureData texData = new TextureData(GLProfile.getDefault(),
						GL2.GL_RGBA /* internalFormat */, textureWidth /* height */,
						numberSamples.get(textureCounter) /* width */, 0 /* border */,
						GL2.GL_RGBA /* pixelFormat */, GL2.GL_FLOAT /* pixelType */,
						false /* mipmap */, false /* dataIsCompressed */,
						false /* mustFlipVertically */, floatBuffer[textureCounter], null);

				tempTexture = TextureIO.newTexture(0);
				tempTexture.updateImage(texData);

				textures.add(tempTexture);

				textureCounter++;
				contentCount = 0;
			}
		}
	}

	@Override
	public void render(GL2 gl) {

		float yOffset = 0.0f;

//		fHeight = viewFrustum.getHeight();
//		fWidth = renderStyle.getWidthLevel1();

		float elementHeight = y / numberOfElements;
		float step = 0;

		gl.glColor4f(1f, 1f, 0f, 1f);

		for (int i = 0; i < numberOfTextures; i++) {

			step = elementHeight * numberSamples.get(numberOfTextures - i - 1);

			textures.get(numberOfTextures - i - 1).enable();
			textures.get(numberOfTextures - i - 1).bind();
			gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
			gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
			gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER,
					GL2.GL_NEAREST);
			gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER,
					GL2.GL_NEAREST);
			TextureCoords texCoords = textures.get(numberOfTextures - i - 1)
					.getImageTexCoords();

			gl.glPushName(pickingManager.getPickingID(uncertaintyHeatMap.getID(),
					EPickingType.HEAT_MAP_CLUSTER_GROUP, groupIndex));
			gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2d(texCoords.left(), texCoords.top());
			gl.glVertex3f(0, yOffset, 0);
			gl.glTexCoord2d(texCoords.left(), texCoords.bottom());
			gl.glVertex3f(0, yOffset + step, 0);
			gl.glTexCoord2d(texCoords.right(), texCoords.bottom());
			gl.glVertex3f(x, yOffset + step, 0);
			gl.glTexCoord2d(texCoords.right(), texCoords.top());
			gl.glVertex3f(x, yOffset, 0);
			gl.glEnd();
			gl.glPopName();

			yOffset += step;
			textures.get(numberOfTextures - i - 1).disable();
		}
	}
}
