package org.caleydo.view.tourguide.v3.ui.detail;

import static org.caleydo.core.view.opengl.layout2.animation.Transitions.LINEAR;
import static org.caleydo.core.view.opengl.layout2.animation.Transitions.NO;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.List;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.animation.MoveTransitions;
import org.caleydo.core.view.opengl.layout2.animation.MoveTransitions.IMoveTransition;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.tourguide.v3.model.ARankColumnModel;
import org.caleydo.view.tourguide.v3.model.IRow;
import org.caleydo.view.tourguide.v3.model.RankTableModel;
import org.caleydo.view.tourguide.v3.model.SimpleHistogram;
import org.caleydo.view.tourguide.v3.model.mixin.IMultiColumnMixin;
import org.caleydo.view.tourguide.v3.model.mixin.IMultiColumnMixin.MultiFloat;
import org.caleydo.view.tourguide.v3.ui.RenderUtils;

public class StackedScoreSummary extends GLElementContainer implements IGLLayout {
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
	private final IMultiColumnMixin model;

	private final IPickingListener pickingListener = new IPickingListener() {
		@Override
		public void pick(Pick pick) {
			onPick(pick);
		}
	};

	public StackedScoreSummary(IMultiColumnMixin model, boolean interactive) {
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
			System.out.println("add");
			for (ARankColumnModel m : this.model) {
				this.add(new Child(m));
			}
			break;
		case MOUSE_OUT:
			System.out.println("remove");
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
		// background
		g.color(model.getBgColor()).fillRect(0, 0, w, h);
		// hist
		int bins = Math.round(w);
		int size = model.size();
		// create a stacked histogram of all values
		SimpleHistogram[] hists = model.getHists(bins);
		Color[] colors = model.getColors();
		Color[] selectedColors = new Color[size];
		int[] selectedBins = new int[size];
		if (selectedRow == null) {
			Arrays.fill(selectedBins,-1);
		} else {
			MultiFloat vs = model.getSplittedValue(selectedRow);
			for(int i = 0; i < size; ++i)
				selectedBins[i] = hists[i].getBinOf(vs.values[i]);
		}
		for(int i = 0; i < size; ++i)
			selectedColors[i] = colors[i].darker();
		RenderUtils.renderStackedHist(g, hists, w, h, selectedBins, colors, selectedColors);
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
			g.color(model.getBgColor()).renderRoundedRect(true, 0, 0, w, h, 5, 2, true, true, false, false);
			super.renderImpl(g, w, h);
		}
	}

}