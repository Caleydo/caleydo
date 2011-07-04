package org.caleydo.view.heatmap.uncertainty;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.StorageVirtualArray;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.view.heatmap.texture.HeatMapTextureRenderer;

/**
 * Uncertainty overview heat map view.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 * @author Clemens Holzhüter
 */

public class OverviewHeatMapRenderer extends LayoutRenderer {

	private HeatMapTextureRenderer textureRenderer = new HeatMapTextureRenderer();

	private GLUncertaintyHeatMap uncertaintyHeatMap;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param sLabel
	 * @param viewFrustum
	 */
	public OverviewHeatMapRenderer(GLUncertaintyHeatMap uncertaintyHeatMap) {
		this.uncertaintyHeatMap = uncertaintyHeatMap;

	}

	public void init(GL2 gl) {
		ContentVirtualArray contentVA = uncertaintyHeatMap.getContentVA();
		StorageVirtualArray storageVA = uncertaintyHeatMap.getStorageVA();
		ISet set = uncertaintyHeatMap.getDataDomain().getSet();
		textureRenderer.init(gl, set, contentVA, storageVA,
				uncertaintyHeatMap.getColorMapper());

	}

	@Override
	public void render(GL2 gl) {
		textureRenderer.setViewHeight(y);
		textureRenderer.setViewWidth(x);
		textureRenderer.render(gl);
	}

}
