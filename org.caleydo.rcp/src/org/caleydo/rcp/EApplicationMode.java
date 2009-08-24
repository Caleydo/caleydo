package org.caleydo.rcp;

import org.caleydo.core.data.collection.set.Set;
import org.caleydo.core.data.selection.VirtualArray;

/**
 * the application mode tells the application how to start and which data-sets need to be
 * loaded and how these data-sets should be loaded.
 * @author Werner Puff
 */
public enum EApplicationMode {
	GENE_EXPRESSION_NEW_DATA,
//	GENE_EXPRESSION_PATHWAY_VIEWER,
	GENE_EXPRESSION_SAMPLE_DATA_RANDOM,
	GENE_EXPRESSION_SAMPLE_DATA_REAL,
	UNSPECIFIED_NEW_DATA,

	/** 
	 * specifies that the UseCase (including {@link Set} and {@link VirtualArray}) is 
	 * loaded from a caleydo server application 
	 */ 
	COLLABORATION_CLIENT,
	
	/**
	 * specifies that the caleydo application runs as a client in a deskotheque 
	 * environment, initialization is done similar to {@link EApplicationMode#COLLABORATION_CLIENT}
	 */
	PLEX_CLIENT, 

	/**
	 * specifies that the UseCase (including {@link Set} and {@link VirtualArray}) is 
	 * loaded from the file system 
	 */
	LOAD_PROJECT
}
