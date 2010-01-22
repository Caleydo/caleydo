package org.caleydo.core.manager.view.creator;

import org.caleydo.core.serialize.ASerializedView;

public interface IViewCreator {

	public ASerializedView createSerializedView();

	public Object createToolBarContent();

	public String getViewType();
}
