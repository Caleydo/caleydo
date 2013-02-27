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
package org.caleydo.view.tourguide.v3.ui;

import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.layout.IHasGLLayoutData;
import org.caleydo.view.tourguide.v3.model.IRow;

/**
 * @author Samuel Gratzl
 *
 */
public class StackedRenderInfo implements IHasGLLayoutData {
	private boolean hasFreeSpace = true;
	private VAlign valign = VAlign.LEFT;
	private final IRow data;

	public StackedRenderInfo(IRow data) {
		this.data = data;
	}

	/**
	 * @param hasFreeSpace
	 *            setter, see {@link hasFreeSpace}
	 */
	public void setHasFreeSpace(boolean hasFreeSpace) {
		this.hasFreeSpace = hasFreeSpace;
	}

	/**
	 * @return the hasFreeSpace, see {@link #hasFreeSpace}
	 */
	public boolean isHasFreeSpace() {
		return hasFreeSpace;
	}
	/**
	 * @return the valign, see {@link #valign}
	 */
	public VAlign getValign() {
		return valign;
	}

	/**
	 * @param valign
	 *            setter, see {@link valign}
	 */
	public void setValign(VAlign valign) {
		this.valign = valign;
	}

	/**
	 * @return the data, see {@link #data}
	 */
	public IRow getData() {
		return data;
	}

	@Override
	public <T> T getLayoutDataAs(Class<T> clazz, T default_) {
		if (clazz.isInstance(data))
			return clazz.cast(data);
		else if (clazz.equals(Boolean.class))
			return clazz.cast(hasFreeSpace);
		else if (clazz.equals(VAlign.class))
			return clazz.cast(valign);
		return default_;
	}

}
