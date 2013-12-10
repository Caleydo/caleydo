/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.idbrowser.internal.model;

import java.util.Set;

import org.caleydo.core.id.IDType;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.vis.lineup.model.IRow;

/**
 * a {@link IRow} for an idtype entry
 *
 * @author Samuel Gratzl
 *
 */
public interface IIDRow extends IRow, ILabeled {
	/**
	 * @param idType
	 * @return
	 */
	Set<Object> get(IDType idType);

	String getAsString(IDType idType, String default_);
}
