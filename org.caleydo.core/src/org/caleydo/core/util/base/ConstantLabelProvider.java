/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.base;

/**
 * @author Samuel Gratzl
 *
 */
public final class ConstantLabelProvider implements ILabelProvider {
	private final String label;

	public ConstantLabelProvider(String label) {
		this.label = label;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.util.base.ILabelProvider#getLabel()
	 */
	@Override
	public String getLabel() {
		return label;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.util.base.ILabelProvider#getProviderName()
	 */
	@Override
	public String getProviderName() {
		return "ConstantLabelProvider: " + label;
	}

}
