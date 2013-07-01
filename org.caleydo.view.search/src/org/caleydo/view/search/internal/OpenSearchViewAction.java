/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.search.internal;

import org.caleydo.core.gui.SimpleAction;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class OpenSearchViewAction extends SimpleAction {

	public OpenSearchViewAction() {
		super("Search", "resources/icons/general/search.png", Activator.getResourceLoader());
	}

	@Override
	public void run() {
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.showView(RcpSearchView.VIEW_TYPE);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
}
