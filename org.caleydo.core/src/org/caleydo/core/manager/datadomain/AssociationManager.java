package org.caleydo.core.manager.datadomain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.caleydo.core.util.collection.MultiHashMap;

/**
 * This manager maps views to dataDomains, thereby signaling which view can use which dataDomain. The
 * {@link DataDomainManager} holds one instance where all possible associations have to be registered. It is
 * legal however, to hold one's own AssociationManager that for example only has a subset of views.
 * 
 * @author Alexander Lex
 */
public class AssociationManager {

	/** maps a dataDomain to multiple views */
	private MultiHashMap<String, String> dataDomainViewAssociations;
	/** maps a view to multiple datadomains */
	private MultiHashMap<String, String> viewDataDomainAssociations;

	public AssociationManager() {
		dataDomainViewAssociations = new MultiHashMap<String, String>();
		viewDataDomainAssociations = new MultiHashMap<String, String>();
	}

	/**
	 * register a association of a dataDomainType to a viewType, thereby indicating the principal
	 * compatibility of those components. This registration can be done from any given place. Most commonly,
	 * either a view specifies its datadomains or a datadomain specifies its views.
	 * 
	 * @param dataDomainType
	 *            the plugin name of the datadomain, typically org.caleydo.datadomain.*
	 * @param viewType
	 *            the plugin name of the view, typically org.caleydo.view.*
	 */
	public void registerDatadomainTypeViewTypeAssociation(String dataDomainType, String viewType) {
		dataDomainViewAssociations.put(dataDomainType, viewType);
		viewDataDomainAssociations.put(viewType, dataDomainType);
	}

	/**
	 * Wrapper for {@link #registerDatadomainTypeViewTypeAssociation(String, String)} that uses a collection
	 * for view types
	 * 
	 * @param dataDomainType
	 * @param viewTypes
	 */
	public void registerDatadomainTypeViewTypeAssociation(String dataDomainType, Collection<String> viewTypes) {
		for (String viewType : viewTypes)
			registerDatadomainTypeViewTypeAssociation(dataDomainType, viewType);

	}

	/**
	 * Wrapper for {@link #registerDatadomainTypeViewTypeAssociation(String, String)} that uses a collection
	 * for dataDomain types
	 * 
	 * @param dataDomainTypes
	 * @param viewTypes
	 */
	public void registerDatadomainTypeViewTypeAssociation(Collection<String> dataDomainTypes, String viewType) {
		for (String dataDomainType : dataDomainTypes)
			registerDatadomainTypeViewTypeAssociation(dataDomainType, viewType);

	}

	/**
	 * Get all datadomain types for a given view type
	 * 
	 * @param viewType
	 *            the view type for which the datadomains are sought.
	 * @return a set of datadomain types
	 */
	public Set<String> getDataDomainTypesForViewTypes(String viewType) {
		return viewDataDomainAssociations.getAll(viewType);
	}

	/**
	 * Get all viewTypes that can handle the datadomain of the specified type
	 * 
	 * @param dataDomainType
	 * @return
	 */
	public Set<String> getViewTypesForDataDomain(String dataDomainType) {
		return dataDomainViewAssociations.getAll(dataDomainType);
	}

	/**
	 * Get all loaded concrete dataDomains that can be used by the view specified. If no dataDomains are
	 * registered for the view, null is returned
	 * 
	 * @param viewType
	 * @return
	 */
	public ArrayList<IDataDomain> getAvailableDataDomainTypesForViewType(String viewType) {

		Set<String> dataDomainTypes = getDataDomainTypesForViewTypes(viewType);
		if (dataDomainTypes == null)
			return null;
		ArrayList<IDataDomain> availabelDataDomainTypes = new ArrayList<IDataDomain>();

		for (String dataDomainType : dataDomainTypes) {
			IDataDomain dataDomain = DataDomainManager.get().getDataDomain(dataDomainType);
			if (dataDomain != null)
				availabelDataDomainTypes.add(dataDomain);
		}
		return availabelDataDomainTypes;
	}

}
