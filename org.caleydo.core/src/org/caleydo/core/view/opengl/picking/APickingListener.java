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
package org.caleydo.core.view.opengl.picking;

/**
 * Convenience class that can be extended instead of implementing {@link IPickingListener} if not all methods
 * need to be implemented, and nothing else speaks against inheritance.
 * 
 * @author Christian Partl
 */

public abstract class APickingListener
	implements IPickingListener {

	@Override
	public void clicked(Pick pick) {
	}

	@Override
	public void doubleClicked(Pick pick) {
	}

	@Override
	public void rightClicked(Pick pick) {
	}

	@Override
	public void mouseOver(Pick pick) {
	}

	@Override
	public void dragged(Pick pick) {
	}

	@Override
	public void mouseOut(Pick pick) {
	}

}
