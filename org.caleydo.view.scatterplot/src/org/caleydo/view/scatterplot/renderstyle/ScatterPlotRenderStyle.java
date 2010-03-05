package org.caleydo.view.scatterplot.renderstyle;

//import java.util.HashMap;

import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;
import org.caleydo.view.scatterplot.GLScatterPlot;

/**
 * ScatterPlot render styles
 * 
 * @author Juergen Pillhofer
 */

public class ScatterPlotRenderStyle extends GeneralRenderStyle {

	

	

	public static final float[] X_AXIS_COLOR = { 0.0f, 0.0f, 0.0f, 1.0f };
	public static final float X_AXIS_LINE_WIDTH = 2.0f;
	public static final float[] Y_AXIS_COLOR = { 0.0f, 0.0f, 0.0f, 1.0f };
	public static final float Y_AXIS_LINE_WIDTH = 2.0f;
	public static float XYAXISDISTANCE = 0.2f;
	

	public static int NR_TEXTURESX = 5;
	public static int NR_TEXTURESY = 5;
	public static int NR_TEXTURES = 25;

	public static float POINTSIZE = 0.05f;

	private int iPointSize = 1;
	public static EScatterPointType POINTSTYLE = EScatterPointType.CROSS;

	public static final float XLABELROTATIONNAGLE = 0.0f;
	public static final float YLABELROTATIONNAGLE = 90.0f;
	public static final float XLABELDISTANCE = 0.03f;
	public static final float YLABELDISTANCE = 0.10f;

	public static final int NUMBER_AXIS_MARKERS = 19;

	public static final int MIN_AXIS_LABEL_TEXT_SIZE = 5;//60;
	public static final int MIN_NUMBER_TEXT_SIZE = 5;//55;
	public static final int LABEL_TEXT_MIN_SIZE = 5;//50;

	public static final float AXIS_Z = 0.0f;
	public static final float SCATTERPOINT_Z = 0.001f;
	public static final float SELECTION_Z = 0.002f;
	public static final float TWOAXISLINE_Z = 0.003f;
	public static final float MATRIX_FULLTEXTURES_Z = 0.003f;
	public static final float LABEL_Z = 0.005f;
	public static final float MATRIX_SELECTIONTEXTURES_Z = 0.004f;
	public static final float MATRIX_SELECTIONRECTANGLE_Z = 0.005f;
	public static final float MATRIX_HISTOGRAMM_Z=0.006f;
	public static final float HIGHLIGHTED_SCATTERPOINT_Z=0.006f;
	public static final float TEXT_ON_LABEL_Z = 0.007f;				
	public static final float MAINVIEW_ZOOM_Z=0.008f;
	public static final float SELECTION_RECTANGLE_Z=0.009f;
	
	
	
	
	public static final float AXIS_MARKER_WIDTH = 0.02f;

	
	private float fCenterXOffset = 0;
	private float fCenterYOffset = 0;
	public float fCenterCorrectionFacktor = 0;

	private boolean bIsEmbedded = true;
	

	private float fSizeHeatmapArrow = 0.17f;

	public ScatterPlotRenderStyle(GLScatterPlot scatterPlot,
			IViewFrustum viewFrustum) {

		super(viewFrustum);

		fCenterXOffset = viewFrustum.getWidth() / 2;
		fCenterYOffset = viewFrustum.getHeight() / 2;
	}

	public void setIsEmbedded(boolean value) {
		bIsEmbedded = value;
		if (bIsEmbedded)
			XYAXISDISTANCE = 0.1f;
		else
			XYAXISDISTANCE = 0.5f;
	}

	

	public float transformNorm2GlobalX(float value) {
		return getAxisWidth() * value + XYAXISDISTANCE;
	}

	public float transformNorm2GlobalY(float value) {
		return getAxisHeight() * value + XYAXISDISTANCE;
	}

	public void setTextureNr(int x, int y) {
		NR_TEXTURESX = x;
		NR_TEXTURESY = y;
		NR_TEXTURES = NR_TEXTURESX * NR_TEXTURESY;
	}

	public boolean setCenterOffsets(float x, float y) {
		if (x == fCenterXOffset && y == fCenterYOffset)
			return false;
		fCenterXOffset = x;
		fCenterYOffset = y;
		return true;
	}

	public float getCenterXOffset() {

		return fCenterXOffset;
	}

	public float getCenterYOffset() {

		return fCenterYOffset;
	}

	public float getXCenter() {

		return viewFrustum.getWidth() - fCenterXOffset;
	}

	public float getYCenter() {

		return viewFrustum.getHeight() - fCenterYOffset;
	}

	public float getXSpacing() {
		return 0.4f;
	}

	public float getYSpacing() {
		return 0.3f;
	}

	public void setPOINTSTYLE(EScatterPointType Type) {
		POINTSTYLE = Type;
	}

	public void setPointSize(int value) {
		POINTSIZE = value / 100.0f;
		iPointSize = value;
	}

	public int getPointSize() {
		return iPointSize;
	}

	
	public float getRenderWidth() {

		// return fRenderWith;
		if (!bIsEmbedded)
			return viewFrustum.getWidth();
		else
			return getXCenter();

	}

	public float getRenderHeight() {

		//
		if (!bIsEmbedded)
			return viewFrustum.getHeight();
		else
			return getYCenter();

	}

	public float getLabelHeight(boolean b2Axis) {

		if (b2Axis)
			return getRenderHeight() / 3;
		else
			return getRenderHeight() / 2;

	}

	public float getLAbelWidth(boolean b2Axis) {

		if (b2Axis)
			return getRenderWidth() / 3;
		else
			return getRenderWidth() / 2;

	}

	public float getAxisHeight() {
		return getRenderHeight() - 2 * XYAXISDISTANCE;
	}

	public float getAxisWidth() {
		return getRenderWidth() - 2 * XYAXISDISTANCE;
	}

	public float getSizeHeatmapArrow() {
		return fSizeHeatmapArrow;
	}
}
