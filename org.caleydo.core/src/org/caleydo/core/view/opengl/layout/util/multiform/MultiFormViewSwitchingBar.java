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
				MultiFormViewSwitchingBar.this.multiFormRenderer.setActive(pick.getObjectID());
			}
		}, buttonPickingType);
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

	private void addButton(int rendererID, MultiFormRenderer multiFormRenderer) {
		Button button = new Button(buttonPickingType, rendererID, multiFormRenderer.getIconPath(rendererID));
		ElementLayout buttonLayout = ElementLayouts.createButton(view, button, DEFAULT_HEIGHT_PIXELS,
				DEFAULT_HEIGHT_PIXELS);
		add(buttonLayout);
		buttons.put(rendererID, new Pair<>(button, buttonLayout));
	}

	private void removeButton(int rendererID) {

		Pair<Button, ElementLayout> buttonPair = buttons.get(rendererID);
		if (buttonPair == null)
			return;

		ElementLayout elementLayout = buttonPair.getSecond();

		int elementIndex = elements.indexOf(elementLayout);

		GL2 gl = view.getParentGLCanvas().asGLAutoDrawAble().getGL().getGL2();

		// remove spacings
		if (elementIndex == 0 && elements.size() > 1) {
			ElementLayout spacing = elements.get(1);
			elements.remove(1);
			spacing.destroy(gl);

		}
		if (elementIndex > 1) {
			ElementLayout spacing = elements.get(elementIndex - 1);
			elements.remove(elementIndex - 1);
			spacing.destroy(gl);
		}

		elementIndex = elements.indexOf(elementLayout);
		elements.remove(elementIndex);
		elementLayout.destroy(gl);

		buttons.remove(rendererID);

		layoutManager.updateLayout();
	}

	@Override
	public void destroyed(MultiFormRenderer multiFormRenderer) {
		this.multiFormRenderer = null;

		destroy(view.getParentGLCanvas().asGLAutoDrawAble().getGL().getGL2());
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
		if (elements.size() > 0) {
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
		multiFormRenderer.removeChangeListener(this);
		buttons.clear();
		super.destroy(gl);
	}

}
