package org.geneview.core.data.view.rep.renderstyle;

import gleem.linalg.Vec3f;
import gleem.linalg.Vec4f;
import java.math.*;

import org.geneview.core.data.GeneralRenderStyle;

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
	
	 
	public static final Vec4f POLYLINE_NO_OCCLUSION_PREV_COLOR = new Vec4f(0.0f, 0.0f, 0.0f, 1.0f);
	
	public static final Vec4f POLYLINE_SELECTED_COLOR = new Vec4f(1.0f, 0.0f, 0.0f, 1.0f);
	public static final float SELECTED_POLYLINE_LINE_WIDTH = 3.0f;
	
	public static final Vec4f POLYLINE_MOUSE_OVER_COLOR = new Vec4f(0.0f, 1.0f, 0.0f, 1.0f);
	public static final float MOUSE_OVER_POLYLINE_LINE_WIDTH = 3.0f;
	

	public static final float DESELECTED_POLYLINE_LINE_WIDTH = 1.0f;
	
	public static final Vec4f Y_AXIS_COLOR = new Vec4f(0.0f, 1.0f, 0.0f, 1.0f);
	public static final Vec4f X_AXIS_COLOR = new Vec4f(0.0f, 0.0f, 1.0f, 1.0f);
	public static final float Y_AXIS_LINE_WIDTH = 1.0f;
	public static final float X_AXIS_LINE_WIDTH = 3.0f;
	
	public static final Vec4f CANVAS_COLOR = new Vec4f(1.0f, 1.0f, 1.0f, 1.0f);
	
	public static final Vec4f GATE_COLOR = new Vec4f(0.61f, 0.705f, 1.0f, 0.8f);
	public static final float GATE_WIDTH = 0.015f;
	public static final float GATE_NEGATIVE_Y_OFFSET = -0.04f;
	public static final float GATE_TIP_HEIGHT = 0.03f;
	
	/**
	 *  lenght of markes on y axis in one direction (1/2 of actual length)
	 */
	public static final float AXIS_MARKER_WIDTH = 0.01f;
	
	
	

	// Line widths
	public static final float POLYLINE_LINE_WIDTH = 1.0f;
	
	// modifiable colors
	protected Vec4f polylineOcclusionPrevColor;
	protected Vec4f polylineDeselectedColor; 
	protected float fOcclusionPrevAlpha = 0.1f;

	
	
	public ParCoordsRenderStyle()
	{
		polylineOcclusionPrevColor = new Vec4f(0.0f, 0.0f, 0.0f, 1.0f);
		polylineDeselectedColor = new Vec4f(0.0f, 0.0f, 0.0f, 0.1f);
	}

	// TODO: intelligent factor for number
	public Vec4f getPolylineOcclusionPrevColor(int iNumberOfRenderedLines) 
	{
	
		fOcclusionPrevAlpha = (float) (4/Math.sqrt(iNumberOfRenderedLines));
		
		polylineOcclusionPrevColor.set(3, fOcclusionPrevAlpha);
		
		return polylineOcclusionPrevColor;
	}
	
	public Vec4f getPolylineDeselectedOcclusionPrevColor(int iNumberOfRenderedLines)
	{
		
		polylineDeselectedColor.set(3, (float)(0.5/Math.sqrt(iNumberOfRenderedLines)));
		return polylineDeselectedColor;
	}
	
	
	
	

}
