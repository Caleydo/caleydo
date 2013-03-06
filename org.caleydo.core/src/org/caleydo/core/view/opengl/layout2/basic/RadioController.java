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
package org.caleydo.core.view.opengl.layout2.basic;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.view.opengl.layout2.basic.GLButton.EButtonMode;

/**
 * @author Samuel Gratzl
 *
 */
public class RadioController implements GLButton.ISelectionCallback {
	private List<GLButton> buttons = new ArrayList<>();
	private int selected = 0;

	private final GLButton.ISelectionCallback callback;

	public RadioController(GLButton.ISelectionCallback callback) {
		this.callback = callback;
	}

	public void add(GLButton b) {
		buttons.add(b);
		b.setMode(EButtonMode.BUTTON);
		b.setSelected(selected == (buttons.size() - 1));
		b.setCallback(this);
	}

	public void setSelected(int index) {
		if (this.selected == index)
			return;
		int i = 0;
		for (GLButton b : buttons) {
			b.setCallback(null);
			b.setSelected((i++) == index);
			b.setCallback(this);
		}
		this.selected = index;
		callback.onSelectionChanged(index < 0 ? null : buttons.get(index), true);
	}

	@Override
	public void onSelectionChanged(GLButton button, boolean selected) {
		int index = buttons.indexOf(button);
		if (this.selected == index)
			return;
		setSelected(index);
	}
}
