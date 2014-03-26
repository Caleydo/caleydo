/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.api.state;

import org.caleydo.core.util.base.ILabeled;

/**
 * definition of a state in the {@link IStateMachine}
 *
 * @author Samuel Gratzl
 *
 */
public interface IState extends ILabeled {

	/**
	 * called when the state will be entered
	 */
	void onEnter();

	/**
	 * called when the state will be leaved
	 */
	void onLeave();

	@Override
	boolean equals(Object obj);
}
