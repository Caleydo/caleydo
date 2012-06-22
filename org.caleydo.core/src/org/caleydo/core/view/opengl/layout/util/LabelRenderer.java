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
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;

/**
 * Renders a text within the given bounds of the ElementLayout. The text is
 * truncated if necessary.
 * 
 * @author Partl
 */
public class LabelRenderer extends APickableLayoutRenderer {

	public static final int ALIGN_LEFT = 0;
	public static final int ALIGN_CENTER = 1;
	public static final int ALIGN_RIGHT = 2;

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
	private int alignment = ALIGN_LEFT;

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
	public LabelRenderer(AGLView view, ILabelProvider labelProvider, String pickingType,
			int id) {
		super(view, pickingType, id);

		this.isPickable = true;
		this.labelProvider = labelProvider;
	}

	public LabelRenderer(AGLView view, ILabelProvider labelProvider) {
		this.view = view;
		this.labelProvider = labelProvider;
		this.isPickable = false;
	}

	public LabelRenderer(AGLView view, ILabelProvider labelProvider,
			List<Pair<String, Integer>> pickingIDs) {
		super(view, pickingIDs);
		this.isPickable = true;
		this.labelProvider = labelProvider;
	}

	public LabelRenderer(AGLView view, String label,
			List<Pair<String, Integer>> pickingIDs) {
		super(view, pickingIDs);
		this.isPickable = true;
		this.label = label;
	}

	public LabelRenderer(AGLView view, String label) {
		this.isPickable = false;
		this.view = view;
		this.label = label;
	}


	/**
	 * @param label
	 *            setter, see {@link #label}
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the label, see {@link #label}
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param alignment
	 *            setter, see {@link #alignment}
	 */
	public void setAlignment(int alignment) {
		this.alignment = alignment;
	}
	

	@Override
	protected void prepare() {
		if(!prevLabel.equals(label)) {
			setDisplayListDirty();
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
		
		CaleydoTextRenderer textRenderer = view.getTextRenderer();
		float ySpacing = view.getPixelGLConverter().getGLHeightForPixelHeight(1);
		
		textRenderer.setColor(0, 0, 0, 1);
		float textWidth = textRenderer.getRequiredTextWidthWithMax(label, y - 2
				* ySpacing, x);
		switch (alignment) {
		case ALIGN_CENTER:
			textRenderer.renderTextInBounds(gl, label, x / 2.0f - textWidth / 2.0f
					+ ySpacing, 2 * ySpacing, 0.1f, x, y - 2 * ySpacing);
			break;
		case ALIGN_RIGHT:
			textRenderer.renderTextInBounds(gl, label, x - textWidth - 4 * ySpacing,
					ySpacing, 0.1f, x, y - 2 * ySpacing);
			break;
		default:
			textRenderer.renderTextInBounds(gl, label, 0, ySpacing, 0.1f, x, y - 2
					* ySpacing);
		}
	}

	@Override
	protected boolean permitsDisplayLists() {
		return true;
	}
}
