/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.parcoords;

import java.util.HashMap;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
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
			this.zDepth = POLYLINE_NORMAL_Z + selectionType.getPriority() * (POLYLINE_SELECTED_Z - POLYLINE_NORMAL_Z);
			this.color = selectionType.getColor();
			updateOcclusionPrev(nrElements);
		}

		void updateOcclusionPrev(int nrElements) {
			this.color[3] = getPolylineOcclusionPrevAlpha(nrElements);
		}
	}

	private HashMap<SelectionType, PolyLineState> hashSelectionTypeToPolylineState;

	// textures
	public static String ICON_PATH = "resources/icons/";

	public static String PC_LARGE_TEXTURE = ICON_PATH + "parcoords128x128.png";

	public static String DROP_NORMAL = ICON_PATH + "drop_normal.png";
	public static String DROP_DELETE = ICON_PATH + "drop_delete.png";
	public static String DROP_DUPLICATE = ICON_PATH + "drop_duplicate.png";
	public static String DROP_MOVE = ICON_PATH + "drop_move.png";
	public static String SMALL_DROP = ICON_PATH + "drop_small.png";
	public static String SMALL_DROP_ROTATED = ICON_PATH + "drop_small_rotated.png";
	public static String ADD_GATE = ICON_PATH + "add_gate.png";
	public static String NAN = ICON_PATH + "nan.png";
	public static String GATE_BOTTOM = ICON_PATH + "gate_bottom.png";
	public static String GATE_TOP = ICON_PATH + "gate_top.png";
	public static String GATE_MENUE = ICON_PATH + "gate_menue.png";
	public static String GATE_BODY = ICON_PATH + "gate_body.png";

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

	public static final float[] GATE_BODY_COLOR = { 0.61f, 0.705f, 1.0f, 0.8f };

	public static final float[] ANGULAR_COLOR = { 0.17f, 0.45f, 0.84f, 1 };
	public static final float[] ANGULAR_POLYGON_COLOR = { 0.17f, 0.45f, 0.84f, 0.4f };

	public static final float ANGLUAR_LINE_WIDTH = 4;

	public static final int NUMBER_AXIS_MARKERS = 9;

	public static final int NAN_Y_OFFSET = 15;

	protected float fOcclusionPrevAlpha = 0.1f;

	// how much room between the axis?
	private float axisSpacing = 0.3f;

	// --- constants to scale ---

	// coordinate system
	private static final float COORDINATE_TOP_SPACING = 0.07f;

	private static final float COORDINATE_BOTTOM_SPACING = 0.06f;

	public static final int AXIS_MARKER_WIDTH = 4;

	private static final float fMinAxisSpacingForText = 0.1f;

	public final PolyLineState normalState = new PolyLineState(SelectionType.NORMAL, (1000));

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
		if (pcs != null && pcs.getDetailLevel() == EDetailLevel.LOW)
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
		return viewFrustum.getHeight() - (COORDINATE_TOP_SPACING + COORDINATE_BOTTOM_SPACING) * getScaling();
	}

	public float getXSpacing() {
		if (pcs.getDetailLevel().equals(EDetailLevel.HIGH))
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
