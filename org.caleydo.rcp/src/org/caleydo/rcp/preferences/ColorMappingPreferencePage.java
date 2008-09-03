package org.caleydo.rcp.preferences;

import org.caleydo.rcp.Activator;
import org.eclipse.core.internal.preferences.EclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class ColorMappingPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage
{

	public ColorMappingPreferencePage()
	{
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Set color mapping for different use cases");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors()
	{
		String[][] sar = { { "1", "1" }, { "2", "2" }, { "3", "3" } };
		ComboFieldEditor numColorPointsCombo = new ComboFieldEditor("NumberOfColorPoints",
				"Number of Color Points", sar, getFieldEditorParent());

		addField(numColorPointsCombo);

		addField(new ColorFieldEditor("Name", "label", getFieldEditorParent()));
		Label firstColorSelectorLabel = new Label(getFieldEditorParent(), 0);
		firstColorSelectorLabel.setText("First Color:");
		ColorSelector myFirstColorSelector = new org.eclipse.jface.preference.ColorSelector(
				getFieldEditorParent());
		myFirstColorSelector.setEnabled(true);
		
//		IEclipsePreferences pref = new EclipsePreferences();
//		pref.getNode("firstStart");

		Label secondColorSelectorLabel = new Label(getFieldEditorParent(), 0);
		secondColorSelectorLabel.setText("Second Color:");
		ColorSelector mySecondColorSelector = new org.eclipse.jface.preference.ColorSelector(
				getFieldEditorParent());
		mySecondColorSelector.setEnabled(true);

		// addField(new DirectoryFieldEditor(PreferenceConstants.P_PATH,
		// "&Directory preference:", getFieldEditorParent()));
		// addField(new BooleanFieldEditor(PreferenceConstants.P_BOOLEAN,
		// "&An example of a boolean preference", getFieldEditorParent()));
		//
		// addField(new RadioGroupFieldEditor(PreferenceConstants.P_CHOICE,
		// "An example of a multiple-choice preference", 1, new String[][] {
		// { "&Choice 1", "choice1" }, { "C&hoice 2", "choice2" } },
		// getFieldEditorParent()));

		// new ColorSelector(getFieldEditorParent())
	}

	@Override
	public void init(IWorkbench workbench)
	{
	}

}