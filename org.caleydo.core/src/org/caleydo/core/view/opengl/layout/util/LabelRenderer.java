package org.caleydo.core.view.opengl.layout.util;

import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;

/**
 * Renders a text within the given bounds of the ElementLayout. The text is trunkated if necessary.
 * 
 * @author Partl
 */
public class LabelRenderer
	extends APickableLayoutRenderer {

	private boolean isPickable;
	private AGLView labelProvider;
	private String label = "Not set";

	/**
	 * @param view
	 *            Rendering view.
	 * @param text
	 *            Text to render.
	 * @param pickingType
	 *            PickingType for the text.
	 * @param id
	 *            ID for picking.
	 */
	public LabelRenderer(AGLView view, AGLView labelProvider, String pickingType, int id) {
		super(view, pickingType, id);

		this.isPickable = true;
		this.labelProvider = labelProvider;
	}

	public LabelRenderer(AGLView view, AGLView labelProvider) {
		this.view = view;
		this.labelProvider = labelProvider;
		this.isPickable = false;
	}

	public LabelRenderer(AGLView view, AGLView labelProvider, List<Pair<String, Integer>> pickingIDs) {
		super(view, pickingIDs);
		this.isPickable = true;
		this.labelProvider = labelProvider;
	}

	public LabelRenderer(AGLView view, String label, List<Pair<String, Integer>> pickingIDs) {
		super(view, pickingIDs);
		this.isPickable = true;
		this.label = label;
	}

	@Override
	public void render(GL2 gl) {

		if (isPickable) {
			pushNames(gl);

			gl.glColor4f(1, 1, 1, 0);
			gl.glBegin(GL2.GL_POLYGON);
			gl.glVertex3f(0, 0, 0.1f);
			gl.glVertex3f(x, 0, 0.1f);
			gl.glVertex3f(x, y, 0.1f);
			gl.glVertex3f(0, y, 0.1f);
			gl.glEnd();

			popNames(gl);
		}

		CaleydoTextRenderer textRenderer = view.getTextRenderer();
		
		float ySpacing = view.getPixelGLConverter().getGLHeightForPixelHeight(1);

		if (labelProvider != null)
			label = labelProvider.getLabel();
		textRenderer.setColor(0, 0, 0, 1);
		textRenderer.renderTextInBounds(gl, label, 0, ySpacing, 0.1f, x, y - 2 * ySpacing);

	}
}
