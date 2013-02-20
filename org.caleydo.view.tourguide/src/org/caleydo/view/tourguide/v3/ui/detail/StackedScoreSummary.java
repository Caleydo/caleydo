package org.caleydo.view.tourguide.v3.ui.detail;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;

import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.view.opengl.layout2.AnimatedGLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.view.tourguide.v2.r.model.DataUtils;
import org.caleydo.view.tourguide.v3.model.IRow;
import org.caleydo.view.tourguide.v3.model.RankTableModel;
import org.caleydo.view.tourguide.v3.model.mixin.IMultiColumnMixin;
import org.caleydo.view.tourguide.v3.model.mixin.IMultiColumnMixin.MultiFloat;
import org.caleydo.view.tourguide.v3.ui.RenderUtils;

public class StackedScoreSummary extends AnimatedGLElementContainer {
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

	public StackedScoreSummary(IMultiColumnMixin model) {
		this.model = model;
		// for (ARankColumnModel col : model) {
		// this.add(col.createSummary());
		// }
	}

	@Override
	protected void init(IGLElementContext context) {
		super.init(context);
		// model.addPropertyChangeListener(ACompositeRankColumnModel.PROP_CHILDREN, listener);
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
		Histogram[] hists = model.getHists(bins);
		Color[] colors = model.getColors();
		Color[] selectedColors = new Color[size];
		int[] selectedBins = new int[size];
		if (selectedRow == null) {
			Arrays.fill(selectedBins,-1);
		} else {
			MultiFloat vs = model.getSplittedValue(selectedRow);
			for(int i = 0; i < size; ++i)
				selectedBins[i] = DataUtils.getHistBin(bins, vs.values[i]);
		}
		for(int i = 0; i < size; ++i)
			selectedColors[i] = colors[i].darker();
		RenderUtils.renderStackedHist(g, hists, w, h, selectedBins, colors, selectedColors);
	}

	protected void onSelectRow(IRow selectedRow) {
		if (this.selectedRow == selectedRow)
			return;
		this.selectedRow = selectedRow;
		repaint();
	}
}