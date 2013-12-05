/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.internal;

import static org.caleydo.core.view.opengl.layout2.layout.GLLayouts.isDefault;
import gleem.linalg.Vec2f;

import java.util.List;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.IPopupLayer;
import org.caleydo.core.view.opengl.layout2.geom.Rect;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;

/**
 * implementation of {@link IPopupLayer} using a {@link GLElementContainer} by using the layout data for meta data about
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
			final Vec2f targetPos = child.getLayoutDataAs(Vec2f.class, new Vec2f(Float.NaN, Float.NaN));
			float x = targetPos.x();
			float y = targetPos.y();
			float wc = child.getSetWidth();
			float hc = child.getSetHeight();
			if (isDefault(targetPos.x()) && isDefault(child.getSetWidth())) {
				x = 0;
				wc = w;
			} else if (targetPos.x() < 0) {
				x = w - wc;
			} else if (isDefault(targetPos.x())) {
				x = (w - wc) * 0.5f;
			} else if (isDefault(child.getSetWidth())) {
				wc = w - wc;
			}
			if (!isDefault(child.getSetX()))
				x = child.getSetX(); // set via dragging

			if (isDefault(targetPos.y()) && isDefault(child.getSetHeight())) {
				y = 0;
				hc = h;
			} else if (targetPos.y() < 0) {
				y = h - hc;
			} else if (isDefault(targetPos.y())) {
				y = (h - hc) * 0.5f;
			} else if (isDefault(child.getSetHeight())) {
				hc = h - hc;
			}
			if (!isDefault(child.getSetY()))
				y = child.getSetY(); // set via dragging

			child.setBounds(x, y, wc, hc);
		}
	}

	@Override
	public void show(GLElement popup, Rect bounds) {
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
	public void show(GLElement popup, Rect bounds, int flags) {
		this.add(new PopupElement(popup, bounds, flags));
	}

}
