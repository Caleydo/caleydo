package org.caleydo.rcp;

import org.caleydo.core.data.collection.set.Set;
import org.caleydo.core.data.selection.VirtualArray;
import org.caleydo.core.manager.usecase.EDataDomain;

/**
 * the application mode tells the application how to start and which data-sets need to be loaded and how these
 * data-sets should be loaded.
 * 
 * @author Werner Puff
 * @author Alexander Lex
 */
public enum EApplicationMode {
	GENE_EXPRESSION_NEW_DATA(EDataDomain.GENETIC_DATA),
	// GENE_EXPRESSION_PATHWAY_VIEWER,
	GENE_EXPRESSION_SAMPLE_DATA(EDataDomain.GENETIC_DATA),
	UNSPECIFIED_NEW_DATA(EDataDomain.GENERAL_DATA),

	/**
	 * specifies that the UseCase (including {@link Set} and {@link VirtualArray}) is loaded from a caleydo
	 * server application
	 */
	COLLABORATION_CLIENT(EDataDomain.UNSPECIFIED),

	/**
	 * specifies that the caleydo application runs as a client in a deskotheque environment, initialization is
	 * done similar to {@link EApplicationMode#COLLABORATION_CLIENT}
	 */
	PLEX_CLIENT(EDataDomain.UNSPECIFIED),

	/**
	 * specifies that the UseCase (including {@link Set} and {@link VirtualArray}) is loaded from the file
	 * system
	 */
	LOAD_PROJECT(EDataDomain.UNSPECIFIED);

	private EDataDomain dataDomain;

	private EApplicationMode(EDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	/**
	 * Returns the data domain (see {@link EDataDomain}) associated with the application mode
	 * 
	 * @return
	 */
	EDataDomain getDataDomain() {
		return dataDomain;
	}
}
