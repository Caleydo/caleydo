package org.caleydo.rcp.view.rcp;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.manager.IDataDomain;
import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.datadomain.IDataDomainBasedView;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.IView;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * TODO document
 * 
 * @author Marc Streit
 */
public abstract class CaleydoRCPViewPart
	extends ViewPart {

	/** serialized representation of the view to initialize the view itself */
	protected ASerializedView initSerializedView;

	protected String dataDomainType = null;

	protected static ArrayList<IAction> alToolbar;
	// protected static ArrayList<IContributionItem> alToolbarContributions;

	protected IGeneralManager generalManager = null;
	protected IEventPublisher eventPublisher = null;

	protected IView view;

	/**
	 * stores the attach status of the viewpart, true means within caleydo's main window, false otherwise
	 */
	protected boolean attached;

	protected Composite parentComposite;

	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		generalManager = GeneralManager.get();
		eventPublisher = generalManager.getEventPublisher();
	}

	/**
	 * Generates and returns a list of all views, caleydo-view-parts and gl-views, contained in this view.
	 * 
	 * @return list of all views contained in this view
	 */
	public List<IView> getAllViews() {
		List<IView> views = new ArrayList<IView>();
		views.add(getView());
		return views;
	}

	public IView getView() {
		return view;
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
		// unregisterEventListeners();
		super.dispose();
	}

	/**
	 * Determines and sets the dataDomain based on the following rules:
	 * <ul>
	 * <li>If a dataDomainType is set in the serializable representation this is used</li>
	 * <li>Else if there is exactly one loaded dataDomain which the view can this is used</li>
	 * <li>Else an exception is thrown</li>
	 * <ul>
	 * 
	 * @param dataDomainBasedView
	 * @param serializedView
	 */
	protected void determineDataDomain(IDataDomainBasedView<IDataDomain> dataDomainBasedView,
		ASerializedView serializedView) {
		if (dataDomainBasedView instanceof IDataDomainBasedView<?>) {
			String dataDomainType = serializedView.getDataDomainType();
			IDataDomain dataDomain = null;
			if (dataDomainType == null) {
				ArrayList<IDataDomain> availableDomains =
					DataDomainManager.getInstance().getAvailableDataDomainTypesForViewTypes(
						serializedView.getViewType());
				if (availableDomains.size() == 0)
					throw new IllegalStateException("No datadomain for this view loaded");
				else if (availableDomains.size() > 1)
					throw new IllegalStateException(
						"Not able to choose which data domain to use - not yet implemented");
				else
					dataDomain = availableDomains.get(0);

			}
			else {
				dataDomain =
					DataDomainManager.getInstance().getDataDomain(serializedView.getDataDomainType());
			}
			dataDomainBasedView.setDataDomain(dataDomain);
		}
	}

	// public void registerEventListeners() {
	// // no registration to the event system in the default implementation
	// }
	//
	// public void unregisterEventListeners() {
	// // no registration to the event system in the default implementation
	// }
}
