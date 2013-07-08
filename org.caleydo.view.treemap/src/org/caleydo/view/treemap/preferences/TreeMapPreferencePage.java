/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.treemap.preferences;

import static org.caleydo.view.treemap.preferences.MyPreferences.TREEMAP_DRAW_CLUSTER_FRAME;
import static org.caleydo.view.treemap.preferences.MyPreferences.TREEMAP_LAYOUT_ALGORITHM;
import static org.caleydo.view.treemap.preferences.MyPreferences.TREEMAP_MAX_DEPTH;

import org.caleydo.view.treemap.Activator;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preference Page for Treemap appearance and behavior.
 * 
 * @author Michael Lafer
 * @author Oliver Pimas
 * @author Alexander Lex
 */
public class TreeMapPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	public TreeMapPreferencePage() {
		super(GRID);
	}

	/**
	 * Creates the gui components which are initialized with default values or
	 * from the pref store.
	 */
	@Override
	public void createFieldEditors() {

		final Composite parent = getFieldEditorParent();
		{
			String[][] labelAndValues = new String[2][2];
			labelAndValues[0][0] = "Simple Layout Algorithm";
			labelAndValues[0][1] = "0";
			labelAndValues[1][0] = "Squarified Treemap Layout Algorithm";
			labelAndValues[1][1] = "1";
			RadioGroupFieldEditor f = new RadioGroupFieldEditor(TREEMAP_LAYOUT_ALGORITHM, "The Algorithm used to layout the Treemap", 1, labelAndValues, parent);
			addField(f);
		}

		addField(new BooleanFieldEditor(TREEMAP_DRAW_CLUSTER_FRAME, "Draw frame for Clusters", parent));
		// addField(new )
		// int maxDepth = preferenceStore.getInt(PreferenceConstants.TREEMAP_MAX_DEPTH);
		// maxDepthButton = new Button(baseComposite, SWT.CHECK);
		// maxDepthButton.setSelection(maxDepth > 0);
		// maxDepthButton.setText("Enable node abstraction");
		// maxDepthButton.addSelectionListener(new SelectionListener() {
		//
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// maxDepthSp.setEnabled(maxDepthButton.getSelection());
		//
		// }
		//
		// @Override
		// public void widgetDefaultSelected(SelectionEvent e) {
		// // TODO Auto-generated method stub
		//
		// }
		// });

		final IntegerFieldEditor maxDepth = new IntegerFieldEditor(TREEMAP_MAX_DEPTH, "Node Abstraction", parent);
		maxDepth.setValidRange(0,999);
		addField(maxDepth);
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Set treemap appearance");
	}

}
