package org.caleydo.core.data.id;

import java.util.HashMap;

import org.caleydo.core.data.mapping.IDMappingManager;

/**
 * An {@link IDCategory} specifies which kinds of {@link IDType}s belong semantically together. The contract
 * is, that all {@link IDType}s that are of the same {@link IDCategory} have to be mappable using an
 * {@link IDMappingManager}.
 * 
 * @author Alexander Lex
 */
public class IDCategory {

	private String categoryName;

	private static HashMap<String, IDCategory> registeredCategories = new HashMap<String, IDCategory>();

	private IDCategory(String categoryName) {
		this.categoryName = categoryName;
	}

	public static IDCategory registerCategory(String categoryName) {
		if (registeredCategories.containsKey(categoryName))
			return registeredCategories.get(categoryName);

		IDCategory idCategory = new IDCategory(categoryName);
		registeredCategories.put(categoryName, idCategory);

		return idCategory;
	}

	public static IDCategory getIDCategory(String categoryName) {
		IDCategory category = registeredCategories.get(categoryName);
		if(category == null)
			throw new IllegalStateException("No category with name " + categoryName + " registered.");
		return category;
	}

	public String getCategoryName() {
		return categoryName;
	}

	@Override
	public String toString() {
		return categoryName;
	}

}
