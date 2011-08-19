package org.caleydo.core.gui.toolbar.action;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.data.loader.ResourceLoader;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class RestoreOriginalDataAction
	extends AToolBarAction {

	public static final String TEXT = "Restore original data";
	public static final String ICON = "resources/icons/general/restore.png";
	String recordPerspectiveID;

	/**
	 * Constructor.
	 */
	public RestoreOriginalDataAction(String recordPerspectiveID) {
		this.recordPerspectiveID = recordPerspectiveID;
		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), ICON)));
	}

	@Override
	public void run() {
		super.run();

		for (IDataDomain dataDomain : DataDomainManager.get().getDataDomains()) {
			if (dataDomain instanceof ATableBasedDataDomain)
				((ATableBasedDataDomain) dataDomain).resetRecordVA(recordPerspectiveID);
		}
	}
}
