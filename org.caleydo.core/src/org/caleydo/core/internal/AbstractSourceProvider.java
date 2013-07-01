/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
