package org.caleydo.core.view.opengl.canvas.storagebased;

import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;

/**
 * Render styles for the parallel coordinates
 * 
 * @author Alexander Lex
 */

public class ParCoordsRenderStyle
	extends GeneralRenderStyle {

	// Z Values
	public static final float POLYLINE_NORMAL_Z = 0.001f;
	public static final float POLYLINE_SELECTED_Z = 0.002f;
	public static final float POLYLINE_DESELECTED_Z = 0;
	public static final float GATE_Z = 0.003f;
	public static final float NAN_Z = 0.003f;

	public static final float AXIS_Z = 0.0f;
	public static final float LABEL_Z = 0.004f;
	public static final float TEXT_ON_LABEL_Z = LABEL_Z + 0.0001f;

	public static final float[] POLYLINE_NO_OCCLUSION_PREV_COLOR = { 0.0f, 0.0f, 0.0f, 1.0f };

	public static final float[] POLYLINE_SELECTED_COLOR = SELECTED_COLOR; // {
	// 0f,
	// 1.0f,
	// 0.0f,
	// 1.0f
	// };

	public static final float SELECTED_POLYLINE_LINE_WIDTH = 4.0f;

	public static final float[] POLYLINE_MOUSE_OVER_COLOR = { 0.9f, 0.9f, 0.0f, 1.0f };

	public static final float MOUSE_OVER_POLYLINE_LINE_WIDTH = 4.0f;

	public static final float DESELECTED_POLYLINE_LINE_WIDTH = 1.0f;

	public static final float[] Y_AXIS_COLOR = { 0.0f, 0.0f, 1.0f, 1.0f };

	public static final float Y_AXIS_LINE_WIDTH = 1.0f;

	public static final float[] Y_AXIS_SELECTED_COLOR = SELECTED_COLOR;

	public static final float Y_AXIS_SELECTED_LINE_WIDTH = 4.0f;

	public static final float[] Y_AXIS_MOUSE_OVER_COLOR = POLYLINE_MOUSE_OVER_COLOR;// MOUSE_OVER_COLOR;

	public static final float Y_AXIS_MOUSE_OVER_LINE_WIDTH = 4.0f;

	public static final float[] X_AXIS_COLOR = { 0.0f, 0.0f, 1.0f, 1.0f };

	public static final float X_AXIS_LINE_WIDTH = 3.0f;

	public static final float[] CANVAS_COLOR = { 1.0f, 1.0f, 1.0f, 1.0f };

	public static final float[] GATE_TIP_COLOR = { 1f, 0.705f, 0f, 1.0f };
	public static final float[] GATE_BODY_COLOR = { 0.61f, 0.705f, 1.0f, 0.8f };

	public static final float[] ANGULAR_COLOR = { 0.17f, 0.45f, 0.84f, 1 };
	public static final float[] ANGULAR_POLYGON_COLOR = { 0.17f, 0.45f, 0.84f, 0.4f };

	public static final float ANGLUAR_LINE_WIDTH = 4;

	// Line widths
	public static final float POLYLINE_LINE_WIDTH = 1.0f;

	public static final int NUMBER_AXIS_MARKERS = 9;

	public static final float NAN_Y_OFFSET = -0.08f;

	// modifiable colors
	protected float[] polylineOcclusionPrevColor = { 0.0f, 0.0f, 0.0f, 1.0f };

	protected float[] polylineDeselectedColor = { 0.0f, 0.0f, 0.0f, 0.001f };

	protected float fOcclusionPrevAlpha = 0.1f;

	// how much room between the axis?
	private float fAxisSpacing = 0.3f;

	// --- constants to scale ---

	// coordinate system
	private static final float COORDINATE_TOP_SPACING = 0.07f;

	private static final float COORDINATE_SIDE_SPACING = 0.05f;

	private static final float COORDINATE_BOTTOM_SPACING = 0.06f;

	public static final float Y_AXIS_LOW = -0.25f;

	public static final float AXIS_MARKER_WIDTH = 0.01f;

	// gates
	public static final float GATE_WIDTH = 0.05f;
	public static final float GATE_BOTTOM_HEIGHT = 0.01f;
	// private static final float GATE_NEGATIVE_Y_OFFSET = 0;
	public static final float GATE_TIP_HEIGHT = 0.08f;

	// buttons below axis
	public static final float AXIS_BUTTONS_Y_OFFSET = 0.12f;

	private static final float fAxisSpacingLowerLimit = 0.001f;

	private static final float fMinAxisSpacingForText = 0.1f;

	private static final float[] BACKGROUND_COLOR = { 1, 1, 1, 1 };

	private static final float fXAxisOverlap = 0.1f;

	// minimum text sizes

	public static final int MIN_AXIS_LABEL_TEXT_SIZE = 60;
	public static final int MIN_NUMBER_TEXT_SIZE = 55;

	private GLParallelCoordinates pcs;

	/**
	 * Constructor.
	 * 
	 * @param viewFrustum
	 */
	public ParCoordsRenderStyle(GLParallelCoordinates pcs, IViewFrustum viewFrustum) {
		super(viewFrustum);
		this.pcs = pcs;
	}

	public float[] getPolylineOcclusionPrevColor(int iNumberOfRenderedLines) {

		fOcclusionPrevAlpha = (float) (6 / Math.sqrt(iNumberOfRenderedLines));

		polylineOcclusionPrevColor[3] = fOcclusionPrevAlpha;

		return polylineOcclusionPrevColor;
	}

	public float[] getPolylineDeselectedOcclusionPrevColor(int iNumberOfRenderedLines) {
		polylineDeselectedColor[3] = (float) (0.2 / Math.sqrt(iNumberOfRenderedLines));
		return polylineDeselectedColor;
	}

	public float getAxisSpacing(final int iNumberOfAxis) {

		fAxisSpacing = getWidthOfCoordinateSystem() / (iNumberOfAxis - 1);

		if (fAxisSpacing < fAxisSpacingLowerLimit * getScaling())
			return fAxisSpacingLowerLimit * getScaling();

		return fAxisSpacing;
	}

	public float getWidthOfCoordinateSystem() {

		// this checks whether we render a global brush or not
		float numberOfSpacings = 2;
		if (pcs.getSet().isSetHomogeneous())
			numberOfSpacings = 2.5f;

		if (pcs.bShowSelectionHeatMap)
			return viewFrustum.getWidth() - COORDINATE_SIDE_SPACING * numberOfSpacings * getScaling()
				- pcs.glSelectionHeatMap.getViewFrustum().getWidth();

		else
			return viewFrustum.getWidth() - COORDINATE_SIDE_SPACING * numberOfSpacings * getScaling();
	}

	public float getXAxisStart() {
		return -fXAxisOverlap;
	}

	public float getXAxisEnd() {
		return getWidthOfCoordinateSystem() + fXAxisOverlap;
	}

	public float getAxisHeight() {
		return viewFrustum.getHeight() - (COORDINATE_TOP_SPACING + COORDINATE_BOTTOM_SPACING) * getScaling();
	}

	public float getXSpacing() {

		return 1.5f * COORDINATE_SIDE_SPACING * getScaling();
	}

	public float getBottomSpacing() {

		return COORDINATE_BOTTOM_SPACING * getScaling();
	}

	public float getAxisButtonYOffset() {

		return AXIS_BUTTONS_Y_OFFSET * getScaling();
	}

	// public float getGateWidth()
	// {
	//
	// return GATE_WIDTH;
	// }

	// public float getGateYOffset()
	// {
	//
	// return GATE_NEGATIVE_Y_OFFSET * getAxisHeight();
	// }

	// @Deprecated // not used anymore?
	// public float getGateMinimumValue()
	// {
	// return NAN_Y_OFFSET * getAxisHeight();
	// }

	// @Deprecated
	// public float getNaNYOffset()
	// {
	// return NAN_Y_OFFSET;
	// }

	// @Deprecated
	// public float getGateTipHeight()
	// {
	//
	// return GATE_TIP_HEIGHT;
	// }

	public float getAxisCaptionSpacing() {

		return COORDINATE_TOP_SPACING / 3 * getScaling();
	}

	public boolean isEnoughSpaceForText(int iNumberOfAxis) {
		getScaling();
		if (getAxisSpacing(iNumberOfAxis) > fMinAxisSpacingForText)
			return true;
		return false;
	}

	@Override
	public float[] getBackgroundColor() {
		return BACKGROUND_COLOR;
	}

	// GATE_WIDTH = 0.015f;
	// private static final float GATE_NEGATIVE_Y_OFFSET = -0.04f;
	// private static final float GATE_TIP_HEIGHT
}
