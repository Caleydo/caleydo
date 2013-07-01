/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.base;

import java.util.Comparator;


/**
 * default implementation of an {@link ILabelProvider}
 *
 * @author Samuel Gratzl
 *
 */
public class DefaultLabelProvider implements ILabelHolder {
	private String label;

	public DefaultLabelProvider() {
		label = "";
	}

	public DefaultLabelProvider(String label) {
		this.label = label;
	}

	@Override
	public final String getLabel() {
		return label;
	}

	@Override
	public final void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String getProviderName() {
		return null;
	}


	public static final Comparator<ILabelProvider> BY_LABEL = new Comparator<ILabelProvider>() {
		@Override
		public int compare(ILabelProvider dd1, ILabelProvider dd2) {
			return String.CASE_INSENSITIVE_ORDER.compare(dd1.getLabel(),dd2.getLabel());
		}
	};
}
