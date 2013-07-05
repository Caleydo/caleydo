/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.info.selection.external;

import org.caleydo.view.info.selection.Activator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

/**
 * @author Samuel Gratzl
 *
 */
public class OpenExternalWrappingAction extends Action {
	private IAction wrappee = null;

	public OpenExternalWrappingAction() {
		super("Search In ...", Activator.getImageDescriptor("resources/icons/external.png"));
		setEnabled(false);
	}

	/**
	 * @param wrappee
	 *            setter, see {@link wrappee}
	 */
	public void setWrappee(IAction wrappee) {
		this.wrappee = wrappee;
		setEnabled(wrappee != null);
		setText(wrappee == null ? "Search In ..." : wrappee.getText());
	}

	/**
	 * @return the wrappee, see {@link #wrappee}
	 */
	public IAction getWrappee() {
		return wrappee;
	}

	@Override
	public void run() {
		if (wrappee != null)
			wrappee.run();
	}
}

