package org.caleydo.view.selectionbrowser.creator;

import java.util.ArrayList;

import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.rcp.view.creator.ARCPViewCreator;
import org.caleydo.rcp.view.rcp.CaleydoRCPViewPart;
import org.caleydo.view.selectionbrowser.RcpSelectionBrowserView;
import org.caleydo.view.selectionbrowser.SerializedSelectionBrowserView;
import org.caleydo.view.selectionbrowser.toolbar.SelectionbrowserToolBarContent;

public class ViewCreator extends ARCPViewCreator {

	public ViewCreator() {
		super(RcpSelectionBrowserView.VIEW_ID);
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

	@Override
	protected void registerDataDomains() {
		ArrayList<String> dataDomainTypes = new ArrayList<String>();

		// TODO: ADD THE POSSIBLE DATA DOMAINS FOR THIS VIEW
		// dataDomainTypes.add("org.caleydo.datadomain.genetic");

		DataDomainManager.getInstance().getAssociationManager()
				.registerDatadomainTypeViewTypeAssociation(dataDomainTypes, RcpSelectionBrowserView.VIEW_ID);
	}
}
