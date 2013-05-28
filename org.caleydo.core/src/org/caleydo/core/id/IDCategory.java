/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.id;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

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

		if (categoryName == null)
			throw new IllegalArgumentException("categoryName was null");

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

	/**
	 * Returns all registered ID categories.
	 *
	 * @return all registered ID categories
	 */
	public static Collection<IDCategory> getAllRegisteredIDCategories() {
		return registeredCategories.values();
	}

	/**
	 * Returns the IDCategory associated with the specified name.
	 *
	 * @param categoryName
	 *            the globally unique name of the category, may not be null
	 * @return Returns null if no IDCategory of that name is specified.
	 */
	public static IDCategory getIDCategory(String categoryName) {

		if (categoryName == null)
			throw new IllegalArgumentException("categoryName was null");

		return registeredCategories.get(categoryName);
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
	 * The name of the category, which should be human-readable and must be unique
	 */
	private String categoryName;

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
	private ArrayList<IDType> idTypes = new ArrayList<IDType>();

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
	public String getDenomination() {
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
	public String getDenominationPlural() {
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
	public ArrayList<IDType> getIdTypes() {
		return idTypes;
	}

	public List<IDType> getPublicIdTypes() {
		List<IDType> idTypes = new ArrayList<>(this.getIdTypes());

		for (Iterator<IDType> it = idTypes.iterator(); it.hasNext();)
			if (it.next().isInternalType())
				it.remove();
		return idTypes;
	}

	/**
	 * Initializes the {@link #primaryMappingType} and the {@link #humanReadableIDType} with best guesses if they were
	 * not set.
	 */
	public void initialize() {
		// if (idTypes.size() == 0)
		// throw new IllegalStateException("No id types specified");

		if (primaryMappingType == null) {
			primaryMappingType = IDType.registerType(categoryName + "_primary", this, EDataType.INTEGER);
			primaryMappingType.setInternalType(true);
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
}
