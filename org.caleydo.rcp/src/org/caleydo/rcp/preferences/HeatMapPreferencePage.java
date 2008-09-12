package org.caleydo.rcp.preferences;

import org.caleydo.core.manager.general.GeneralManager;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preference page for heat map specific settings
 * 
 * @author Alexander Lex
 */
public class HeatMapPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage
{

	public HeatMapPreferencePage()
	{
		super(GRID);
		// setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setPreferenceStore(GeneralManager.get().getPreferenceStore());
		setDescription("Set Preferences for the heat map view.");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors()
	{

	}

	@Override
	protected void performDefaults()
	{

	}

	@Override
	public void init(IWorkbench workbench)
	{

	}

}