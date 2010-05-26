package org.caleydo.core.manager.usecase;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

public class DataDomainManager {

	private static DataDomainManager dataDomainManager;

	private DataDomainManager() {
	}

	public static DataDomainManager getInstance() {
		if (dataDomainManager == null)
			dataDomainManager = new DataDomainManager();

		return dataDomainManager;
	}

	public AUseCase createDataDomain(EDataDomain dataDomainType) {
		IExtensionRegistry reg = Platform.getExtensionRegistry();

		IExtensionPoint ep = reg.getExtensionPoint("org.caleydo.datadomain.UseCase");
		IExtension ext = ep.getExtension("org.caleydo.datadomain.genetic.GeneticUseCase");
		IConfigurationElement[] ce = ext.getConfigurationElements();

		try {
			AUseCase useCase = (AUseCase) ce[0].createExecutableExtension("class");
			return useCase;
		}
		catch (Exception ex) {
			throw new RuntimeException("Could not instantiate KEGG Pathway Resource Loader", ex);
		}

	}

}
