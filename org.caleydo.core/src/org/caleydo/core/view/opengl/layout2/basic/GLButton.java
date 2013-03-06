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
package org.caleydo.core.view.opengl.layout2.basic;

import java.awt.Color;

import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.core.view.opengl.picking.AdvancedPick;
import org.caleydo.core.view.opengl.picking.Pick;

/**
 * a simple basic widget for a button with a lot of basic effects
 *
 * @author Samuel Gratzl
 *
 */
public class GLButton extends PickableGLElement {
	/**
	 * is mouse over
	 */
	private boolean hovered = false;
	/**
	 * is mouse down
	 */
	private boolean armed = false;
	/**
	 * is selected
	 */
	private boolean selected = false;

	/**
	 * effect to render when the mouse is over the component
	 */
	private IGLRenderer hoverEffect = GLRenderers.drawRoundedRect(Color.DARK_GRAY);
	/**
	 * effect to render when the mouse is down
	 */
	private IGLRenderer armedEffect = GLRenderers.fillRoundedRect(new Color(1, 1, 1, 0.3f));
	/**
	 * effect to render when the component is selected
	 */
	private IGLRenderer selectedRenderer = null;

	/**
	 * callback for selection state changes
	 */
	private ISelectionCallback callback = DUMMY_CALLBACK;

	/**
	 * mode controlling the behavior on clicked
	 */
	private EButtonMode mode = EButtonMode.BUTTON;

	public GLButton() {

	}

	public GLButton(EButtonMode mode) {
		this.mode = mode;
	}

	/**
	 * @param mode
	 *            setter, see {@link mode}
	 */
	public GLButton setMode(EButtonMode mode) {
		if (this.mode == mode)
			return this;
		this.mode = mode;
		return this;
	}

	/**
	 * @return the mode, see {@link #mode}
	 */
	public EButtonMode getMode() {
		return mode;
	}

	/**
	 * @param armedEffect
	 *            setter, see {@link armedEffect}
	 */
	public GLButton setArmedEffect(IGLRenderer armedEffect) {
		if (armedEffect == null)
			armedEffect = GLRenderers.DUMMY;
		if (this.armedEffect.equals(armedEffect))
			return this;
		this.armedEffect = armedEffect;
		if (armed)
			repaint();
		return this;
	}

	/**
	 * @param hoverEffect
	 *            setter, see {@link hoverEffect}
	 */
	public GLButton setHoverEffect(IGLRenderer hoverEffect) {
		if (hoverEffect == null)
			hoverEffect = GLRenderers.DUMMY;
		if (this.hoverEffect.equals(hoverEffect))
			return this;
		this.hoverEffect = hoverEffect;
		if (hovered)
			repaint();
		return this;
	}

	/**
	 * @param selectedRenderer
	 *            setter, see {@link selectedRenderer}
	 */
	public GLButton setSelectedRenderer(IGLRenderer selectedRenderer) {
		if (this.selectedRenderer != null && this.selectedRenderer.equals(selectedRenderer))
			return this;
		this.selectedRenderer = selectedRenderer;
		if (selected)
			repaint();
		return this;
	}

	/**
	 * @param selected
	 *            setter, see {@link selected}
	 */
	public final GLButton setSelected(boolean selected) {
		if (this.selected == selected)
			return this;
		this.selected = selected;
		fireCallback(this.selected);
		repaint();
		return this;
	}

	protected final void fireCallback(boolean state) {
		callback.onSelectionChanged(this, state);
	}

	/**
	 * @param callback
	 *            setter, see {@link callback}
	 */
	public final GLButton setCallback(ISelectionCallback callback) {
		if (callback == null)
			callback = DUMMY_CALLBACK;
		if (this.callback == callback)
			return this;
		this.callback = callback;
		return this;
	}

	/**
	 * @return the selected, see {@link #selected}
	 */
	public final boolean isSelected() {
		return selected;
	}

	@Override
	protected void onClicked(Pick pick) {
		if (pick.isAnyDragging())
			return;
		armed = true;
		if (!(pick instanceof AdvancedPick)) {
			// compatible mode
			switch (mode) {
			case BUTTON:
				fireCallback(true);
				armed = false;
				break;
			case CHECKBOX:
				this.setSelected(!isSelected());
				armed = false;
				break;
			default:
				break;
			}
		}
		repaint();
	}

	@Override
	protected void onMouseReleased(Pick pick) {
		if (!armed)
			return;
		armed = false;
		switch (mode) {
		case BUTTON:
			fireCallback(true);
			break;
		case CHECKBOX:
			this.setSelected(!isSelected());
			break;
		default:
			break;
		}
		repaint();
	}

	@Override
	protected void onMouseOver(Pick pick) {
		if (pick.isAnyDragging())
			return;
		hovered = true;
		repaint();
	}

	@Override
	protected void onMouseOut(Pick pick) {
		if (!hovered)
			return;
		armed = false;
		hovered = false;
		repaint();
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		if (selected && selectedRenderer != null)
			selectedRenderer.render(g, w, h, this);
		else
			super.renderImpl(g, w, h);

		if (hovered)
			hoverEffect.render(g, w, h, this);
		if (armed)
			armedEffect.render(g, w, h, this);
	}

	/**
	 * mode of this button, the _COMPATIBLE versions are for the old picking manager, where the mouse pressed is the
	 * trigger, the basic versions react on the mouse released event
	 *
	 * @author Samuel Gratzl
	 *
	 */
	public enum EButtonMode {
		BUTTON, CHECKBOX
	}

	/**
	 * callback interface for selection changes of a button
	 *
	 * @author Samuel Gratzl
	 *
	 */
	public interface ISelectionCallback {
		void onSelectionChanged(GLButton button, boolean selected);
	}

	private static final ISelectionCallback DUMMY_CALLBACK = new ISelectionCallback() {
		@Override
		public void onSelectionChanged(GLButton button, boolean selected) {

		}
	};
}
