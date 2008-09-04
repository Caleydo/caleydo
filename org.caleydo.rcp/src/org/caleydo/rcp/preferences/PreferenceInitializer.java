package org.caleydo.rcp.preferences;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.rcp.Activator;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;

import com.sun.gluegen.runtime.CPU;

import static org.caleydo.rcp.preferences.PreferenceConstants.*;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer
	extends AbstractPreferenceInitializer
{

	@Override
	public void initializeDefaultPreferences()
	{
		
		IPreferenceStore store = GeneralManager.get().getPreferenceStore();
		store.setDefault(PreferenceConstants.P_BOOLEAN, true);
		store.setDefault(PreferenceConstants.P_CHOICE, "choice2");
		store.setDefault(PreferenceConstants.P_STRING, "Default value");

		
		store.setDefault(COLOR_MARKER_POINT_VALUE + "1", 0.0f);
		store.setDefault(COLOR_MARKER_POINT_VALUE + "2", 0.2f);
		store.setDefault(COLOR_MARKER_POINT_VALUE + "3", 1.0f);	
		store.setDefault(COLOR_MARKER_POINT_COLOR + "1", "255,126,0");
		store.setDefault(COLOR_MARKER_POINT_COLOR + "2", "0,255,0");
		store.setDefault(COLOR_MARKER_POINT_COLOR + "3", "0,0,255");
		

		
		store.setDefault(NUMBER_OF_COLOR_MARKER_POINTS, 3);
	}
}
