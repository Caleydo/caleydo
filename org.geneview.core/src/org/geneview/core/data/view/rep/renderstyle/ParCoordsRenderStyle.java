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
	
	// final Colors 
	public static final Vec4f POLYLINE_NO_OCCLUSION_PREV_COLOR = new Vec4f(0.0f, 0.0f, 0.0f, 1.0f);
	public static final Vec4f POLYLINE_SELECTED_COLOR = new Vec4f(1.0f, 0.0f, 0.0f, 1.0f);
	
	public static final Vec4f Y_AXIS_COLOR = new Vec4f(0.0f, 0.0f, 0.0f, 1.0f);
	public static final Vec4f X_AXIS_COLOR = new Vec4f(0.0f, 0.0f, 0.0f, 1.0f);
	
	public static final Vec4f CANVAS_COLOR = new Vec4f(1.0f, 1.0f, 1.0f, 1.0f);
	
	// modifiable colors
	protected Vec4f polylineOcclusionPrevColor;
	

	
	// Line widths
	public static final float POLYLINE_LINE_WIDTH = 1.0f;
	public static final float SELECTED_POLYLINE_LINE_WIDTH = 2.0f;

	public static final float Y_AXIS_LINE_WIDTH = 1.0f;
	public static final float X_AXIS_LINE_WIDTH = 3.0f;
	
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
