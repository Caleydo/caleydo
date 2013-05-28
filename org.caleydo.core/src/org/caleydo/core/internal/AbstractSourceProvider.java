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
package org.caleydo.core.internal;

import java.util.HashMap;
import java.util.Map;

import org.caleydo.core.manager.GeneralManager;

public class AbstractSourceProvider
	extends org.eclipse.ui.AbstractSourceProvider {

	public final static String RELEASE_STATE = "org.caleydo.core.isReleaseState";

	private final static String RELEASE_VERSION = "releaseVersion";
	private final static String FULL_VERSION = "fullVersion";

	@Override
	public void dispose() {
	}

	@Override
	public Map<String, String> getCurrentState() {
		Map<String, String> currentState = new HashMap<String, String>(1);
		String currentStateTmp = GeneralManager.RELEASE_MODE ? FULL_VERSION : RELEASE_VERSION;
		currentState.put(RELEASE_STATE, currentStateTmp);
		return currentState;
	}

	@Override
	public String[] getProvidedSourceNames() {
		return new String[] { RELEASE_STATE };
	}

}
