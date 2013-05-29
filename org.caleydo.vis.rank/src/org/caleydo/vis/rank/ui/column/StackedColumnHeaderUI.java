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
package org.caleydo.vis.rank.ui.column;

import static org.caleydo.vis.rank.ui.RenderStyle.COLUMN_SPACE;
import static org.caleydo.vis.rank.ui.RenderStyle.HIST_HEIGHT;
import static org.caleydo.vis.rank.ui.RenderStyle.LABEL_HEIGHT;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Locale;

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.IGLElementParent;
import org.caleydo.core.view.opengl.layout2.IMouseLayer.IDragInfo;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.vis.rank.config.IRankTableUIConfig;
import org.caleydo.vis.rank.internal.event.WeightsChangedEvent;
import org.caleydo.vis.rank.model.ACompositeRankColumnModel;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.StackedRankColumnModel;
import org.caleydo.vis.rank.model.mixin.ICollapseableColumnMixin;
import org.caleydo.vis.rank.model.mixin.ICompressColumnMixin;
import org.caleydo.vis.rank.ui.IColumnRenderInfo;
import org.caleydo.vis.rank.ui.RenderStyle;
import org.caleydo.vis.rank.ui.SeparatorUI;
import org.caleydo.vis.rank.ui.StackedSeparatorUI;
import org.caleydo.vis.rank.ui.TableHeaderUI;
/**
 * @author Samuel Gratzl
 *
 */
public class StackedColumnHeaderUI extends ACompositeHeaderUI implements IThickHeader, IColumnRenderInfo {
	protected static final int SUMMARY = 0;
	public final AlignmentDragInfo align = new AlignmentDragInfo();

	protected final StackedRankColumnModel model;

