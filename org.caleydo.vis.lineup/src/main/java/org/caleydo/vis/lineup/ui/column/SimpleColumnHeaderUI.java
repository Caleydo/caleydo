/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.ui.column;

import static org.caleydo.vis.lineup.ui.RenderStyle.HIST_HEIGHT;
import static org.caleydo.vis.lineup.ui.RenderStyle.LABEL_HEIGHT;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementParent;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.vis.lineup.config.IRankTableUIConfig;
import org.caleydo.vis.lineup.model.ACompositeRankColumnModel;
import org.caleydo.vis.lineup.model.ARankColumnModel;
import org.caleydo.vis.lineup.model.StackedRankColumnModel;
import org.caleydo.vis.lineup.model.mixin.ICollapseableColumnMixin;
import org.caleydo.vis.lineup.model.mixin.ICompressColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IRankableColumnMixin;
import org.caleydo.vis.lineup.ui.IColumnRenderInfo;
import org.caleydo.vis.lineup.ui.RenderStyle;
import org.caleydo.vis.lineup.ui.TableHeaderUI;
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
				break;
			case ACompositeRankColumnModel.PROP_CHILDREN_ORDER:
				onChildrenOrderChanged(model.getChildren());
				break;
			}
		}
	};

	public SimpleColumnHeaderUI(ACompositeRankColumnModel model, IRankTableUIConfig config) {
		super(config, 1);
		this.model = model;
		setLayoutData(model);
		this.add(0, createSummary(model, config));
		model.addPropertyChangeListener(ACompositeRankColumnModel.PROP_CHILDREN, childrenChanged);
		model.addPropertyChangeListener(ACompositeRankColumnModel.PROP_CHILDREN_ORDER, listener);
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
		model.removePropertyChangeListener(ACompositeRankColumnModel.PROP_CHILDREN_ORDER, listener);
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
		return +RenderStyle.GROUP_COLUMN_PADDING;
	}

	@Override
	public float getTopPadding(boolean smallHeader) {
		return LABEL_HEIGHT + 2;
	}

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		IGLLayoutElement summary = children.get(0);

		if (model instanceof ICompressColumnMixin && ((ICompressColumnMixin) model).isCompressed()) {
			boolean isSmallHeader = isSmallHeader();
			summary.setBounds(getLeftPadding(), getTopPadding(isSmallHeader), w - getLeftPadding(), h
					- getTopPadding(isSmallHeader));
			for (IGLLayoutElement child : children.subList(1, children.size()))
				child.hide();
			return;
		}

		// simplesummaryHeaderUI doesn't have a hist (never)
		summary.setBounds(getLeftPadding() + 2, 2, w - 4 - getLeftPadding(), (isSmallHeader()
				|| (summary.asElement() instanceof SimpleSummaryHeaderUI) ? 0 : HIST_HEIGHT)
				+ LABEL_HEIGHT);
		super.layoutColumns(children, w, h);
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		final boolean isCompressed = (model instanceof ICompressColumnMixin && ((ICompressColumnMixin) model)
				.isCompressed());
		if (!isCompressed) {
			g.decZ().decZ();
			float left = getLeftPadding();
			g.save();
			g.move(left, 0);
			config.renderHeaderBackground(g, w - getLeftPadding(), h, 0, model);
			g.restore();

			g.lineWidth(RenderStyle.COLOR_STACKED_BORDER_WIDTH);
			g.color(RenderStyle.COLOR_STACKED_BORDER);

			g.drawRect(left - 1, 0, w - left + 1, h);
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

	@Override
	public Color getBarOutlineColor() {
		return config.getBarOutlineColor();
	}

}

