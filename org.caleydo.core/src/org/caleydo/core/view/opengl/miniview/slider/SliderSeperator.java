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
package org.caleydo.core.view.opengl.miniview.slider;

/**
 * OpenGL2 Slider Seperator
 * 
 * @author Stefan Sauer
 */
public class SliderSeperator {
	private int iID = 0;
	private float fPos = 0;
	private SliderSeperatorBond bond = null;

	public SliderSeperator(int id) {
		iID = id;
	}

	public SliderSeperator(int id, float pos) {
		iID = id;
		fPos = pos;
	}

	public int getID() {
		return iID;
	}

	public float getPos() {
		return fPos;
	}

	public void setPos(float pos) {
		fPos = pos;
	}

	public boolean hasSeperatorBond() {
		return bond != null ? true : false;
	}

	public SliderSeperatorBond getBond() {
		return bond;
	}

	public void setBond(SliderSeperatorBond bond) {
		this.bond = bond;
	}

}
