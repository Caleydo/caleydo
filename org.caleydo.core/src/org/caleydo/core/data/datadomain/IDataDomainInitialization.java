/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.datadomain;

import org.caleydo.core.id.IDType;

/**
 * Extension point interface class that triggers the initialization of data
 * domains. The initialization concerns the creation of ID types and ID
 * mappings. Note that this method is intended for initialization of the data
 * domain in general. It does not create an data domain instance.
 * 
 * @author Marc Streit
 */
public interface IDataDomainInitialization {

	/**
	 * Initialization of any {@link IDType}s and ID mapping tables that are
	 * required for a data domain.
	 */
	public void createIDTypesAndMapping();

}
