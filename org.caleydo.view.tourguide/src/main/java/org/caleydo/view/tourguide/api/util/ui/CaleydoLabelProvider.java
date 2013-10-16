/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.api.util.ui;

import org.caleydo.core.util.base.ILabeled;
import org.eclipse.jface.viewers.LabelProvider;

/**
 * {@link LabelProvider} for a {@link ILabeled} element
 * 
 * @author Samuel Gratzl
 * 
 */
public class CaleydoLabelProvider extends org.eclipse.jface.viewers.LabelProvider {
	@Override
	public String getText(Object element) {
		if (element instanceof ILabeled) {
			return ((ILabeled) element).getLabel();
		}
		return super.getText(element);
	}
}

