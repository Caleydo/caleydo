package org.geneview.core.data.view.rep.renderstyle;

import org.geneview.core.data.GeneralRenderStyle;
import org.geneview.core.data.view.camera.IViewFrustum;

/**
 * 
 * Render styles for the parallel coordinates
 * 
 * @author Alexander Lex
 *
 */

public class ParCoordsRenderStyle 
extends GeneralRenderStyle 
{
	
	 
	public static final float[] POLYLINE_NO_OCCLUSION_PREV_COLOR = {0.0f, 0.0f, 0.0f, 1.0f};
	
	public static final float[] POLYLINE_SELECTED_COLOR = {1.0f, 0.0f, 0.0f, 1.0f};
	public static final float SELECTED_POLYLINE_LINE_WIDTH = 3.0f;
	
	public static final float[] POLYLINE_MOUSE_OVER_COLOR = {0.0f, 1.0f, 0.0f, 1.0f};
	public static final float MOUSE_OVER_POLYLINE_LINE_WIDTH = 3.0f;

	public static final float DESELECTED_POLYLINE_LINE_WIDTH = 1.0f;
	
	

	public static final float[] Y_AXIS_COLOR = {0.0f, 1.0f, 0.0f, 1.0f};
	public static final float[] X_AXIS_COLOR = {0.0f, 0.0f, 1.0f, 1.0f};
	public static final float Y_AXIS_LINE_WIDTH = 1.0f;
	public static final float X_AXIS_LINE_WIDTH = 3.0f;
	public static final float AXIS_Z = 0.0f;
	
	public static final float[] CANVAS_COLOR = {1.0f, 1.0f, 1.0f, 1.0f};
	
	public static final float[] GATE_COLOR = {0.61f, 0.705f, 1.0f, 0.8f};
	


	// Line widths
	public static final float POLYLINE_LINE_WIDTH = 1.0f;
	
	// modifiable colors
	protected float[] polylineOcclusionPrevColor  = {0.0f, 0.0f, 0.0f, 1.0f};
	protected float[] polylineDeselectedColor = {0.0f, 0.0f, 0.0f, 0.1f};
	protected float fOcclusionPrevAlpha = 0.1f;
	
	// how much room between the axis?
	private float fAxisSpacing = 0.5f;

	
	
	// --- constants to scale --- 

	
	// coordinate system
	private static final float COORDINATE_TOP_SPACING = 0.07f;
	private static final float COORDINATE_SIDE_SPACING = 0.05f;
	private static final float COORDINATE_BOTTOM_SPACING = 0.07f;
	public static final float Y_AXIS_LOW = -0.1f;
	public static final float AXIS_MARKER_WIDTH = 0.01f;
	
	// gates
	private static final float GATE_WIDTH = 0.005f;
	private static final float GATE_NEGATIVE_Y_OFFSET = -0.01f;
	private static final float GATE_TIP_HEIGHT = 0.01f;
		
	// buttons below axis
	private static final float AXIS_BUTTONS_Y_OFFSET = 0.04f;
	private static final float AXIS_BUTTONS_X_SPACING = 0.005f;
	private static final float AXIS_BUTTON_WIDTH = 0.018f;
	
	private static final float fAxisSpacingLowerLimit = 0.1f;
	

	public ParCoordsRenderStyle(IViewFrustum viewFrustum)
	{
		super(viewFrustum);
	}

	public float[] getPolylineOcclusionPrevColor(int iNumberOfRenderedLines) 
	{
	
		fOcclusionPrevAlpha = (float) (4/Math.sqrt(iNumberOfRenderedLines));
		
		polylineOcclusionPrevColor[3] = fOcclusionPrevAlpha;
		
		return polylineOcclusionPrevColor;
	}
	
	public float[] getPolylineDeselectedOcclusionPrevColor(int iNumberOfRenderedLines)
	{
		
		polylineDeselectedColor[3] =  (float)(0.5/Math.sqrt(iNumberOfRenderedLines));
		return polylineDeselectedColor;
	}
	
	public float getAxisSpacing(final int iNumberOfAxis)
	{
		fAxisSpacing = (fFrustumWidth - COORDINATE_SIDE_SPACING * 2 * fScaling) / (iNumberOfAxis - 1);
		
		if (fAxisSpacing < fAxisSpacingLowerLimit * fScaling)
			return fAxisSpacingLowerLimit * fScaling;
		
		return fAxisSpacing;
	}
	
	public float getAxisHeight()
	{
		return fFrustumHeight - (COORDINATE_TOP_SPACING + COORDINATE_BOTTOM_SPACING) * fScaling;
	}
	
	public float getXSpacing()
	{
		return COORDINATE_SIDE_SPACING * fScaling;
	}
	
	public float getBottomSpacing()
	{
		return COORDINATE_BOTTOM_SPACING * fScaling;
	}
	
	public float getAxisButtonYOffset()
	{
		return AXIS_BUTTONS_Y_OFFSET * fScaling;
	}
	
	public float getAxisButtonXSpacing()
	{
		return AXIS_BUTTONS_X_SPACING * fScaling;
	}
	
	public float getAxisButtonWidht()
	{
		return AXIS_BUTTON_WIDTH * fScaling;
	}
	
	public float getGateWidth()
	{
		return GATE_WIDTH * fScaling;
	}
	
	public float getGateYOffset()
	{
		return GATE_NEGATIVE_Y_OFFSET * fScaling;
	}
	
	public float getGateTipHeight()
	{
		return GATE_TIP_HEIGHT * fScaling;
	}
	
	public float getAxisCaptionSpacing()
	{
		return COORDINATE_TOP_SPACING / 3 * fScaling;
	}
//	 GATE_WIDTH = 0.015f;
//	private static final float GATE_NEGATIVE_Y_OFFSET = -0.04f;
//	private static final float GATE_TIP_HEIGHT 
}
