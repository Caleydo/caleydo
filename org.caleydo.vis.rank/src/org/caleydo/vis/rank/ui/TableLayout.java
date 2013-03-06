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
package org.caleydo.vis.rank.ui;

import java.util.List;

import org.caleydo.core.view.opengl.layout2.layout.GLFlowLayout;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;

/**
 * @author Samuel Gratzl
 *
 */
public class TableLayout extends GLFlowLayout {
	private final boolean hasButtons;

	public TableLayout(boolean hasButtons) {
		super(false, 0, GLPadding.ZERO);
		this.hasButtons = hasButtons;
	}

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		if (!hasButtons || children.size() < 1) {
			super.doLayout(children, w, h);
			return;
		}
		IGLLayoutElement buttons = children.get(0);
		buttons.setBounds(0, 0, w, buttons.getSetHeight());
		super.doLayout(children.subList(1, children.size()), w, h);
	}
}
