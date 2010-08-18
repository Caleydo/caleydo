package org.caleydo.rcp.action.toolbar.general;

import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.core.manager.datadomain.ISetBasedDataDomain;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.view.toolbar.action.AToolBarAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class RestoreOriginalDataAction
	extends AToolBarAction {
	public static final String TEXT = "Restore original data";
	public static final String ICON = "resources/icons/general/save.png";

	/**
	 * Constructor.
	 */
	public RestoreOriginalDataAction() {
		super(-1);

		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), ICON)));
	}

	@Override
	public void run() {
		super.run();

		for (IDataDomain dataDomain : DataDomainManager.getInstance().getDataDomains()) {
			if (dataDomain instanceof ISetBasedDataDomain)
				((ISetBasedDataDomain)dataDomain).restoreOriginalContentVA();
		}
	}
}
