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
package org.caleydo.vis.rank.model;

import java.awt.Color;

import org.caleydo.vis.rank.model.mixin.ICollapseableColumnMixin;
import org.caleydo.vis.rank.model.mixin.IHideableColumnMixin;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class ABasicRankColumnModel extends ARankColumnModel implements IHideableColumnMixin,
		ICollapseableColumnMixin {
	public static final int COLLAPSED_WIDTH = 16;
	private boolean collapsed = false;

	public ABasicRankColumnModel(Color color, Color bgColor) {
		super(color, bgColor);
	}

	@Override
	public boolean isCollapsed() {
		return collapsed;
	}

	@Override
	public float getPreferredWidth() {
		if (isCollapsed())
			return COLLAPSED_WIDTH;
		return getWeight();
	}

	@Override
	public final boolean isCollapseAble() {
		return parent.isCollapseAble(this);
	}

	@Override
	public void setCollapsed(boolean collapsed) {
		if (this.collapsed == collapsed)
			return;
		if (collapsed && !parent.isCollapseAble(this))
			return;
		propertySupport.firePropertyChange(PROP_COLLAPSED, this.collapsed, this.collapsed = collapsed);
	}


	@Override
	public boolean hide() {
		return parent.hide(this);
	}

	@Override
	public boolean isHideAble() {
		return parent.isHideAble(this);
	}

	@Override
	public boolean isDestroyAble() {
		return parent.isDestroyAble(this);
	}

	@Override
	public boolean destroy() {
		if (!isDestroyAble())
			return false;
		return getTable().destroy(this);
	}
}
