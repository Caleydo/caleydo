package org.caleydo.view.base.action.toolbar;

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
