package org.caleydo.core.view.opengl.renderstyle;

import gleem.linalg.Vec4f;
import java.awt.Font;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import com.sun.opengl.util.j2d.TextRenderer;

/**
 * Render styles for the parallel coordinates
 * 
 * @author Sauer Stefan
 */

public class GlyphRenderStyle
	extends GeneralRenderStyle
{

	public GlyphRenderStyle(IViewFrustum viewFrustum)
	{

		super(viewFrustum);
	}

	@Override
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

	public Vec4f getGridColor()
	{
		return new Vec4f(0.2f, 0.2f, 0.2f, 1f);
	}

}
