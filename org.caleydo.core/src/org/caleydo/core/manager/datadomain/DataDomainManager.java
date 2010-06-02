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

/**
 * <p>
 * Singleton that manages dataDomains based on their dataDomainType (the string plug-in id) and the concrete
 * object.
 * </p>
 * <p>
 * Also holds associations between dataDomains and views that can use the datadomain.
 * </p>
 * 
 * @author Alexander Lex
 */
public class DataDomainManager {

	private static DataDomainManager dataDomainManager;
	private HashMap<String, IDataDomain> registeredDataDomains;

	/** maps a dataDomain to multiple views */
	private MultiHashMap<String, String> dataDomainViewAssociations;
	/** maps a view to multiple datadomains */
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

	/**
	 * Create a new dataDomain
	 * 
	 * @param dataDomainType
	 *            the plug-in id of the datadomain
	 * @return
	 */
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

	/**
	 * Returns all dataDomains
	 * 
	 * @return
	 */
	public Collection<IDataDomain> getDataDomains() {
		return registeredDataDomains.values();
	}

	/**
	 * Get the concrete dataDomain object for the dataDomainType. Returns null if no dataDomain is mapped to
	 * the type.
	 * 
	 * @param dataDomainType
	 * @return
	 */
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
	 * Get all loaded concrete dataDomains that can be used by the view specified
	 * 
	 * @param viewType
	 * @return
	 */
	public ArrayList<IDataDomain> getAvailableDataDomainTypesForViewTypes(String viewType) {
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
