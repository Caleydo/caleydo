/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.api.state;

import org.caleydo.core.util.base.ILabeled;

/**
 * transition between to {@link IState}
 * 
 * to be more precise the outgoing Transition and if the chooses it {@link #apply(IReactions)} will be performed which
 * may switch to another state
 * 
 * @author Samuel Gratzl
 * 
 */
public interface ITransition extends ILabeled {
	void apply(IReactions onApply);
}
