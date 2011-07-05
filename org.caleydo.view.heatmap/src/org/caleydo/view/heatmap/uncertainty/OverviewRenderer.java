package org.caleydo.view.heatmap.uncertainty;

import java.util.ArrayList;

import org.caleydo.core.data.collection.set.Set;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.group.ContentGroupList;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.LineSeparatorRenderer;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;


/**
 * Uncertainty overview heat map view.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 * @author Clemens Holzhüter
 */

public class OverviewRenderer extends LayoutRenderer {

	private ClusterRenderer clusterHeatMapRenderer;
	private GLUncertaintyHeatMap uncertaintyHeatMap;

	private ElementLayout lineSeparatorLayout;

	private Column overviewLayout;

	private final static int CLUSTER_SPACER_SIZE = 10;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param label
	 * @param viewFrustum
	 */
	public OverviewRenderer(GLUncertaintyHeatMap uncertaintyHeatMap,
			Column overviewLayout) {
		this.uncertaintyHeatMap = uncertaintyHeatMap;
		this.overviewLayout = overviewLayout;
	}

	public void init() {

		overviewLayout.clear();
		
		ContentVirtualArray contentVA = uncertaintyHeatMap.getContentVA();
		ContentGroupList groupList = contentVA.getGroupList();

		int counter = 0;

		int lastLayoutElement = 0;

		if (groupList != null) {
			//int totalSpacerSize = spacerSize * (clusterList.size() - 1);
			for (int groupIndex = 0; groupIndex < groupList.size(); groupIndex++) {

				// creatinng Texture for each cluster

				// creating Layout for each cluster
				ContentVirtualArray clusterVA = this.getClusterVA(groupIndex);
				float ratio = (float) clusterVA.size()
						/ ((float) contentVA.getIndexList().size());
			
				Row clusterLayout = new Row("clusterLayout_" + counter);
				clusterLayout.setDebug(false);
				clusterLayout.setRatioSizeY(ratio);

				clusterHeatMapRenderer = new ClusterRenderer(
						uncertaintyHeatMap, clusterLayout, clusterVA, groupIndex);
				clusterLayout.setRenderer(clusterHeatMapRenderer);

				overviewLayout.add(lastLayoutElement, clusterLayout);

				clusterHeatMapRenderer.init();
				counter++;

				if (groupIndex < (groupList.size() - 1)) {
					lineSeparatorLayout = new ElementLayout("lineSeparator");
					PixelGLConverter pixelGLConverter = uncertaintyHeatMap
							.getParentGLCanvas().getPixelGLConverter();
					lineSeparatorLayout.setPixelGLConverter(pixelGLConverter);
					lineSeparatorLayout.setPixelSizeY(CLUSTER_SPACER_SIZE);
					lineSeparatorLayout.setRatioSizeX(1);
					lineSeparatorLayout.setRenderer(new LineSeparatorRenderer(
							false));
					lineSeparatorLayout.setFrameColor(0.0f, 0.0f, 0.0f, 0.3f);
					
					overviewLayout.add(lastLayoutElement, lineSeparatorLayout);
					// overviewLayout.append(lineSeparatorLayout);
				}
			}
		} else {
			Row clusterLayout = new Row("clusterLayout");
			clusterLayout.setDebug(false);
			clusterLayout.setRatioSizeY(1);

			clusterHeatMapRenderer = new ClusterRenderer(uncertaintyHeatMap,
					clusterLayout, contentVA, 0);
			clusterLayout.setRenderer(clusterHeatMapRenderer);
			clusterHeatMapRenderer.init();
			overviewLayout.add(lastLayoutElement, clusterLayout);
		}
		
		overviewLayout.updateSubLayout();
	}

	public ContentVirtualArray getClusterVA(int clusterIndex) {
		ContentVirtualArray contentVA = uncertaintyHeatMap.getContentVA();
		ContentGroupList clusterList = contentVA.getGroupList();
		if (clusterList == null) {
			return contentVA;
		}
		Group group = clusterList.getGroups().get(clusterIndex);

		ArrayList<Integer> clusterGenes = uncertaintyHeatMap.getContentVA()
				.getIDsOfGroup(group.getID());
		ContentVirtualArray clusterVA = new ContentVirtualArray(Set.CONTENT,
				clusterGenes);


		return clusterVA;
	}
}
