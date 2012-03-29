package org.caleydo.view.parcoords;

import java.util.HashMap;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.DetailLevel;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;

/**
 * Render styles for the parallel coordinates
 * 
 * @author Alexander Lex
 */

public class PCRenderStyle extends GeneralRenderStyle {

	public class PolyLineState {
		float lineWidth;
		float zDepth;
		float[] color;
		SelectionType selectionType;

		PolyLineState(SelectionType selectionType, int nrElements) {
			this.selectionType = selectionType;
			this.lineWidth = selectionType.getLineWidth();
			this.zDepth = POLYLINE_NORMAL_Z + selectionType.getPriority()
					* (POLYLINE_SELECTED_Z - POLYLINE_NORMAL_Z);
			this.color = selectionType.getColor();
			updateOcclusionPrev(nrElements);
		}

		void updateOcclusionPrev(int nrElements) {
			this.color[3] = getPolylineOcclusionPrevAlpha(nrElements);
		}
	}

	private HashMap<SelectionType, PolyLineState> hashSelectionTypeToPolylineState;

	// Z Values
	private static final float POLYLINE_NORMAL_Z = 0.001f;
	private static final float POLYLINE_SELECTED_Z = 0.002f;
	public static final float GATE_Z = 0.003f;
	public static final float NAN_Z = 0.003f;

	public static final float AXIS_Z = 0.0f;
	public static final float LABEL_Z = 0.004f;
	public static final float TEXT_ON_LABEL_Z = LABEL_Z + 0.0001f;

	public static final float[] X_AXIS_COLOR = { 0.5f, 0.5f, 0.5f, 1.0f };
	public static final float X_AXIS_LINE_WIDTH = 3.0f;

	public static final float[] Y_AXIS_COLOR = X_AXIS_COLOR;

	public static final float Y_AXIS_LINE_WIDTH = 1.0f;

	public static final float Y_AXIS_SELECTED_LINE_WIDTH = 4.0f;

	public static final float Y_AXIS_MOUSE_OVER_LINE_WIDTH = 4.0f;

	public static final float[] CANVAS_COLOR = { 1.0f, 1.0f, 1.0f, 1.0f };

	public static final float[] GATE_BODY_COLOR = { 0.61f, 0.705f, 1.0f, 0.8f };

	public static final float[] ANGULAR_COLOR = { 0.17f, 0.45f, 0.84f, 1 };
	public static final float[] ANGULAR_POLYGON_COLOR = { 0.17f, 0.45f, 0.84f, 0.4f };

	public static final float ANGLUAR_LINE_WIDTH = 4;

	public static final int NUMBER_AXIS_MARKERS = 9;

	public static final float NAN_Y_OFFSET = -0.08f;

	protected float fOcclusionPrevAlpha = 0.1f;

	// how much room between the axis?
	private float axisSpacing = 0.3f;

	// --- constants to scale ---

	// coordinate system
	private static final float COORDINATE_TOP_SPACING = 0.07f;

	// private static final float COORDINATE_SIDE_SPACING = 0.05f;

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

	// private static final float fAxisSpacingLowerLimit = 0.001f;

	private static final float fMinAxisSpacingForText = 0.1f;

	// private static final float fXAxisOverlap = 0.1f;

	// minimum text sizes

	public static final int MIN_AXIS_LABEL_TEXT_SIZE = 60;
	public static final int MIN_NUMBER_TEXT_SIZE = 55;

	public final PolyLineState normalState = new PolyLineState(SelectionType.NORMAL,
			(1000));

	private GLParallelCoordinates pcs;

	/**
	 * Constructor.
	 * 
	 * @param viewFrustum
	 */
	public PCRenderStyle(GLParallelCoordinates pcs, ViewFrustum viewFrustum) {
		super(viewFrustum);
		hashSelectionTypeToPolylineState = new HashMap<SelectionType, PolyLineState>();
		hashSelectionTypeToPolylineState.put(SelectionType.NORMAL, normalState);

		this.pcs = pcs;
	}

	private float getPolylineOcclusionPrevAlpha(int numberOfRenderedLines) {

		fOcclusionPrevAlpha = (float) (6 / Math.sqrt(numberOfRenderedLines));
		if (pcs != null && pcs.getDetailLevel() == DetailLevel.LOW)
			fOcclusionPrevAlpha /= 5;

		return fOcclusionPrevAlpha;
	}

	public float getAxisSpacing(final int iNumberOfAxis) {

		axisSpacing = getWidthOfCoordinateSystem() / (iNumberOfAxis - 1);

		return axisSpacing;
	}

	public float getWidthOfCoordinateSystem() {

		// this checks whether we render a global brush or not
		// float numberOfSpacings = 2;
		// if (pcs.getTable().isSetHomogeneous())
		// numberOfSpacings = 2.5f;

		return viewFrustum.getWidth() - 2 * getXSpacing();
	}

	public float getXAxisStart() {
		return 0 - getXSpacing() / 4;
	}

	public float getXAxisEnd() {
		return getWidthOfCoordinateSystem() + getXSpacing() / 4;
	}

	public float getAxisHeight() {
		return viewFrustum.getHeight()
				- (COORDINATE_TOP_SPACING + COORDINATE_BOTTOM_SPACING) * getScaling();
	}

	public float getXSpacing() {
		if (pcs.getDetailLevel().equals(DetailLevel.HIGH))
			return viewFrustum.getWidth() / 30;
		else
			return viewFrustum.getWidth() / 50;
		// return pcs.getPixelGLConverter()
		// .getGLWidthForPixelWidth(20);
	}

	public float getBottomSpacing() {

		return COORDINATE_BOTTOM_SPACING * getScaling();
	}

	public PolyLineState getPolyLineState(SelectionType selectionType, int nrElements) {
		if (hashSelectionTypeToPolylineState.containsKey(selectionType)) {
			PolyLineState state = hashSelectionTypeToPolylineState.get(selectionType);
			state.updateOcclusionPrev(nrElements);
			return state;
		} else {
			PolyLineState newState = new PolyLineState(selectionType, nrElements);
			hashSelectionTypeToPolylineState.put(selectionType, newState);
			return newState;
		}
	}

	public float getAxisCaptionSpacing() {

		return COORDINATE_TOP_SPACING / 3 * getScaling();
	}

	public boolean isEnoughSpaceForText(int iNumberOfAxis) {
		getScaling();
		if (getAxisSpacing(iNumberOfAxis) > fMinAxisSpacingForText)
			return true;
		return false;
	}
}
