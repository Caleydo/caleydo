package org.caleydo.core.manager.event.view.treemap;

import org.caleydo.core.manager.event.AEvent;

public class ToggleColoringModeEvent extends AEvent {

	private boolean bCalculateColor;
	
	@Override
	public boolean checkIntegrity() {
		return true;
	}
	
	public void setCalculateColor(boolean flag){
		bCalculateColor=flag;
	}
	
	public boolean isCalculateColor(){
		return bCalculateColor;
	}

}