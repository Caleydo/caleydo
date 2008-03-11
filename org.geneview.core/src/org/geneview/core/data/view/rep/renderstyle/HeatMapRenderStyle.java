package org.geneview.core.data.view.rep.renderstyle;

import org.geneview.core.data.GeneralRenderStyle;
import org.geneview.core.data.view.camera.IViewFrustum;

/**
 * 
 * @author Alexander Lex
 *
 */

public class HeatMapRenderStyle 
extends GeneralRenderStyle 
{
	public static final float FIELD_Z = 0.0f;
	public static final float SELECTION_Z = 0.001f;
	
	public HeatMapRenderStyle(IViewFrustum viewFrustum)
	{
		super(viewFrustum);
	}
	
	public float getFieldWidth()
	{
		return 0.07f;
	}
	
	public float getFieldHeight()
	{
		return 0.4f;
	}
	
	public float getBottomSpacing()
	{
		// TODO: this is only correct for 4 rows
		return (fFrustumHeight / 2 - getFieldHeight() * 2);
	}
	
//	public float getXSpacing()
//	{
//		return COORDINATE_SIDE_SPACING * fScaling;
//	}
//	
//	public float getYSpacing()
//	{
//		
//	}
	
	
	
}
