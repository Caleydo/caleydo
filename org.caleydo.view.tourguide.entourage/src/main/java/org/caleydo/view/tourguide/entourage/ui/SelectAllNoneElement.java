/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.entourage.ui;

import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.basic.EButtonIcon;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.core.view.opengl.util.text.ETextStyle;

/**
 * @author Samuel Gratzl
 *
 */
public class SelectAllNoneElement extends GLButton implements IGLRenderer, ISelectionCallback {
	private final Iterable<GLButton> buttons;

	public SelectAllNoneElement(Iterable<GLButton> buttons) {
		super(EButtonMode.CHECKBOX);
		this.buttons = buttons;
		this.setRenderer(this);
		this.setSelected(true);
		this.setSize(-1, 18);
		this.setCallback(this);
	}

	@Override
	public void onSelectionChanged(GLButton button, boolean selected) {
		for(GLButton b : buttons) {
			b.setSelected(selected);
		}
	}

	@Override
	public void render(GLGraphics g, float w, float h, GLElement parent) {
		final boolean s = isSelected();
		String icon = EButtonIcon.CHECKBOX.get(s);
		g.fillImage(icon, 1, 1, h - 2, h - 2);
		String label = s ? "Select None" : "Select All";
		if (label != null && label.length() > 0)
			g.drawText(label, h, 0, w - h, 13, VAlign.LEFT, ETextStyle.ITALIC);
	}
}
