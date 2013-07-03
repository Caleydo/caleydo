/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.io.parser.ascii;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import org.caleydo.core.data.collection.column.NumericalColumn;
import org.caleydo.core.data.collection.column.container.FloatContainer;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.MappingType;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.IDSpecification;
import org.caleydo.core.io.IDTypeParsingRules;

import com.google.common.primitives.Floats;

/**
 * <p>
 * Parser for matrix data that is stored in linear format, i.e., one row has idtype1 and idtype2 plus a value.
 * </p>
 * <p>
 * e.g.:
 * 
 * <pre>
 * Sample1 | Gene1 | 0.4
 * Sample1 | Gene2 | 0.6
 * </pre>
 * 
 * </p>
 * <p>
 * This is only implemented for homogeneous float at the moment!
 * </p>
 */
public class LinearDataParser extends ATextParser {

	/**
	 * Imports data from file to this table. uses first dimension and overwrites first selection.
	 */
	protected ArrayList<ArrayList<Float>> targetRawContainer;

	/** The {@link ATableBasedDataDomain} for which the file is loaded */
	private ATableBasedDataDomain dataDomain;

	/** The {@link DataSetDescription} on which the loading of the file is based */
	private DataSetDescription dataSetDescription;

	/**
	 * Constructor.
	 */
	public LinearDataParser(ATableBasedDataDomain dataDomain, DataSetDescription dataSetDescription) {
		super(dataSetDescription.getDataSourcePath());

		this.dataDomain = dataDomain;
		this.dataSetDescription = dataSetDescription;
		targetRawContainer = new ArrayList<>();
	}

	@Override
	protected void parseFile(BufferedReader reader) throws IOException {
		// prepare for id setting of column IDs
		IDMappingManager columnIDMappingManager;
		IDType internalColumnIDType;
		IDType externalColumnIDType = IDType.getIDType(dataSetDescription.getColumnIDSpecification().getIdType());

		IDTypeParsingRules parsingRules = null;
		if (dataSetDescription.getColumnIDSpecification().getIdTypeParsingRules() != null)
			parsingRules = dataSetDescription.getColumnIDSpecification().getIdTypeParsingRules();
		else if (externalColumnIDType.getIdTypeParsingRules() != null)
			parsingRules = externalColumnIDType.getIdTypeParsingRules();

		if (!dataDomain.getDataSetDescription().isTransposeMatrix()) {
			columnIDMappingManager = dataDomain.getDimensionIDMappingManager();
			internalColumnIDType = dataDomain.getDimensionIDType();
		} else {
			columnIDMappingManager = dataDomain.getRecordIDMappingManager();
			internalColumnIDType = dataDomain.getRecordIDType();
		}

		MappingType columnMappingType = columnIDMappingManager.createMap(internalColumnIDType, externalColumnIDType,
				false, true);

		// ------------- ID parsing stuff ------------------------------
		IDSpecification rowIDSpecification = dataSetDescription.getRowIDSpecification();
		IDCategory rowIDCategory = IDCategory.getIDCategory(rowIDSpecification.getIdCategory());
		IDType externalRowIDType = IDType.getIDType(rowIDSpecification.getIdType());

		IDType internalRowIDType;
		if (dataDomain.isColumnDimension())
			internalRowIDType = dataDomain.getRecordIDType();
		else
			internalRowIDType = dataDomain.getDimensionIDType();

		IDMappingManager rowIDMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(rowIDCategory);

		MappingType rowMappingType = rowIDMappingManager.createMap(internalRowIDType, externalRowIDType, false, true);

		int columnOfRowIDs = dataSetDescription.getColumnOfRowIds();
		int columnOfColumnIDs = dataSetDescription.getRowOfColumnIDs();
		// fixme this is a hack needed to use the same datasetdescription
		int columnOfData = dataSetDescription.getParsingRules().iterator().next().getFromColumn();

		for (int headerCount = 0; headerCount < dataSetDescription.getNumberOfHeaderLines(); headerCount++) {
			reader.readLine();
		}

		String line;
		while ((line = reader.readLine()) != null) {
			String[] splitLine = line.split(dataSetDescription.getDelimiter());
			String columnID = splitLine[columnOfColumnIDs];
			String rowID = splitLine[columnOfRowIDs];

			Integer columnNumber = columnIDMappingManager.getID(externalColumnIDType, internalColumnIDType, columnID);
			if (columnNumber == null) {
				columnNumber = targetRawContainer.size();
				columnIDMappingManager.addMapping(columnMappingType, columnNumber, columnID);
				int nrRows = 0;
				if (targetRawContainer.size() > 0) {
					nrRows = targetRawContainer.get(0).size();
				}
				ArrayList<Float> newList = new ArrayList<Float>(nrRows);
				for (int i = 0; i < nrRows; i++) {
					newList.add(Float.NaN);
				}
				targetRawContainer.add(newList);

			}

			Integer rowNumber = rowIDMappingManager.getID(externalRowIDType, internalRowIDType, rowID);
			ArrayList<Float> column = targetRawContainer.get(columnNumber);
			if (rowNumber == null) {
				rowNumber = column.size();
				rowIDMappingManager.addMapping(rowMappingType, rowNumber, rowID);
				for (ArrayList<Float> tColumn : targetRawContainer) {
					tColumn.add(Float.NaN);
				}
			}
			try {
				float data = Float.parseFloat(splitLine[columnOfData]);
				column.set(rowNumber, data);

			} catch (NumberFormatException nfe) {
				// nothing to do, is already NAN
			}
		}

		int depth = targetRawContainer.get(0).size();
		for (ArrayList<Float> tColumn : targetRawContainer) {
			if (depth != tColumn.size())
				throw new IllegalStateException("Columns don't have the same length" + depth + " / " + tColumn.size());
			float[] fColumn = Floats.toArray(tColumn);
			FloatContainer container = new FloatContainer(fColumn);

			NumericalColumn<FloatContainer, Float> column = new NumericalColumn<>(
					dataSetDescription.getDataDescription());

			column.setRawData(container);

			dataDomain.getTable().addColumn(column);
		}
	}
}
