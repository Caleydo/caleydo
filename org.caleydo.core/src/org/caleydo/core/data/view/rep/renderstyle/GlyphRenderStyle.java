package org.caleydo.core.data.view.rep.renderstyle;

import java.awt.Font;
import org.caleydo.core.data.view.camera.IViewFrustum;
import com.sun.opengl.util.j2d.TextRenderer;

/**
 * Render styles for the parallel coordinates
 * 
 * @author Alexander Lex
 */

public class GlyphRenderStyle
	extends GeneralRenderStyle
{

	public GlyphRenderStyle(IViewFrustum viewFrustum)
	{

		super(viewFrustum);
	}

	public float getButtonWidht()
	{

		return 1.0f;
	}

	public TextRenderer getScatterplotTextRenderer()
	{

		TextRenderer textRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 16), false);
		textRenderer.setColor(0, 0, 0, 1);
		return textRenderer;
	}

}
