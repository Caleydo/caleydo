/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2;

import org.caleydo.core.view.opengl.layout2.internal.MouseLayer;
import org.caleydo.core.view.opengl.layout2.internal.PopupLayer;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;

/**
 * can act as a root element with a dedicated mouse layer
 *
 * @author Samuel Gratzl
 *
 */
public final class WindowGLElement extends GLElementContainer {
	private final GLElement root;
	private final PopupLayer popupLayer;
	private final MouseLayer mouseLayer;

	public WindowGLElement(GLElement root) {
		super(GLLayouts.LAYERS);
		this.root = root;
		this.add(root);
		this.popupLayer = new PopupLayer();
		this.add(popupLayer.setzDelta(2.f));
		this.mouseLayer = new MouseLayer();
		this.add(mouseLayer.setzDelta(5.f));
	}

	/**
	 * @return the root, see {@link #root}
	 */
	public GLElement getRoot() {
		return root;
	}

	/**
	 * @return the popupLayer, see {@link #popupLayer}
	 */
	public PopupLayer getPopupLayer() {
		return popupLayer;
	}

	/**
	 * @return the mouseLayer, see {@link #mouseLayer}
	 */
	public MouseLayer getMouseLayer() {
		return mouseLayer;
	}
}
