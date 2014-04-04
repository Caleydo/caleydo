/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.id;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.google.common.base.Preconditions;

/**
 * <p>
 * An {@link IDCategory} specifies which kinds of {@link IDType}s belong semantically together. The contract is, that
 * all {@link IDType}s that are of the same {@link IDCategory} have to be mappable using an {@link IDMappingManager}.
 * </p>
 * <p>
 * For every <code>IDCategory</code> a {@link #primaryMappingType} must be defined, of which the associated IDs must be
 * Integers.
 * </p>
 * <p>
 * The IDCategory also defines a human-readable ID type ( {@link #humanReadableIDType}), which should be used to present
 * IDs of this category to the user.
 * <p>
 * An IDCategory also holds human readable versions of its name ( {@link #denomination} and {@link #denominationPlural})
 * which should be used to identify the <code>IDCategory</code> to the user.
 * </p>
 * <p>
 * This class is also a singleton that manages all IDCategories
 * </p>
 *
 * @author Alexander Lex
 */
@XmlJavaTypeAdapter(IDCategory.SerializeAdapter.class)
public class IDCategory {

	private static final ConcurrentMap<String, IDCategory> registeredCategories = new ConcurrentHashMap<>();

	/**
	 * <p>
	 * Register a new IDCategory with the name categoryName. For every category, the name has to be globally unique.
	 * </p>
	 * <p>
	 * It is legal to "try" to register an IDCategory which may be already registered. If an IDCategory of this name is
	 * already registered, the previously registered IDCategory is returned.
	 * </p>
	 *
	 * @param categoryName
	 *            the globally unique name of the category, may not be null
	 * @return the newly registered IDCategory, or a previously registered category of the same name.
	 */
	public static IDCategory registerCategory(String categoryName) {
		Preconditions.checkNotNull(categoryName, "categoryName was null");

		if (registeredCategories.containsKey(categoryName)) {
			Logger.log(new Status(IStatus.INFO, "IDCategory", "IDCategory " + categoryName
					+ " already registered previously."));
			return registeredCategories.get(categoryName);
		}

		Logger.log(new Status(IStatus.INFO, "IDCategory", "Registered new IDCategory " + categoryName + "."));

		IDCategory idCategory = new IDCategory(categoryName);
		registeredCategories.put(categoryName, idCategory);

		return idCategory;
	}

	public static IDCategory registerInternalCategory(String categoryName) {
		IDCategory category = registerCategory(categoryName);
		category.setInternalCategory(true);
		return category;
	}

	/**
	 * @param dimensionIDCategoryName
	 * @return
	 */
	public static IDCategory registerCategoryIfAbsent(String category) {
		registeredCategories.putIfAbsent(category, new IDCategory(category));
		return registeredCategories.get(category);
	}

	/**
	 * Returns all registered ID categories.
	 *
	 * @return all registered ID categories
	 */
	public static Collection<IDCategory> getAllRegisteredIDCategories() {
		return Collections.unmodifiableCollection(registeredCategories.values());
	}

	/**
	 * Returns the IDCategory associated with the specified name.
	 *
	 * @param categoryName
	 *            the globally unique name of the category, may not be null
	 * @return Returns null if no IDCategory of that name is specified.
	 */
	public static IDCategory getIDCategory(String categoryName) {
		Preconditions.checkNotNull(categoryName, "categoryName was null");
		return registeredCategories.get(categoryName);
	}

	/**
	 * The name of the category, which should be human-readable and must be unique
	 */
	private final String categoryName;

	/**
	 * For every IDCategory one central mapping type has to be defined, which must be mappable to every other IDType of
	 * the same IDCategory. The data type of this IDType must be Integer, so that it can be used in
	 * {@link SelectionManager}s
	 */
	private IDType primaryMappingType;

	/**
	 * True if a the primary mapping type is created by default, false if it is specified externally.
	 */
	private boolean isPrimaryMappingTypeDefault = true;

	/**
	 * The id type that should be used if an ID from this category should be printed human readable
	 */
	private IDType humanReadableIDType;

	/**
	 * The human-readable name of the IDCategory. Defaults to the String of {@link #humanReadableIDType}
	 */
	private String denomination;

