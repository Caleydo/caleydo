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
package org.caleydo.vis.rank.ui.detail;

import static org.caleydo.core.view.opengl.layout2.animation.Transitions.LINEAR;
import static org.caleydo.core.view.opengl.layout2.animation.Transitions.NO;
import static org.caleydo.vis.rank.ui.RenderStyle.binsForWidth;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.animation.MoveTransitions;
import org.caleydo.core.view.opengl.layout2.animation.MoveTransitions.IMoveTransition;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.core.view.opengl.layout2.renderer.RoundedRectRenderer;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.vis.rank.internal.event.AnnotationEditEvent;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.model.MaxCompositeRankColumnModel;
import org.caleydo.vis.rank.model.RankTableModel;
import org.caleydo.vis.rank.model.SimpleHistogram;
import org.caleydo.vis.rank.ui.RenderStyle;
import org.caleydo.vis.rank.ui.RenderUtils;

/**
 * @author Samuel Gratzl
 *
 */
public class MaxScoreSummary extends GLElementContainer implements IGLLayout {
	private final PropertyChangeListener listener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			switch (evt.getPropertyName()) {
			case RankTableModel.PROP_SELECTED_ROW:
				onSelectRow((IRow) evt.getNewValue());
				break;
			default:
				repaint();
				break;
			}

		}
	};
	private IRow selectedRow = null;
	private final MaxCompositeRankColumnModel model;

	private final IPickingListener pickingListener = new IPickingListener() {
		@Override
		public void pick(Pick pick) {
			onPick(pick);
		}
	};

	public MaxScoreSummary(MaxCompositeRankColumnModel model, boolean interactive) {
		this.model = model;
		setLayout(this);
		setzDelta(0.5f);
		this.setVisibility(EVisibility.PICKABLE);
		this.onPick(pickingListener);
	}


	/**
	 * @param pick
	 */
	protected void onPick(Pick pick) {
		if (pick.isAnyDragging())
			return;
		switch (pick.getPickingMode()) {
		case MOUSE_OVER:
			for (ARankColumnModel m : this.model) {
				this.add(new Child(m));
			}
			break;
		case MOUSE_OUT:
			this.clear();
			break;
		default:
			break;
		}
	}

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		float y = h;
		for (IGLLayoutElement child : children) {
			child.setBounds(0, y, w, h + 12);
			y += h + 12;
		}
	}

	@Override
	protected void init(IGLElementContext context) {
		super.init(context);
		RankTableModel table = model.getTable();
		table.addPropertyChangeListener(RankTableModel.PROP_SELECTED_ROW, listener);
		this.selectedRow = table.getSelectedRow();
	}

	@Override
	protected void takeDown() {
		// model.removePropertyChangeListener(ACompositeRankColumnModel.PROP_CHILDREN, listener);
		RankTableModel table = model.getTable();
		table.removePropertyChangeListener(RankTableModel.PROP_SELECTED_ROW, listener);
		super.takeDown();
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		SimpleHistogram hist = model.getHist(binsForWidth(w));
		int selectedBin = selectedRow == null ? -1 : hist.getBinOf(model.applyPrimitive(selectedRow));
		RenderUtils.renderHist(g, hist, w, h, selectedBin, model.getColor(), model.getColor().darker());
		// // background
		// g.color(model.getBgColor()).fillRect(0, 0, w, h);
		// // hist
		// int size = model.size();
		// // create a stacked histogram of all values
		// SimpleHistogram[] hists = model.getHists(binsForWidth(w));
		// Color[] colors = model.getColors();
		// Color[] selectedColors = new Color[size];
		// int[] selectedBins = new int[size];
		// if (selectedRow == null) {
		// Arrays.fill(selectedBins,-1);
		// } else {
		// MultiFloat vs = model.getSplittedValue(selectedRow);
		// for(int i = 0; i < size; ++i)
		// selectedBins[i] = hists[i].getBinOf(vs.values[i]);
		// }
		// for(int i = 0; i < size; ++i)
		// selectedColors[i] = colors[i].darker();
		// RenderUtils.renderStackedHist(g, hists, w, h, selectedBins, colors, selectedColors);
		//
		// if (model.getFilterMin() > 0) {
		// g.color(0, 0, 0, 0.25f).fillRect(0, 0, model.getFilterMin() * w, h);
		// }
		// if (model.getFilterMax() < 1) {
		// g.color(0, 0, 0, 0.25f).fillRect(model.getFilterMax() * w, 0, (1 - model.getFilterMax()) * w, h);
		// }
		super.renderImpl(g, w, h);
	}

	protected void onSelectRow(IRow selectedRow) {
		if (this.selectedRow == selectedRow)
			return;
		this.selectedRow = selectedRow;
		repaint();
	}

	private static class Child extends GLElementContainer {
		private static final IMoveTransition move = new MoveTransitions.MoveTransitionBase(NO, LINEAR, NO, NO);
		private final ARankColumnModel model;
		public Child(ARankColumnModel model) {
			super(GLLayouts.flowVertical(0));
			setLayoutData(move);
			this.model = model;
			this.add(new GLElement(model.getHeaderRenderer()).setSize(-1, 10).setLocation(0, 2));
			this.add(model.createSummary(false));
			setzDelta(0.5f);
		}

		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			g.color(model.getBgColor());
			RoundedRectRenderer.render(g, 0, 0, w, h, RenderStyle.HEADER_ROUNDED_RADIUS, 3,
					RoundedRectRenderer.FLAG_FILL | RoundedRectRenderer.FLAG_TOP);
			super.renderImpl(g, w, h);
		}
	}

	@ListenTo(sendToMe = true)
	private void onSetAnnotation(AnnotationEditEvent event) {
		model.setTitle(event.getTitle());
		model.setDescription(event.getDescription());
	}

}