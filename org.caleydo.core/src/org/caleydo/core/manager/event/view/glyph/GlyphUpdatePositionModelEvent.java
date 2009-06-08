package org.caleydo.core.manager.event.view.glyph;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.EPositionModel;

public class GlyphUpdatePositionModelEvent
	extends AEvent {

	public static final int X_AXIS = 0;
	public static final int Y_AXIS = 1;

	private EPositionModel positionModel = null;
	private int value = -1;
	private int axis = -1;
	int iViewID;

	/**
	 * @param positionModel
	 * @param use
	 *            static members X_AXIS and Y_AXIS to indicate the axis
	 * @param this is the internal column number of the selected column
	 */
	public GlyphUpdatePositionModelEvent(int iViewID, EPositionModel positionModel, int axis, int colnum) {
		super();
		this.iViewID = iViewID;
		this.positionModel = positionModel;
		this.axis = axis;
		this.value = colnum;
	}
	
	public int getViewID() {
		return iViewID;
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

}
