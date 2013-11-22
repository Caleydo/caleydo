/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.parcoords.v2.internal;

import static org.caleydo.view.parcoords.PCRenderStyle.AXIS_MARKER_WIDTH;
import static org.caleydo.view.parcoords.PCRenderStyle.NUMBER_AXIS_MARKERS;
import static org.caleydo.view.parcoords.PCRenderStyle.Y_AXIS_COLOR;

import java.util.List;

import org.caleydo.core.data.collection.table.NumericalTable;
import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.util.format.Formatter;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.parcoords.PCRenderStyle;
import org.caleydo.view.parcoords.v2.ParallelCoordinateElement;

/**
 * @author Samuel Gratzl
 *
 */
public class NumericalAxisElement extends AAxisElement {
	private final GLButton addGate;
	private GateElement gate;

	public NumericalAxisElement(int id, String label) {
		super(id, label);

		this.addGate = new GLButton();
		this.add(addGate.setCallback(new ISelectionCallback() {
			@Override
			public void onSelectionChanged(GLButton button, boolean selected) {
				onAddGate();
			}
		}).setRenderer(GLRenderers.fillImage(PCRenderStyle.ADD_GATE)));
	}

	@Override
	protected AAxisElement createClone() {
		return new NumericalAxisElement(id, label);
	}

	/**
	 * @param gateElement
	 */
	void removeGate(GateElement gate) {
		assert gate == this.gate;
		this.gate = null;
		this.remove(gate);
		getParent().repaint();
	}

	@Override
	public boolean apply(double in) {
		if (!super.apply(in))
			return false;
		if (gate != null)
			return gate.apply(in);
		return true;
	}

	/**
	 *
	 */
	protected void onAddGate() {
		if (gate != null)
			return;
		gate = new GateElement();
		this.add(gate);
	}

	@Override
	public boolean doLayout(List<? extends IGLLayoutElement> children, float w, float h, IGLLayoutElement parent,
			int deltaTimeMs) {
		super.doLayout(children, w, h, parent, deltaTimeMs);
		final IGLLayoutElement add = children.get(3);
		add.setBounds(-8, -32, 16, 32);
		if (children.size() > 4)
			children.get(4).setBounds(-8, 0, 16, h);
		return false;
	}


	String getRawValue(float normalized) {
		ParallelCoordinateElement p = findParent();
		TablePerspective t = p.getSelections().getTablePerspective();
		Table table = t.getDataDomain().getTable();
		if (table instanceof NumericalTable) {
			double raw = ((NumericalTable) table).getRawForNormalized(Table.Transformation.LINEAR, normalized);
			return Formatter.formatNumber(raw);
		}
		return "";
	}

	/**
	 * @param g
	 * @param w
	 * @param h
	 */
	@Override
	protected void renderMarkers(GLGraphics g, float w, float h) {
		// markers on axis
		g.color(Y_AXIS_COLOR);
		float delta = h / (NUMBER_AXIS_MARKERS + 1);
		for (int i = 1; i < NUMBER_AXIS_MARKERS; ++i) {
			float at = delta * i;
			g.drawLine(-AXIS_MARKER_WIDTH, at, +AXIS_MARKER_WIDTH, at);
		}
	}
}
