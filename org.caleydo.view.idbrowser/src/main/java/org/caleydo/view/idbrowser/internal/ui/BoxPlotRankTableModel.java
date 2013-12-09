/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.idbrowser.internal.ui;

import java.util.Set;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.idbrowser.internal.model.PrimaryIDRow;
import org.caleydo.vis.lineup.model.ARankColumnModel;
import org.caleydo.vis.lineup.model.IRow;
import org.caleydo.vis.lineup.ui.detail.ValueElement;

/**
 * @author Samuel Gratzl
 *
 */
public class BoxPlotRankTableModel extends ARankColumnModel {
	private final ATableBasedDataDomain d;
	private final EDimension dim;

	/**
	 * @param d
	 * @param dim
	 */
	public BoxPlotRankTableModel(ATableBasedDataDomain d, EDimension dim) {
		super(Color.GRAY, new Color(0.95f, .95f, .95f));
		this.d = d;
		assert DataSupportDefinitions.numericalTables.apply(d);
		setHeaderRenderer(GLRenderers.drawText(d.getLabel(), VAlign.CENTER));
		this.dim = dim;
	}

	/**
	 * @param distributionRankTableModel
	 */
	public BoxPlotRankTableModel(BoxPlotRankTableModel clone) {
		super(clone);
		this.d = clone.d;
		this.dim = clone.dim;
		setHeaderRenderer(GLRenderers.drawText(d.getLabel(), VAlign.CENTER));
	}

	@Override
	public ARankColumnModel clone() {
		return new BoxPlotRankTableModel(this);
	}

	@Override
	public String getValue(IRow row) {
		return null;
	}

	public IDType getIDType() {
		return dim.select(d.getDimensionIDType(), d.getRecordIDType());
	}

	public boolean has(IRow row) {
		Set<Object> ids = ((PrimaryIDRow) row).get(getIDType());
		if (ids == null || ids.isEmpty())
			return false;
		return true;
	}

	@Override
	public GLElement createSummary(boolean interactive) {
		return new GLElement();
	}

	@Override
	public ValueElement createValue() {
		return new MyValueElement();
	}

	private class MyValueElement extends ValueElement {

		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			if (h < 1)
				return;
			PrimaryIDRow row = (PrimaryIDRow) getRow();
			g.drawText(has(row) ? "Found" : "Not Found", 2, 1, w, h - 3);

		}
	}

}
