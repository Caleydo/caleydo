/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.generic;

import org.caleydo.core.data.datadomain.IDataDomainInitialization;

/**
 * @author Alexander Lex
 * 
 */
public class GenericDataDomainInitialization implements IDataDomainInitialization {

	private static boolean isAlreadyInitialized = false;

	@Override
	public void createIDTypesAndMapping() {
		if (isAlreadyInitialized)
			return;

		isAlreadyInitialized = true;
		// nothing to do

	}

}
