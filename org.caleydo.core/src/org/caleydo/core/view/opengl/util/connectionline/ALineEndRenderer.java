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
/**
 * 
 */
package org.caleydo.core.view.opengl.util.connectionline;

/**
 * Abstract base class for all attribute renderers that are supposed to be
 * rendered at one end of the connection line. Specifies, which line end to use.
 * 
 * @author Christian
 * 
 */
public abstract class ALineEndRenderer implements IConnectionLineAttributeRenderer {

	/**
	 * Specifies whether the attribute shall be rendered at the first or last
	 * line point.
	 */
	protected boolean isLineEnd1;

	/**
	 * @param isLineEnd1
	 *            see {@link #isLineEnd1}
	 */
	public ALineEndRenderer(boolean isLineEnd1) {
		this.isLineEnd1 = isLineEnd1;
	}

	/**
	 * @param isLineEnd1
	 *            setter, see {@link #isLineEnd1}
	 */
	public void setLineEnd1(boolean isLineEnd1) {
		this.isLineEnd1 = isLineEnd1;
	}

	/**
	 * @return the isLineEnd1, see {@link #isLineEnd1}
	 */
	public boolean isLineEnd1() {
		return isLineEnd1;
	}

}
