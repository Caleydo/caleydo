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
package org.caleydo.view.heatmap.heatmap.renderer;

import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.heatmap.renderer.spacing.RecordSpacing;

/**
 * Base class for all elements that render heat map content elements (e.g. the
 * heat map row itself, the content caption etc.)
 * 
 * @author Alexander Lex
 * 
 */
public abstract class AHeatMapRenderer extends LayoutRenderer {

	protected RecordSpacing recordSpacing;

	protected GLHeatMap heatMap;

	public AHeatMapRenderer(GLHeatMap heatMap) {
		this.heatMap = heatMap;
	}

	public void setRecordSpacing(RecordSpacing contentSpacing) {
		this.recordSpacing = contentSpacing;
	}

	@Override
	public void updateSpacing() {
	}
}
