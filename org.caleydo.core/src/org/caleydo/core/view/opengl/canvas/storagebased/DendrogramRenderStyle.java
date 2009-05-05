package org.caleydo.core.view.opengl.canvas.storagebased;

import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;

/**
 * Dendrogram render styles
 * 
 * @author Bernhard Schlegl
 */

public class DendrogramRenderStyle
	extends GeneralRenderStyle {

	public static final float SIDE_SPACING = 0.1f;

	public DendrogramRenderStyle(AGLDendrogram dendrogram, IViewFrustum viewFrustum) {

		super(viewFrustum);

	}
}
