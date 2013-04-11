package org.caleydo.datadomain.genetic;

import org.caleydo.core.data.datadomain.ADataSupportDefinition;
import org.caleydo.core.data.datadomain.IDataDomain;

/**
 * Defines the {@link DataDomain}s supported by {@link GLEnRoutePathway}.
 *
 * @author Christian Partl
 *
 */
public class GeneticDataSupportDefinition extends ADataSupportDefinition {

	public GeneticDataSupportDefinition() {
	}

	@Override
	public boolean apply(IDataDomain dataDomain) {
		return dataDomain != null && dataDomain instanceof GeneticDataDomain;
	}

}
