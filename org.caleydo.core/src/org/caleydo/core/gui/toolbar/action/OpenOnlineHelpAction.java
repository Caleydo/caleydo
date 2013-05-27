/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.gui.toolbar.action;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.link.LinkHandler;
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
		LinkHandler.openLink(url);
	}
}
