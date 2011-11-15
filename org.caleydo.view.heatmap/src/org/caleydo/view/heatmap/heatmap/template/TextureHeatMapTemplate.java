/**
 * 
 */
package org.caleydo.view.heatmap.heatmap.template;

import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutTemplate;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.heatmap.renderer.texture.HeatMapTextureRenderer;

/**
 * @author Alexander Lex
 * 
 */
public class TextureHeatMapTemplate extends LayoutTemplate {

	protected GLHeatMap heatMap;
	protected HeatMapTextureRenderer heatMapRenderer;

	/**
	 * 
	 */
	public TextureHeatMapTemplate(GLHeatMap heatMap) {
		heatMapRenderer = new HeatMapTextureRenderer(heatMap);
	}

	@Override
	public void setStaticLayouts() {
		ElementLayout mainLayout = new ElementLayout();
		setBaseElementLayout(mainLayout);
		mainLayout.setRatioSizeX(1);
		mainLayout.setRatioSizeY(1);
		mainLayout.setRenderer(heatMapRenderer);
		heatMapRenderer.init();
	}

}
