/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.idbrowser.internal.ui;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataSupportDefinitions;
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

	@Override
	public GLElement createSummary(boolean interactive) {
		return new GLElement();
	}

	@Override
	public ValueElement createValue() {
		return new MyValueElement();
	}

	private static class MyValueElement extends ValueElement {

		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			if (h < 1)
				return;
			PrimaryIDRow row = (PrimaryIDRow) getRow();
			// AScoreRow r = this.getLayoutDataAs(AScoreRow.class, null);
			// Collection<GroupInfo> infos = r.getGroupInfos();
			// int sum = 0;
			// for (GroupInfo info : infos)
			// sum += info.getSize();
			// if (sum == 0)
			// return;
			// float factor = w / sum;
			//
			// Color ping = new Color(0.85f, 0.85f, 0.85f);
			// Color pong = Color.LIGHT_GRAY;
			// Color tmp;
			// float xi = 0;
			// for (GroupInfo info : infos) {
			// float wi = info.getSize() * factor;
			// if (wi <= 0)
			// continue;
			// if (info.getColor() != null)
			// g.color(info.getColor());
			// else {
			// g.color(ping);
			// tmp = ping;
			// ping = pong;
			// pong = tmp;
			// }
			// g.fillRect(xi, 1, wi, h - 2);
			// xi += wi;
			// }
			// if (getRenderInfo().getBarOutlineColor() != null) {
			// // outline
			// g.color(getRenderInfo().getBarOutlineColor()).drawRect(0, 1, w, h - 2);
			// }
		}
	}

}
