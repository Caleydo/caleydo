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

	/**
	 * If a toolbar is available for this view this method is overwritten in the concrete subcluss. There the
	 * toolbar object is returned.
	 */
	@Override
	public Object createToolBarContent() {

		// No toolbar available for this view
		return null;
	}
}
