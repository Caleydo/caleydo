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
package org.caleydo.vis.rank.config;

import gleem.linalg.Vec2f;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.basic.IScrollBar;
import org.caleydo.core.view.opengl.layout2.basic.ScrollBar;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.model.RankTableModel;
import org.caleydo.vis.rank.ui.RenderStyle;

/**
 * basic implementation of a {@link IRankTableUIConfig}
 *
 * @author Samuel Gratzl
 *
 */
public class RankTableUIConfigBase implements IRankTableUIConfig {
	private final boolean isInteractive;
	private final boolean isMoveAble;
	private final boolean canChangeWeights;

	public RankTableUIConfigBase(boolean isInteractive, boolean isMoveAble, boolean canChangeWeights) {
		this.isInteractive = isInteractive;
		this.isMoveAble = isMoveAble;
		this.canChangeWeights = canChangeWeights;
	}

	@Override
	public boolean isSmallHeaderByDefault() {
		return false;
	}

	@Override
	public boolean isInteractive() {
		return isInteractive;
	}

	@Override
	public boolean isMoveAble() {
		return isMoveAble;
	}

	@Override
	public boolean canChangeWeights() {
		return canChangeWeights;
	}

	@Override
	public IScrollBar createScrollBar(boolean horizontal) {
		return new ScrollBar(horizontal);
	}

	@Override
	public void renderIsOrderByGlyph(GLGraphics g, float w, float h, boolean orderByIt) {
		float rx = RenderStyle.HEADER_ROUNDED_RADIUS_X;
		float ry = RenderStyle.HEADER_ROUNDED_RADIUS_Y;
		if (orderByIt) {
			g.color(RenderStyle.ORDER_BY_COLOR);
			g.drawLine(rx, 0, 0, ry);
			g.drawLine(w - rx, 0, w - 0.5f, ry);
		} else {
			g.color(1, 1, 1, 0.25f);
		}
		g.fillPolygon(new Vec2f(0, 0), new Vec2f(rx, 0), new Vec2f(0, ry));
		g.fillPolygon(new Vec2f(w - rx, 0), new Vec2f(w, 0), new Vec2f(w, ry));
		// g.fillImage(RenderStyle.ICON_STAR, w - 16, -8, 16, 16);
	}

	@Override
	public void renderHeaderBackground(GLGraphics g, float w, float h, float labelHeight, ARankColumnModel model) {
		g.color(model.getBgColor());
		g.fillRect(0, 0, w, h);
	}

	@Override
	public boolean isShowColumnPool() {
		return true;
	}

	@Override
	public EButtonBarPositionMode getButtonBarPosition() {
		return EButtonBarPositionMode.ABOVE_LABEL;
	}

	@Override
	public void renderRowBackground(GLGraphics g, float x, float y, float w, float h, boolean even, IRow row,
			IRow selected) {
		if (row == selected) {
				g.color(RenderStyle.COLOR_SELECTED_ROW);
				g.incZ();
				g.fillRect(x, y, w, h);
				g.color(RenderStyle.COLOR_SELECTED_BORDER);
				g.drawLine(x, y, x + w, y);
				g.drawLine(x, y+h, x + w, y+h);
				g.decZ();
		} else if (!even) {
			g.color(RenderStyle.COLOR_BACKGROUND_EVEN);
			g.fillRect(x, y, w, h);
		}
	}

	@Override
	public boolean canEditValues() {
		return true;
	}

	@Override
	public Color getBarOutlineColor() {
		return null;
	}

	@Override
	public void onRowClick(RankTableModel table, PickingMode pickingMode, IRow row, boolean isSelected) {
		if (!isSelected && pickingMode == PickingMode.CLICKED) {
			table.setSelectedRow(row);
		}
	}
}
