package org.caleydo.core.data.dimension;

import org.caleydo.core.data.collection.dimension.ADimension;
import org.caleydo.core.data.collection.dimension.NominalDimension;
import org.caleydo.core.data.collection.dimension.NumericalDimension;
import org.caleydo.core.data.id.ManagedObjectType;
import org.caleydo.core.manager.AManager;

/**
 * Manager for dimension objects.
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class DimensionManager
	extends AManager<ADimension> {

	public ADimension createDimension(final ManagedObjectType type) {
		switch (type) {
			case DIMENSION_NUMERICAL:
				return new NumericalDimension();
			case DIMENSION_NOMINAL:
				return new NominalDimension<String>();

			default:
				throw new IllegalStateException("Failed due to unhandled type [" + type.toString() + "]");
		}
	}
	
	public ADimension createDimension(final ManagedObjectType type, int dimensionID) {
		switch (type) {
			case DIMENSION_NUMERICAL:
				return new NumericalDimension(dimensionID);
			case DIMENSION_NOMINAL:
				return new NominalDimension<String>(dimensionID);

			default:
				throw new IllegalStateException("Failed due to unhandled type [" + type.toString() + "]");
		}
	}
}
