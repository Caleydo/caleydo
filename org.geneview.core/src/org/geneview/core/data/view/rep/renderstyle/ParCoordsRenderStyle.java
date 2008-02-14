package org.geneview.core.data.view.rep.renderstyle;

import gleem.linalg.Vec3f;
import gleem.linalg.Vec4f;

import org.geneview.core.data.ARenderStyle;

/**
 * 
 * Render styles for the parallel coordinates
 * 
 * @author Alexander Lex
 *
 */

public class ParCoordsRenderStyle 
extends ARenderStyle 
{
	
	 
	public static final Vec4f POLYLINE_NO_OCCLUSION_PREV_COLOR = new Vec4f(1.0f, 0.0f, 0.0f, 1.0f);
	
	public static final Vec4f POLYLINE_SELECTED_COLOR = new Vec4f(1.0f, 0.0f, 0.0f, 1.0f);
	public static final float SELECTED_POLYLINE_LINE_WIDTH = 3.0f;
	
	public static final Vec4f POLYLINE_MOUSE_OVER_COLOR = new Vec4f(0.0f, 1.0f, 0.0f, 1.0f);
	public static final float MOUSE_OVER_POLYLINE_LINE_WIDTH = 3.0f;
	
	public static final Vec4f Y_AXIS_COLOR = new Vec4f(0.0f, 1.0f, 0.0f, 1.0f);
	public static final Vec4f X_AXIS_COLOR = new Vec4f(0.0f, 0.0f, 1.0f, 1.0f);
	public static final float Y_AXIS_LINE_WIDTH = 1.0f;
	public static final float X_AXIS_LINE_WIDTH = 3.0f;
	
	public static final Vec4f CANVAS_COLOR = new Vec4f(1.0f, 1.0f, 1.0f, 1.0f);
	
	public static final Vec4f GATE_COLOR = new Vec4f(0.61f, 0.705f, 1.0f, 0.8f);
	public static final float GATE_WIDTH = 0.015f;
	public static final float GATE_NEGATIVE_Y_OFFSET = -0.1f;
	public static final float GATE_TIP_HEIGHT = 0.03f;
	
	// modifiable colors
	protected Vec4f polylineOcclusionPrevColor;
	

	
	// Line widths
	public static final float POLYLINE_LINE_WIDTH = 1.0f;


	
	
	public ParCoordsRenderStyle()
	{
		polylineOcclusionPrevColor = new Vec4f(0.0f, 0.0f, 0.0f, 1.0f);
	}

	// TODO: intelligent factor for number
	public Vec4f getPolylineOcclusionPrevColor(int numberOfRenderedLines) 
	{
		if (numberOfRenderedLines > 100)
		{
			polylineOcclusionPrevColor.set(3, 0.1f);
		}
		else
		{
			polylineOcclusionPrevColor.set(3, 0.6f);
		}
		return polylineOcclusionPrevColor;
	}
	
	
	
	

}
