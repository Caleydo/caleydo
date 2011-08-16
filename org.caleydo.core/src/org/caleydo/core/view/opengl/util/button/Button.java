package org.caleydo.core.view.opengl.util.button;

import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;

/**
 * Class representing a button. It holds several properties of a button to be
 * displayed. The rendering should be done by a {@link ButtonRenderer}.
 * 
 * @author Christian Partl
 * 
 */
public class Button {

	private boolean isSelected;
	private int buttonID;
	private EIconTextures iconTexture;
	private PickingType pickingType;

	/**
	 * @param pickingType
	 *            PickingType of the button.
	 * @param buttonID
	 *            ID used for picking.
	 * @param iconTexture
	 *            Texture for the button.
	 */
	public Button(PickingType pickingType, int buttonID,
			EIconTextures iconTexture) {
		isSelected = false;
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
	public PickingType getPickingType() {
		return pickingType;
	}

	/**
	 * Sets the Picking type of the button.
	 * 
	 * @param pickingType
	 */
	public void setPickingType(PickingType pickingType) {
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

}
