/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.tourguide.internal.view.col;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.tourguide.internal.view.PerspectiveRow;
import org.caleydo.vis.rank.model.StringRankColumnModel;
import org.caleydo.vis.rank.ui.detail.ValueElement;

/**
 * @author Samuel Gratzl
 *
 */
public class DataDomainRankColumnModel extends StringRankColumnModel {
	private final IAddToStratomex stratomex;

	public DataDomainRankColumnModel(IAddToStratomex stratomex) {
		super(GLRenderers.drawText("Data Domain", VAlign.CENTER), PerspectiveRow.TO_DATADOMAIN);
		this.stratomex = stratomex;
	}

	public DataDomainRankColumnModel(DataDomainRankColumnModel copy) {
		super(copy);
		this.stratomex = copy.stratomex;
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
		private final IPickingListener addto = new IPickingListener() {
			@Override
			public void pick(Pick pick) {
				onAddToPick(pick);
			}
		};
		private int addtoPickingId;
		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			float hint = Math.min(h - 2, 12);
			if (hint <= 0)
				return;
			PerspectiveRow r = this.getLayoutDataAs(PerspectiveRow.class, null);
			IDataDomain dataDomain = r.getDataDomain();
			g.color(dataDomain.getColor()).fillRect(1, (h - hint) * 0.5f, hint, hint);
			if (stratomex.canAdd2Stratomex(r)) {
				g.fillImage("resources/icons/view/tourguide/add.png", 1, (h - hint) * 0.5f, hint, hint);
			}
			if (h < 5 || w < 20)
				return;
			float x = hint + 2;
			float hi = Math.min(h, 16);
			g.drawText(dataDomain, x, 1 + (h - hi) * 0.5f, w - 2 - x, hi - 2);
		}

		protected void onAddToPick(Pick pick) {
			if (pick.isAnyDragging())
				return;
			switch(pick.getPickingMode()) {
			case MOUSE_RELEASED:
				PerspectiveRow r = this.getLayoutDataAs(PerspectiveRow.class, null);
				if (stratomex.canAdd2Stratomex(r)) {
					stratomex.add2Stratomex(r);
				}
				break;
			default:
				break;
			}
		}

		@Override
		protected void init(IGLElementContext context) {
			super.init(context);
			this.addtoPickingId = context.registerPickingListener(addto);
		}

		@Override
		protected void takeDown() {
			context.unregisterPickingListener(addtoPickingId);
			addtoPickingId = -1;
			super.takeDown();
		}

		@Override
		protected void renderPickImpl(GLGraphics g, float w, float h) {
			float hint = Math.min(h - 2, 12);
			if (hint <= 0)
				return;
			g.pushName(addtoPickingId);
			g.fillRect(1, (h - hint) * 0.5f, hint, hint);
			g.popName();
		}
	}
}