package org.caleydo.view.browser.creator;

import org.caleydo.core.manager.view.creator.ASWTViewCreator;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.AView;
import org.caleydo.view.browser.GenomeHTMLBrowser;
import org.caleydo.view.browser.SerializedHTMLBrowserView;

public class ViewCreator extends ASWTViewCreator {

	public ViewCreator(String viewType) {
		super(viewType);
	}

	@Override
	public AView createView(int parentContainerID, String label) {

		return new GenomeHTMLBrowser(parentContainerID, label);
	}

	@Override
	public ASerializedView createSerializedView() {

		return new SerializedHTMLBrowserView();
	}
}
