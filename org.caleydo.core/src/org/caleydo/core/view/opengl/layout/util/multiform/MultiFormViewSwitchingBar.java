/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout.util.multiform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.media.opengl.GL2;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.ElementLayouts;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.button.Button;

/**
 * Bar with buttons that can be used to change the rendered content of a {@link MultiFormRenderer}. Buttons are rendered
 * as squares with the height of this toolbar as length for their sides. The buttons are ordered according to the order
 * the corresponding renderers were added to the <code>MultiFormRenderer</code>.
 *
 * @author Christian Partl
 *
 */
public class MultiFormViewSwitchingBar extends Row implements IMultiFormChangeListener {

	public final static int DEFAULT_HEIGHT_PIXELS = 16;
	public final static int BUTTON_SPACING_PIXELS = 2;

	/**
	 * Parent view with canvas.
	 */
	private final AGLView view;

	/**
	 * Picking type used for buttons.
	 */
	private final String buttonPickingType;

	/**
	 * MultiFormRenderer the buttons of this bar are for.
	 */
	private MultiFormRenderer multiFormRenderer;

	/**
	 * Map associating rendererIDs with corresponding buttons and layouts.
	 */
	private Map<Integer, Pair<Button, ElementLayout>> buttons = new HashMap<>();

	/**
	 * @param multiFormRenderer
	 *            The {@link MultiFormRenderer} buttons for view switching shall be created for.
	 * @param view
	 *            The view with canvas that renders this toolbar.
	 */
	public MultiFormViewSwitchingBar(MultiFormRenderer multiFormRenderer, AGLView view) {
		setPixelSizeY(DEFAULT_HEIGHT_PIXELS);
		multiFormRenderer.addChangeListener(this);
		this.view = view;
		buttonPickingType = MultiFormViewSwitchingBar.class.getName() + hashCode();
		this.multiFormRenderer = multiFormRenderer;

		createButtonsForMultiformRenderer(multiFormRenderer);

		view.addTypePickingListener(new APickingListener() {
			@Override
			public void clicked(Pick pick) {
				MultiFormViewSwitchingBar.this.multiFormRenderer.setActive(pick.getObjectID(), true);
			}
		}, buttonPickingType);
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
		if (buttons.containsKey(rendererID))
			view.addIDPickingListener(pickingListener, buttonPickingType, rendererID);
	}

	public void setToolTip(String text, int rendererID) {
		if (buttons.containsKey(rendererID))
			view.addIDPickingTooltipListener(text, buttonPickingType, rendererID);
	}

	private void createButtonsForMultiformRenderer(MultiFormRenderer multiFormRenderer) {
		Set<Integer> rendererIDs = multiFormRenderer.getRendererIDs();
		List<Integer> idList = new ArrayList<>(rendererIDs);
		Collections.sort(idList);

		for (int i = 0; i < idList.size(); i++) {
			int rendererID = idList.get(i);
			addButton(rendererID, multiFormRenderer);
			if (i < idList.size() - 1) {
				add(ElementLayouts.createXSpacer(BUTTON_SPACING_PIXELS));
			}
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
	public void addButton(int rendererID, MultiFormRenderer multiFormRenderer) {

		Button button = new Button(buttonPickingType, rendererID, multiFormRenderer.getIconPath(rendererID));
		ElementLayout buttonLayout = ElementLayouts.createButton(view, button, DEFAULT_HEIGHT_PIXELS,
				DEFAULT_HEIGHT_PIXELS, 0.22f);

		if (buttons.containsKey(rendererID)) {
			Pair<Button, ElementLayout> buttonPair = buttons.get(rendererID);
			ElementLayout elementLayout = buttonPair.getSecond();

			int elementIndex = indexOf(elementLayout);
			add(elementIndex, buttonLayout);

			remove(elementLayout);
			GL2 gl = view.getParentGLCanvas().asGLAutoDrawAble().getGL().getGL2();
			elementLayout.destroy(gl);

		} else {
			add(buttonLayout);
		}
		buttons.put(rendererID, new Pair<>(button, buttonLayout));
		setToolTip("Switch to " + multiFormRenderer.getVisInfo(rendererID).getLabel(), rendererID);
	}

	/**
	 * Removes the button corresponding to the renderer specified by the provided ID from the toolbar.
	 *
	 * @param rendererID
	 *            ID of the renderer the button corresponds to.
	 */
	public void removeButton(int rendererID) {

		Pair<Button, ElementLayout> buttonPair = buttons.get(rendererID);
		if (buttonPair == null)
			return;

		ElementLayout elementLayout = buttonPair.getSecond();

		int elementIndex = indexOf(elementLayout);

		GL2 gl = view.getParentGLCanvas().asGLAutoDrawAble().getGL().getGL2();

		// remove spacings
		if (elementIndex == 0 && size() > 1) {
			ElementLayout spacing = get(1);
			remove(1);
			spacing.destroy(gl);
		}
		if (elementIndex > 1) {
			ElementLayout spacing = get(elementIndex - 1);
			remove(elementIndex - 1);
			spacing.destroy(gl);
		}

		elementIndex = indexOf(elementLayout);
		remove(elementIndex);
		elementLayout.destroy(gl);

		buttons.remove(rendererID);
		view.removeAllIDPickingListeners(buttonPickingType, rendererID);

		if (layoutManager != null) {
			layoutManager.updateLayout();
		} else {
			updateSpacings();
		}

	}

	@Override
	public void destroyed(MultiFormRenderer multiFormRenderer) {
		this.multiFormRenderer = null;

		// destroy(view.getParentGLCanvas().asGLAutoDrawAble().getGL().getGL2());
	}

	@Override
	public void activeRendererChanged(MultiFormRenderer multiFormRenderer, int rendererID, int previousRendererID,
			boolean wasTriggeredByUser) {
		selectButton(previousRendererID, false);
		selectButton(rendererID, true);
	}

	private void selectButton(int rendererID, boolean select) {
		Pair<Button, ElementLayout> buttonPair = buttons.get(rendererID);
		if (buttonPair != null) {
			Button button = buttonPair.getFirst();
			button.setSelected(select);
		}
	}

	@Override
	public void rendererAdded(MultiFormRenderer multiFormRenderer, int rendererID) {
		if (size() > 0) {
			add(ElementLayouts.createXSpacer(BUTTON_SPACING_PIXELS));
		}
		addButton(rendererID, multiFormRenderer);
	}

	@Override
	public void rendererRemoved(MultiFormRenderer multiFormRenderer, int rendererID) {
		removeButton(rendererID);
	}

	@Override
	public void destroy(GL2 gl) {
		view.removeAllTypePickingListeners(buttonPickingType);
		for (Integer rendererID : buttons.keySet()) {
			view.removeAllIDPickingListeners(buttonPickingType, rendererID);
		}
		if (multiFormRenderer != null)
			multiFormRenderer.removeChangeListener(this);
		buttons.clear();
		super.destroy(gl);
	}
}
