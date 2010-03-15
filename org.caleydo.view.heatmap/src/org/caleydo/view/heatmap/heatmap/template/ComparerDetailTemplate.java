package org.caleydo.view.heatmap.heatmap.template;

import javax.media.opengl.GL;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;

public class ComparerDetailTemplate extends ARenderTemplate {

	private boolean isLeft = true;

	public ComparerDetailTemplate(GLHeatMap heatMap, boolean isLeft) {
		super(heatMap);
		this.isLeft = isLeft;
		HEAT_MAP_X = 0.7f;
		HEAT_MAP_Y = 1f;

		useContentCaptions = true;
		CONTENT_CAPTION_X = 0.29f;
		CONTENT_CAPTION_Y = HEAT_MAP_Y;

		useCaptionCage = true;
		CAGE_X = 0.3f;
		CAGE_Y = HEAT_MAP_Y;

		initRenderers();

	}

	@Override
	public void render(GL gl) {
		// FIXME: this should be called externally
		frustumChanged();

		if (isLeft)
			renderLeft(gl);
		else
			renderRight(gl);

		// GLHelperFunctions.drawViewFrustum(gl, viewFrustum);

	}

	private void renderLeft(GL gl) {

		captionCageRenderer.renderCage(gl);

		gl.glTranslatef(spacing, 0, 0);
		contentCaptionRenderer.renderContentCaptions(gl);
		gl.glTranslatef(-spacing, 0, 0);

		gl.glTranslatef(cageX, 0, 0);
		heatMapRenderer.renderHeatMap(gl);
		heatMapRenderer.renderSelection(gl, SelectionType.MOUSE_OVER);
		heatMapRenderer.renderSelection(gl, SelectionType.SELECTION);
		gl.glTranslatef(-cageX, 0, 0);

	}

	private void renderRight(GL gl) {
		heatMapRenderer.renderHeatMap(gl);
		heatMapRenderer.renderSelection(gl, SelectionType.MOUSE_OVER);
		heatMapRenderer.renderSelection(gl, SelectionType.SELECTION);

		gl.glTranslatef(heatMapX + spacing, 0, 0);
		contentCaptionRenderer.renderContentCaptions(gl);
		gl.glTranslatef(-heatMapX - spacing, 0, 0);

		gl.glTranslatef(heatMapX, 0, 0);
		captionCageRenderer.renderCage(gl);
		gl.glTranslatef(-heatMapX, 0, 0);
	}

}
