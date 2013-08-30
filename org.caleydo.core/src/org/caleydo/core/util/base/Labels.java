/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.util.base;

import java.util.Comparator;

import org.eclipse.jface.viewers.LabelProvider;

import com.google.common.base.Function;

/**
 * function utilities for labels
 *
 * @author Samuel Gratzl
 *
 */
public class Labels {
	/**
	 * comparator by case insensitivve label
	 */
	public static final Comparator<ILabelProvider> BY_LABEL = new Comparator<ILabelProvider>() {
		@Override
		public int compare(ILabelProvider dd1, ILabelProvider dd2) {
			return String.CASE_INSENSITIVE_ORDER.compare(dd1.getLabel(), dd2.getLabel());
		}
	};

	/**
	 * function to map to label
	 */
	public static final Function<ILabelProvider, String> TO_LABEL = new Function<ILabelProvider, String>() {
		@Override
		public String apply(ILabelProvider arg0) {
			return arg0 == null ? null : arg0.getLabel();
		}
	};

	/**
	 * swt label provider
	 */
	public static final LabelProvider PROVIDER = new LabelProvider() {
		@Override
		public String getText(Object element) {
			if (element instanceof ILabeled) {
				return ((ILabeled) element).getLabel();
			}
			return super.getText(element);
		}
	};
}
