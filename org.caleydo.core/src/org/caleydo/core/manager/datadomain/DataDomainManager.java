package org.caleydo.core.manager.datadomain;

import java.util.Collection;
import java.util.HashMap;

import org.caleydo.core.manager.IDataDomain;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

public class DataDomainManager {

	private static DataDomainManager dataDomainManager;
	private HashMap<String, IDataDomain> registeredDataDomains;

	private DataDomainManager() {
		registeredDataDomains = new HashMap<String, IDataDomain>(8);
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

	public void register(IDataDomain dataDomain) {
		registeredDataDomains.put(dataDomain.getDataDomainType(), dataDomain);
	}

}
