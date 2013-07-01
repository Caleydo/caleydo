/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.table.action;

import org.caleydo.view.table.Activator;
import org.caleydo.view.table.TableView;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;

/**
 * switch between show me just the selected elements and everything
 *
 * @author Samuel Gratzl
 *
 */
public class SelectionOnlyAction extends Action {
	private final TableView view;

	public SelectionOnlyAction(TableView view) {
		super("Shown only the selected items", AS_CHECK_BOX);
		setToolTipText("Shown only the selected items");
		setImageDescriptor(Activator.getResourceLoader().getImageDescriptor(Display.getCurrent(),
				"resources/icons/table_select_big.png"));
		this.view = view;
	}

	@Override
	public void run() {
		view.setSelectionOnly(isChecked());
	}
}
