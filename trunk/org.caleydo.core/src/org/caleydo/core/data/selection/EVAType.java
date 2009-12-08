package org.caleydo.core.data.selection;

/**
 * Enum for VA selection. TODO: Redesign, this is not good
 * 
 * @author Alexander Lex
 */

public enum EVAType {

	/**
	 * All elements in the storage, subject to filters
	 */
	CONTENT(EVAType.CONTENT_PRIMARY),

	/**
	 * Used for contextual views, for example in the bucket, where only a subset of the data based on for
	 * example the pathways are used
	 */
	CONTENT_CONTEXT(EVAType.CONTENT_PRIMARY),

	/**
	 * Type that may not be used in event communication, only for private sub views
	 */
	CONTENT_EMBEDDED_HM(EVAType.CONTENT_PRIMARY),

	/**
	 * All storages (initially)
	 */
	STORAGE(EVAType.STORAGE_PRIMARY);

	private String primaryVAType;

	public static final String CONTENT_PRIMARY = "Content";
	public static final String STORAGE_PRIMARY = "Storage";

	private EVAType(String primaryVAType) {
		this.primaryVAType = primaryVAType;
	}

	public String getPrimaryVAType() {
		return primaryVAType;
	}

	public static EVAType getVATypeForPrimaryVAType(String primaryVAType) {
		if (primaryVAType.equals(CONTENT_PRIMARY))
			return CONTENT;
		else if (primaryVAType.equals(STORAGE_PRIMARY))
			return STORAGE;
		else
			throw new IllegalArgumentException("Unknown primaryVAType: " + primaryVAType);
	}

}
