/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.api.state;

import org.caleydo.core.data.perspective.table.TablePerspective;

import com.google.common.base.Predicate;

/**
 * special kind of a state that triggers that the user is able to select a {@link TablePerspective} stratification
 * 
 * @author Samuel Gratzl
 * 
 */
public interface ISelectStratificationState extends IState,Predicate<TablePerspective> {
	/**
	 * called when the users selects a stratification
	 * 
	 * @param tablePerspective
	 * @param reactions
	 */
	void select(TablePerspective tablePerspective, IReactions reactions);

	/**
	 * @return whether automatically the left one of me should be selected, see #1202
	 */
	boolean isAutoSelect();
}

