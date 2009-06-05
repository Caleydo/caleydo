package org.caleydo.core.manager.event.view.glyph;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.EPositionModel;

public class SetPositionModelEvent
	extends AEvent {

	EPositionModel positionModel;
	int iViewID;

	public SetPositionModelEvent(int iViewID, EPositionModel positionModel) {
		super();
		this.iViewID = iViewID;
		this.positionModel = positionModel;
	}

	public EPositionModel getPositionModel() {
		return positionModel;
	}
	
	public int getViewID() {
		return iViewID;
	}

	@Override
	public boolean checkIntegrity() {
		if (positionModel == null)
			return false;
		return true;
	}

}
