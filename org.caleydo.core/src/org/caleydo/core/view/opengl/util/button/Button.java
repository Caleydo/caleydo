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
package org.caleydo.core.view.opengl.util.button;

import org.caleydo.core.view.opengl.util.texture.EIconTextures;

/**
 * Class representing a button. It holds several properties of a button to be displayed. The rendering should
 * be done by a {@link ButtonRenderer}.
 * 
 * @author Christian Partl
 */
public class Button {

	private boolean isSelected;
	private boolean isVisible;
	private int buttonID;
	private EIconTextures iconTexture;
	private String pickingType;

	/**
	 * @param pickingType
	 *            PickingType of the button.
	 * @param buttonID
	 *            ID used for picking.
	 * @param iconTexture
	 *            Texture for the button.
	 */
	public Button(String pickingType, int buttonID, EIconTextures iconTexture) {
		isSelected = false;
		setVisible(true);
		this.buttonID = buttonID;
		this.pickingType = pickingType;
		this.setIconTexture(iconTexture);
	}

	/**
	 * @return ID used for picking.
	 */
	public int getButtonID() {
		return buttonID;
	}

	/**
	 * Sets the ID used for picking.
	 * 
	 * @param buttonID
	 */
	public void setButtonID(int buttonID) {
		this.buttonID = buttonID;
	}

	/**
	 * @return Picking type of button.
	 */
	public String getPickingType() {
		return pickingType;
	}

	/**
	 * Sets the Picking type of the button.
	 * 
	 * @param pickingType
	 */
	public void setPickingType(String pickingType) {
		this.pickingType = pickingType;
	}

	/**
	 * @return True, if the button is currently selected, false otherwise.
	 */
	public boolean isSelected() {
		return isSelected;
	}

	/**
	 * Sets whether the button shall be selected.
	 * 
	 * @param isSelected
	 */
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	/**
	 * Sets the texture for the button.
	 * 
	 * @param iconTexture
	 */
	public void setIconTexture(EIconTextures iconTexture) {
		this.iconTexture = iconTexture;
	}

	/**
	 * @return Texture of button.
	 */
	public EIconTextures getIconTexture() {
		return iconTexture;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public boolean isVisible() {
		return isVisible;
	}

}
