package org.caleydo.core.manager.event.view.glyph;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.EPositionModel;

public class SetPositionModelEvent
	extends AEvent {

	EPositionModel positionModel;

	public SetPositionModelEvent(EPositionModel positionModel) {
		this.positionModel = positionModel;
	}

	public EPositionModel getPositionModel() {
		return positionModel;
	}

	@Override
	public boolean checkIntegrity() {
		if (positionModel == null)
			return false;
		return true;
	}

}
