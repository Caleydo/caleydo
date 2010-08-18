package org.caleydo.view.selectionbrowser.creator;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.rcp.view.creator.ARCPViewCreator;
import org.caleydo.rcp.view.rcp.CaleydoRCPViewPart;
import org.caleydo.view.selectionbrowser.RcpSelectionBrowserView;
import org.caleydo.view.selectionbrowser.SerializedSelectionBrowserView;
import org.caleydo.view.selectionbrowser.toolbar.SelectionbrowserToolBarContent;

public class ViewCreator extends ARCPViewCreator {

	public ViewCreator(String viewType) {
		super(viewType);
	}

	@Override
	public ASerializedView createSerializedView() {

		return new SerializedSelectionBrowserView();
	}

	@Override
	public Object createToolBarContent() {
		return new SelectionbrowserToolBarContent();
	}

	@Override
	public CaleydoRCPViewPart createView(int parentContainerID, String label) {
		return new RcpSelectionBrowserView();
	}
}
