/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.media.opengl.GL;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout.util.multiform.IMultiFormChangeListener;
import org.caleydo.core.view.opengl.layout.util.multiform.MultiFormRenderer;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.EButtonMode;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;

/**
 * @author Christian
 *
 */
public class GLElementViewSwitchingBar extends GLElementContainer implements IMultiFormChangeListener {

	public final static int DEFAULT_HEIGHT_PIXELS = 16;
	public final static int BUTTON_SPACING_PIXELS = 2;

	/**
	 * Picking type used for buttons.
	 */
	// private final String buttonPickingType;

	/**
	 * MultiFormRenderer the buttons of this bar are for.
	 */
	private MultiFormRenderer multiFormRenderer;

	/**
	 * Map associating rendererIDs with corresponding buttons and layouts.
	 */
	private Map<Integer, GLButton> buttons = new HashMap<>();

	/**
	 * PickingListener for buttons to switch views.
	 */
	private IPickingListener buttonPickingListener = new APickingListener() {
		@Override
		public void clicked(Pick pick) {
			GLElementViewSwitchingBar.this.multiFormRenderer.setActive(pick.getObjectID(), true);
		}
	};

	/**
	 * @param multiFormRenderer
	 *            The {@link MultiFormRenderer} buttons for view switching shall be created for.
	 */
	public GLElementViewSwitchingBar(MultiFormRenderer multiFormRenderer) {

		setSize(Float.NaN, DEFAULT_HEIGHT_PIXELS);
		setLayout(GLLayouts.flowHorizontal(BUTTON_SPACING_PIXELS));
		multiFormRenderer.addChangeListener(this);
		// buttonPickingType = MultiFormViewSwitchingBar.class.getName() + hashCode();
		this.multiFormRenderer = multiFormRenderer;

		createButtonsForMultiformRenderer(multiFormRenderer);

	}

	/**
	 * Adds a specified picking listener for a button identified by the corresponding rendererID, if such a button is
	 * present in the bar. This listener will be unregistered when the button is removed from the bar or the bar is
	 * destroyed.
	 *
	 * @param pickingListener
	 * @param rendererID
	 */
	public void addButtonPickingListener(IPickingListener pickingListener, int rendererID) {
		GLButton button = buttons.get(rendererID);
		if (button != null) {
			button.onPick(pickingListener);
		}
	}

	/**
	 * Sets the tooltip of the button of the associated renderer.
	 *
	 * @param toolTip
	 * @param rendererID
	 */
	public void setButtonToolTip(String toolTip, int rendererID) {
		GLButton button = buttons.get(rendererID);
		if (button != null) {
			button.setTooltip(toolTip);
		}
	}

	private void createButtonsForMultiformRenderer(MultiFormRenderer multiFormRenderer) {
		Set<Integer> rendererIDs = multiFormRenderer.getRendererIDs();
		List<Integer> idList = new ArrayList<>(rendererIDs);
		Collections.sort(idList);

		for (int i = 0; i < idList.size(); i++) {
			int rendererID = idList.get(i);
			addButton(rendererID, multiFormRenderer);
		}
	}

	/**
	 * Adds a button for a specified renderer. If a button already exists for this renderer, it will be replaced. Note
	 * that buttons usually do not have to be added manually, as they are created automatically for all renderers of a
	 * {@link MultiFormRenderer}.
	 *
	 * @param rendererID
	 *            ID of the renderer a button should be added for.
	 * @param multiFormRenderer
	 *            The <code>MultiFormRenderer</code> the renderer belongs to.
	 */
	public void addButton(final int rendererID, final MultiFormRenderer multiFormRenderer) {

		// Button button = new Button(buttonPickingType, rendererID, multiFormRenderer.getIconPath(rendererID));
		// ElementLayout buttonLayout = ElementLayouts.createButton(view, button, DEFAULT_HEIGHT_PIXELS,
		// DEFAULT_HEIGHT_PIXELS, 0.22f);
		GLButton button = new GLButton(EButtonMode.CHECKBOX);
		button.setRenderer(GLRenderers.fillImage(multiFormRenderer.getIconPath(rendererID)));
		button.setSelectedRenderer(new IGLRenderer() {
			@Override
			public void render(GLGraphics g, float w, float h, GLElement parent) {
				g.fillImage(multiFormRenderer.getIconPath(rendererID), 0, 0, w, h);
				g.gl.glEnable(GL.GL_BLEND);
				g.gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
				g.gl.glEnable(GL.GL_LINE_SMOOTH);
				g.color(new Color(1, 1, 1, 0.5f)).fillRoundedRect(0, 0, w, h, Math.min(w, h) * 0.25f);
				g.gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);
			}
		});
		int activeRendererID = multiFormRenderer.getActiveRendererID();
		if (activeRendererID == -1) {
			activeRendererID = multiFormRenderer.getDefaultRendererID();
		}
		if (rendererID == activeRendererID) {
			button.setSelected(true);
		}

		button.onPick(buttonPickingListener);
		button.setPickingObjectId(rendererID);

		// GLElementAdapter(view, new ButtonRenderer.Builder(view, button)
		// .zCoordinate(0.22f).build());
		button.setSize(DEFAULT_HEIGHT_PIXELS, DEFAULT_HEIGHT_PIXELS);

		if (buttons.containsKey(rendererID)) {
			GLButton oldButton = buttons.get(rendererID);
			// GLElement element = buttonPair.getSecond();

			int elementIndex = indexOf(oldButton);
			add(elementIndex, button);

			remove(oldButton);
		} else {
			add(button);
		}
		buttons.put(rendererID, button);

		setSize(buttons.size() * DEFAULT_HEIGHT_PIXELS + (buttons.size() - 1) * BUTTON_SPACING_PIXELS,
				DEFAULT_HEIGHT_PIXELS);
	}

	/**
	 * Removes the button corresponding to the renderer specified by the provided ID from the toolbar.
	 *
	 * @param rendererID
	 *            ID of the renderer the button corresponds to.
	 */
	public void removeButton(int rendererID) {

		GLButton button = buttons.get(rendererID);
		if (button == null)
			return;
		remove(button);
		buttons.remove(rendererID);
	}

	@Override
	public void destroyed(MultiFormRenderer multiFormRenderer) {
		this.multiFormRenderer = null;
		takeDown();
	}

	@Override
	public void activeRendererChanged(MultiFormRenderer multiFormRenderer, int rendererID, int previousRendererID,
			boolean wasTriggeredByUser) {
		selectButton(previousRendererID, false);
		selectButton(rendererID, true);
	}

	private void selectButton(int rendererID, boolean select) {
		GLButton button = buttons.get(rendererID);
		if (button != null) {
			button.setSelected(select);
			// buttonPair.getSecond().repaint();
		}
	}

	@Override
	public void rendererAdded(MultiFormRenderer multiFormRenderer, int rendererID) {
		addButton(rendererID, multiFormRenderer);
	}

	@Override
	public void rendererRemoved(MultiFormRenderer multiFormRenderer, int rendererID) {
		removeButton(rendererID);
	}

	@Override
	public void takeDown() {
		if (multiFormRenderer != null)
			multiFormRenderer.removeChangeListener(this);
		buttons.clear();
		super.takeDown();
	}

}
