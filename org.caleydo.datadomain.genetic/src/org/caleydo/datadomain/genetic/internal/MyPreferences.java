/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.genetic.internal;

import org.caleydo.datadomain.genetic.Activator;
import org.caleydo.datadomain.genetic.Organism;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * @author Samuel Gratzl
 *
 */
public class MyPreferences {
	private static final String LAST_CHOSEN_ORGANISM = "lastChosenOrganism";

	private static IPreferenceStore prefs() {
		return Activator.getDefault().getPreferenceStore();
	}
	public static Organism getLastChosenOrganism() {
		String v = prefs().getString(LAST_CHOSEN_ORGANISM);
		return Organism.MUS_MUSCULUS.name().equals(v) ? Organism.MUS_MUSCULUS : Organism.HOMO_SAPIENS;
	}

	public static void setLastChosenOrganism(Organism organism) {
		prefs().setValue(LAST_CHOSEN_ORGANISM, organism.name());
	}
}
