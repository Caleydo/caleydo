package org.caleydo.rcp.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public abstract class CaleydoViewPart
	extends ViewPart {
	protected static ArrayList<IAction> alToolbar;
	// protected static ArrayList<IContributionItem> alToolbarContributions;

	protected int iViewID;

	protected Composite swtComposite;

	/**
	 * Generates and returns a list of all view-ids, caleydo-view-part-ids and gl-view-ids, 
	 * contained in this view. 
	 * @return list of all view-ids contained in this view 
	 */
	public List<Integer> getAllViewIDs() {
		List<Integer> ids = new ArrayList<Integer>();
		ids.add(getViewID());
		return ids;
	}
	
	public int getViewID() {
		return iViewID;
	}

	public Composite getSWTComposite() {
		return swtComposite;
	}
}