	/**
	 * Same as {@link #denomination} but in plural. Defaults to <code>denomination</code>+"s".
	 */
	private String denominationPlural;

	/**
	 * All registered IDTypes for this category.
	 */
	private List<IDType> idTypes = new ArrayList<IDType>();

	/**
	 * flag determining whether a category is internal only, meaning that it is dynamically generated using, e.g., a
	 * running number (true), or an externally provided ID (e.g., refseq, false)
	 */
	private boolean isInternalCategory = false;

	/** Private constructor, created through the static instances */
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
	 * Convenience method that checks whether an {@link IDType} is of this category.
	 *
	 * @param idType
	 * @return
	 */
	public boolean isOfCategory(IDType idType) {
		return idType.getIDCategory().equals(this);
	}

	/**
	 * @param denomination
	 *            setter, see {@link #denomination}
	 */
	public void setDenomination(String denomination) {
		this.denomination = denomination;
		this.denominationPlural = this.denomination + "s";
	}

	/**
	 * @return the denomination, see {@link #denomination}
	 */
	public String getDenomination(boolean capitalized) {
		if (capitalized) {
			return denomination.substring(0, 1).toUpperCase() + denomination.substring(1, denomination.length());
		}
		return denomination;
	}

	/**
	 * Setter. Must be called after {@link #setDenomination(String)}, otherwise it is overridden.
	 *
	 * @param denominationPlural
	 *            setter, see {@link #denominationPlural}
	 */
	public void setDenominationPlural(String denominationPlural) {
		this.denominationPlural = denominationPlural;
	}

