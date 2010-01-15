package org.caleydo.core.manager.event;

import org.caleydo.core.view.opengl.canvas.AGLView;

public class SetMinViewSizeEvent
	extends AEvent {

	private int minHeight;
	private int minWidth;
	private AGLView view;

	public SetMinViewSizeEvent() {
		minHeight = -1;
		minWidth = -1;
		view = null;
	}

	@Override
	public boolean checkIntegrity() {
		if ((minHeight == -1) || (minWidth == -1) || (view == null))
			throw new IllegalStateException("selectionDelta was not set");
		return true;
	}

	public int getMinHeight() {
		return minHeight;
	}

	public void setMinHeight(int minHeight) {
		this.minHeight = minHeight;
	}

	public int getMinWidth() {
		return minWidth;
	}

	public void setMinWidth(int minWidth) {
		this.minWidth = minWidth;
	}

	public void setMinViewSize(int minWidth, int minHeight) {
		this.minWidth = minWidth;
		this.minHeight = minHeight;
	}

	public AGLView getView() {
		return view;
	}

	public void setView(AGLView view) {
		this.view = view;
	}

}
