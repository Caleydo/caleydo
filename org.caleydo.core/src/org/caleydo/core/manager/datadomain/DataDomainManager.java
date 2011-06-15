package org.caleydo.core.manager.datadomain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

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
 * The DataDomainManager holds the default {@link AssociationManager}, which stores associations between views
 * and dataDomains. Notice that it is legal to hold a private AssociationManager for special cases.
 * </p>
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class DataDomainManager {

	private static DataDomainManager dataDomainManager;
	private HashMap<String, ArrayList<IDataDomain>> registeredDataDomains;

	private AssociationManager associationManager;
	private DataDomainGraph dataDomainGraph;

	private DataDomainManager() {
		registeredDataDomains = new HashMap<String, ArrayList<IDataDomain>>(8);
		associationManager = new AssociationManager();
		dataDomainGraph = new DataDomainGraph();
	}

	public static DataDomainManager get() {
		if (dataDomainManager == null)
			dataDomainManager = new DataDomainManager();

		return dataDomainManager;
	}

	/**
	 * Create a new dataDomain. The created dataDomain is also registered with the manager.
	 * 
	 * @param dataDomainType
	 *            the plug-in id of the data domain
	 * @return
	 */
	public IDataDomain createDataDomain(String dataDomainType) {

		IExtensionRegistry reg = Platform.getExtensionRegistry();

		IExtensionPoint ep = reg.getExtensionPoint("org.caleydo.datadomain.DataDomain");
		IExtension ext = ep.getExtension(dataDomainType);
		IConfigurationElement[] ce = ext.getConfigurationElements();

		try {
			ADataDomain dataDomain = (ADataDomain) ce[0].createExecutableExtension("class");
			Thread thread = new Thread(dataDomain, dataDomainType);
			thread.start();
			return dataDomain;
		}
		catch (Exception ex) {
			throw new RuntimeException("Could not instantiate data domain " + dataDomainType, ex);
		}
	}

	/**
	 * Returns all dataDomains
	 * 
	 * @return
	 */
	public Collection<IDataDomain> getDataDomains() {

		Collection<IDataDomain> dataDomains = new ArrayList<IDataDomain>();
		for (ArrayList<IDataDomain> dataDomainsPerType : registeredDataDomains.values())
			dataDomains.addAll(dataDomainsPerType);

		return dataDomains;
	}

	/**
	 * Get the concrete dataDomain object for the dataDomainType. Returns null if no dataDomain is mapped to
	 * the type.
	 * 
	 * @deprecated Returns only the first registered data domain for this type. When multiple data sets are
	 *             loaded this might be a problem.
	 * @param dataDomainType
	 * @return
	 */
	public IDataDomain getDataDomain(String dataDomainType) {

		if (registeredDataDomains.containsKey(dataDomainType))
			return registeredDataDomains.get(dataDomainType).get(0);

		return null;
	}

	/**
	 * register a concrete data domain
	 * 
	 * @param dataDomain
	 */
	public void register(IDataDomain dataDomain) {

		if (!registeredDataDomains.containsKey(dataDomain.getDataDomainType()))
			registeredDataDomains.put(dataDomain.getDataDomainType(), new ArrayList<IDataDomain>());

		registeredDataDomains.get(dataDomain.getDataDomainType()).add(dataDomain);
		dataDomainGraph.addDataDomain(dataDomain);
	}

	/**
	 * Returns the default association manager which is valid system-wide.
	 * 
	 * @return
	 */
	public AssociationManager getAssociationManager() {
		return associationManager;
	}

	public DataDomainGraph getDataDomainGraph() {
		return dataDomainGraph;
	}
}
