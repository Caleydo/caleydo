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
package org.caleydo.core.event;

import org.caleydo.core.view.opengl.canvas.AGLView;

public class SetMinViewSizeEvent
	extends AEvent {

	private int minHeight;
	private int minWidth;
	private AGLView view;

	public SetMinViewSizeEvent() {
		minHeight = -1;
		minWidth = -1;
		view = null;
	}

	@Override
	public boolean checkIntegrity() {
		if ((minHeight == -1) || (minWidth == -1) || (view == null))
			throw new IllegalStateException("parameters not set");
		return true;
	}

	public int getMinHeight() {
		return minHeight;
	}

	public void setMinHeight(int minHeight) {
		this.minHeight = minHeight;
	}

	public int getMinWidth() {
		return minWidth;
	}

	public void setMinWidth(int minWidth) {
		this.minWidth = minWidth;
	}

	public void setMinViewSize(int minWidth, int minHeight) {
		this.minWidth = minWidth;
		this.minHeight = minHeight;
	}

	public AGLView getView() {
		return view;
	}

	public void setView(AGLView view) {
		this.view = view;
	}

}
