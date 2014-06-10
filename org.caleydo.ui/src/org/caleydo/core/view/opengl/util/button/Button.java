/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.util.button;

import org.caleydo.core.view.opengl.util.texture.EIconTextures;

/**
 * Class representing a button. It holds several properties of a button to be displayed. The rendering should be done by
 * a {@link ButtonRenderer}.
 *
 * @author Christian Partl
 */
public class Button {

	private boolean isSelected;
	private boolean isVisible;
	private int buttonID;
	/**
	 * Path to the image that shall be used as icon for the button.
	 */
	private String iconPath;
	private String pickingType;

	/**
	 * @param pickingType
	 *            PickingType of the button.
	 * @param buttonID
	 *            ID used for picking.
	 * @param iconTexture
	 *            Texture for the button.
	 */
	@Deprecated
	public Button(String pickingType, int buttonID, EIconTextures iconTexture) {
		isSelected = false;
		setVisible(true);
		this.buttonID = buttonID;
		this.pickingType = pickingType;
		this.setIconTexture(iconTexture);
	}

	public Button(String pickingType, int buttonID, String iconPath) {
		isSelected = false;
		setVisible(true);
		this.buttonID = buttonID;
		this.pickingType = pickingType;
		this.iconPath = iconPath;
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
		this.iconPath = iconTexture.getFileName();
	}

	/**
	 * @param iconPath
	 *            setter, see {@link iconPath}
	 */
	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}

	/**
	 * @return the iconPath, see {@link #iconPath}
	 */
	public String getIconPath() {
		return iconPath;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public boolean isVisible() {
		return isVisible;
	}

}
