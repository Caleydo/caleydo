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
package org.caleydo.core.view.opengl.layout.util.multiform;

import java.util.HashMap;
import java.util.Map;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.button.Button;

/**
 * @author Christian
 *
 */
public class GLElementViewSwitchingBar extends GLElementContainer implements IMultiFormChangeListener {

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
	public GLElementViewSwitchingBar(MultiFormRenderer multiFormRenderer, AGLView view) {
		setSize(Float.NaN, DEFAULT_HEIGHT_PIXELS);
		multiFormRenderer.addChangeListener(this);
		this.view = view;
		buttonPickingType = MultiFormViewSwitchingBar.class.getName() + hashCode();
		this.multiFormRenderer = multiFormRenderer;

		createButtonsForMultiformRenderer(multiFormRenderer);

		view.addTypePickingListener(new APickingListener() {
			@Override
			public void clicked(Pick pick) {
				GLElementViewSwitchingBar.this.multiFormRenderer.setActive(pick.getObjectID());
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
		if (buttons.keySet().contains(rendererID))
			view.addIDPickingListener(pickingListener, buttonPickingType, rendererID);
	}

	private void createButtonsForMultiformRenderer(MultiFormRenderer multiFormRenderer) {
		// Set<Integer> rendererIDs = multiFormRenderer.getRendererIDs();
		// List<Integer> idList = new ArrayList<>(rendererIDs);
		// Collections.sort(idList);
		//
		// for (int i = 0; i < idList.size(); i++) {
		// int rendererID = idList.get(i);
		// addButton(rendererID, multiFormRenderer);
		// if (i < idList.size() - 1) {
		// add(ElementLayouts.createXSpacer(BUTTON_SPACING_PIXELS));
		// }
		// }
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

		// Button button = new Button(buttonPickingType, rendererID, multiFormRenderer.getIconPath(rendererID));
		// ElementLayout buttonLayout = ElementLayouts.createButton(view, button, DEFAULT_HEIGHT_PIXELS,
		// DEFAULT_HEIGHT_PIXELS, 0.22f);
		//
		// if (buttons.containsKey(rendererID)) {
		// Pair<Button, ElementLayout> buttonPair = buttons.get(rendererID);
		// ElementLayout elementLayout = buttonPair.getSecond();
		//
		// int elementIndex = indexOf(elementLayout);
		// add(elementIndex, buttonLayout);
		//
		// remove(elementLayout);
		// GL2 gl = view.getParentGLCanvas().asGLAutoDrawAble().getGL().getGL2();
		// elementLayout.destroy(gl);
		//
		// } else {
		// add(buttonLayout);
		// }
		// buttons.put(rendererID, new Pair<>(button, buttonLayout));
	}

	/**
	 * Removes the button corresponding to the renderer specified by the provided ID from the toolbar.
	 *
	 * @param rendererID
	 *            ID of the renderer the button corresponds to.
	 */
	public void removeButton(int rendererID) {

		// Pair<Button, ElementLayout> buttonPair = buttons.get(rendererID);
		// if (buttonPair == null)
		// return;
		//
		// ElementLayout elementLayout = buttonPair.getSecond();
		//
		// int elementIndex = indexOf(elementLayout);
		//
		// GL2 gl = view.getParentGLCanvas().asGLAutoDrawAble().getGL().getGL2();
		//
		// // remove spacings
		// if (elementIndex == 0 && size() > 1) {
		// ElementLayout spacing = get(1);
		// remove(1);
		// spacing.destroy(gl);
		// }
		// if (elementIndex > 1) {
		// ElementLayout spacing = get(elementIndex - 1);
		// remove(elementIndex - 1);
		// spacing.destroy(gl);
		// }
		//
		// elementIndex = indexOf(elementLayout);
		// remove(elementIndex);
		// elementLayout.destroy(gl);
		//
		// buttons.remove(rendererID);
		// view.removeAllIDPickingListeners(buttonPickingType, rendererID);
		//
		// if (layoutManager != null) {
		// layoutManager.updateLayout();
		// } else {
		// updateSpacings();
		// }

	}

	@Override
	public void destroyed(MultiFormRenderer multiFormRenderer) {
		this.multiFormRenderer = null;

		// destroy(view.getParentGLCanvas().asGLAutoDrawAble().getGL().getGL2());
	}

	@Override
	public void activeRendererChanged(MultiFormRenderer multiFormRenderer, int rendererID, int previousRendererID) {
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
		// if (size() > 0) {
		// add(ElementLayouts.createXSpacer(BUTTON_SPACING_PIXELS));
		// }
		// addButton(rendererID, multiFormRenderer);
	}

	@Override
	public void rendererRemoved(MultiFormRenderer multiFormRenderer, int rendererID) {
		removeButton(rendererID);
	}

	// @Override
	// public void destroy(GL2 gl) {
	// view.removeAllTypePickingListeners(buttonPickingType);
	// for (Integer rendererID : buttons.keySet()) {
	// view.removeAllIDPickingListeners(buttonPickingType, rendererID);
	// }
	// if (multiFormRenderer != null)
	// multiFormRenderer.removeChangeListener(this);
	// buttons.clear();
	// super.destroy(gl);
	// }

}
