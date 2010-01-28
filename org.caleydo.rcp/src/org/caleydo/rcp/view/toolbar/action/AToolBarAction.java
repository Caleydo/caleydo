package org.caleydo.rcp.view.toolbar.action;

import org.eclipse.jface.action.Action;

public class AToolBarAction extends Action {
	protected int iViewID;

	/**
	 * Constructor.
	 */
	public AToolBarAction(int iViewID) {
		this.iViewID = iViewID;
	}

}
