/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.id;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.IDSpecification;
import org.caleydo.core.io.IDTypeParsingRules;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.Status;

/**
 * This class provides a static method to initialize {@link IDType}s and
 * {@link IDCategory} from the Information provided in the
 * {@link DataSetDescription}.
 *
 * @author Alexander Lex
 *
 */
public class IDTypeInitializer {

	/**
	 * <p>
	 * Initializes {@link IDType}s and {@link IDCategory} based on the
	 * information provides in the {@link DataSetDescription}.
	 * </p>
	 * <p>
	 * Creates default values as specified in the {@link IDSpecification}, or
	 * creates default values for <code>IDSpecification</code>s themselves if
	 * necessary.
	 * </p>
	 *
	 * @param dataSetDescription
	 *            the {@link DataSetDescription} containing the externally
	 *            specified information about IDs
	 * @return the {@link DataSetDescription} enriched by default values
	 */
	public static DataSetDescription initIDs(DataSetDescription dataSetDescription) {
		String dimensionIDCategoryName;
		String dimensionIDTypeName;
		String recordIDCategoryName;
		String recordIDTypeName;
		IDTypeParsingRules recordIDTypeParsingRules;
		IDTypeParsingRules dimensionIDTypeParsingRules;

		IDSpecification rowIDSpecification = dataSetDescription.getRowIDSpecification();
		if (rowIDSpecification == null) {
			rowIDSpecification = new IDSpecification();
			rowIDSpecification.setIDSpecification(dataSetDescription.getDataSetName()
					+ "_row", dataSetDescription.getDataSetName() + "_row");
			dataSetDescription.setRowIDSpecification(rowIDSpecification);
			Logger.log(new Status(Status.INFO, "DataLoader",
					"Automatically creating row ID specification for "
							+ dataSetDescription.getDataSetName()));

		}
		IDSpecification columnIDSpecification = dataSetDescription
				.getColumnIDSpecification();
		if (columnIDSpecification == null) {
			columnIDSpecification = new IDSpecification();
			columnIDSpecification.setIDSpecification(dataSetDescription.getDataSetName()
					+ "_column", dataSetDescription.getDataSetName() + "_column");
			dataSetDescription.setColumnIDSpecification(columnIDSpecification);
			Logger.log(new Status(Status.INFO, "DataLoader",
					"Automatically creating column ID specification for "
							+ dataSetDescription.getDataSetName()));
		}

		if (rowIDSpecification.getIdCategory() == null) {
			// setting default category name
			rowIDSpecification.setIdCategory(rowIDSpecification.getIdType());
		}

		if (columnIDSpecification.getIdCategory() == null) {
			columnIDSpecification.setIdCategory(columnIDSpecification.getIdType());
		}

		if (dataSetDescription.isTransposeMatrix()) {
			dimensionIDTypeName = rowIDSpecification.getIdType();
			dimensionIDCategoryName = rowIDSpecification.getIdCategory();
			dimensionIDTypeParsingRules = rowIDSpecification.getIdTypeParsingRules();

			recordIDTypeName = columnIDSpecification.getIdType();
			recordIDCategoryName = columnIDSpecification.getIdCategory();
			recordIDTypeParsingRules = columnIDSpecification.getIdTypeParsingRules();
		} else {
			dimensionIDTypeName = columnIDSpecification.getIdType();
			dimensionIDCategoryName = columnIDSpecification.getIdCategory();
			dimensionIDTypeParsingRules = columnIDSpecification.getIdTypeParsingRules();

			recordIDTypeName = rowIDSpecification.getIdType();
			recordIDCategoryName = rowIDSpecification.getIdCategory();
			recordIDTypeParsingRules = rowIDSpecification.getIdTypeParsingRules();
		}

		if (dimensionIDCategoryName == null)
			dimensionIDCategoryName = dimensionIDTypeName;
		if (recordIDCategoryName == null)
			recordIDCategoryName = recordIDTypeName;

		IDCategory dimensionIDCategory = IDCategory.getIDCategory(dimensionIDCategoryName);
		if (dimensionIDCategory == null) {
			dimensionIDCategory = IDCategory.registerCategory(dimensionIDCategoryName);
		}

		IDCategory recodIDCategory = IDCategory.getIDCategory(recordIDCategoryName);
		if (recodIDCategory == null) {
			recodIDCategory = IDCategory.registerCategory(recordIDCategoryName);
		}

		IDType recordIDType = IDType.getIDType(recordIDTypeName);
		if (recordIDType == null) {
			recordIDType = IDType.registerType(recordIDTypeName, recodIDCategory,
					EDataType.STRING);
			if (recordIDTypeParsingRules != null  && recordIDTypeParsingRules.isDefault())
				recordIDType.setIdTypeParsingRules(recordIDTypeParsingRules);
		}

		IDType dimensionIDType = IDType.getIDType(dimensionIDTypeName);
		if (dimensionIDType == null) {
			dimensionIDType = IDType.registerType(dimensionIDTypeName,
					dimensionIDCategory, EDataType.STRING);
			if (dimensionIDTypeParsingRules != null && dimensionIDTypeParsingRules.isDefault())
				dimensionIDType.setIdTypeParsingRules(dimensionIDTypeParsingRules);
		}

		return dataSetDescription;
	}

}
