/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.parcoords.v2.internal;

import java.util.List;

import org.caleydo.core.util.function.IDoublePredicate;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout2;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.parcoords.PCRenderStyle;
import org.caleydo.view.parcoords.v2.internal.DragElement.IDragCallback;

/**
 * @author Samuel Gratzl
 *
 */
public class GateElement extends GLElementContainer implements IDoublePredicate, IGLLayout2, IDragCallback {
	private float from = 0;
	private float to = 1;

	/**
	 *
	 */
	public GateElement() {
		DragElement top = new DragElement();
		DragElement bottom = new DragElement();
		GLButton close = new GLButton();
		this.add(top.setCallback(this).setPickingObjectId(1)
				.setRenderer(GLRenderers.fillImage(PCRenderStyle.GATE_MENUE)));
		this.add(bottom.setCallback(this).setPickingObjectId(2)
				.setRenderer(GLRenderers.fillImage(PCRenderStyle.GATE_MENUE)));
		this.add(close);
		setLayout(this);
	}

	@Override
	public void onDragged(DragElement elem, float dx, float dy) {
		boolean isTop = elem.getPickingObjectId() == 1;
		float h = getSize().y();
		if (isTop)
			from += dy / h;
		else
			to += dy / h;
		relayout();
	}

	@Override
	public boolean apply(double in) {
		return from <= in && in <= to;
	}

	@Override
	public boolean apply(Double input) {
		return apply(input.doubleValue());
	}

	@Override
	public boolean doLayout(List<? extends IGLLayoutElement> children, float w, float h, IGLLayoutElement parent,
			int deltaTimeMs) {
		children.get(0).setBounds(-16, from * h, 77, 22);
		children.get(1).setBounds(-16, to * h + 22, 77, 22);
		return false;
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.fillImage(PCRenderStyle.GATE_BODY, 0, h * from, w, h * (to - from));
		super.renderImpl(g, w, h);
	}
}