	/**
	 * @return the denominationPlural, see {@link #denominationPlural}
	 */
	public String getDenominationPlural(boolean capitalized) {
		if (capitalized) {
			return denominationPlural.substring(0, 1).toUpperCase()
					+ denominationPlural.substring(1, denominationPlural.length());
		}
		return denominationPlural;
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
				throw new IllegalStateException("Primary mapping type of " + categoryName + " was already set to "
						+ this.primaryMappingType + ". Cannot set to " + primaryMappingType);
			return;
		}
		isPrimaryMappingTypeDefault = false;
		this.primaryMappingType = primaryMappingType;
	}

	/**
	 * @return the isPrimaryMappingTypeDefault, see {@link #isPrimaryMappingTypeDefault}
	 */
	public boolean isPrimaryMappingTypeDefault() {
		return isPrimaryMappingTypeDefault;
	}

	/**
	 * @param humanReadableIDType
	 *            setter, see {@link #humanReadableIDType}
	 */
	public void setHumanReadableIDType(IDType humanReadableIDType) {
		this.humanReadableIDType = humanReadableIDType;
	}

	/**
	 * @return the primaryMappingType, see {@link #primaryMappingType}
	 */
	public IDType getPrimaryMappingType() {
		return primaryMappingType;
	}

	/**
	 * @return the humanReadableIDType, see {@link #humanReadableIDType}
	 */
	public IDType getHumanReadableIDType() {
		return humanReadableIDType;
	}

	/**
	 * @param isInternaltType
	 *            setter, see {@link #isInternalCategory}
	 */
	public void setInternalCategory(boolean isInternaltType) {
		this.isInternalCategory = isInternaltType;
	}

	/**
	 * @return the isInternalCategory, see {@link #isInternalCategory}
	 */
	public boolean isInternaltCategory() {
		return isInternalCategory;
	}

	/**
	 * Add an ID type to the category.
	 *
	 * @param idType
	 *            the IDType to add
	 */
	public void addIDType(IDType idType) {
		idTypes.add(idType);
	}

	/**
	 * Remove an ID type from the category.
	 *
	 * @param idType
	 *            the IDType to remove
	 */
	public void removeIDType(IDType idType) {
		idTypes.remove(idType);
	}

	/**
	 * Returns all ID types that are associated to this category.
	 *
	 * @return the idTypes, see {@link #idTypes}
	 */
	public List<IDType> getIdTypes() {
		return Collections.unmodifiableList(idTypes);
	}

	public List<IDType> getPublicIdTypes() {
		List<IDType> publics = new ArrayList<>(this.idTypes.size());
		for (IDType idType : this.idTypes)
			if (!idType.isInternalType())
				publics.add(idType);
		return publics;
	}

	/**
	 * Initializes the {@link #primaryMappingType} and the {@link #humanReadableIDType} with best guesses if they were
	 * not set.
	 */
	public void initialize() {
		// if (idTypes.size() == 0)
		// throw new IllegalStateException("No id types specified");

		if (primaryMappingType == null) {
			primaryMappingType = IDType.registerInternalType(categoryName + "_primary", this, EDataType.INTEGER);
		}

		if (humanReadableIDType == null) {
			IDType candidateHRType = null;
			for (IDType idType : idTypes) {
				if (!idType.isInternalType()) {
					if (candidateHRType != null) {
						throw new IllegalStateException("To many candidates for Human Readable Types in "
								+ this.toString());
					}
					candidateHRType = idType;
				}
			}
			if (candidateHRType == null) {
				humanReadableIDType = primaryMappingType;
			} else {
				humanReadableIDType = candidateHRType;
			}
		}

		if (denomination == null) {
			setDenomination(categoryName.toLowerCase());
		}
	}

	/**
	 * Calculates the probability of the specified id list to belong to every {@link IDType} of this category.
	 *
	 * @param idList
	 *            List of IDs the probabilities should be calculated for.
	 * @param checkInternalIDTypes
	 *            Determines whether also internal id types should be considered.
	 * @return List of {@link Pair}s specifying the probability of the specified id list to belong to the IDtypes of
	 *         this category. The list is decsendingly ordered according to the probability.
	 */
	public List<Pair<Float, IDType>> getListOfIDTypeAffiliationProbabilities(List<String> idList,
			boolean checkInternalIDTypes) {

		List<Pair<Float, IDType>> probabilityList = new ArrayList<Pair<Float, IDType>>(idTypes.size());

		boolean mappingManagerExists = IDMappingManagerRegistry.get().hasIDMappingManager(this);

		for (IDType idType : idTypes) {
			if (idType.isInternalType() && !checkInternalIDTypes)
				continue;

			if (mappingManagerExists) {
				float affiliationProbability = idType.calcProbabilityOfIDTypeAffiliation(idList);
				probabilityList.add(new Pair<Float, IDType>(affiliationProbability, idType));
			} else {
				// FIXME: This is a rather hacky solution for TCGA SAMPLES
				if (idType.getTypeName().equals("TCGA_SAMPLE")) {
					int numMatchedIDs = 0;
					for (String currentID : idList) {
						if (currentID.toUpperCase().contains("TCGA")) {
							numMatchedIDs++;
						}
					}
					float affiliationProbability = (float) numMatchedIDs / (float) idList.size();
					probabilityList.add(new Pair<Float, IDType>(affiliationProbability, idType));
				} else {
					probabilityList.add(new Pair<Float, IDType>(0.0f, idType));
				}
			}

		}

		Collections.sort(probabilityList, Collections.reverseOrder(Pair.<Float> compareFirst()));

		return probabilityList;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((categoryName == null) ? 0 : categoryName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IDCategory other = (IDCategory) obj;
		return Objects.equals(categoryName, other.categoryName);
	}

	public static final class IDCategoryBuilder {
		private final String categoryName;

		public IDCategoryBuilder(String categoryName) {
			super();
			this.categoryName = categoryName;
		}

		public IDCategory build() {
			return new IDCategory(categoryName);
		}
	}

	protected static class IDCategoryData {
		public String name;
	}

	protected static class SerializeAdapter extends
			XmlAdapter<IDCategoryData, IDCategory> {

		@Override
		public IDCategory unmarshal(IDCategoryData category) {
			IDCategory cat = getIDCategory(category.name);
			if( cat == null ) {
				cat = registerCategoryIfAbsent(category.name);
				cat.initialize();
			}
			
			return cat;
		}

		@Override
		public IDCategoryData marshal(IDCategory category) {
			IDCategoryData categoryData = new IDCategoryData();
			categoryData.name = category.categoryName;
			return categoryData;
		}
	}
}


