package org.caleydo.view.heatmap.heatmap.renderer;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.uncertainty.OverviewRenderer;

/**
 * 
 * Renderer connects the overview with the detail.
 * 
 * @author Marc Streit
 * 
 */
public class OverviewDetailConnectorRenderer extends LayoutRenderer {

	private OverviewRenderer overviewHeatMap;
	private GLHeatMap detailHeatMap;

	public OverviewDetailConnectorRenderer(OverviewRenderer overviewHeatMap,
			GLHeatMap detailHeatMap) {

		this.overviewHeatMap = overviewHeatMap;
		this.detailHeatMap = detailHeatMap;
	}

	public void render(GL2 gl) {

		gl.glColor4f(0f, 0f, 0f, 0.5f);

		float yOverview = overviewHeatMap.getSelectedClusterY();

		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex3f(0, yOverview, 0);
		gl.glVertex3f(0, yOverview+overviewHeatMap.getSelectedClusterHeight(), 0);
		gl.glVertex3f(x, y, 0);
		gl.glVertex3f(x, 0, 0);
		gl.glEnd();
	}
}
