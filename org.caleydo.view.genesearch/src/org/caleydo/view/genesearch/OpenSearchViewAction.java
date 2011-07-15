package org.caleydo.view.genesearch;

import org.caleydo.core.gui.toolbar.action.AToolBarAction;
import org.caleydo.data.loader.ResourceLoader;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class OpenSearchViewAction extends AToolBarAction {

	public static final String TEXT = "Search";
	public static final String ICON = "resources/icons/general/search.png";

	/**
	 * Constructor.
	 */
	public OpenSearchViewAction() {
		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(
				PlatformUI.getWorkbench().getDisplay(), ICON)));
	}

	@Override
	public void run() {
		super.run();

		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.showView(RcpGeneSearchView.VIEW_TYPE);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
}
