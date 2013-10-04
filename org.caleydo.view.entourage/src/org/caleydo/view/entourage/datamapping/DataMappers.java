/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.entourage.datamapping;

import org.caleydo.core.util.ExtensionUtils;

/**
 * @author Christian
 *
 */
public final class DataMappers {

	private static final String EXTENSION_ID = "org.caleydo.view.entourage.datamapper";

	private DataMappers() {

	}

	public static final IDataMapper getDataMapper() {
		return ExtensionUtils.findFirstImplementation(EXTENSION_ID, "class", IDataMapper.class);
	}

}
