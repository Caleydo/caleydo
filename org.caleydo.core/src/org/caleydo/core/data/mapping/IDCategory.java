package org.caleydo.core.data.mapping;

import java.util.HashMap;

/**
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
		return registeredCategories.get(categoryName);
	}

	public String getCategoryName() {
		return categoryName;
	}

}
