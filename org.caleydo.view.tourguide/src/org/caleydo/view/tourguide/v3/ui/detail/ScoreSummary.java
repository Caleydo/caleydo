package org.caleydo.view.tourguide.v3.ui.detail;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.view.tourguide.v3.model.IRow;
import org.caleydo.view.tourguide.v3.model.RankTableModel;
import org.caleydo.view.tourguide.v3.model.SimpleHistogram;
import org.caleydo.view.tourguide.v3.model.mixin.IRankableColumnMixin;
import org.caleydo.view.tourguide.v3.ui.RenderUtils;


public class ScoreSummary extends GLElement {
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
	private final IRankableColumnMixin model;

	public ScoreSummary(IRankableColumnMixin model, boolean interactive) {
		this.model = model;
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
		RankTableModel table = model.getTable();
		table.removePropertyChangeListener(RankTableModel.PROP_SELECTED_ROW, listener);
		super.takeDown();
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		// background
		g.color(model.getBgColor()).fillRect(0, 0, w, h);
		// hist
		SimpleHistogram hist = model.getHist(binsForWidth(w));
		int selectedBin = selectedRow == null ? -1 : hist.getBinOf(model.getValue(selectedRow));
		RenderUtils.renderHist(g, hist, w, h, selectedBin, model.getColor(), model.getColor().darker());
	}

	protected void onSelectRow(IRow selectedRow) {
		if (this.selectedRow == selectedRow)
			return;
		this.selectedRow = selectedRow;
		repaint();
	}

	/**
	 * @param w
	 * @return
	 */
	public static int binsForWidth(float w) {
		return Math.round(w * 0.25f);
	}
}