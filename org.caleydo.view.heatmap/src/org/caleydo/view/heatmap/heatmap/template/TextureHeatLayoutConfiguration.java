/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 * 
 */
package org.caleydo.view.heatmap.heatmap.template;

import javax.media.opengl.GL2;

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
	public TextureHeatLayoutConfiguration(GL2 gl, GLHeatMap heatMap) {
		heatMapRenderer = new HeatMapTextureRenderer(heatMap);
		heatMapRenderer.initialize(gl);
	}

	@Override
	public void setStaticLayouts() {
		ElementLayout mainLayout = new ElementLayout();
		baseElementLayout = mainLayout;
		mainLayout.setRatioSizeX(1);
		mainLayout.setRatioSizeY(1);
		mainLayout.setRenderer(heatMapRenderer);
		
	}
	
	public void updateColorMapping(GL2 gl)
	{
		heatMapRenderer.initialize(gl);
	}

}
