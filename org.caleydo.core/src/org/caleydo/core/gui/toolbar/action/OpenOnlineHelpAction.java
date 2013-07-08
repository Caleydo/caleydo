/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.gui.toolbar.action;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.system.BrowserUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.ui.PlatformUI;

public class OpenOnlineHelpAction extends Action {

	private static final String LABEL = "Help";
	private static final String ICON = "resources/icons/general/help_16.png";

	private String url;

	public OpenOnlineHelpAction(String url, boolean useSmallIcon) {
		this.url = url;
		setText(LABEL);
		setToolTipText(LABEL);
		setImageDescriptor(ImageDescriptor.createFromImage(useSmallIcon ? GeneralManager.get().getResourceLoader()
				.getImage(PlatformUI
				.getWorkbench().getDisplay(), ICON) : JFaceResources.getImage(Dialog.DLG_IMG_HELP)));
		setChecked(false);
	}

	@Override
	public void run() {
		super.run();
		setChecked(false);
		BrowserUtils.openURL(url);
	}
}
