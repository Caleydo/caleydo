package org.caleydo.view.heatmap.heatmap.template;

import javax.media.opengl.GL;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;

public class DefaultRenderTemplate extends ARenderTemplate {

	public DefaultRenderTemplate(GLHeatMap heatMap) {
		super(heatMap);
		initRenderers();
	}

	public void render(GL gl) {
		// FIXME: this should be called externally
		frustumChanged();

		// GLHelperFunctions.drawViewFrustum(gl, viewFrustum);
		heatMapRenderer.renderHeatMap(gl);
		heatMapRenderer.renderSelection(gl, SelectionType.MOUSE_OVER);
		heatMapRenderer.renderSelection(gl, SelectionType.SELECTION);

		gl.glTranslatef(heatMapX + spacing, 0, 0);
		contentCaptionRenderer.renderContentCaptions(gl);
		gl.glTranslatef(-heatMapX - spacing, 0, 0);

	}

}
