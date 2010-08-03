package org.caleydo.rcp.wizard.project;


public enum ProjectMode {

	GENE_EXPRESSION_NEW_DATA,

	/** specifies to load an entire sample project */
	SAMPLE_PROJECT,

	GENE_EXPRESSION_SAMPLE_DATA,
	UNSPECIFIED_NEW_DATA,

	/**
	 * specifies that the UseCase (including {@link Set} and {@link VirtualArray}) is loaded from a caleydo
	 * server application
	 */
	COLLABORATION_CLIENT,

	/**
	 * specifies that the caleydo application runs as a client in a deskotheque environment, initialization is
	 * done similar to {@link EApplicationMode#COLLABORATION_CLIENT}
	 */
	PLEX_CLIENT,

	/**
	 * Needed for starting caleydo without loading any data. For example needed for eye tracker test setup.
	 */
	NO_DATA,

	/**
	 * specifies that the UseCase (including {@link Set} and {@link VirtualArray}) is loaded from the file
	 * system
	 */
	LOAD_PROJECT;

}
