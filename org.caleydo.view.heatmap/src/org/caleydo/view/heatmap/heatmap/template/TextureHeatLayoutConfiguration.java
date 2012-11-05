/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
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
