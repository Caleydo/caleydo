package org.caleydo.rcp.action.toolbar.general;

import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.action.toolbar.AToolBarAction;
import org.caleydo.rcp.views.swt.SearchView;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class OpenSearchViewAction
	extends AToolBarAction {
	public static final String TEXT = "Search";
	public static final String ICON = "resources/icons/general/search.png";

	/**
	 * Constructor.
	 */
	public OpenSearchViewAction() {
		super(-1);

		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), ICON)));
	}

	@Override
	public void run() {
		super.run();

		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(SearchView.ID);
		}
		catch (PartInitException e) {
			e.printStackTrace();
		}
	}
}
