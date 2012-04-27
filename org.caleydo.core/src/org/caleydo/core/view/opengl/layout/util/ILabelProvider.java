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
package org.caleydo.core.view.opengl.layout.util;

/**
 * Interface for classes that provide a label. The {@link #getLabel()} method is
 * used as callback to keep the text up-to-date for classes using this text.
 * 
 * @author Christian
 * 
 */
public interface ILabelProvider {

	/**
	 * Callback method that provides label.
	 * 
	 * @return
	 */
	public String getLabel();

	/**
	 * @return True, if the label provided is a default label, false otherwise.
	 */
	public boolean isDefaultLabel();

	/**
	 * Callback method that provides a secondary label.
	 * 
	 * @return
	 */
	public String getSecondaryLabel();
}
