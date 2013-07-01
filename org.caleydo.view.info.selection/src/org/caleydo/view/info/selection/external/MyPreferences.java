/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.info.selection.external;

import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.datadomain.genetic.EGeneIDTypes;
import org.caleydo.view.info.selection.Activator;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * @author Samuel Gratzl
 *
 */
public class MyPreferences extends AbstractPreferenceInitializer {

	private static IPreferenceStore prefs() {
		return Activator.getDefault().getPreferenceStore();
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = prefs();
		store.setDefault("external.idcategory.GENE.pattern",
				"http://www.genecards.org/index.php?path=/Search/keyword/{0}");
		store.setDefault("external.idcategory.GENE.idType", EGeneIDTypes.GENE_SYMBOL.name());
		store.setDefault("external.idcategory.GENE.label", "GeneCards");
	}

	static OpenExternally getExternalIDCategory(IDCategory category) {
		return getExternalIDCategory(prefs(), category, false);
	}

	static OpenExternally getExternalIDCategory(IPreferenceStore prefs, IDCategory category, boolean defaultValue) {

		final String prefix = "external.idcategory." + category + ".";

		String pattern = defaultValue ? prefs.getDefaultString(prefix + "pattern") : prefs
				.getString(prefix + "pattern");
		IDType type = IDType.getIDType(defaultValue ? prefs.getDefaultString(prefix + "idType") : prefs
				.getString(prefix + "idType"));
		String label = defaultValue ? prefs.getDefaultString(prefix + "label") : prefs.getString(prefix + "label");

		if (type == null || pattern == null || pattern.isEmpty())
			return null;
		return new OpenExternally(pattern, label, type);
	}

	public static class OpenExternally {
		private final String pattern;
		private final String label;
		private final IDType idType;

		public OpenExternally(String pattern, String label, IDType idType) {
			this.pattern = pattern;
			this.label = label;
			this.idType = idType;
		}

		/**
		 * @return the label, see {@link #label}
		 */
		public String getLabel() {
			return label;
		}

		/**
		 * @return the idType, see {@link #idType}
		 */
		public IDType getIdType() {
			return idType;
		}

		/**
		 * @return the pattern, see {@link #pattern}
		 */
		public String getPattern() {
			return pattern;
		}
	}
}
