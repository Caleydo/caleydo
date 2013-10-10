/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.internal.prefs;

import java.util.ArrayList;
import java.util.Collection;

import org.caleydo.vis.lineup.internal.Activator;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
/**
 * @author Samuel Gratzl
 *
 */
public class MyPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private Collection<Label> labels = new ArrayList<>();

	public MyPreferencePage() {
		super(GRID);
	}

	@Override
	public void createFieldEditors() {
		final Composite parent = getFieldEditorParent();

		createLabel("Transitions", parent);
		addField(new ColorFieldEditor("transitions.color.up", "Rank Increase", parent));
		addField(new ColorFieldEditor("transitions.color.down", "Rank Decrease", parent));
	}

	private void createLabel(String label, final Composite parent) {
		Label l = new Label(parent, SWT.NONE);
		l.setText(label);
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		labels.add(l);
	}

	@Override
	protected void adjustGridLayout() {
		super.adjustGridLayout();
		int cols = ((GridLayout) (getFieldEditorParent().getLayout())).numColumns;
		for (Label label : labels)
			((GridData) label.getLayoutData()).horizontalSpan = cols;
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("LineUp View settings");
	}
}