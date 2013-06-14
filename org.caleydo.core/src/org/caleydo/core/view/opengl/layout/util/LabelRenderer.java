/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout.util;

import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.util.base.ILabelProvider;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.util.text.ITextRenderer;

/**
 * Renders a text within the given bounds of the ElementLayout. The text is
 * truncated if necessary.
 *
 * @author Partl
 */
public class LabelRenderer
	extends APickableLayoutRenderer {

	/**
	 *
	 */
	private static final float PADDING_BOTTOM = 0.01f;

	public enum LabelAlignment {
		LEFT, CENTER, RIGHT
	}

	private boolean isPickable;
	private ILabelProvider labelProvider;
	private String label = "Not set";

	/**
	 * The text of the label that was rendered in the last frame. This variable
	 * is used to detect whether a new display list has to be built.
	 */
	private String prevLabel = "";

	/**
	 * Specifies the alignment of the text.
	 */
	private LabelAlignment alignment = LabelAlignment.LEFT;

	/**
	 * Flag determines if text should rendered a little bit higher than the
	 * baseline of the layout.
	 */
	private boolean usePaddingBottom = false;

	private final ITextRenderer textRenderer;

	/**
	 * @param view Rendering view.
	 * @param text Text to render.
	 * @param pickingType PickingType for the text.
	 * @param id ID for picking.
	 */
	protected LabelRenderer(AGLView view, ILabelProvider labelProvider, String pickingType, int id) {
		super(view, pickingType, id);
		this.textRenderer = view.getTextRenderer();
		this.isPickable = true;
		this.labelProvider = labelProvider;
	}

	public LabelRenderer(AGLView view, ITextRenderer textRenderer, ILabelProvider labelProvider,
			List<Pair<String, Integer>> pickingIDs) {
		super(view, pickingIDs);
		this.textRenderer = textRenderer;
		this.isPickable = true;
		this.labelProvider = labelProvider;
	}

	public LabelRenderer(AGLView view, ITextRenderer textRenderer, ILabelProvider labelProvider) {
		this.view = view;
		this.textRenderer = textRenderer;
		this.labelProvider = labelProvider;
		this.isPickable = false;
	}

	/**
	 * @param alignment
	 *            the alignment to set
	 */
	public LabelRenderer setAlignment(LabelAlignment alignment) {
		this.alignment = alignment;
		return this;
	}

	/**
	 * @param paddingBottom setter, see {@link #usePaddingBottom}
	 */
	public LabelRenderer usePaddingBottom(boolean paddingBottom) {
		this.usePaddingBottom = paddingBottom;
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.caleydo.core.view.opengl.layout.util.APickableLayoutRenderer#addPickingID(java.lang.String, int)
	 */
	@Override
	public APickableLayoutRenderer addPickingID(String pickingType, int id) {
		isPickable = true;
		return super.addPickingID(pickingType, id);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.caleydo.core.view.opengl.layout.util.APickableLayoutRenderer#addPickingIDs(java.util.List)
	 */
	@Override
	public void addPickingIDs(List<Pair<String, Integer>> pickingIDs) {
		if (pickingIDs != null && !pickingIDs.isEmpty())
			isPickable = true;
		super.addPickingIDs(pickingIDs);
	}

	@Override
	protected void prepare() {
		if (labelProvider != null)
			label = labelProvider.getLabel();

		if (!prevLabel.equals(label)) {
			setDisplayListDirty(true);
		}
		prevLabel = label;
	}

	@Override
	protected void renderContent(GL2 gl) {

		if (labelProvider != null)
			label = labelProvider.getLabel();

		if (isPickable) {
			pushNames(gl);

			gl.glColor4f(1, 1, 1, 0);
			gl.glBegin(GL2.GL_POLYGON);
			gl.glVertex3f(0, 0, 0.05f);
			gl.glVertex3f(x, 0, 0.05f);
			gl.glVertex3f(x, y, 0.05f);
			gl.glVertex3f(0, y, 0.05f);
			gl.glEnd();

			popNames(gl);
		}

		float ySpacing = view.getPixelGLConverter().getGLHeightForPixelHeight(1);

		textRenderer.setColor(Color.BLACK);
		float textWidth = Math.min(textRenderer.getTextWidth(label, y - 2 * ySpacing), x);

		float padding = 0;
		if (usePaddingBottom)
			padding = PADDING_BOTTOM;

		switch (alignment) {
		case CENTER:
			textRenderer.renderTextInBounds(gl, label, x / 2.0f - textWidth / 2.0f + ySpacing + padding, 2 * ySpacing
					+ padding, 0.1f, x, y - 2 * ySpacing);
			break;
		case RIGHT:
			textRenderer.renderTextInBounds(gl, label, x - textWidth - 4 * ySpacing, ySpacing + padding, 0.1f, x, y - 2
					* ySpacing + padding);
			break;
		default:
			textRenderer.renderTextInBounds(gl, label, 0, ySpacing + padding, 0.1f, x, y - 2 * ySpacing + padding);
		}
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return true;
	}
}
