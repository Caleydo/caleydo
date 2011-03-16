package org.caleydo.view.visbricks.brick.ui;

import org.caleydo.core.manager.picking.EPickingType;

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
	private EPickingType pickingType;

	/**
	 * @param pickingType PickingType of the button.
	 * @param buttonID ID used for picking.
	 */
	public Button(EPickingType pickingType, int buttonID) {
		isSelected = false;
		this.buttonID = buttonID;
		this.pickingType = pickingType;
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
	public EPickingType getPickingType() {
		return pickingType;
	}

	/**
	 * Sets the Picking type of the button.
	 * 
	 * @param pickingType 
	 */
	public void setPickingType(EPickingType pickingType) {
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

}
