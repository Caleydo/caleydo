/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.ui.column;

import static org.caleydo.vis.lineup.ui.RenderStyle.COLUMN_SPACE;
import static org.caleydo.vis.lineup.ui.RenderStyle.HIST_HEIGHT;
import static org.caleydo.vis.lineup.ui.RenderStyle.LABEL_HEIGHT;

import java.util.List;
import java.util.Locale;

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.IMouseLayer.IDragInfo;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.vis.lineup.config.IRankTableUIConfig;
import org.caleydo.vis.lineup.internal.event.WeightsChangedEvent;
import org.caleydo.vis.lineup.model.ACompositeRankColumnModel;
import org.caleydo.vis.lineup.model.ARankColumnModel;
import org.caleydo.vis.lineup.model.StackedRankColumnModel;
import org.caleydo.vis.lineup.ui.RenderStyle;
import org.caleydo.vis.lineup.ui.SeparatorUI;
import org.caleydo.vis.lineup.ui.StackedSeparatorUI;
/**
 * @author Samuel Gratzl
 *
 */
public class StackedColumnHeaderUI extends SimpleColumnHeaderUI {
	public final AlignmentDragInfo align = new AlignmentDragInfo();

	private final IPickingListener onWeightsClicked = new IPickingListener() {
		@Override
		public void pick(Pick pick) {
			onWeightsClicked(pick);
		}
	};
	private int weightsClickedPickingId = -1;

	public StackedColumnHeaderUI(StackedRankColumnModel model, IRankTableUIConfig config) {
		super(model, config);
	}

	@Override
	protected GLElement createSummary(ACompositeRankColumnModel model, IRankTableUIConfig config) {
		return new StackedSummaryHeaderUI((StackedRankColumnModel) model, config);
	}

	@Override
	protected SeparatorUI createSeparator(int index) {
		return new StackedSeparatorUI(this, index);
	}

	@Override
	protected GLElement wrapImpl(ARankColumnModel model) {
		GLElement g = ColumnUIs.createHeader(model, config, false);
		return g;
	}

	@Override
	protected void init(IGLElementContext context) {
		weightsClickedPickingId = context.registerPickingListener(onWeightsClicked);
		super.init(context);
	}

	@Override
	protected void takeDown() {
		context.unregisterPickingListener(weightsClickedPickingId);
		super.takeDown();
	}

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {

		IGLLayoutElement summary = children.get(0);

		StackedRankColumnModel stacked = getModel();
		if (stacked.isCompressed()) {
			boolean isSmallHeader = isSmallHeader();
			summary.setBounds(0, getTopPadding(isSmallHeader), w, h - getTopPadding(isSmallHeader));
			for (IGLLayoutElement child : children.subList(1, children.size()))
				child.hide();
			return;
		}

		summary.setBounds(getLeftPadding() + 2, 2, w - 4 - getLeftPadding(), (isSmallHeader() ? 0 : HIST_HEIGHT)
				+ LABEL_HEIGHT);

		super.layoutColumns(children, w, h);

		// update the alignment infos
		if (config.isMoveAble()) {
			List<? extends IGLLayoutElement> separators = children.subList(numColumns + 2, children.size());
			final IGLLayoutElement sep0 = children.get(numColumns + 1);
			((StackedSeparatorUI) sep0.asElement()).setAlignment(stacked.getSingleAlignment());
			for (IGLLayoutElement sep : separators) {
				((StackedSeparatorUI) sep.asElement()).setAlignment(stacked.getSingleAlignment());
			}
		}
	}

	@Override
	public float getTopPadding(boolean smallHeader) {
		return (smallHeader ? 0 : HIST_HEIGHT) + LABEL_HEIGHT * 2;
	}

	@Override
	protected float getLeftPadding() {
		return RenderStyle.STACKED_COLUMN_PADDING;
	}

	/**
	 * @return the model, see {@link #model}
	 */
	public StackedRankColumnModel getModel() {
		return (StackedRankColumnModel) model;
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		super.renderImpl(g, w, h);

		if (!((StackedRankColumnModel) model).isCompressed()) {
			g.incZ();
			renderWeights(g, w);
			g.decZ();
		}
	}


	protected void renderWeights(GLGraphics g, float w) {
		// render the distributions
		StackedRankColumnModel stacked = getModel();
		float[] weights = stacked.getWeights();
		float histHeight = isSmallHeader() ? 0 : HIST_HEIGHT;
		float yi = histHeight + LABEL_HEIGHT + 7;
		float hi = LABEL_HEIGHT - 6;
		float x = getLeftPadding();
		g.lineWidth(RenderStyle.COLOR_STACKED_BORDER_WIDTH);
		g.color(RenderStyle.COLOR_STACKED_BORDER);
		g.drawLine(getLeftPadding(), histHeight + LABEL_HEIGHT + 4, w - getLeftPadding(), histHeight + LABEL_HEIGHT + 4);
		g.lineWidth(1);
		for (int i = 0; i < numColumns; ++i) {
			float wi = stacked.getChildWidth(i) + COLUMN_SPACE;
			// g.drawLine(x, yi, x, yi + hi + 2);
			g.drawText(String.format(Locale.ENGLISH, "%.2f%%", weights[i] * 100), x, yi, wi, hi - 4,
					VAlign.CENTER);
			// g.drawLine(x, yi + hi, x + wi, yi + hi);
			x += wi;
		}
	}

	@Override
	protected float getChildWidth(int i, ARankColumnModel model) {
		return getModel().getChildWidth(i);
	}

	public void setAlignment(int index) {
		getModel().setAlignment(index);
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		g.pushName(weightsClickedPickingId);
		float histHeight = isSmallHeader() ? 0 : HIST_HEIGHT;
		float yi = histHeight + LABEL_HEIGHT + 7;
		float hi = LABEL_HEIGHT - 6;
		float x = COLUMN_SPACE + 2;
		g.fillRect(x, yi, w - x, hi);
		g.popName();
		super.renderPickImpl(g, w, h);
	}

	/**
	 * @param pick
	 */
	protected void onWeightsClicked(Pick pick) {
		if (pick.isAnyDragging() || pick.getPickingMode() != PickingMode.DOUBLE_CLICKED)
			return;
		EditWeightsDialog.show(getModel(), this);
	}

	@ListenTo(sendToMe = true)
	private void onWeightsChanged(WeightsChangedEvent event) {
		getModel().setWeights(event.getWeights());
		relayout();
	}

	public static class AlignmentDragInfo implements IDragInfo {
		@Override
		public String getLabel() {
			return "AlignmentMarker";
		}
	}
}

