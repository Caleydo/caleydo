package org.caleydo.core.data.selection;

import java.util.HashMap;
import java.util.Set;

public class ContentVAType
	extends IVAType {

	/**
	 * All elements in the storage, subject to filters
	 */
	public static final ContentVAType CONTENT = new ContentVAType("CONTENT", false);

	/**
	 * Used for contextual views, for example in the bucket, where only a subset of the data based on for
	 * example the pathways are used
	 */
	public static final ContentVAType CONTENT_CONTEXT = new ContentVAType("CONTENT_CONTEXT", true);

	/**
	 * Type that may not be used in event communication, only for private sub views
	 */
	public static final ContentVAType CONTENT_EMBEDDED_HM = new ContentVAType("CONTENT_EMBEDDED_HM", true);

	private static ContentVAType primaryVAType = CONTENT;

	private static HashMap<ContentVAType, Boolean> registeredTypes;

	private boolean isEmptyByDefault = false;

	ContentVAType() {
		if (registeredTypes == null)
			registeredTypes = new HashMap<ContentVAType, Boolean>();
		registeredTypes.put(this, null);
	}

	ContentVAType(String stringRep, boolean isEmptyByDefault) {
		this();
		this.stringRep = stringRep;
		this.isEmptyByDefault = isEmptyByDefault;
	}

	public static ContentVAType getPrimaryVAType() {
		return primaryVAType;
	}

	public static synchronized Set<ContentVAType> getRegisteredVATypes() {
		return registeredTypes.keySet();
	}

	public void setEmptyByDefault(boolean isEmptyByDefault) {
		this.isEmptyByDefault = isEmptyByDefault;
	}

	public boolean isEmptyByDefault() {
		return isEmptyByDefault;
	}
}
