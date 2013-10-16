/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.pathway.manager;


/**
 * Class that holds information about a specific pathway database.
 *
 * @author Marc Streit
 */
public final class PathwayDatabase {

	private final EPathwayDatabaseType type;

	/**
	 * Constructor.
	 */
	public PathwayDatabase(EPathwayDatabaseType type) {
		this.type = type;
	}

	public EPathwayDatabaseType getType() {
		return type;
	}

	public String getName() {
		return type.getName();
	}

	public String getURL() {
		return type.getURL();
	}
}
