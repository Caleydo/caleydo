package org.caleydo.core.manager.view.creator;

import org.caleydo.core.serialize.ASerializedView;

public abstract class AViewCreator
	implements IViewCreator {

	private String viewType;

	public AViewCreator(String viewType) {
		this.viewType = viewType;
	}

	@Override
	public abstract ASerializedView createSerializedView();

	@Override
	public String getViewType() {
		return viewType;
	}
}
