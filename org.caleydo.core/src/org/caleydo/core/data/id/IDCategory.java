package org.caleydo.core.data.id;

import java.util.HashMap;

import org.caleydo.core.data.mapping.IDMappingManager;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.Status;

/**
 * <p>
 * An {@link IDCategory} specifies which kinds of {@link IDType}s belong semantically together. The contract
 * is, that all {@link IDType}s that are of the same {@link IDCategory} have to be mappable using an
 * {@link IDMappingManager}.
 * </p>
 * <p>
 * An IDCategory also holds a {@link #primaryMappingType}, which must be set after the IDType itself was
 * created.
 * </p>
 * 
 * @author Alexander Lex
 */
public class IDCategory {

	private static HashMap<String, IDCategory> registeredCategories = new HashMap<String, IDCategory>();

	/**
	 * <p>
	 * Register a new IDCategory with the name categoryName. For every category, the name has to be globally
	 * unique.
	 * </p>
	 * <p>
	 * It is legal to "try" to register an IDCategory which may be already registered. If an IDCategory of
	 * this name is already registered, the previously registered IDCategory is returned.
	 * </p>
	 * 
	 * @param categoryName
	 *            the globally unique name of the category, may not be null
	 * @return the newly registered IDCategory, or a previously registered category of the same name.
	 */
	public static IDCategory registerCategory(String categoryName) {
		if (categoryName == null)
			throw new IllegalArgumentException("categoryName was null");
		if (registeredCategories.containsKey(categoryName)) {
			Logger.log(new Status(Status.INFO, "IDCategory", "IDCategory " + categoryName
				+ " already registered previously."));
			return registeredCategories.get(categoryName);
		}
		Logger.log(new Status(Status.INFO, "IDCategory", "Registered new IDCategory " + categoryName + "."));

		IDCategory idCategory = new IDCategory(categoryName);
		registeredCategories.put(categoryName, idCategory);

		return idCategory;
	}

	/**
	 * Returns the IDCategory associated with the specified name.
	 * 
	 * @param categoryName
	 *            the globally unique name of the category, may not be null
	 * @throws Throws
	 *             an IllegalArgumenteException if no category associated with the name is registered
	 */
	public static IDCategory getIDCategory(String categoryName) {
		if (categoryName == null)
			throw new IllegalArgumentException("categoryName was null");
		IDCategory category = registeredCategories.get(categoryName);
		if (category == null)
			throw new IllegalStateException("No category with name " + categoryName + " registered.");
		return category;
	}

	/** The name of the category, which should be human-readable and must be unique */
	private String categoryName;

	/**
	 * For every IDCategory one central mapping type has to be defined, which must be mappable to every other
	 * IDType of the same IDCategory. The data type of this IDType must be Integer, so that it can be used in
	 * {@link SelectionManager}s
	 */
	private IDType primaryMappingType;

	/** Private constructor, created through the static insances */
	private IDCategory(String categoryName) {
		this.categoryName = categoryName;
	}

	/**
	 * @return the categoryName, see {@link #categoryName}
	 */
	public String getCategoryName() {
		return categoryName;
	}

	@Override
	public String toString() {
		return categoryName;
	}

	/**
	 * May be called more than once with the same IDType
	 * 
	 * @param primaryMappingType
	 *            setter, see {@link #primaryMappingType}
	 */
	public void setPrimaryMappingType(IDType primaryMappingType) {
		if (primaryMappingType == null || !primaryMappingType.getIDCategory().equals(this))
			throw new IllegalArgumentException("Cannot set primaryMappingType " + primaryMappingType);
		if (this.primaryMappingType != null) {
			if (!this.primaryMappingType.equals(primaryMappingType))
				throw new IllegalStateException("Primary mapping type was already set to "
					+ this.primaryMappingType + ". Cannot set to " + primaryMappingType);
			return;
		}
		this.primaryMappingType = primaryMappingType;
	}

	/**
	 * @return the primaryMappingType, see {@link #primaryMappingType}
	 */
	public IDType getPrimaryMappingType() {
		return primaryMappingType;
	}

}
