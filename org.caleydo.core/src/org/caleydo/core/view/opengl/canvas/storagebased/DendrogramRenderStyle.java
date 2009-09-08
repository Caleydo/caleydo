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
	public static final float[] CUT_OFF_COLOR = { 0f, 0f, 1f, 0.4f };
	public static final float[] CUT_OFF_HANDLE_COLOR = { 0f, 0f, 1f, 1f };

	public static final float DENDROGRAM_Z = 0.001f;
	public static final float SELECTION_Z = 0.005f;
	public static final float SUB_DENDROGRAM_Z = 0.01f;
	public static final float CUT_OFF_Z = 0.01f;

	private float fWidthCutOff = 0.05f;
	private float fDendrogramLineWidth = 0.1f;

	GLDendrogram dendrogram;

	public DendrogramRenderStyle(GLDendrogram dendrogram, IViewFrustum viewFrustum) {

		super(viewFrustum);

		this.dendrogram = dendrogram;
	}

	public float getWidthCutOff() {
		return fWidthCutOff;
	}

	public float getDendrogramLineWidth() {
		return fDendrogramLineWidth;
	}
}
