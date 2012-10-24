package org.caleydo.view.pathway;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.datadomain.IDataSupportDefinition;
import org.caleydo.datadomain.genetic.GeneticDataDomain;

/**
 * Defines the {@link DataDomain}s supported by {@link GLPathway}.
 * 
 * @author Christian Partl
 * 
 */
public class PathwayDataSupportDefinition
	implements IDataSupportDefinition {

	public PathwayDataSupportDefinition() {
	}

	@Override
	public boolean isDataDomainSupported(IDataDomain dataDomain) {
		return dataDomain != null && dataDomain instanceof GeneticDataDomain;
	}

}
