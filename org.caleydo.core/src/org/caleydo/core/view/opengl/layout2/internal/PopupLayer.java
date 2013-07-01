/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.internal;

import static org.caleydo.core.view.opengl.layout2.layout.GLLayouts.isDefault;
import gleem.linalg.Vec4f;

import java.util.List;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.IMouseLayer;
import org.caleydo.core.view.opengl.layout2.IPopupLayer;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;

/**
 * implementation of {@link IMouseLayer} using a {@link GLElementContainer} by using the layout data for meta data about
 * elements
 *
 * @author Samuel Gratzl
 *
 */
public final class PopupLayer extends GLElementContainer implements IPopupLayer, IGLLayout {

	public PopupLayer() {
		super();
		setLayout(this);
	}

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		for (IGLLayoutElement child : children) {
			float x = child.getSetX();
			float y = child.getSetY();
			float wc = child.getSetWidth();
			float hc = child.getSetHeight();
			if (isDefault(child.getSetX()) && isDefault(child.getSetWidth())) {
				x = 0;
				wc = w;
			} else if (isDefault(child.getSetX())) {
				x = w - wc;
			} else if (isDefault(child.getSetWidth())) {
				wc = w - wc;
			}

			if (isDefault(child.getSetY()) && isDefault(child.getSetHeight())) {
				y = 0;
				hc = h;
			} else if (isDefault(child.getSetY())) {
				y = h - hc;
			} else if (isDefault(child.getSetHeight())) {
				hc = h - hc;
			}

			child.setBounds(x, y, wc, hc);
		}
	}

	@Override
	public void show(GLElement popup, Vec4f bounds) {
		show(popup, bounds, FLAG_ALL);
	}

	@Override
	public void hide(GLElement popup) {
		for(GLElement g : this) {
			if (((PopupElement) g).getContent() == popup) {
				remove(g);
				return;
			}
		}
	}

	@Override
	public void show(GLElement popup, Vec4f bounds, int flags) {
		this.add(new PopupElement(popup, bounds, flags));
	}

}
