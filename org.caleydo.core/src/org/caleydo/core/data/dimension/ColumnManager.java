package org.caleydo.core.data.dimension;

import org.caleydo.core.data.collection.dimension.AColumn;
import org.caleydo.core.data.collection.dimension.NominalColumn;
import org.caleydo.core.data.collection.dimension.NumericalColumn;
import org.caleydo.core.data.id.ManagedObjectType;
import org.caleydo.core.manager.AManager;

/**
 * Manager for dimension objects.
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class ColumnManager
	extends AManager<AColumn> {

	public AColumn createDimension(final ManagedObjectType type) {
		switch (type) {
			case DIMENSION_NUMERICAL:
				return new NumericalColumn();
			case DIMENSION_NOMINAL:
				return new NominalColumn<String>();

			default:
				throw new IllegalStateException("Failed due to unhandled type [" + type.toString() + "]");
		}
	}
	
	public AColumn createDimension(final ManagedObjectType type, int dimensionID) {
		switch (type) {
			case DIMENSION_NUMERICAL:
				return new NumericalColumn(dimensionID);
			case DIMENSION_NOMINAL:
				return new NominalColumn<String>(dimensionID);

			default:
				throw new IllegalStateException("Failed due to unhandled type [" + type.toString() + "]");
		}
	}
}
