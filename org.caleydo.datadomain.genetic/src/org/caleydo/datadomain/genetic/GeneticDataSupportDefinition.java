package org.caleydo.datadomain.genetic;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.datadomain.IDataSupportDefinition;

/**
 * Defines the {@link DataDomain}s supported by {@link GLEnRoutePathway}.
 * 
 * @author Christian Partl
 * 
 */
public class GeneticDataSupportDefinition
	implements IDataSupportDefinition {

	public GeneticDataSupportDefinition() {
	}

	@Override
	public boolean isDataDomainSupported(IDataDomain dataDomain) {
		return dataDomain != null && dataDomain instanceof GeneticDataDomain;
	}

}
