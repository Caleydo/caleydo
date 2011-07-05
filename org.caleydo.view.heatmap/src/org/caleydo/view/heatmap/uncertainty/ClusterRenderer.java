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
import org.caleydo.view.heatmap.texture.BarplotTextureRenderer;
import org.caleydo.view.heatmap.texture.HeatMapTextureRenderer;

/**
 * Uncertainty overview heat map view.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 * @author Clemens Holzhüter
 */

public class ClusterRenderer extends LayoutRenderer {

	private HeatMapTextureRenderer textureRenderer;
	private BarplotTextureRenderer barTextureRenderer;
	private GLUncertaintyHeatMap uncertaintyHeatMap;

	private Column clusterHeatMapLayout;
	private Column clusterBarLayout;

	private ViewFrustum viewFrustum;
	private LayoutManager templateRenderer;

	private Object template;

	private Row clusterLayout;

	private ContentVirtualArray clusterVA;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param label
	 * @param viewFrustum
	 */
	public ClusterRenderer(GLUncertaintyHeatMap uncertaintyHeatMap,
			Row clusterLayout, ContentVirtualArray clusterVA) {
		this.uncertaintyHeatMap = uncertaintyHeatMap;
		this.clusterLayout = clusterLayout;
		this.clusterVA = clusterVA;
	}

	public void init() {
		ContentVirtualArray contentVA = uncertaintyHeatMap.getContentVA();
		ContentGroupList clusterList = contentVA.getGroupList();

		int counter = 0;

		StorageVirtualArray storageVA = uncertaintyHeatMap.getStorageVA();
		ISet set = uncertaintyHeatMap.getDataDomain().getSet();

		clusterHeatMapLayout = new Column("heatmap");
		clusterHeatMapLayout.setRatioSizeX(0.8f);

		clusterBarLayout = new Column("bar");
		clusterBarLayout.setRatioSizeX(0.2f);

		textureRenderer = new HeatMapTextureRenderer();
		clusterHeatMapLayout.setRenderer(textureRenderer);

		barTextureRenderer = new BarplotTextureRenderer();
		clusterBarLayout.setRenderer(barTextureRenderer);

		clusterLayout.append(clusterHeatMapLayout);
		clusterLayout.append(clusterBarLayout);

		textureRenderer.init(set, clusterVA, storageVA,
				uncertaintyHeatMap.getColorMapper());

		barTextureRenderer.init(set, clusterVA, storageVA,
				uncertaintyHeatMap.getColorMapper());

	}

	@Override
	public void render(GL2 gl) {

	}

}
