package org.caleydo.rcp.view.rcp;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.core.manager.event.EventPublisher;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.IView;
import org.caleydo.rcp.startup.StartupProcessor;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * Base class for all RCP views available in Caleydo.
 * 
 * @author Marc Streit
 */
public abstract class CaleydoRCPViewPart
	extends ViewPart {

	/** serialized representation of the view to initialize the view itself */
	protected ASerializedView initSerializedView;

	protected String dataDomainType = null;

	protected static ArrayList<IAction> alToolbar;

	protected EventPublisher eventPublisher = null;

	protected IView view;

	/**
	 * stores the attach status of the viewpart, true means within caleydo's main window, false otherwise
	 */
	protected boolean attached;

	protected Composite parentComposite;

	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		eventPublisher = GeneralManager.get().getEventPublisher();
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
	 * <li>If no dataDomain is registered, null is returned</li>
	 * <li>If a dataDomainType is set in the serializable representation this is used</li>
	 * <li>Else if there is exactly one loaded dataDomain which the view can this is used</li>
	 * <li>Else an exception is thrown</li>
	 * <ul>
	 * 
	 * @param dataDomainBasedView
	 * @param serializedView
	 */
	protected String determineDataDomain(ASerializedView serializedView) {

		// first we check if the data domain was manually specified
		for (Pair<String, String> startView : StartupProcessor.get().getAppInitData()
			.getAppArgumentStartViewWithDataDomain()) {
			if (startView.getFirst().equals(serializedView.getViewID())) {
				dataDomainType = startView.getSecond();
				// StartupProcessor.get().getAppArgumentStartViewWithDataDomain().remove(startView);
				return dataDomainType;
			}
		}

		// then we check whether the serialization has a datadomain already
		String dataDomainType = serializedView.getDataDomainType();
		if (dataDomainType != null)
			return dataDomainType;
		else {
			ArrayList<IDataDomain> availableDomains =
				DataDomainManager.getInstance().getAssociationManager()
					.getAvailableDataDomainTypesForViewTypes(serializedView.getViewType());
			if (availableDomains == null)
				return null;
			else if (availableDomains.size() == 0)
				throw new IllegalStateException("No datadomain for this view loaded");
			else if (availableDomains.size() > 1)
				throw new IllegalStateException(
					"Not able to choose which data domain to use - not yet implemented");
			else
				return availableDomains.get(0).getDataDomainType();

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
