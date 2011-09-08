package org.caleydo.view.heatmap.heatmap.renderer.texture;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.GLProfile;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.util.mapping.color.ColorMapper;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.view.heatmap.uncertainty.GLUncertaintyHeatMap;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

public class BarplotTextureRenderer extends LayoutRenderer {

	private final static int MAX_SAMPLES_PER_TEXTURE = 2000;

	private int numberOfTextures = 0;

	private int numberOfElements = 0;

	private int samplesPerTexture = 0;

	/** array of textures for holding the data samples */
	private ArrayList<Texture> textures = new ArrayList<Texture>();

	private ArrayList<Integer> numberSamples = new ArrayList<Integer>();

	private boolean orientation = true;

	private DimensionVirtualArray dimensionVA;

	private RecordVirtualArray recordVA;

	private DataTable set;

	private float[] lightCertain = GLUncertaintyHeatMap.DATA_VALID[0];
	private float[] lightUncertain = GLUncertaintyHeatMap.getUncertaintyColor(0);

	private GLUncertaintyHeatMap glUncHeatmap;

	public void setOrientationLeft(boolean tr) {
		orientation = tr;
	}

	public void initTextures(ArrayList<Float> uncertaintyVA) {

		if (dimensionVA == null || recordVA == null)
			return;

		int textureWidth = dimensionVA.size();
		int textureHeight = numberOfElements = recordVA.size();
		if (uncertaintyVA != null) {
			textureHeight = numberOfElements = uncertaintyVA.size();
		}

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

		FloatBuffer[] floatBuffer = new FloatBuffer[numberOfTextures];

		for (int iTexture = 0; iTexture < numberOfTextures; iTexture++) {

			if (iTexture == numberOfTextures - 1) {
				numberSamples.add(textureHeight - samplesPerTexture * iTexture);
				floatBuffer[iTexture] = FloatBuffer
						.allocate((textureHeight - samplesPerTexture * iTexture)
								* textureWidth * 4);
			} else {
				numberSamples.add(samplesPerTexture);
				floatBuffer[iTexture] = FloatBuffer.allocate(samplesPerTexture
						* textureWidth * 4);
			}
		}

		int contentCount = 0;
		int textureCounter = 0;

		for (int index = 0; index < numberOfElements; index++) {

			float uncertainty;
			contentCount++;

			if (uncertaintyVA != null) {
				uncertainty = uncertaintyVA.get(index);
			} else if (glUncHeatmap.isMaxUncertaintyCalculated()) {
				uncertainty = glUncHeatmap.getMaxUncertainty(recordVA.get(index));
			} else
				uncertainty = 1; // TODO: check why this is necessary. actually
									// this should never happen, because the
									// plot should not be created in the first
									// place

			for (int i = 0; i < textureWidth; i++) {
				float[] rgba = new float[4];
				if (((float) i / textureWidth) > uncertainty) {
					if (uncertainty >= 1)
						rgba = this.lightCertain;
					else
						rgba = this.lightUncertain;
				} else {
					rgba = GLUncertaintyHeatMap.BACKGROUND;
				}

				floatBuffer[textureCounter].put(rgba);
			}
			if (contentCount >= numberSamples.get(textureCounter)) {
				floatBuffer[textureCounter].rewind();

				TextureData texData = new TextureData(GLProfile.getDefault(),
						GL2.GL_RGBA /* internalFormat */, textureWidth /* height */,
						numberSamples.get(textureCounter) /* width */, 0 /* border */,
						GL2.GL_RGBA /* pixelFormat */, GL2.GL_FLOAT /* pixelType */,
						true /* mipmap */, false /* dataIsCompressed */,
						false /* mustFlipVertically */, floatBuffer[textureCounter], null);

				tempTexture = TextureIO.newTexture(0);
				tempTexture.updateImage(texData);

				textures.add(tempTexture);

				textureCounter++;
				contentCount = 0;
			}
		}

	}

	public void init(GLUncertaintyHeatMap glUncHeatmap, DataTable set,
			RecordVirtualArray recordVA, DimensionVirtualArray dimensionVA,
			ColorMapper colorMapper) {

		this.glUncHeatmap = glUncHeatmap;
		this.dimensionVA = dimensionVA;
		this.recordVA = recordVA;
		this.set = set;

		initTextures(null);
	}

	@Override
	public void render(GL2 gl) {

		float yOffset = 0.0f;

		// fHeight = viewFrustum.getHeight();
		// fWidth = renderStyle.getWidthLevel1();

		float elementHeight = y / numberOfElements;
		float step = 0;

		for (int i = 0; i < numberOfTextures; i++) {

			step = elementHeight * numberSamples.get(numberOfTextures - i - 1);

			textures.get(numberOfTextures - i - 1).enable();
			textures.get(numberOfTextures - i - 1).bind();
			gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
			gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
			gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
			gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER,
					GL2.GL_NEAREST);
			gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER,
					GL2.GL_NEAREST);
			TextureCoords texCoords = textures.get(numberOfTextures - i - 1)
					.getImageTexCoords();

			// gl.glPushName(pickingManager.getPickingID(uniqueID,
			// EPickingType.HIER_HEAT_MAP_TEXTURE_SELECTION, numberOfTextures -
			// i));
			float x1, x2 = 0;
			if (orientation) {
				x1 = 0;
				x2 = x;
			} else {
				x1 = x;
				x2 = 0;
			}
			gl.glBegin(GL2.GL_QUADS);
			gl.glColor4f(1f, 1f, 1f, 1f);

			gl.glTexCoord2d(texCoords.left(), texCoords.top());
			gl.glVertex3f(x1, yOffset, 0);
			gl.glTexCoord2d(texCoords.left(), texCoords.bottom());
			gl.glVertex3f(x1, yOffset + step, 0);
			gl.glTexCoord2d(texCoords.right(), texCoords.bottom());
			gl.glVertex3f(x2, yOffset + step, 0);
			gl.glTexCoord2d(texCoords.right(), texCoords.top());
			gl.glVertex3f(x2, yOffset, 0);
			gl.glEnd();
			// gl.glPopName();

			yOffset += step;
			textures.get(numberOfTextures - i - 1).disable();
		}
	}

	public void setLightUnCertainColor(float[] light) {
		this.lightUncertain = light;

	}

	public void setLightCertainColor(float[] light) {
		this.lightCertain = light;

	}

}
