package org.caleydo.core.view.opengl.canvas.radial;

import java.awt.geom.Rectangle2D;

import javax.media.opengl.GL;

import com.sun.opengl.util.j2d.TextRenderer;

public class TextItem
	extends ALabelItem {
	
	private static final String sTextForHeightCalculation = "Text without characters below the bottom textline";

	private String sText;
	private TextRenderer textRenderer;
	private float fTextScaling;

	public TextItem(String sText) {
		this.sText = sText;
	}

	@Override
	public void draw(GL gl) {
		textRenderer.setColor(0.2f, 0.2f, 0.2f, 1);
		textRenderer.begin3DRendering();

		textRenderer
			.draw3D(sText, vecPosition.x(), vecPosition.y(), 0, fTextScaling);

		textRenderer.end3DRendering();
		textRenderer.flush();
	}

	public void setRenderingProperties(TextRenderer textRenderer, float fTextScaling) {
		this.textRenderer = textRenderer;
		this.fTextScaling = fTextScaling;

		Rectangle2D bounds = textRenderer.getBounds(sTextForHeightCalculation);
		fHeight = (float) bounds.getHeight() * fTextScaling;
		bounds = textRenderer.getBounds(sText);
		fWidth = (float) bounds.getWidth() * fTextScaling;
	}
}
