package org.caleydo.core.view.opengl.util.scrollbar;

import org.caleydo.core.manager.picking.EPickingType;

public class ScrollBar {

	private int minValue;
	private int maxValue;
	private int selection;
	private int pageSize;
	private EPickingType pickingType;
	private int id;

	public ScrollBar(int minValue, int maxValue, int selection, int pageSize, EPickingType pickingType, int id) {
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.selection = selection;
		this.pageSize = pageSize;
		this.pickingType = pickingType;
		this.id = id;
	}

	public int getMinValue() {
		return minValue;
	}

	public void setMinValue(int minValue) {
		this.minValue = minValue;
	}

	public int getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	public int getSelection() {
		return selection;
	}

	public void setSelection(int selection) {
		this.selection = selection;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public EPickingType getPickingType() {
		return pickingType;
	}

	public void setPickingType(EPickingType pickingType) {
		this.pickingType = pickingType;
	}

	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}

}
