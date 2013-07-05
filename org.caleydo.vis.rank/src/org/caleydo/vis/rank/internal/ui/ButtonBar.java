/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.internal.ui;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.caleydo.core.event.ADirectedEvent;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.contextmenu.GenericContextMenuItem;
import org.caleydo.core.view.contextmenu.item.SeparatorMenuItem;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.data.loader.ResourceLocators.IResourceLocator;
import org.caleydo.vis.rank.internal.event.TriggerButtonEvent;
import org.caleydo.vis.rank.ui.RenderStyle;

import com.google.common.collect.Iterables;

/**
 * simple bar of buttons in a horizontal row
 *
 * @author Samuel Gratzl
 *
 */
public class ButtonBar extends GLElementContainer  {
	public ButtonBar() {
		super(GLLayouts.flowHorizontal(1));
		setSize(Float.NaN, RenderStyle.BUTTON_WIDTH);
	}


	public GLButton addButton(GLButton b, String label, String deselectedImage, String selectedImage) {
		return addButton(size(), b, label, deselectedImage, selectedImage);
	}

	public GLButton addButton(int index, GLButton b, String label, String deselectedImage, String selectedImage) {
		b.setTooltip(label);
		b.setRenderer(new ImageRenderer(deselectedImage));
		b.setSelectedRenderer(new ImageRenderer(selectedImage));
		this.add(index, b.setSize(RenderStyle.BUTTON_WIDTH, -1));
		return b;
	}

	public GLButton addButton(GLButton b) {
		this.add(b.setSize(RenderStyle.BUTTON_WIDTH, -1));
		return b;
	}

	public void addSpacer() {
		this.add(new GLElement());
	}

	public float getMinWidth() {
		int buttons = Iterables.size(Iterables.filter(this, GLButton.class));
		return buttons * (RenderStyle.BUTTON_WIDTH + 1);
	}

	/**
	 * tries to convert the contained buttons to context menu items, that trigger {@link TriggerButtonEvent} events
	 *
	 * @param receiver
	 *            event receiver
	 * @param locator
	 *            loader to load the image for a button
	 * @return
	 */
	public List<AContextMenuItem> asContextMenu(IResourceLocator locator) {
		List<AContextMenuItem> items = new ArrayList<>(size());
		for (GLElement elem : this) {
			if (elem instanceof GLButton) {
				items.add(asItem((GLButton) elem, locator));
			} else {
				items.add(SeparatorMenuItem.INSTANCE);
			}
		}
		return items;
	}

	private AContextMenuItem asItem(GLButton elem, IResourceLocator locator) {
		String label = Objects.toString(elem.getTooltip(),elem.toString());
		ADirectedEvent event = new TriggerButtonEvent(elem).to(this);
		AContextMenuItem item = new GenericContextMenuItem(label, event);
		// if (elem.getMode() == EButtonMode.CHECKBOX) {
		// item.setType(EContextMenuType.CHECK);
		// item.setState(elem.isSelected());
		// }
		String imagePath = toImagePath(elem.isSelected() ? elem.getSelectedRenderer() : elem.getRenderer());
		if (imagePath != null) {
			@SuppressWarnings("resource")
			InputStream in = locator.get(imagePath);
			if (in != null)
				item.setImageInputStream(in);
		}

		return item;
	}

	@ListenTo(sendToMe = true)
	private void onTriggerButton(TriggerButtonEvent event) {
		GLButton b = event.getButton();
		b.setSelected(!b.isSelected());
	}

	private String toImagePath(IGLRenderer renderer) {
		if (renderer instanceof ImageRenderer) {
			return ((ImageRenderer) renderer).image;
		}
		return null;
	}

	private static class ImageRenderer implements IGLRenderer {
		private final String image;

		public ImageRenderer(String image) {
			this.image = image;
		}

		@Override
		public void render(GLGraphics g, float w, float h, GLElement parent) {
			g.fillImage(image, 0, 0, w, h);
		}
	}
}
