package org.caleydo.core.view.opengl.renderstyle.infoarea;

import java.awt.Color;

/**
 * Render style for info overlay panel in OpenGL views.
 * 
 * @author Marc Streit
 */
public class AInfoOverlayRenderStyle {

	public final static int MAX_OVERLAY_HEIGHT = 300;

	public final static int OVERLAY_WIDTH = 800;

	public final static int LINE_HEIGHT = 20;

	public final static Color fontColor = Color.WHITE;

	public final static Color backgroundColor = new Color(0, 0, 0, 0.3f);

	public final static Color borderColor = Color.DARK_GRAY;

	public enum VerticalPosition {
		TOP,
		CENTER,
		BOTTOM
	}
}
