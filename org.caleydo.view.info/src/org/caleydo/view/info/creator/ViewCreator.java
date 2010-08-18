package org.caleydo.view.info.creator;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.rcp.view.creator.ARCPViewCreator;
import org.caleydo.rcp.view.rcp.CaleydoRCPViewPart;
import org.caleydo.view.info.RcpInfoAreaView;
import org.caleydo.view.info.SerializedInfoView;
import org.caleydo.view.info.toolbar.InfoToolBarContent;

public class ViewCreator extends ARCPViewCreator {

	public ViewCreator(String viewType) {
		super(viewType);
	}

	@Override
	public ASerializedView createSerializedView() {

		return new SerializedInfoView();
	}

	@Override
	public Object createToolBarContent() {
		return new InfoToolBarContent();
	}

	@Override
	public CaleydoRCPViewPart createView(int parentContainerID, String label) {
		return new RcpInfoAreaView();
	}
}
