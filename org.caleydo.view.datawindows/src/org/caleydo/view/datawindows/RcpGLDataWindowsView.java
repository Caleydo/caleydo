package org.caleydo.view.datawindows;

import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.rcp.Application;
import org.caleydo.rcp.view.rcp.ARcpGLViewPart;
import org.eclipse.swt.widgets.Composite;

public class RcpGLDataWindowsView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLDataWindowsView() {
		super();
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		createGLCanvas();
		createGLView(initSerializedView, glCanvas.getID());
	}

	@Override
	public ASerializedView createDefaultSerializedView() {
		
		// Find data domain for this view
		for (Pair<String, String> startView : Application.startViewWithDataDomain) {
			if (startView.getFirst().equals(this.getViewGUIID())) {
				dataDomain = DataDomainManager.getInstance().getDataDomain(startView.getSecond());
				Application.startViewWithDataDomain.remove(startView);
				break;
			}
		}
		
		if (dataDomain == null)
			throw new IllegalStateException("Data domain is not set for new view "+this.getViewGUIID());
		
		SerializedDataWindowsView serializedView = new SerializedDataWindowsView(dataDomain.getDataDomainType());
		return serializedView;
	}

	@Override
	public String getViewGUIID() {
		return GLDataWindows.VIEW_ID;
	}

}