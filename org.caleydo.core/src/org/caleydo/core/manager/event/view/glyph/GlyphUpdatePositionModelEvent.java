package org.caleydo.core.manager.event.view.glyph;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.EPositionModel;

@XmlRootElement
@XmlType
public class GlyphUpdatePositionModelEvent
	extends AEvent {

	public static final int X_AXIS = 0;
	public static final int Y_AXIS = 1;

	private EPositionModel positionModel = null;
	private int value = -1;
	private int axis = -1;
	int viewID;

	/**
	 * default no-arg constructor
	 */
	public GlyphUpdatePositionModelEvent() {
		// nothing to initialize here
	}
	
	/**
	 * @param positionModel
	 * @param use
	 *            static members X_AXIS and Y_AXIS to indicate the axis
	 * @param this is the internal column number of the selected column
	 */
	public GlyphUpdatePositionModelEvent(int iViewID, EPositionModel positionModel, int axis, int colnum) {
		super();
		this.viewID = iViewID;
		this.positionModel = positionModel;
		this.axis = axis;
		this.value = colnum;
	}
	
	public int getViewID() {
		return viewID;
	}

	@Override
	public boolean checkIntegrity() {
		if (positionModel == null || value == -1 || (axis != 0 && axis != 1))
			return false;
		return true;
	}

	public EPositionModel getPositionModel() {
		return positionModel;
	}

	public int getValue() {
		return value;
	}

	public int getAxis() {
		return axis;
	}

	public void setPositionModel(EPositionModel positionModel) {
		this.positionModel = positionModel;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public void setAxis(int axis) {
		this.axis = axis;
	}

	public void setViewID(int viewID) {
		this.viewID = viewID;
	}

}
