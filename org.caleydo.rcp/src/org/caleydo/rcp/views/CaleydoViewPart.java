package org.caleydo.rcp.views;

import java.util.ArrayList;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public abstract class CaleydoViewPart
	extends ViewPart {
	protected static ArrayList<IAction> alToolbar;
	// protected static ArrayList<IContributionItem> alToolbarContributions;

	protected int iViewID;

	protected Composite swtComposite;

	public int getViewID() {
		return iViewID;
	}

	public Composite getSWTComposite() {
		return swtComposite;
	}
}
