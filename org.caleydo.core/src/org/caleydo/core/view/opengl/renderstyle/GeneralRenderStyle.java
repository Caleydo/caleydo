package org.caleydo.core.view.opengl.renderstyle;

import java.text.DecimalFormat;
import org.caleydo.core.view.opengl.camera.IViewFrustum;

/**
 * Render Styles for the whole system
 * 
 * @author Alexander Lex
 */
public class GeneralRenderStyle
{

	private static final float VERY_SMALL_FONT_SCALING_FACTOR = 0.0005f;

	private static final float SMALL_FONT_SCALING_FACTOR = 0.0007f;

	private static final float HEADING_FONT_SCALING_FACTOR = 0.0009f;

	public static final float INFO_AREA_Z = 0.02f;

	public static final float INFO_AREA_CONNECTION_Z = 0.01f;

	public static final float MINIVEW_Z = 0.02f;

	public static final float[] SELECTED_COLOR = { 1, 1, 0, 1 };

	public static final float[] MOUSE_OVER_COLOR = { 1, 1, 0, 1 };
	
	private static final float[] BACKGROUND_COLOR = {0.7f, 0.7f, 0.7f, 1f};
	
	public static final float LOADING_BOX_HALF_WIDTH = 1f;
	
	public static final float LOADING_BOX_HALF_HEIGHT = 0.3f;
	
	public static final float SELECTION_BOX_WIDTH = 0.5f;
	
	public static final float SELECTION_BOX_HEIGHT = 0.3f;

	public static final float SELECTED_LINE_WIDTH = 3;

	public static final float MOUSE_OVER_LINE_WIDTH = 3;

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
	private GeneralRenderStyle()
	{		
	}

	/**
	 * Constructor.
	 */
	public GeneralRenderStyle(IViewFrustum viewFrustum)
	{
		this();
		decimalFormat = new DecimalFormat("#####.#");
		this.viewFrustum = viewFrustum;
		// fFrustumWidth = viewFrustum.getRight() - viewFrustum.getLeft();
		// fFrustumHeight = viewFrustum.getTop() - viewFrustum.getBottom();
		// scaling is set to the smaller of the two

	}
	
	public float getXCenter()
	{
		return (viewFrustum.getRight() - viewFrustum.getLeft())/2;
	}
	
	public float getYCenter()
	{
		return (viewFrustum.getTop() - viewFrustum.getBottom())/2;
	}
	
	public static DecimalFormat getDecimalFormat()
	{
		return decimalFormat;
	}

	public float getSmallFontScalingFactor()
	{
		float fScaling  = SMALL_FONT_SCALING_FACTOR * getScaling();
		return fScaling;
	
	}

	public float getVerySmallFontScalingFactor()
	{

		return VERY_SMALL_FONT_SCALING_FACTOR * getScaling();
	}

	public float getHeadingFontScalingFactor()
	{

		return HEADING_FONT_SCALING_FACTOR * getScaling();
	}

	public float getVerySmallSpacing()
	{

		return BUTTONS_SPACING / 5 * getScaling();
	}
	
	public float getSmallSpacing()
	{

		return BUTTONS_SPACING * getScaling();
	}

	public float getButtonWidht()
	{

		return BUTTON_WIDTH * getScaling();
	}

	public float getScaling()
	{
		float fScaling;
		if (viewFrustum.getWidth() > viewFrustum.getHeight())
			fScaling = viewFrustum.getWidth();
		else
			fScaling = viewFrustum.getHeight();
		return fScaling;
	}
	
	public float[] getBackgroundColor()
	{
		return BACKGROUND_COLOR;
	}


}
