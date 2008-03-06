package org.geneview.core.data;

import org.geneview.core.data.view.camera.IViewFrustum;

/**
 * Render Styles for the whole system
 * 
 * @author Alexander Lex
 *
 */
public class GeneralRenderStyle 
{
	
	private static final float SMALL_FONT_SCALING_FACTOR = 0.0007f;
	private static final float HEADING_FONT_SCALING_FACTOR = 0.001f;
	
	public static final float INFO_AREA_Z = 0.02f;
	public static final float INFO_AREA_CONNECTION_Z = 0.01f;
	public static final float MINIVEW_Z = 0.02f;
	
	protected float fFrustumHeight = 0;
	protected float fFrustumWidth = 0;
	protected float fScaling = 0;
	
	protected static final float BUTTONS_SPACING = 0.005f;
	protected static final float BUTTON_WIDTH = 0.018f;
	
	public GeneralRenderStyle()
	{
		
	}
	
	public GeneralRenderStyle(IViewFrustum viewFrustum)
	{
		fFrustumWidth = viewFrustum.getRight() - viewFrustum.getLeft();
		fFrustumHeight = viewFrustum.getTop() - viewFrustum.getBottom();
		// scaling is set to the smaller of the two
		if(fFrustumWidth > fFrustumHeight)
			fScaling = fFrustumHeight;
		else
			fScaling = fFrustumWidth;
	}
	
	public float getSmallFontScalingFactor()
	{
		return SMALL_FONT_SCALING_FACTOR * fScaling;
	}
	
	public float getHeadingFontScalingFactor()
	{
		return HEADING_FONT_SCALING_FACTOR * fScaling;
	}
	
	public float getButtonSpacing()
	{
		return BUTTONS_SPACING * fScaling;
	}
	
	public float getButtonWidht()
	{
		return BUTTON_WIDTH * fScaling;
	}
	
	public float getScaling() 
	{
		return fScaling;
	}
	
}
