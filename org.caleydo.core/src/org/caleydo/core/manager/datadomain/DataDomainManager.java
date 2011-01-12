package org.caleydo.core.manager.datadomain;

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
 */
public class DataDomainManager {

	private static DataDomainManager dataDomainManager;
	private HashMap<String, IDataDomain> registeredDataDomains;

	private AssociationManager associationManager;

	private DataDomainManager() {
		registeredDataDomains = new HashMap<String, IDataDomain>(8);
		associationManager = new AssociationManager();

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
			IDataDomain dataDomain = (IDataDomain) ce[0].createExecutableExtension("class");
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
	 * register a concrete data domain
	 * 
	 * @param dataDomain
	 */
	public void register(IDataDomain dataDomain) {
		registeredDataDomains.put(dataDomain.getDataDomainType(), dataDomain);
	}

	/**
	 * Returns the default association manager which is valid system-wide.
	 * 
	 * @return
	 */
	public AssociationManager getAssociationManager() {
		return associationManager;
	}

	public <T extends IDataDomain> T guessDataDomain(Class<?> dataDomainClass) {

		for (IDataDomain dataDomain : registeredDataDomains.values()) {
			if (dataDomainClass.equals(dataDomain.getClass()))
				return (T) dataDomain;
		}
		return null;
	}

}
