/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.treemap.preferences;

import org.caleydo.view.treemap.Activator;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * @author Samuel Gratzl
 *
 */
public class MyPreferences extends AbstractPreferenceInitializer {
	static final String TREEMAP_MAX_DEPTH = "treemapMaxDepth";
	static final String TREEMAP_LAYOUT_ALGORITHM = "treemapLayoutAlgorithm";
	static final String TREEMAP_DRAW_CLUSTER_FRAME = "treemapDrawClusterFrame";


	private static IPreferenceStore prefs() {
		return Activator.getDefault().getPreferenceStore();
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = prefs();
		store.setDefault(TREEMAP_DRAW_CLUSTER_FRAME, true);
		store.setDefault(TREEMAP_LAYOUT_ALGORITHM, 1);
		store.setDefault(TREEMAP_MAX_DEPTH, 0);
	}

	public static int getMapDepth() {
		return prefs().getInt(TREEMAP_MAX_DEPTH);
	}
	public static boolean isDrawClusterFrame() {
		return prefs().getBoolean(TREEMAP_DRAW_CLUSTER_FRAME);
	}
	public static int getLayoutAlgorithm() {
		return prefs().getInt(TREEMAP_LAYOUT_ALGORITHM);
	}

}
