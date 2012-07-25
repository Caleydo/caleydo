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
package org.caleydo.view.stratomex.brick.ui;

import org.caleydo.core.view.opengl.util.button.Button;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.stratomex.brick.EContainedViewType;
import org.caleydo.view.stratomex.brick.layout.IViewTypeChangeListener;

/**
 * Button that is supposed to be used for switching views in bricks.
 * 
 * @author Christian Partl
 * 
 */
public class BrickViewSwitchingButton extends Button implements IViewTypeChangeListener {

	private EContainedViewType viewType;

	/**
	 * @param pickingType
	 * @param buttonID
	 * @param iconTexture
	 * @param viewType
	 *            The view type that is associated with this button.
	 */
	public BrickViewSwitchingButton(String pickingType, int buttonID,
			EIconTextures iconTexture, EContainedViewType viewType) {
		super(pickingType, buttonID, iconTexture);
		this.viewType = viewType;
	}

	/**
	 * Sets the view type that is associated with this button.
	 * 
	 * @param viewType
	 */
	public void setViewType(EContainedViewType viewType) {
		this.viewType = viewType;
	}

	/**
	 * @return the view type that is associated with this button.
	 */
	public EContainedViewType getViewType() {
		return viewType;
	}

	@Override
	public void viewTypeChanged(EContainedViewType viewType) {
		if (viewType == this.viewType) {
			setSelected(true);
		} else {
			setSelected(false);
		}

	}

}
