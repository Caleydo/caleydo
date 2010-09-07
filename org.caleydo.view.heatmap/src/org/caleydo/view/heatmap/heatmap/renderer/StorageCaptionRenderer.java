package org.caleydo.view.heatmap.heatmap.renderer;

import java.awt.Font;

import javax.media.opengl.GL;

import org.caleydo.core.data.selection.StorageVirtualArray;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;
import org.caleydo.core.view.opengl.util.GLHelperFunctions;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.view.heatmap.HeatMapRenderStyle;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;

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

		float xPosition = 0;
		float fieldWidth = contentSpacing.getFieldWidth();
		// offset to center the caption
		float xOffset = 0;

		float height = (float) textRenderer.getScaledBounds(gl, "TEST", fFontScaling,
				HeatMapRenderStyle.LABEL_TEXT_MIN_SIZE).getHeight();

		if (fieldWidth > height)
			xOffset = (fieldWidth - height) / 2;
		for (Integer storageID : storageVA) {
			String label = heatMap.getSet().get(storageID).getLabel();

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

		textRenderer.setColor(0, 0, 0, 1);

	}
	// {
	// float fFontScaling = 0;
	//
	// float fColumnDegrees = 0;
	// float fLineDegrees = 0;
	//
	// fColumnDegrees = 90;// 60;
	// fLineDegrees = 0;
	// // render column captions
	// if (heatMap.getDetailLevel() == EDetailLevel.HIGH
	// && heatMap.getStorageVA().size() < 60) {
	// if (iCount == heatMap.getContentVA().size()) {
	// fXPosition = 0;
	//
	// if (heatMap.bClusterVisualizationExperimentsActive)
	// gl.glTranslatef(+renderStyle
	// .getWidthClusterVisualization(), 0, 0);
	//
	// for (Integer iStorageIndex : heatMap.getStorageVA()) {
	// textRenderer.setColor(0, 0, 0, 1);
	// renderCaption(gl, heatMap.getSet().get(iStorageIndex)
	// .getLabel(), fXPosition + fFieldWidth / 2,
	// fYPosition + 0.05f - 0.5f, 0, fColumnDegrees,
	// renderStyle.getSmallFontScalingFactor());
	// fXPosition += fFieldWidth;
	// }
	//
	// if (heatMap.bClusterVisualizationExperimentsActive)
	// gl.glTranslatef(-renderStyle
	// .getWidthClusterVisualization(), 0, 0);
	// }
	// }
	// }
	//
	// public void renderCaption(GL gl, String sLabel, float fXOrigin,
	// float fYOrigin, float fZOrigin, float fRotation, float fFontScaling) {
	// if (heatMap.isRenderedRemote()
	// && heatMap.getRemoteRenderingGLCanvas().getViewType().equals(
	// "org.caleydo.view.bucket"))
	// fFontScaling *= 1.5;
	// if (sLabel.length() > GeneralRenderStyle.NUM_CHAR_LIMIT + 1) {
	// sLabel = sLabel.substring(0, GeneralRenderStyle.NUM_CHAR_LIMIT - 2);
	// sLabel = sLabel + "..";
	// }
	//
	// // textRenderer.setColor(0, 0, 0, 1);
	// gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
	// gl.glTranslatef(fXOrigin, fYOrigin, fZOrigin);
	// gl.glRotatef(fRotation, 0, 0, 1);
	// textRenderer.begin3DRendering();
	// textRenderer.draw3D(gl, sLabel, 0, 0, 0, fFontScaling,
	// HeatMapRenderStyle.LABEL_TEXT_MIN_SIZE);
	// textRenderer.end3DRendering();
	// gl.glRotatef(-fRotation, 0, 0, 1);
	// gl.glTranslatef(-fXOrigin, -fYOrigin, -fZOrigin);
	// // textRenderer.begin3DRendering();
	// gl.glPopAttrib();
	// }

}
