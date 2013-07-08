/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.api.state;


/**
 * simple implementation of an {@link ITransition}
 * 
 * @author Samuel Gratzl
 * 
 */

public class SimpleTransition implements ITransition {
	private final IState target;
	private final String label;

	public SimpleTransition(IState target, String label) {
		this.target = target;
		this.label = label;
	}

	/**
	 * @return the target, see {@link #target}
	 */
	public IState getTarget() {
		return target;
	}

	@Override
	public void apply(IReactions onApply) {
		onApply.switchTo(target);
	}

	/**
	 * @return the label, see {@link #label}
	 */
	@Override
	public String getLabel() {
		return label;
	}
}
