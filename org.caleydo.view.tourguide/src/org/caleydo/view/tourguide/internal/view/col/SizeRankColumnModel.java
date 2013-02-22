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

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.util.BitSet;
import java.util.List;

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.tourguide.internal.view.PerspectiveRow;
import org.caleydo.view.tourguide.v3.event.FilterEvent;
import org.caleydo.view.tourguide.v3.model.ABasicFilterableRankColumnModel;
import org.caleydo.view.tourguide.v3.model.IRow;
import org.caleydo.view.tourguide.v3.model.mixin.IFilterColumnMixin;
import org.caleydo.view.tourguide.v3.ui.GLPropertyChangeListeners;

/**
 * @author Samuel Gratzl
 *
 */
public class SizeRankColumnModel extends ABasicFilterableRankColumnModel implements IFilterColumnMixin, IGLRenderer {
	private int min = 0;
	private int max = Integer.MAX_VALUE;

	public SizeRankColumnModel() {
		super(Color.GRAY, new Color(.95f, .95f, .95f));
		setHeaderRenderer(this);
	}

	@Override
	public GLElement createSummary(boolean interactive) {
		return new MyHeaderElement(interactive);
	}

	@Override
	public GLElement createValue() {
		return new MyElement();
	}

	@ListenTo(sendToMe = true)
	private void onSetFilter(FilterEvent event) {
		invalidAllFilter();
		// propertySupport.firePropertyChange(PROP_FILTER, this.filter, this.filter = (String) event.getFilter());
	}

	@Override
	public boolean isFiltered() {
		return min > 0 || max < Integer.MAX_VALUE;
	}

	@Override
	protected void updateMask(BitSet todo, List<IRow> data, BitSet mask) {
		for (int i = todo.nextSetBit(0); i >= 0; i = todo.nextSetBit(i + 1)) {
			PerspectiveRow prow = (PerspectiveRow) data.get(i);
			int value = getValue(prow);
			mask.set(i++, value >= min && value <= max);
		}
	}

	private int getValue(PerspectiveRow prow) {
		if (prow.getGroup() != null)
			return prow.getGroup().getSize();
		return prow.getStratification().getVirtualArray().size();
	}

	@Override
	public void render(GLGraphics g, float w, float h, GLElement parent) {
		g.drawText("Group Size", 0, 0, w, h, VAlign.CENTER);
	}

	class MyElement extends GLElementContainer {
		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			if (h < 5)
				return;
			super.renderImpl(g, w, h);
			float hi = Math.min(h, 18);
			PerspectiveRow r = getLayoutDataAs(PerspectiveRow.class, null);
			g.drawText(getValue(r) + "", 1, 1 + (h - hi) * 0.5f, w - 2, hi - 2);
		}
	}

	private class MyHeaderElement extends PickableGLElement {
		private final PropertyChangeListener repaintListner = GLPropertyChangeListeners.repaintOnEvent(this);

		public MyHeaderElement(boolean interactive) {
			setzDelta(0.25f);
			if (!interactive)
				setVisibility(EVisibility.VISIBLE);
		}

		@Override
		protected void init(IGLElementContext context) {
			super.init(context);
			addPropertyChangeListener(PROP_FILTER, repaintListner);
		}

		@Override
		protected void takeDown() {
			removePropertyChangeListener(PROP_FILTER, repaintListner);
			super.takeDown();
		}

		@Override
		protected void onMouseReleased(Pick pick) {
			if (pick.isAnyDragging())
				return;
			// editFilter();
		}

		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			super.renderImpl(g, w, h);
			if (w < 20)
				return;
			g.drawText("Filter:", 4, 2, w - 4, 12);
			// String t = "<None>";
			// if (filter != null)
			// t = filter;
			// g.drawText(t, 4, 18, w - 4, 12);
		}
	}
}