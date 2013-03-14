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
package org.caleydo.vis.rank.internal.ui;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.vis.rank.ui.RenderStyle;

import com.google.common.collect.Iterables;

/**
 * simple bar of buttons in a horizontal row
 * 
 * @author Samuel Gratzl
 * 
 */
public class ButtonBar extends GLElementContainer {
	public ButtonBar() {
		super(GLLayouts.flowHorizontal(1));
		setSize(Float.NaN, RenderStyle.BUTTON_WIDTH);
	}

	public void addButton(GLButton b) {
		this.add(b.setSize(RenderStyle.BUTTON_WIDTH, -1));
	}
	public void addSpacer() {
		this.add(new GLElement());
	}

	public float getMinWidth() {
		int buttons = Iterables.size(Iterables.filter(this, GLButton.class));
		return buttons * (RenderStyle.BUTTON_WIDTH + 1);
	}
}
