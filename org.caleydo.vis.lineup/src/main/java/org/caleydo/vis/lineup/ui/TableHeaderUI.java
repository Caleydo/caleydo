/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.ui;

import static org.caleydo.vis.lineup.ui.RenderStyle.HIST_HEIGHT;
import static org.caleydo.vis.lineup.ui.RenderStyle.LABEL_HEIGHT;

import java.util.List;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.vis.lineup.config.IRankTableUIConfig;
import org.caleydo.vis.lineup.model.ARankColumnModel;
import org.caleydo.vis.lineup.model.RankTableModel;
import org.caleydo.vis.lineup.ui.column.ACompositeHeaderUI;
import org.caleydo.vis.lineup.ui.column.ColumnUIs;

/**
 * a visualzation of the table header row, in HTML it would be the thead section
 *
 * @author Samuel Gratzl
 *
 */
public final class TableHeaderUI extends ACompositeHeaderUI {
	private final RankTableModel table;
	private boolean isSmallHeader;

	public TableHeaderUI(RankTableModel table, IRankTableUIConfig config) {
		super(config, 1 /* for the button */);
		this.table = table;
		this.table.addPropertyChangeListener(RankTableModel.PROP_COLUMNS, childrenChanged);
		this.isSmallHeader = config.isSmallHeaderByDefault();
		setLayoutData(table);
		setSize(-1, ((isSmallHeader ? 0 : HIST_HEIGHT) + LABEL_HEIGHT * 2) * 1);

		{
			GLButton toggleSmallHeader = new GLButton(GLButton.EButtonMode.CHECKBOX);
			toggleSmallHeader.setSelected(isSmallHeader);
			toggleSmallHeader.setCallback(new GLButton.ISelectionCallback() {
				@Override
				public void onSelectionChanged(GLButton button, boolean selected) {
					setSmallHeader(selected);
				}
			});
			toggleSmallHeader.setTooltip("Toggle Small / Thick Headers");
			toggleSmallHeader.setRenderer(GLRenderers.fillImage(RenderStyle.ICON_SMALL_HEADER_ON));
			toggleSmallHeader.setSelectedRenderer(GLRenderers.fillImage(RenderStyle.ICON_SMALL_HEADER_OFF));

			this.add(toggleSmallHeader);
		}

		this.init(table.getColumns());
	}

	/**
	 * @return the isCompact, see {@link #isCompact}
	 */
	@Override
	public boolean isSmallHeader() {
		return isSmallHeader;
	}

	/**
	 * @param isCompact
	 *            setter, see {@link isCompact}
	 */
	public void setSmallHeader(boolean isCompact) {
		if (this.isSmallHeader == isCompact)
			return;
		this.isSmallHeader = isCompact;
		relayout();
		setHasThick(max(this, 0));
		relayoutParent();
	}

	@Override
	protected void setHasThick(float thickOffset) {
		super.setHasThick(thickOffset);
		setSize(-1, ((!isSmallHeader ? HIST_HEIGHT : 0) + LABEL_HEIGHT) * 1 + thickOffset);
	}

	@Override
	protected GLElement wrapImpl(ARankColumnModel model) {
		return ColumnUIs.createHeader(model, config, true);
	}

	@Override
	protected void takeDown() {
		this.table.removePropertyChangeListener(RankTableModel.PROP_COLUMNS, childrenChanged);
		super.takeDown();
	}

	@Override
	protected float getChildWidth(int i, ARankColumnModel model) {
		return model.getWidth();
	}

	@Override
	public boolean canMoveHere(int index, ARankColumnModel model, boolean clone) {
		return table.isMoveAble(model, index, clone);
	}

	@Override
	public void moveHere(int index, ARankColumnModel model, boolean clone) {
		assert canMoveHere(index, model, clone);
		this.table.move(model, index, clone);
	}

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		IGLLayoutElement toggleSmall = children.get(0);
		float x = super.layoutColumns(children, w, h);
		if (x >= w)
			x = w - 16;
		toggleSmall.setBounds(x, thickOffset + (LABEL_HEIGHT - 16) * 0.5f, 16, 16);
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		if (!isSmallHeader) {
			g.incZ(-0.5f);
			g.color(RenderStyle.COLOR_BACKGROUND_EVEN);
			g.fillRect(0, h - RenderStyle.HIST_HEIGHT, w, RenderStyle.HIST_HEIGHT);
			g.incZ(0.5f);
		}
		super.renderImpl(g, w, h);
	}
}


