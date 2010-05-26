package org.caleydo.rcp.action.toolbar.general;

import org.caleydo.core.manager.IDataDomain;
import org.caleydo.core.manager.general.GeneralManager;
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

		for (IDataDomain useCase : GeneralManager.get().getAllUseCases()) {
			useCase.restoreOriginalContentVA();
		}
	}
}
