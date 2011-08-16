package org.caleydo.view.heatmap.heatmap.renderer.texture;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.GLProfile;

import org.caleydo.core.data.collection.dimension.DataRepresentation;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.mapping.color.ColorMapper;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.picking.PickingManager;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.view.heatmap.uncertainty.GLUncertaintyHeatMap;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

public class HeatMapTextureRenderer extends LayoutRenderer {

	private final static int MAX_SAMPLES_PER_TEXTURE = 2000;

	private int numberOfTextures = 0;

	private int numberOfElements = 0;

	private int samplesPerTexture = 0;

	/** array of textures for holding the data samples */
	private ArrayList<Texture> textures = new ArrayList<Texture>();

	private ArrayList<Integer> numberSamples = new ArrayList<Integer>();

	private GLUncertaintyHeatMap uncertaintyHeatMap;

	private PickingManager pickingManager = GeneralManager.get().getViewManager()
			.getPickingManager();

	private int groupIndex;
	private FloatBuffer[] floatBuffer;

	private int numberOfExpirments;

	public Column heatmapLayout;

	private RecordVirtualArray recordVA;

	private DimensionVirtualArray dimensionVA;

	private DataTable table;

	public HeatMapTextureRenderer(GLUncertaintyHeatMap uncertaintyHeatMap,
			Column heatmapLayout) {

		this.uncertaintyHeatMap = uncertaintyHeatMap;
		this.heatmapLayout = heatmapLayout;
		// selectionUpdateListener.setExclusiveDataDomainType(dataDomain.getDataDomainType());

	}

	/*
	 * Init textures, build array of textures used for holding the whole samples
	 */
	public void init(GLUncertaintyHeatMap uncertaintyHeatMap, DataTable table,
			RecordVirtualArray recordVA, DimensionVirtualArray dimensionVA, int groupIndex) {

		this.recordVA = recordVA;
		this.dimensionVA = dimensionVA;
		this.table = table;

		this.uncertaintyHeatMap = uncertaintyHeatMap;
		ColorMapper colorMapper = uncertaintyHeatMap.getColorMapper();
		this.groupIndex = groupIndex;

		int textureHeight = numberOfElements = recordVA.size();
		int textureWidth = numberOfExpirments = dimensionVA.size();

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

		floatBuffer = new FloatBuffer[numberOfTextures];

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

		for (Integer recordIndex : recordVA) {
			contentCount++;
			for (Integer dimensionIndex : dimensionVA) {
				// if
				// (contentSelectionManager.checkStatus(SelectionType.DESELECTED,
				// recordIndex)) {
				// fOpacity = 0.3f;
				// } else {
				// fOpacity = 1.0f;
				// }

				fLookupValue = table.get(dimensionIndex).getFloat(
						uncertaintyHeatMap.getRenderingRepresentation(), recordIndex);

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

		// fHeight = viewFrustum.getHeight();
		// fWidth = renderStyle.getWidthLevel1();

		float elementHeight = y / numberOfElements;
		float step = 0;

		gl.glColor4f(1f, 1f, 0f, 1f);

		for (int i = 0; i < numberOfTextures; i++) {

			step = elementHeight * numberSamples.get(numberOfTextures - i - 1);
			renderTexture(gl, textures.get(numberOfTextures - i - 1), 0, yOffset, x,
					yOffset + step);

			yOffset += step;
		}

	}

	private void renderTexture(GL2 gl, Texture texture, float x, float y, float width,
			float height) {

		texture.enable();
		texture.bind();
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
		TextureCoords texCoords = texture.getImageTexCoords();

		gl.glPushName(pickingManager.getPickingID(uncertaintyHeatMap.getID(),
				PickingType.HEAT_MAP_RECORD_GROUP, groupIndex));
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
		gl.glPopName();

		texture.disable();
	}

	/**
	 * Render marker next to OverviewBar for visualization of selected elements
	 * in the data set
	 * 
	 * @param gl
	 */
	public float getUncertaintyForLine(int imageLine, int width, int height) {

		float maxUncertainty = 0;
		float val = 0;
		float uncertainty = 0;

		float ratio = (float) numberOfElements / (float) height;
		int startGene = (int) ((ratio * imageLine) - (Math.round(ratio / 2f)));
		int endGene = (int) ((ratio * imageLine) + (Math.round(ratio / 2f)));
		startGene = startGene < 0 ? 0 : startGene;
		endGene = endGene > numberOfElements - 1 ? numberOfElements - 1 : endGene;

		for (int exps = 0; exps < numberOfExpirments; exps++) {
			val = 0;

			for (int i = startGene; i < endGene; i++) {
				// byte[] abgr = new byte[4];

				val = val
						+ ((table.get(dimensionVA.get(exps)).getFloat(
								DataRepresentation.NORMALIZED, recordVA.get(i))));
			}
			// buffer.get(abgr, i * numberOfExpirments * 4 + exps * 4, 4);

			// getting avr over genes
			val = val / (float) (endGene - startGene);
			// unc = difference
			uncertainty = 0;
			for (int i = startGene; i < endGene; i++) {
				float tempVal = table.get(dimensionVA.get(exps)).getFloat(
						DataRepresentation.NORMALIZED, recordVA.get(i));
				uncertainty = Math.abs(val - tempVal);
				if (uncertainty > maxUncertainty) {
					maxUncertainty = uncertainty;
				}
			}
			// uncertainty = uncertainty / (float) (endGene - startGene);;
			// System.out.println(maxUncertainty);
		}

		return 1 - (maxUncertainty * 2);
	}

}
