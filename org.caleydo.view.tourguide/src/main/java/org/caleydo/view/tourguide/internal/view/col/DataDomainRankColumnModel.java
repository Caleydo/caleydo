/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.view.col;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.tourguide.internal.model.AScoreRow;
import org.caleydo.vis.lineup.model.StringRankColumnModel;
import org.caleydo.vis.lineup.ui.detail.ValueElement;

/**
 * @author Samuel Gratzl
 *
 */
public class DataDomainRankColumnModel extends StringRankColumnModel {

	public DataDomainRankColumnModel() {
		super(GLRenderers.drawText("Data domain", VAlign.CENTER), AScoreRow.TO_DATADOMAIN);
	}

	public DataDomainRankColumnModel(DataDomainRankColumnModel copy) {
		super(copy);
	}

	@Override
	public DataDomainRankColumnModel clone() {
		return new DataDomainRankColumnModel(this);
	}

	@Override
	public boolean isDestroyAble() {
		return false;
	}

	@Override
	public ValueElement createValue() {
		return new MyElement();
	}

	class MyElement extends ValueElement {
		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			float hint = Math.min(h - 2, 12);
			if (hint <= 0)
				return;
			AScoreRow r = this.getLayoutDataAs(AScoreRow.class, null);
			IDataDomain dataDomain = r.getDataDomain();
			g.color(dataDomain.getColor()).fillRect(1, (h - hint) * 0.5f, hint, hint);
			if (h < 5 || w < 20)
				return;
			float x = hint + 2;
			float hi = Math.min(h, 16);
			g.drawText(dataDomain, x, 1 + (h - hi) * 0.5f, w - 2 - x, hi - 2);
		}
	}
}
