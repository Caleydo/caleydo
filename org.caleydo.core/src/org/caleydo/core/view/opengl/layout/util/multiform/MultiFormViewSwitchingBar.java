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
package org.caleydo.core.view.opengl.layout.util.multiform;

import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.Row;

/**
 * Bar with buttons that can be used to change the rendered content of a {@link MultiFormRenderer}.
 *
 * @author Christian Partl
 *
 */
public class MultiFormViewSwitchingBar extends Row implements IMultiFormChangeListener {

	public MultiFormViewSwitchingBar(MultiFormRenderer multiFormRenderer, AGLView view) {

	}

	@Override
	public void destroyed(MultiFormRenderer multiFormRenderer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void activeRendererChanged(MultiFormRenderer multiFormRenderer, int rendererID, int previousRendererID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void rendererAdded(MultiFormRenderer multiFormRenderer, int rendererID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void rendererRemoved(MultiFormRenderer multiFormRenderer, int rendererID) {
		// TODO Auto-generated method stub

	}

}
