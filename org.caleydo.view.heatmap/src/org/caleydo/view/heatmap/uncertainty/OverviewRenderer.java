package org.caleydo.view.heatmap.uncertainty;

import java.util.ArrayList;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.Set;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.StorageVirtualArray;
import org.caleydo.core.data.virtualarray.group.ContentGroupList;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.view.heatmap.texture.HeatMapTextureRenderer;

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

	private Row clusterLayout;

	private ViewFrustum viewFrustum;
	private LayoutManager templateRenderer;

	private Object template;

	private Column overviewLayout;
	
	
	private ContentGroupList clusterList;
	private ContentVirtualArray contentVA;

	private int spacerSize = 2;
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

	public void init(GL2 gl) {
		ContentVirtualArray contentVA = uncertaintyHeatMap.getContentVA();
		ContentGroupList clusterList = contentVA.getGroupList();

		int counter = 0;

		
		this.overviewLayout = overviewLayout;
		StorageVirtualArray storageVA = uncertaintyHeatMap.getStorageVA();
		ISet set = uncertaintyHeatMap.getDataDomain().getSet();

		int lastLayoutElement = overviewLayout.size();
		if (clusterList != null) {
			int totalSpacerSize = spacerSize * (clusterList.size() - 1);
			for (int i = 0; i< clusterList.size(); i++ ) {
				// creatinng Texture for each cluster
				
				// creating Layout for each cluster
				ContentVirtualArray clusterVA = this.getClusterVA(i);
				float ratio = (float) clusterVA.size()
						/ (float) contentVA.getIndexList().size();
				clusterLayout = new Row("clusterLayout_" + counter);
				clusterLayout.setDebug(false);
				clusterLayout.setRatioSizeY(ratio);

				clusterHeatMapRenderer = new ClusterRenderer(uncertaintyHeatMap, clusterLayout, clusterVA );
				clusterLayout.setRenderer(clusterHeatMapRenderer);

				overviewLayout.add(lastLayoutElement, clusterLayout);

				clusterHeatMapRenderer.init(gl);
				counter++;
			}
		} else {
			
			clusterLayout = new Row("clusterLayout");
			clusterLayout.setDebug(false);
			clusterLayout.setRatioSizeY(1);

			clusterHeatMapRenderer = new ClusterRenderer(uncertaintyHeatMap, clusterLayout, contentVA );
			clusterLayout.setRenderer(clusterHeatMapRenderer);
			clusterHeatMapRenderer.init(gl);
			overviewLayout.add(lastLayoutElement, clusterLayout);


		}
	}

	@Override
	public void render(GL2 gl) {

	}

	public ContentVirtualArray getClusterVA(int clusterIndex) {
		ContentVirtualArray contentVA = uncertaintyHeatMap.getContentVA();
		ContentGroupList clusterList = contentVA.getGroupList();	
		if (clusterList == null) {
			return contentVA;
		}
		Group group = clusterList.getGroups().get(clusterIndex);
		
		ArrayList<Integer> clusterGenes = uncertaintyHeatMap.getContentVA().getIDsOfGroup(group
				.getID());
		ContentVirtualArray clusterVA = new ContentVirtualArray(
				Set.CONTENT, clusterGenes);

		return clusterVA;
	}

}
