/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.basic;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.core.view.opengl.picking.AdvancedPick;
import org.caleydo.core.view.opengl.picking.Pick;

/**
 * a simple basic widget for a button with a lot of basic effects
 *
 * @author Samuel Gratzl
 *
 */
public class GLButton extends AGLButton {
	/**
	 * is selected
	 */
	private boolean selected = false;

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
	 * @return the selectedRenderer, see {@link #selectedRenderer}
	 */
	public IGLRenderer getSelectedRenderer() {
		return selectedRenderer;
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
	 * mode of this button
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

	/**
	 * creates an {@link IconLabelRenderer} with a checkbox icon
	 *
	 * @param label
	 * @return
	 */
	public static IconLabelRenderer createCheckRenderer(String label) {
		return new IconLabelRenderer(label, "checkbox");
	}

	/**
	 * creates an {@link IconLabelRenderer} with a radio icon
	 *
	 * @param label
	 * @return
	 */
	public static IconLabelRenderer createRadioRenderer(String label) {
		return new IconLabelRenderer(label, "radio");
	}

	protected static String getStandardIcon(String mode, boolean selected) {
		return String.format("resources/icons/general/%s_%sselected.png", mode, (selected ? "" : "not_"));
	}

	public static class IconLabelRenderer implements IGLRenderer {
		private final String label;
		private final String prefix;

		private IconLabelRenderer(String label, String prefix) {
			this.label = label;
			this.prefix = prefix;
		}

		/**
		 * @return the label, see {@link #label}
		 */
		public String getLabel() {
			return label;
		}

		@Override
		public void render(GLGraphics g, float w, float h, GLElement parent) {
			boolean s = ((GLButton) parent).isSelected();

			String icon = getStandardIcon(prefix, s);
			g.fillImage(icon, 1, 1, h - 2, h - 2);
			if (label != null && label.length() > 0)
				g.drawText(label, h, 0, w - h, 13);
		}
	}
}
