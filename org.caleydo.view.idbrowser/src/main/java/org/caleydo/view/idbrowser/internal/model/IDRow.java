/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.idbrowser.internal.model;

import java.util.Objects;

import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.vis.lineup.model.ARow;

/**
 * @author Samuel Gratzl
 *
 */
public class IDRow extends ARow implements ILabeled {
	private final IDType idType;
	private final Object id;

	public IDRow(IDType idType, Object id) {
		this.idType = idType;
		this.id = id;
	}

	@Override
	public String getLabel() {
		return Objects.toString(id);
	}

	public IDCategory getCategory() {
		return idType.getIDCategory();
	}

	/**
	 * @return the id, see {@link #id}
	 */
	public Object getId() {
		return id;
	}

	/**
	 * @return the idType, see {@link #idType}
	 */
	public IDType getIdType() {
		return idType;
	}
}
