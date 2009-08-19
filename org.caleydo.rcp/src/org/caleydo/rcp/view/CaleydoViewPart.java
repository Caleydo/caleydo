package org.caleydo.rcp.view;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * TODO document
 * 
 * @author Marc Streit
 */
public abstract class CaleydoViewPart
	extends ViewPart {
	protected static ArrayList<IAction> alToolbar;
	// protected static ArrayList<IContributionItem> alToolbarContributions;

	protected IGeneralManager generalManager = null;
	protected IEventPublisher eventPublisher = null; 

	protected int iViewID;

	/** stores the attach status of the viewpart, true means within caleydo's main window, false otherwise */
	protected boolean attached;
	
	protected Composite parentComposite;

	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		generalManager = GeneralManager.get();
		eventPublisher = generalManager.getEventPublisher();
		registerEventListeners();
	}
	
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
		return parentComposite;
	}

	public boolean isAttached() {
		return attached;
	}

	public void setAttached(boolean attached) {
		this.attached = attached;
	}

	@Override
	public void dispose() {
		unregisterEventListeners();
		super.dispose();
	}
	
	public void registerEventListeners() {
		// no registration to the event system in the default implementation 
	}

	public void unregisterEventListeners() {
		// no registration to the event system in the default implementation 
	}
}
