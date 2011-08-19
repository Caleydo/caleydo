package org.caleydo.core.data.collection;

/**
 * Collection of different types of dimensions.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public enum DimensionType {
	// Needed by the parser
	ABORT(null),
	SKIP(null),

	INT(Integer.class),
	FLOAT(Float.class),
	STRING(String.class),

	CERTAINTY(Float.class);

	private <T> DimensionType(Class<T> dimensionClass) {

	}

}
