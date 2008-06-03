package org.caleydo.core.data.view.rep.renderstyle;

import org.caleydo.core.data.GeneralRenderStyle;
import org.caleydo.core.data.view.camera.IViewFrustum;

/**
 * 
 * Render styles for the parallel coordinates
 * 
 * @author Alexander Lex
 *
 */

public class GlyphRenderStyle 
extends GeneralRenderStyle 
{
	public GlyphRenderStyle(IViewFrustum viewFrustum)
	{
		super(viewFrustum);
	}
	
	public float getButtonWidht() {
		return 1.0f;
	}

}
