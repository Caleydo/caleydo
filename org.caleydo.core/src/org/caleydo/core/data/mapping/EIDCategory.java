package org.caleydo.core.data.mapping;

/**
 * <p>
 * General categorization of the different ID Types. When two IDs are in the same category a mapping between
 * them should be (at least theoretically) possible. An exception is other, where no assumption about
 * equivalency is valid.
 * </p>
 * <p>
 * Every ID type in {@link EIDType} has to specify its category
 * </p>
 * 
 * @author Alexander Lex
 */
public enum EIDCategory {
	GENE,
	EXPERIMENT,
	PATHWAY,
	OTHER
}
