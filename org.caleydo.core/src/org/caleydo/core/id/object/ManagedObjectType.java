/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.id.object;

/**
 * Types of managed objects
 *
 * @author Marc Streit
 * @author Alexander Lex
 * @deprecated should be phased out / Alex
 */
@Deprecated
public enum ManagedObjectType {

	GL_VIEW(29),

	PATHWAY(58),
	PATHWAY_VERTEX(60),
	PATHWAY_VERTEX_REP(61),

	DIMENSION_GROUP_SPACER(70);

	private final int idPrefix;

	/**
	 * Constructor.
	 */
	private ManagedObjectType(final int iIdPrefix) {
		this.idPrefix = iIdPrefix;
	}

	public int getIdPrefix() {
		return idPrefix;
	}
}
