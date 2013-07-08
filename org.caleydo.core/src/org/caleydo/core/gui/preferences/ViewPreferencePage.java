/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.gui.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ViewPreferencePage
	extends PreferencePage
	implements IWorkbenchPreferencePage {

	public ViewPreferencePage() {

	}

	public ViewPreferencePage(String title) {
		super(title);
	}

	public ViewPreferencePage(String title, ImageDescriptor image) {
		super(title, image);
	}

	@Override
	public void init(IWorkbench workbench) {
		setDescription("View Specific Preferences");
	}

	@Override
	protected Control createContents(Composite parent) {
		return new Composite(parent, SWT.NULL);
	}
}
