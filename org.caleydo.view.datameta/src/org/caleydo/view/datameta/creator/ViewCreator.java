package org.caleydo.view.datameta.creator;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.rcp.view.creator.ARCPViewCreator;
import org.caleydo.rcp.view.rcp.CaleydoRCPViewPart;
import org.caleydo.view.datameta.RcpDataMetaView;
import org.caleydo.view.datameta.SerializedDataMetaView;

public class ViewCreator extends ARCPViewCreator {

	public ViewCreator(String viewType) {
		super(viewType);
	}

	@Override
	public CaleydoRCPViewPart createView(int parentContainerID, String label) {
		return new RcpDataMetaView();
	}

	@Override
	public ASerializedView createSerializedView() {
		return new SerializedDataMetaView();
	}

}
