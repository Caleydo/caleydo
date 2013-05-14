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
package org.caleydo.view.tourguide.api.state;

import java.util.List;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.io.gui.dataimport.widget.ICallback;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;

/**
 * @author Samuel Gratzl
 *
 */

public class ButtonTransition implements ITransition {
	private final IState target;
	private final String label;

	public ButtonTransition(IState target, String label) {
		this.target = target;
		this.label = label;
	}

	@Override
	public boolean apply(List<TablePerspective> existing) {
		return true;
	}

	@Override
	public void onSourceEnter(ICallback<IState> onApply) {

	}

	@Override
	public GLElement create(final ICallback<IState> onApply) {
		GLButton b = new GLButton();
		b.setCallback(new ISelectionCallback() {
			@Override
			public void onSelectionChanged(GLButton button, boolean selected) {
				onApply.on(ButtonTransition.this.target);
			}
		});
		b.setRenderer(new MultiLineTextRenderer(label));
		return b;
	}
}
