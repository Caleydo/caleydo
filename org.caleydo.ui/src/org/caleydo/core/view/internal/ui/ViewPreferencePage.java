/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.internal.ui;

import org.caleydo.core.gui.util.ScaleFieldEditor2;
import org.caleydo.core.gui.util.SpinnerFieldEditor;
import org.caleydo.core.view.internal.Activator;
import org.caleydo.core.view.internal.MyPreferences;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.ScaleFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ViewPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public ViewPreferencePage() {
		super(GRID);
		setDescription("View Preferences");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI blocks needed to manipulate various
	 * types of preferences. Each field editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {
		final Composite parent = getFieldEditorParent();
		ScaleFieldEditor s = new ScaleFieldEditor2(MyPreferences.VIEW_ZOOM_FACTOR, "View scaling factor:", parent, 10,
				300, 10, 20, ScaleFieldEditor2.PERCENT_FORMATTER);
		addField(s);
		addField(new SpinnerFieldEditor(MyPreferences.FPS, "Target frames per seconds (FPS)", parent, 5, 60, 5, 10));
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("View Preferences.");
	}
}
