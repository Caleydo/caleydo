package org.caleydo.view.heatmap.heatmap.renderer;

import java.awt.Font;

import javax.media.opengl.GL;

import org.caleydo.core.data.selection.StorageVirtualArray;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.view.heatmap.HeatMapRenderStyle;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;

/**
 * Rendering the storage captions for the GLHeatmap
 * 
 * @author Alexander Lex
 * 
 */
public class StorageCaptionRenderer extends AContentRenderer {

	private CaleydoTextRenderer textRenderer;
	private int fontSize = 24;

	private float fFontScaling = GeneralRenderStyle.SMALL_FONT_SCALING_FACTOR / 1.2f;

	public StorageCaptionRenderer(GLHeatMap heatMap) {
		super(heatMap);

		textRenderer = new CaleydoTextRenderer(new Font("Arial", Font.PLAIN, fontSize),
				false);
	}

	@Override
	public void render(GL gl) {

		StorageVirtualArray storageVA = heatMap.getStorageVA();
		textRenderer.setColor(0, 0, 0, 1);

		float xPosition = 0;
		float fieldWidth = contentSpacing.getFieldWidth();
		// offset to center the caption
		float xOffset = 0;

		float height = (float) textRenderer.getScaledBounds(gl, "TEST", fFontScaling,
				HeatMapRenderStyle.LABEL_TEXT_MIN_SIZE).getHeight();

		if (fieldWidth > height)
			xOffset = (fieldWidth - height) / 2;
		for (Integer storageID : storageVA) {
			String label = heatMap.getDataDomain().getStorageLabel(storageID);

			float fRotation = -90;
			float yOffset = 0.57f;

			gl.glTranslatef(xPosition + xOffset, yOffset, 0);
			gl.glRotatef(fRotation, 0, 0, 1);
			textRenderer.renderText(gl, label, 0, 0f, 0, fFontScaling,
					HeatMapRenderStyle.LABEL_TEXT_MIN_SIZE);
			gl.glRotatef(-fRotation, 0, 0, 1);
			gl.glTranslatef(-xPosition - xOffset, -yOffset, 0);
			xPosition += fieldWidth;

		}

	}

}
