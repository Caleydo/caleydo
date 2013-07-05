/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.api.state;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.collection.Pair;

import com.google.common.base.Predicate;

/**
 * basic state for triggering to select a group in Stratomex
 *
 * @author Samuel Gratzl
 *
 */
public interface ISelectGroupState extends IState, Predicate<Pair<TablePerspective, Group>> {
	/**
	 * called when the user selects a stratification and its corresponding group
	 *
	 * @param tablePerspective
	 * @param group maybe null for all
	 * @param reaction
	 */
	void select(TablePerspective tablePerspective, Group group, IReactions reaction);

	/**
	 * whether select all groups is supported
	 * 
	 * @return
	 */
	boolean isSelectAllSupported();
}
