package org.caleydo.view.heatmap.uncertainty;

import static org.caleydo.view.heatmap.HeatMapRenderStyle.SELECTION_Z;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.SpacerRenderer;
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

	public HeatMapTextureRenderer textureRenderer;
	private BarplotTextureRenderer dataUncBarTextureRenderer;
	public BarplotTextureRenderer visUncBarTextureRenderer;

	private GLUncertaintyHeatMap uncertaintyHeatMap;

	private Column clusterHeatMapLayout;
	private Column clusterDataUncBarLayout;
	private Column clusterVisUncBarLayout;

	private Row clusterLayout;

	private RecordVirtualArray clusterVA;

	private int clusterIndex;
	// private int height;
	// private int width;
	//

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

		DimensionVirtualArray dimensionVA = uncertaintyHeatMap.getDimensionVA();
		DataTable set = uncertaintyHeatMap.getDataDomain().getTable();

		clusterHeatMapLayout = new Column("heatmap");
		clusterHeatMapLayout.setRatioSizeX(1f);

		clusterDataUncBarLayout = new Column("bar");
		clusterDataUncBarLayout.setPixelGLConverter(uncertaintyHeatMap
				.getPixelGLConverter());
		clusterDataUncBarLayout.setPixelSizeX(50);

		clusterVisUncBarLayout = new Column("bar2");
		clusterVisUncBarLayout.setPixelGLConverter(uncertaintyHeatMap.getPixelGLConverter());
		clusterVisUncBarLayout.setPixelSizeX(50);

		textureRenderer = new HeatMapTextureRenderer(uncertaintyHeatMap,
				clusterHeatMapLayout);
		clusterHeatMapLayout.setRenderer(textureRenderer);

		dataUncBarTextureRenderer = new BarplotTextureRenderer();
		clusterDataUncBarLayout.setRenderer(dataUncBarTextureRenderer);
		dataUncBarTextureRenderer.setOrientationLeft(true);

		visUncBarTextureRenderer = new BarplotTextureRenderer();
		clusterVisUncBarLayout.setRenderer(visUncBarTextureRenderer);

		clusterLayout.append(clusterVisUncBarLayout);

		{
			ElementLayout lineSeparatorLayout = new ElementLayout("lineSeparator");

			PixelGLConverter pixelGLConverter = uncertaintyHeatMap.getPixelGLConverter();
			lineSeparatorLayout.setPixelGLConverter(pixelGLConverter);
			lineSeparatorLayout.setPixelSizeX(2);
			lineSeparatorLayout.setRenderer(new SpacerRenderer(false));
			lineSeparatorLayout.setFrameColor(0.0f, 0.0f, 0.0f, 0.2f);
			clusterLayout.append(lineSeparatorLayout);

		}
		clusterLayout.append(clusterDataUncBarLayout);
		{
			ElementLayout lineSeparatorLayout = new ElementLayout("lineSeparator");

			PixelGLConverter pixelGLConverter = uncertaintyHeatMap.getPixelGLConverter();
			lineSeparatorLayout.setPixelGLConverter(pixelGLConverter);
			lineSeparatorLayout.setPixelSizeX(2);
			lineSeparatorLayout.setRenderer(new SpacerRenderer(false));
			lineSeparatorLayout.setFrameColor(0.0f, 0.0f, 0.0f, 0.8f);
			clusterLayout.append(lineSeparatorLayout);

		}
		clusterLayout.append(clusterHeatMapLayout);
		{
			ElementLayout lineSeparatorLayout = new ElementLayout("lineSeparator");

			lineSeparatorLayout.setPixelGLConverter(uncertaintyHeatMap.getPixelGLConverter());
			lineSeparatorLayout.setPixelSizeX(2);
			lineSeparatorLayout.setRenderer(new SpacerRenderer(false));
			lineSeparatorLayout.setFrameColor(0.0f, 0.0f, 0.0f, 0.3f);
			clusterLayout.append(lineSeparatorLayout);

		}

		textureRenderer.init(uncertaintyHeatMap, set, clusterVA, dimensionVA, clusterIndex);

		dataUncBarTextureRenderer.init(uncertaintyHeatMap, set, clusterVA, dimensionVA,
				uncertaintyHeatMap.getColorMapper());

		visUncBarTextureRenderer.init(uncertaintyHeatMap, set, clusterVA, dimensionVA,
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

		setMouseOverElements = uncertaintyHeatMap.getContentSelectionManager()
				.getElements(SelectionType.MOUSE_OVER);
		setSelectedElements = uncertaintyHeatMap.getContentSelectionManager()
				.getElements(SelectionType.SELECTION);

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

}
