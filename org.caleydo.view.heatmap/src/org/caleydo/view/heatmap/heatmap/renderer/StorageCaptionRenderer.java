package org.caleydo.view.heatmap.heatmap.renderer;


public class StorageCaptionRenderer {

//	public void renderStorageCaptions(GL gl)
//	{
//		float fFontScaling = 0;
//
//		float fColumnDegrees = 0;
//		float fLineDegrees = 0;
//
//		fColumnDegrees = 90;// 60;
//		fLineDegrees = 0;
//		// render column captions
//		if (heatMap.getDetailLevel() == EDetailLevel.HIGH
//				&& heatMap.getStorageVA().size() < 60) {
//			if (iCount == heatMap.getContentVA().size()) {
//				fXPosition = 0;
//
//				if (heatMap.bClusterVisualizationExperimentsActive)
//					gl.glTranslatef(+renderStyle
//							.getWidthClusterVisualization(), 0, 0);
//
//				for (Integer iStorageIndex : heatMap.getStorageVA()) {
//					textRenderer.setColor(0, 0, 0, 1);
//					renderCaption(gl, heatMap.getSet().get(iStorageIndex)
//							.getLabel(), fXPosition + fFieldWidth / 2,
//							fYPosition + 0.05f - 0.5f, 0, fColumnDegrees,
//							renderStyle.getSmallFontScalingFactor());
//					fXPosition += fFieldWidth;
//				}
//
//				if (heatMap.bClusterVisualizationExperimentsActive)
//					gl.glTranslatef(-renderStyle
//							.getWidthClusterVisualization(), 0, 0);
//			}
//		}
//	}
//	
//	public void renderCaption(GL gl, String sLabel, float fXOrigin,
//			float fYOrigin, float fZOrigin, float fRotation, float fFontScaling) {
//		if (heatMap.isRenderedRemote()
//				&& heatMap.getRemoteRenderingGLCanvas().getViewType().equals(
//						"org.caleydo.view.bucket"))
//			fFontScaling *= 1.5;
//		if (sLabel.length() > GeneralRenderStyle.NUM_CHAR_LIMIT + 1) {
//			sLabel = sLabel.substring(0, GeneralRenderStyle.NUM_CHAR_LIMIT - 2);
//			sLabel = sLabel + "..";
//		}
//
//		// textRenderer.setColor(0, 0, 0, 1);
//		gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
//		gl.glTranslatef(fXOrigin, fYOrigin, fZOrigin);
//		gl.glRotatef(fRotation, 0, 0, 1);
//		textRenderer.begin3DRendering();
//		textRenderer.draw3D(gl, sLabel, 0, 0, 0, fFontScaling,
//				HeatMapRenderStyle.LABEL_TEXT_MIN_SIZE);
//		textRenderer.end3DRendering();
//		gl.glRotatef(-fRotation, 0, 0, 1);
//		gl.glTranslatef(-fXOrigin, -fYOrigin, -fZOrigin);
//		// textRenderer.begin3DRendering();
//		gl.glPopAttrib();
//	}
	
}
