package org.caleydo.view.heatmap.uncertainty;

import java.util.ArrayList;

import org.caleydo.core.data.collection.set.Set;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.group.ContentGroupList;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutManager;
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

	private GLHeatMap detailHeatMap;
	private ElementLayout lineSeparatorLayout;

	private ViewFrustum viewFrustum;
	private LayoutManager templateRenderer;

	private Object template;

	private Column overviewLayout;

	private ContentGroupList clusterList;
	private ContentVirtualArray contentVA;

	private int spacerSize = 3;

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

//		overviewLayout.clear();
		
		ContentVirtualArray contentVA = uncertaintyHeatMap.getContentVA();
		ContentGroupList clusterList = contentVA.getGroupList();

		int counter = 0;

		int lastLayoutElement = 0;//overviewLayout.size();

		if (clusterList != null) {
			int totalSpacerSize = spacerSize * (clusterList.size() - 1);
			for (int i = 0; i < clusterList.size(); i++) {

				// creatinng Texture for each cluster

				// creating Layout for each cluster
				ContentVirtualArray clusterVA = this.getClusterVA(i);
				float ratio = (float) clusterVA.size()
						/ ((float) contentVA.getIndexList().size());
			
				Row clusterLayout = new Row("clusterLayout_" + counter);
				clusterLayout.setDebug(false);
				clusterLayout.setRatioSizeY(ratio);

				clusterHeatMapRenderer = new ClusterRenderer(
						uncertaintyHeatMap, clusterLayout, clusterVA);
				clusterLayout.setRenderer(clusterHeatMapRenderer);

				overviewLayout.add(lastLayoutElement, clusterLayout);

				clusterHeatMapRenderer.init();
				counter++;

				if (i < (clusterList.size() - 1)) {
					lineSeparatorLayout = new ElementLayout("lineSeparator");
					PixelGLConverter pixelGLConverter = uncertaintyHeatMap
							.getParentGLCanvas().getPixelGLConverter();
					lineSeparatorLayout.setPixelGLConverter(pixelGLConverter);
					lineSeparatorLayout.setPixelSizeY(spacerSize);
					lineSeparatorLayout.setRatioSizeX(1);
					lineSeparatorLayout.setRenderer(new LineSeparatorRenderer(
							false));
					lineSeparatorLayout.setFrameColor(0.0f, 0.0f, 0.0f, 0.3f);
					
					overviewLayout.add(lastLayoutElement, lineSeparatorLayout);
					// overviewLayout.append(lineSeparatorLayout);
				}
				
				// JUST FOR TESTING
				if (i == 0)
					detailHeatMap.setContentVA(clusterVA);
			}
		} else {
			Row clusterLayout = new Row("clusterLayout");
			clusterLayout.setDebug(false);
			clusterLayout.setRatioSizeY(1);

			clusterHeatMapRenderer = new ClusterRenderer(uncertaintyHeatMap,
					clusterLayout, contentVA);
			clusterLayout.setRenderer(clusterHeatMapRenderer);
			clusterHeatMapRenderer.init();
			overviewLayout.add(lastLayoutElement, clusterLayout);
		}
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

	public void setDetailHeatMap(GLHeatMap detailHeatMap) {
		this.detailHeatMap = detailHeatMap;		
	}
}
