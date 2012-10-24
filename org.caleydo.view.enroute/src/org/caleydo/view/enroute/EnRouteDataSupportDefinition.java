package org.caleydo.view.enroute;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.datadomain.IDataSupportDefinition;
import org.caleydo.datadomain.genetic.GeneticDataDomain;

/**
 * Defines the {@link DataDomain}s supported by {@link GLEnRoutePathway}.
 * 
 * @author Christian Partl
 * 
 */
public class EnRouteDataSupportDefinition
	implements IDataSupportDefinition {

	public EnRouteDataSupportDefinition() {
	}

	@Override
	public boolean isDataDomainSupported(IDataDomain dataDomain) {
		return dataDomain != null && dataDomain instanceof GeneticDataDomain;
	}

}
