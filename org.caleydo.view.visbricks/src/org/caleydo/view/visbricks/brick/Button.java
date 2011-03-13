package org.caleydo.view.visbricks.brick;

import org.caleydo.core.manager.picking.EPickingType;

public class Button {


	private boolean isSelected;
	private int buttonID;
	private EPickingType pickingType;

	public Button(EPickingType pickingType, int buttonID) {
		isSelected = false;
		this.buttonID = buttonID;
		this.pickingType = pickingType;
	}
	
	public int getButtonID() {
		return buttonID;
	}

	public void setButtonID(int buttonID) {
		this.buttonID = buttonID;
	}

	public EPickingType getPickingType() {
		return pickingType;
	}

	public void setPickingType(EPickingType pickingType) {
		this.pickingType = pickingType;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

}
