package org.caleydo.view.heatmap.uncertainty;

import static org.caleydo.view.heatmap.HeatMapRenderStyle.SELECTION_Z;

import java.util.ArrayList;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.view.heatmap.heatmap.renderer.texture.BarplotTextureRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.texture.HeatMapTextureRenderer;

/**
 * Uncertainty overview heat map view.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 * @author Clemens Holzhüter
 */

public class ClusterRenderer extends LayoutRenderer {

	private HeatMapTextureRenderer textureRenderer;

	private BarplotTextureRenderer dataUncBarTextureRenderer;

	private BarplotTextureRenderer visUncBarTextureRenderer;

	private GLUncertaintyHeatMap uncertaintyHeatMap;

	private Column clusterHeatMapLayout;
	private Column clusterDataUncBarLayout;
	private Column clusterVisUncBarLayout;

	private Row clusterLayout;

	private RecordVirtualArray clusterVA;

	private int clusterIndex;

	private java.util.Set<Integer> setMouseOverElements;
	private java.util.Set<Integer> setSelectedElements;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param label
	 * @param viewFrustum
	 */
	public ClusterRenderer(GLUncertaintyHeatMap uncertaintyHeatMap, Row clusterLayout,
			RecordVirtualArray clusterVA, int clusterIndex) {

		this.uncertaintyHeatMap = uncertaintyHeatMap;
		this.clusterLayout = clusterLayout;
		this.clusterVA = clusterVA;
		this.clusterIndex = clusterIndex;
	}

	public void init() {

		DimensionVirtualArray dimensionVA = uncertaintyHeatMap.getDataContainer()
				.getDimensionPerspective().getVirtualArray();
		DataTable table = uncertaintyHeatMap.getDataDomain().getTable();

		clusterHeatMapLayout = new Column("heatmap");
		clusterHeatMapLayout.setRatioSizeX(1f);

		clusterVisUncBarLayout = new Column("visual uncertainty bar");
		clusterVisUncBarLayout.setPixelGLConverter(uncertaintyHeatMap
				.getPixelGLConverter());
		clusterVisUncBarLayout.setPixelSizeX(50);

		textureRenderer = new HeatMapTextureRenderer(uncertaintyHeatMap);
		clusterHeatMapLayout.setRenderer(textureRenderer);

		visUncBarTextureRenderer = new BarplotTextureRenderer();
		clusterVisUncBarLayout.setRenderer(visUncBarTextureRenderer);

		clusterLayout.append(clusterVisUncBarLayout);

		// only add data uncertainty plot if an uncertainty in data space is
		// available
		if (uncertaintyHeatMap.isMaxUncertaintyCalculated()) {

			clusterDataUncBarLayout = new Column("data uncertainty bar");
			clusterDataUncBarLayout.setPixelGLConverter(uncertaintyHeatMap
					.getPixelGLConverter());
			clusterDataUncBarLayout.setPixelSizeX(50);

			dataUncBarTextureRenderer = new BarplotTextureRenderer();
			clusterDataUncBarLayout.setRenderer(dataUncBarTextureRenderer);
			dataUncBarTextureRenderer.setOrientationLeft(true);

			clusterLayout.append(clusterDataUncBarLayout);

			dataUncBarTextureRenderer.init(uncertaintyHeatMap, table, clusterVA,
					dimensionVA, uncertaintyHeatMap.getColorMapper());
		}

		clusterLayout.append(clusterHeatMapLayout);

		textureRenderer.init(uncertaintyHeatMap.getRenderingRepresentation());
		textureRenderer.setGroupIndex(clusterIndex);

		visUncBarTextureRenderer.init(uncertaintyHeatMap, table, clusterVA, dimensionVA,
				uncertaintyHeatMap.getColorMapper());

		visUncBarTextureRenderer.setLightCertainColor(GLUncertaintyHeatMap.VIS_UNC);
		visUncBarTextureRenderer.setLightUnCertainColor(GLUncertaintyHeatMap.VIS_UNC);
		// visUncBarTextureRenderer.setDarkColor(uncertaintyHeatMap.darkDark);
	}

	@Override
	public void render(GL2 gl) {
		renderSelectedElementsLevel1(gl);
	}

	private void renderSelectedElementsLevel1(GL2 gl) {
		float height = y;

		float heightElem = height / clusterVA.size();

		setMouseOverElements = uncertaintyHeatMap.getRecordSelectionManager()
				.getElements(SelectionType.MOUSE_OVER);
		setSelectedElements = uncertaintyHeatMap.getRecordSelectionManager().getElements(
				SelectionType.SELECTION);

		gl.glLineWidth(2f);

		for (Integer mouseOverElement : setMouseOverElements) {

			int index = clusterVA.indexOf(mouseOverElement);
			if (index < 0)
				break;

			// if ((index >= iFirstSampleLevel1 && index <= iLastSampleLevel1)
			// == false) {
			gl.glColor4fv(SelectionType.MOUSE_OVER.getColor(), 0);
			gl.glBegin(GL2.GL_LINES);

			gl.glVertex3f(0, height - heightElem * index, SELECTION_Z);
			gl.glVertex3f(x, height - heightElem * index, SELECTION_Z);
			gl.glEnd();
			// }
		}

		for (Integer selectedElement : setSelectedElements) {

			int index = clusterVA.indexOf(selectedElement);
			if (index < 0)
				break;

			// if ((index >= iFirstSampleLevel1 && index <= iLastSampleLevel1)
			// == false) {
			gl.glColor4fv(SelectionType.SELECTION.getColor(), 0);
			gl.glBegin(GL2.GL_LINES);
			gl.glVertex3f(0, height - heightElem * index, SELECTION_Z);
			gl.glVertex3f(x, height - heightElem * index, SELECTION_Z);
			gl.glEnd();
			// }
		}
	}

	public void updateVisualUncertainty(final GL2 gl,
			final PixelGLConverter pixelGLConverter) {
		if (textureRenderer != null && visUncBarTextureRenderer != null) {

			visUncBarTextureRenderer.initTextures(calcVisualUncertainty(gl,
					pixelGLConverter, textureRenderer));
		}
	}

	private ArrayList<Float> calcVisualUncertainty(final GL2 gl,
			final PixelGLConverter pixelGLConverter, HeatMapTextureRenderer renderer) {

		ArrayList<Float> visualUncertainty = new ArrayList<Float>();

		float scaledX = renderer.getLayout().getSizeScaledX();
		float scaledY = renderer.getLayout().getSizeScaledY();

		int width = pixelGLConverter.getPixelWidthForGLWidth(scaledX);

		int pixelHeight = pixelGLConverter.getPixelWidthForGLWidth(scaledY);

		for (int pixel = 0; pixel < pixelHeight; pixel++) {
			// visualUncertainty.add(renderer.getVisualUncertaintyForLine(pixel,
			// width,
			// pixelHeight));
		}

		return visualUncertainty;
	}
}
