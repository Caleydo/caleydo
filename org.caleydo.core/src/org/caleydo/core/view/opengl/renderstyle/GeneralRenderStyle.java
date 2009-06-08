package org.caleydo.core.view.opengl.renderstyle;

import java.text.DecimalFormat;

import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * Render Styles for the whole system
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class GeneralRenderStyle {

	public static final float VERY_SMALL_FONT_SCALING_FACTOR = 0.002f;

	public static final float SMALL_FONT_SCALING_FACTOR = 0.003f;

	public static final float HEADING_FONT_SCALING_FACTOR = 0.005f;

	public static final float INFO_AREA_Z = 0.02f;

	public static final float INFO_AREA_CONNECTION_Z = 0.01f;

	public static final float MINIVEW_Z = 0.02f;

	public static final float[] SELECTED_COLOR = { 1, 0, 1, 1 };
	public static final Color SELECTED_COLOR_SWT = new Color(Display.getCurrent(), 255, 0, 255);
	public static final java.awt.Color SELECTED_COLOR_AWT = new java.awt.Color(255, 0, 255);

	public static final float[] MOUSE_OVER_COLOR = { 1, 1, 0, 1 };
	public static final Color MOUSE_OVER_COLOR_SWT = new Color(Display.getCurrent(), 255, 255, 0);
	public static final java.awt.Color MOUSE_OVER_COLOR_AWT = new java.awt.Color(255, 255, 0, 0);

	private static final float[] BACKGROUND_COLOR = { 0.7f, 0.7f, 0.7f, 1f };

	public static final float[] PANEL_BACKGROUN_COLOR = { 0.85f, 0.85f, 0.85f, 1f };

	public static final float LOADING_BOX_HALF_WIDTH = 1f;

	public static final float LOADING_BOX_HALF_HEIGHT = 0.3f;

	public static final float SELECTION_BOX_WIDTH = 0.5f;

	public static final float SELECTION_BOX_HEIGHT = 0.3f;

	public static final float SELECTED_LINE_WIDTH = 3;

	public static final float MOUSE_OVER_LINE_WIDTH = 3;

	public static final int NUM_CHAR_LIMIT = 15;

	// protected float fFrustumHeight = 0;
	//
	// protected float fFrustumWidth = 0;

	// protected float fScaling = 1;

	protected static final float BUTTONS_SPACING = 0.005f;

	protected static final float BUTTON_WIDTH = 0.018f;

	protected IViewFrustum viewFrustum;

	private static DecimalFormat decimalFormat;

	/**
	 * Default constructor.
	 */
	private GeneralRenderStyle() {
	}

	/**
	 * Constructor.
	 */
	public GeneralRenderStyle(IViewFrustum viewFrustum) {
		this();
		decimalFormat = new DecimalFormat("#####.#");
		this.viewFrustum = viewFrustum;
		// fFrustumWidth = viewFrustum.getRight() - viewFrustum.getLeft();
		// fFrustumHeight = viewFrustum.getTop() - viewFrustum.getBottom();
		// scaling is set to the smaller of the two

	}

	public static DecimalFormat getDecimalFormat() {
		return decimalFormat;
	}

	public float getSmallFontScalingFactor() {
		float fScaling = SMALL_FONT_SCALING_FACTOR;
		return fScaling;
	}

	public float getVerySmallFontScalingFactor() {

		return VERY_SMALL_FONT_SCALING_FACTOR;// * getScaling();
	}

	public float getHeadingFontScalingFactor() {

		return HEADING_FONT_SCALING_FACTOR;// * getScaling();
	}

	public float getVerySmallSpacing() {

		return BUTTONS_SPACING / 5 * getScaling();
	}

	public float getSmallSpacing() {

		return BUTTONS_SPACING * getScaling();
	}

	public float getButtonWidht() {

		return BUTTON_WIDTH * getScaling();
	}

	public float getScaling() {
		float fScaling;
		if (viewFrustum.getWidth() > viewFrustum.getHeight()) {
			fScaling = viewFrustum.getWidth();
		}
		else {
			fScaling = viewFrustum.getHeight();
		}
		return fScaling;
	}

	public float[] getBackgroundColor() {
		return BACKGROUND_COLOR;
	}

}
