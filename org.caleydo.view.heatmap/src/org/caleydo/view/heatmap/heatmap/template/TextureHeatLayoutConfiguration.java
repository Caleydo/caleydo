/**
 * 
 */
package org.caleydo.view.heatmap.heatmap.template;

import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutConfiguration;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.heatmap.renderer.texture.HeatMapTextureRenderer;

/**
 * @author Alexander Lex
 * 
 */
public class TextureHeatLayoutConfiguration extends LayoutConfiguration {

	protected GLHeatMap heatMap;
	protected HeatMapTextureRenderer heatMapRenderer;

	/**
	 * 
	 */
	public TextureHeatLayoutConfiguration(GLHeatMap heatMap) {
		heatMapRenderer = new HeatMapTextureRenderer(heatMap);
	}

	@Override
	public void setStaticLayouts() {
		ElementLayout mainLayout = new ElementLayout();
		baseElementLayout = mainLayout;
		mainLayout.setRatioSizeX(1);
		mainLayout.setRatioSizeY(1);
		mainLayout.setRenderer(heatMapRenderer);
		heatMapRenderer.init();
	}

}