	private final PropertyChangeListener listener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			switch (evt.getPropertyName()) {
			case StackedRankColumnModel.PROP_WEIGHTS:
			case StackedRankColumnModel.PROP_ALIGNMENT:
				relayout();
				break;
			case ICompressColumnMixin.PROP_COMPRESSED:
			case ICollapseableColumnMixin.PROP_COLLAPSED:
				onCompressedChanged();
			}
		}
	};

	private final IPickingListener onWeightsClicked = new IPickingListener() {
		@Override
		public void pick(Pick pick) {
			onWeightsClicked(pick);
		}
	};
	private int weightsClickedPickingId = -1;

	public StackedColumnHeaderUI(StackedRankColumnModel model, IRankTableUIConfig config) {
		super(config, 1);
		this.model = model;
		setLayoutData(model);
		this.add(0, new StackedSummaryHeaderUI(model, config));
		model.addPropertyChangeListener(ACompositeRankColumnModel.PROP_CHILDREN, childrenChanged);
		model.addPropertyChangeListener(StackedRankColumnModel.PROP_ALIGNMENT, listener);
		model.addPropertyChangeListener(StackedRankColumnModel.PROP_WEIGHTS, listener);
		model.addPropertyChangeListener(ICompressColumnMixin.PROP_COMPRESSED, listener);
		model.addPropertyChangeListener(ICollapseableColumnMixin.PROP_COLLAPSED, listener);
		init(model);
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
		model.removePropertyChangeListener(StackedRankColumnModel.PROP_ALIGNMENT, listener);
		model.removePropertyChangeListener(ACompositeRankColumnModel.PROP_CHILDREN, childrenChanged);
		model.removePropertyChangeListener(StackedRankColumnModel.PROP_WEIGHTS, childrenChanged);
		model.removePropertyChangeListener(ICompressColumnMixin.PROP_COMPRESSED, listener);
		model.removePropertyChangeListener(ICollapseableColumnMixin.PROP_COLLAPSED, listener);
		super.takeDown();
	}

	@Override
	protected boolean isSmallHeader() {
		if (getParent() == null)
			return false;
		IGLElementParent p = getParent();
		while (!(p instanceof TableHeaderUI))
			p = p.getParent();
		return ((TableHeaderUI) p).isSmallHeader();
	}

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {

		IGLLayoutElement summary = children.get(0);

		if (model.isCompressed()) {
			summary.setBounds(0, getTopPadding(), w, h - getTopPadding());
			for (IGLLayoutElement child : children.subList(1, children.size()))
				child.hide();
			return;
		}

		summary.setBounds(2, 2, w - 4, (isSmallHeader() ? 0 : HIST_HEIGHT) + LABEL_HEIGHT);

		super.layoutColumns(children, w, h);

		// update the alignment infos
		if (config.isMoveAble()) {
			List<? extends IGLLayoutElement> separators = children.subList(numColumns + 2, children.size());
			final IGLLayoutElement sep0 = children.get(numColumns + 1);
			((StackedSeparatorUI) sep0.asElement()).setAlignment(this.model.getAlignment());
			for (IGLLayoutElement sep : separators) {
				((StackedSeparatorUI) sep.asElement()).setAlignment(this.model.getAlignment());
			}
		}
	}


	protected void onCompressedChanged() {
		((StackedSummaryHeaderUI) get(SUMMARY)).setHasTitle(model.isCompressed());
		relayout();
		relayoutParent();
	}

	@Override
	protected float getLeftPadding() {
		return +RenderStyle.STACKED_COLUMN_PADDING;
	}

	@Override
	protected float getTopPadding() {
		return (isSmallHeader() ? 0 : HIST_HEIGHT) + LABEL_HEIGHT * 2;
	}

	/**
	 * @return the model, see {@link #model}
	 */
	public StackedRankColumnModel getModel() {
		return model;
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		if (!model.isCompressed()) {
			g.decZ().decZ();
			g.color(model.getBgColor());
			g.fillRect(0, 0, w, h);
			// RoundedRectRenderer.render(g, 0, 0, w, h, RenderStyle.HEADER_ROUNDED_RADIUS, 3,
			// RoundedRectRenderer.FLAG_FILL | RoundedRectRenderer.FLAG_TOP);

			g.lineWidth(RenderStyle.COLOR_STACKED_BORDER_WIDTH);
			g.color(RenderStyle.COLOR_STACKED_BORDER);

			// gl.glBegin(GL.GL_LINE_STRIP);
			// {
			// gl.glVertex3f(0, h, z);
			// renderRoundedCorner(g, 0, 0, RenderStyle.HEADER_ROUNDED_RADIUS, 0, RoundedRectRenderer.FLAG_TOP_LEFT);
			// renderRoundedCorner(g, w - RenderStyle.HEADER_ROUNDED_RADIUS, 0, RenderStyle.HEADER_ROUNDED_RADIUS, 0,
			// RoundedRectRenderer.FLAG_TOP_RIGHT);
			// gl.glVertex3f(w, h, z);
			// }
			// gl.glEnd();
			g.drawRect(0, 0, w, h);
			g.lineWidth(1);
			g.incZ().incZ();

			// g.drawLine(x, yi, x, yi + hi + 2);
		}
		super.renderImpl(g, w, h);

		if (!model.isCompressed()) {
			g.incZ();
			config.renderIsOrderByGlyph(g, w, h, model.getMyRanker().getOrderBy() == model);
			renderWeights(g, w);
			g.decZ();
		}
	}


	protected void renderWeights(GLGraphics g, float w) {
		// render the distributions
		float[] weights = model.getWeights();
		float histHeight = isSmallHeader() ? 0 : HIST_HEIGHT;
		float yi = histHeight + LABEL_HEIGHT + 7;
		float hi = LABEL_HEIGHT - 6;
		float x = COLUMN_SPACE;
		g.lineWidth(RenderStyle.COLOR_STACKED_BORDER_WIDTH);
		g.color(RenderStyle.COLOR_STACKED_BORDER);
		g.drawLine(0, histHeight + LABEL_HEIGHT + 4, w, histHeight + LABEL_HEIGHT + 4);
		g.lineWidth(1);
		for (int i = 0; i < numColumns; ++i) {
			float wi = this.model.getChildWidth(i) + COLUMN_SPACE;
			// g.drawLine(x, yi, x, yi + hi + 2);
			g.drawText(String.format(Locale.ENGLISH, "%.2f%%", weights[i] * 100), x, yi, wi, hi - 4,
					VAlign.CENTER);
			// g.drawLine(x, yi + hi, x + wi, yi + hi);
			x += wi;
		}
	}

	@Override
	protected float getChildWidth(int i, ARankColumnModel model) {
		return this.model.getChildWidth(i);
	}

	public void setAlignment(int index) {
		model.setAlignment(index);
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
		EditWeightsDialog.show(this.model, this);
	}

	@ListenTo(sendToMe = true)
	private void onWeightsChanged(WeightsChangedEvent event) {
		model.setWeights(event.getWeights());
		relayout();
	}

	@Override
	public boolean canMoveHere(int index, ARankColumnModel model, boolean clone) {
		return this.model.isMoveAble(model, index, clone);
	}

	@Override
	public void moveHere(int index, ARankColumnModel model, boolean clone) {
		assert canMoveHere(index, model, clone);
		this.model.move(model, index, clone);
	}

	public static class AlignmentDragInfo implements IDragInfo {

	}


	@Override
	public boolean isCollapsed() {
		return model.isCollapsed();
	}

	@Override
	public VAlign getAlignment() {
		return VAlign.LEFT;
	}

	@Override
	public boolean hasFreeSpace() {
		return true;
	}

}

