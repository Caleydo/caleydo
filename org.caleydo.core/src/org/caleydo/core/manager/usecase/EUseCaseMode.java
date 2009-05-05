package org.caleydo.core.manager.usecase;

/**
 * This mode determines whether the user can load and work with
 * gene expression data or otherwise if an not further specified data
 * set is loaded. In the case of the unspecified data set some specialized
 * gene expression features are not available.
 * 
 * @author Marc Streit
 *
 */
public enum EUseCaseMode {
	GENETIC_DATA,
	CLINICAL_DATA,
	UNSPECIFIED_DATA
}
