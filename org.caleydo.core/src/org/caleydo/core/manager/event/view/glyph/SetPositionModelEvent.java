package org.caleydo.core.manager.event.view.glyph;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.EPositionModel;

@XmlRootElement
@XmlType
public class SetPositionModelEvent
	extends AEvent {

	EPositionModel positionModel;
	int viewID;

	public SetPositionModelEvent() {
		// nothing to initialize here
	}

	public SetPositionModelEvent(int iViewID, EPositionModel positionModel) {
		super();
		this.viewID = iViewID;
		this.positionModel = positionModel;
	}

	public EPositionModel getPositionModel() {
		return positionModel;
	}

	public int getViewID() {
		return viewID;
	}

	@Override
	public boolean checkIntegrity() {
		if (positionModel == null)
			return false;
		return true;
	}

	public void setPositionModel(EPositionModel positionModel) {
		this.positionModel = positionModel;
	}

	public void setViewID(int viewID) {
		this.viewID = viewID;
	}

}
