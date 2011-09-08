package org.caleydo.view.heatmap.uncertainty;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.RecordGroupList;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.SpacerRenderer;
import org.caleydo.core.view.opengl.layout.util.Zoomer;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;

/**
 * Uncertainty overview heat map view.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 * @author Clemens Holzhüter
 */

public class OverviewRenderer extends LayoutRenderer {

	private ClusterRenderer clusterRenderer;

	private final static int CLUSTER_SPACER_SIZE = 10;

	private GLUncertaintyHeatMap uncertaintyHeatMap;

	private GLHeatMap detailHeatMap;

	private ElementLayout lineSeparatorLayout;

	private Column overviewLayout;

	private int selectedClusterIndex = 0;

	private List<Row> clusterLayoutList = new ArrayList<Row>();

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param label
	 * @param viewFrustum
	 */
	public OverviewRenderer(GLUncertaintyHeatMap uncertaintyHeatMap, Column overviewLayout) {
		this.uncertaintyHeatMap = uncertaintyHeatMap;
		this.overviewLayout = overviewLayout;
		Zoomer zoomer = new Zoomer(uncertaintyHeatMap, overviewLayout);
		this.overviewLayout.setZoomer(zoomer);
	}

	public void init() {

		// overviewLayout.clear();
		clusterLayoutList.clear();

		RecordVirtualArray recordVA = uncertaintyHeatMap.getRecordVA();
		RecordGroupList clusterList = recordVA.getGroupList();

		int counter = 0;

		int lastLayoutElement = 0;

		// If the dataset is unclustered yet, the whole VA is given to the
		// detail heat map.
		if (clusterList == null && detailHeatMap != null) {
			detailHeatMap.setRecordVA(recordVA);
			detailHeatMap.setDisplayListDirty();
		}

		if (clusterList != null) {
			// int totalSpacerSize = spacerSize * (clusterList.size() - 1);
			for (int clusterIndex = 0; clusterIndex < clusterList.size(); clusterIndex++) {

				// creatinng Texture for each cluster

				// creating Layout for each cluster
				RecordVirtualArray clusterVA = this.getClusterVA(clusterIndex);
				float ratio = (float) clusterVA.size()
						/ ((float) recordVA.getIndexList().size());

				Row clusterLayout = new Row("clusterLayout_" + counter);
				clusterLayout.setRatioSizeY(ratio);
				clusterLayoutList.add(clusterLayout);

				clusterRenderer = new ClusterRenderer(uncertaintyHeatMap, clusterLayout,
						clusterVA, clusterIndex);
				clusterLayout.setRenderer(clusterRenderer);

				overviewLayout.add(lastLayoutElement, clusterLayout);

				clusterRenderer.init();
				counter++;

				if (clusterIndex < (clusterList.size() - 1)) {
					lineSeparatorLayout = new ElementLayout("lineSeparator");
					PixelGLConverter pixelGLConverter = uncertaintyHeatMap
							.getPixelGLConverter();
					lineSeparatorLayout.setPixelGLConverter(pixelGLConverter);
					lineSeparatorLayout.setPixelSizeY(CLUSTER_SPACER_SIZE);
					lineSeparatorLayout.setRatioSizeX(1);
					lineSeparatorLayout.setRenderer(new SpacerRenderer(false));
					lineSeparatorLayout.setFrameColor(0.0f, 0.0f, 0.0f, 0.3f);

					overviewLayout.add(lastLayoutElement, lineSeparatorLayout);
					// overviewLayout.append(lineSeparatorLayout);
				}

				// Initially the first cluster gets selected
				if (clusterIndex == 0 && detailHeatMap != null) {
					detailHeatMap.setRecordVA(clusterVA);
					detailHeatMap.setDisplayListDirty();
				}
			}
		} else {
			Row clusterLayout = new Row("clusterLayout");
			clusterLayout.setDebug(false);
			clusterLayout.setRatioSizeY(1);

			clusterRenderer = new ClusterRenderer(uncertaintyHeatMap, clusterLayout,
					recordVA, 0);
			clusterLayout.setRenderer(clusterRenderer);
			clusterRenderer.init();
			overviewLayout.add(lastLayoutElement, clusterLayout);
		}

		overviewLayout.updateSubLayout();
	}

	public RecordVirtualArray getClusterVA(int clusterIndex) {
		RecordVirtualArray recordVA = uncertaintyHeatMap.getRecordVA();
		RecordGroupList clusterList = recordVA.getGroupList();
		if (clusterList == null) {
			return recordVA;
		}
		Group group = clusterList.getGroups().get(clusterIndex);

		ArrayList<Integer> clusterGenes = uncertaintyHeatMap.getRecordVA()
				.getIDsOfGroup(group.getGroupID());
		RecordVirtualArray clusterVA = new RecordVirtualArray("Custom", clusterGenes);

		return clusterVA;
	}

	public void setSelectedGroup(int selectedGroup) {
		this.selectedClusterIndex = selectedGroup;
	}

	public float getSelectedClusterY() {

		if (clusterLayoutList.size() == 0)
			return 0;

		return clusterLayoutList.get(selectedClusterIndex).getTranslateY()
				- overviewLayout.getTranslateY();
	}

	public float getSelectedClusterHeight() {

		if (clusterLayoutList.size() == 0)
			return uncertaintyHeatMap.getViewFrustum().getHeight();

		return clusterLayoutList.get(selectedClusterIndex).getSizeScaledY();
	}

	public ArrayList<ClusterRenderer> getClusterRendererList() {
		ArrayList<ClusterRenderer> ret = new ArrayList<ClusterRenderer>();
		for (Row layout : clusterLayoutList) {
			ret.add((ClusterRenderer) layout.getRenderer());
		}
		if (ret.size() == 0) {
			ret.add(clusterRenderer);
		}
		return ret;
	}

	public void setDetailHeatMap(GLHeatMap detailHeatMap) {
		this.detailHeatMap = detailHeatMap;
	}

	public GLUncertaintyHeatMap getUncertaintyHeatMap() {
		return uncertaintyHeatMap;
	}

	@Override
	public void render(GL2 gl) {
	
	}
}
