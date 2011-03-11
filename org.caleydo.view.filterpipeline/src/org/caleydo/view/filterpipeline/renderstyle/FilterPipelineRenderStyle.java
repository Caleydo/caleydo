package org.caleydo.view.filterpipeline.renderstyle;

import java.util.ArrayList;

import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;

/**
 * Render style.
 * 
 * @author Thomas Geymayer
 */
public class FilterPipelineRenderStyle extends GeneralRenderStyle
{
	public final float DRAG_LINE_WIDTH = 2;
	public final float FILTER_SPACING_BOTTOM = .2f;
	public final float FILTER_SPACING_TOP = .2f;
	
	public final float[] BACKGROUND_COLOR = {.9f, .8f, .7f, 1};
	public final float[] DRAG_LINE_COLOR  = {.2f, .2f, .2f, 1};
	public final float[] DRAG_OVER_COLOR  = {.9f, .4f, .5f, .8f};
	
	public final float[] FILTER_PASSED_ALL_COLOR  = {.2f, 0.8f, .3f, 0.5f};
	public final float[] FILTER_COMBINED_BACKGROUND_COLOR  = {.9f, .9f, .9f, 1};
	
	private ArrayList<float[]> filterColors = new ArrayList<float[]>();

	public FilterPipelineRenderStyle(ViewFrustum viewFrustum)
	{
		super(viewFrustum);
		
		filterColors.add(new float[]{1.f,0.4f,0.3f,1});
		filterColors.add(new float[]{0.6f,0.4f,1.f,1});
		filterColors.add(new float[]{0.6f,1.f,0.3f,1});
	}
	
	public ViewFrustum getViewFrustum()
	{
		return viewFrustum;
	}
	
	public float[] getFilterColor(int filterId)
	{
		return filterColors.get(filterId % filterColors.size());
	}
	
	public float[] getFilterColorDrag(int filterId)
	{
		float[] color = filterColors.get(filterId % filterColors.size()).clone();
		color[3] = 0.5f;
		return color;
	}
	
	public float[] getFilterColorCombined(int filterId)
	{
		float[] color = filterColors.get(filterId % filterColors.size()).clone();
		color[3] = 0.4f;
		return color;
	}
}
