package org.caleydo.core.manager.datadomain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import org.caleydo.core.manager.IDataDomain;
import org.caleydo.core.util.collection.MultiHashMap;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

public class DataDomainManager {

	private static DataDomainManager dataDomainManager;
	private HashMap<String, IDataDomain> registeredDataDomains;

	private MultiHashMap<String, String> dataDomainViewAssociations;
	private MultiHashMap<String, String> viewDataDomainAssociations;

	private DataDomainManager() {
		registeredDataDomains = new HashMap<String, IDataDomain>(8);
		dataDomainViewAssociations = new MultiHashMap<String, String>();
		viewDataDomainAssociations = new MultiHashMap<String, String>();
	}

	public static DataDomainManager getInstance() {
		if (dataDomainManager == null)
			dataDomainManager = new DataDomainManager();

		return dataDomainManager;
	}

	public IDataDomain createDataDomain(String dataDomainType) {
		
		IExtensionRegistry reg = Platform.getExtensionRegistry();

		IExtensionPoint ep = reg.getExtensionPoint("org.caleydo.datadomain.DataDomain");
		IExtension ext = ep.getExtension(dataDomainType);
		IConfigurationElement[] ce = ext.getConfigurationElements();

		try {
			IDataDomain dataDomain = (IDataDomain) ce[0].createExecutableExtension("class");
			register(dataDomain);
			return dataDomain;
		}
		catch (Exception ex) {
			throw new RuntimeException("Could not instantiate data domain", ex);
		}
	}

	public Collection<IDataDomain> getDataDomains() {
		return registeredDataDomains.values();
	}

	public IDataDomain getDataDomain(String dataDomainType) {

		return registeredDataDomains.get(dataDomainType);
	}

	/**
	 * register a concrete datadomain
	 * 
	 * @param dataDomain
	 */
	public void register(IDataDomain dataDomain) {
		registeredDataDomains.put(dataDomain.getDataDomainType(), dataDomain);
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
	 * @param dataDomainType
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

	public ArrayList<IDataDomain> getListOfAvailableDataDomainTypesForViewTypes(String viewType) {
		Set<String> dataDomainTypes = getDataDomainTypesForViewTypes(viewType);
		ArrayList<IDataDomain> availabelDataDomainTypes = new ArrayList<IDataDomain>();

		for (String dataDomainType : dataDomainTypes) {
			IDataDomain dataDomain = getDataDomain(dataDomainType);
			if (dataDomain != null)
				availabelDataDomainTypes.add(dataDomain);
		}
		return availabelDataDomainTypes;

	}

}
