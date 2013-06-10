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

import static org.caleydo.vis.rank.ui.RenderStyle.HIST_HEIGHT;
import static org.caleydo.vis.rank.ui.RenderStyle.LABEL_HEIGHT;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementParent;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.vis.rank.config.IRankTableUIConfig;
import org.caleydo.vis.rank.model.ACompositeRankColumnModel;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.StackedRankColumnModel;
import org.caleydo.vis.rank.model.mixin.ICollapseableColumnMixin;
import org.caleydo.vis.rank.model.mixin.ICompressColumnMixin;
import org.caleydo.vis.rank.model.mixin.IRankableColumnMixin;
import org.caleydo.vis.rank.ui.IColumnRenderInfo;
import org.caleydo.vis.rank.ui.RenderStyle;
import org.caleydo.vis.rank.ui.TableHeaderUI;
/**
 * @author Samuel Gratzl
 *
 */
public class SimpleColumnHeaderUI extends ACompositeHeaderUI implements IThickHeader, IColumnRenderInfo {
	protected static final int SUMMARY = 0;
	protected final ACompositeRankColumnModel model;

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

	public SimpleColumnHeaderUI(ACompositeRankColumnModel model, IRankTableUIConfig config) {
		super(config, 1);
		this.model = model;
		setLayoutData(model);
		this.add(0, createSummary(model, config));
		model.addPropertyChangeListener(ACompositeRankColumnModel.PROP_CHILDREN, childrenChanged);
		model.addPropertyChangeListener(ICompressColumnMixin.PROP_COMPRESSED, listener);
		model.addPropertyChangeListener(ICollapseableColumnMixin.PROP_COLLAPSED, listener);
		model.addPropertyChangeListener(StackedRankColumnModel.PROP_ALIGNMENT, listener);
		model.addPropertyChangeListener(StackedRankColumnModel.PROP_WEIGHTS, listener);
		init(model);
	}

	protected GLElement createSummary(ACompositeRankColumnModel model, IRankTableUIConfig config) {
		return new SimpleSummaryHeaderUI(model, config);
	}

	protected void onCompressedChanged() {
		if (model instanceof ICompressColumnMixin) {
			((AColumnHeaderUI) get(SUMMARY)).setHasTitle(((ICompressColumnMixin) model).isCompressed());
		}
		relayout();
		relayoutParent();
	}

	@Override
	protected GLElement wrapImpl(ARankColumnModel model) {
		GLElement g = ColumnUIs.createHeader(model, config, false);
		return g;
	}

	@Override
	protected void takeDown() {
		model.removePropertyChangeListener(ACompositeRankColumnModel.PROP_CHILDREN, childrenChanged);
		model.removePropertyChangeListener(ICompressColumnMixin.PROP_COMPRESSED, listener);
		model.removePropertyChangeListener(ICollapseableColumnMixin.PROP_COLLAPSED, listener);
		model.removePropertyChangeListener(StackedRankColumnModel.PROP_ALIGNMENT, listener);
		model.removePropertyChangeListener(StackedRankColumnModel.PROP_WEIGHTS, childrenChanged);
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
	protected float getLeftPadding() {
		return +RenderStyle.STACKED_COLUMN_PADDING;
	}

	@Override
	public float getTopPadding(boolean smallHeader) {
		return LABEL_HEIGHT;
	}

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		IGLLayoutElement summary = children.get(0);

		if (model instanceof ICompressColumnMixin && ((ICompressColumnMixin) model).isCompressed()) {
			boolean isSmallHeader = isSmallHeader();
			summary.setBounds(0, getTopPadding(isSmallHeader), w, h - getTopPadding(isSmallHeader));
			for (IGLLayoutElement child : children.subList(1, children.size()))
				child.hide();
			return;
		}

		summary.setBounds(2, 2, w - 4, (isSmallHeader() ? 0 : HIST_HEIGHT) + LABEL_HEIGHT);
		super.layoutColumns(children, w, h);
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		final boolean isCompressed = (model instanceof ICompressColumnMixin && ((ICompressColumnMixin) model)
				.isCompressed());
		if (!isCompressed) {
			g.decZ().decZ();
			config.renderHeaderBackground(g, w, h, LABEL_HEIGHT, model);

			g.lineWidth(RenderStyle.COLOR_STACKED_BORDER_WIDTH);
			g.color(RenderStyle.COLOR_STACKED_BORDER);

			g.drawRect(0, 0, w, h);
			g.lineWidth(1);
			g.incZ().incZ();
		}
		renderBaseImpl(g, w, h);

		if (model instanceof IRankableColumnMixin && !isCompressed) {
			g.incZ();
			config.renderIsOrderByGlyph(g, w, h, model.getMyRanker().getOrderBy() == model);
			g.decZ();
		}
	}

	protected void renderBaseImpl(GLGraphics g, float w, float h) {
		super.renderImpl(g, w, h);
	}

	@Override
	protected float getChildWidth(int i, ARankColumnModel model) {
		return model.getWidth();
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

