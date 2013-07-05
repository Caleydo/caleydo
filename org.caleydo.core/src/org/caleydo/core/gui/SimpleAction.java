/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.gui;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.data.loader.ResourceLoader;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.PlatformUI;

/**
 * common simplification for an action
 *
 * @author Samuel Gratzl
 *
 */
public abstract class SimpleAction extends Action {

	public SimpleAction(String label, String iconResource) {
		this(label, iconResource, GeneralManager.get().getResourceLoader());
	}

	public SimpleAction(String label, String iconResource, ResourceLoader loader) {
		super(label, loader.getImageDescriptor(PlatformUI.getWorkbench().getDisplay(), iconResource));
		setToolTipText(label);
	}
}
