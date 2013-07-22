/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.collection.table;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.io.DataDescription;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.NumericalProperties;
import org.caleydo.core.io.parser.ascii.LinearDataParser;
import org.caleydo.core.io.parser.ascii.TabularDataParser;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Utility class that features creating, loading and saving sets and dimensions.
 *
 * @author Werner Puff
 * @author Alexander Lex
 */
public class TableUtils {

	/**
	 * Creates the {@link Table} from a previously prepared dimension definition.
	 *
	 * @param dataDomain
	 * @param createDefaultDimensionPerspectives
	 * @param createDefaultRecordPerspective
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void loadData(ATableBasedDataDomain dataDomain, DataSetDescription dataSetDescription,
			boolean createDefaultDimensionPerspectives, boolean createDefaultRecordPerspective) {

		// --------- data loading ---------------
		Table table;

		if (dataSetDescription.getDataDescription() == null) {
			table = new Table(dataDomain);
		} else {
			DataDescription dataDescription = dataSetDescription.getDataDescription();
			if (dataDescription.getNumericalProperties() != null) {

				NumericalTable nTable = new NumericalTable(dataDomain);
				table = nTable;

				NumericalProperties numericalProperties = dataDescription.getNumericalProperties();

				nTable.setDataCenter(numericalProperties.getDataCenter());

				if (numericalProperties.getMin() != null) {
					nTable.setMin(numericalProperties.getMin());
				}
				if (numericalProperties.getMax() != null) {
					nTable.setMax(numericalProperties.getMax());
				}

				nTable.setDefaultDataTransformation(sanitize(numericalProperties.getDataTransformation()));

			} else if (dataDescription.getCategoricalClassDescription() != null) {
				CategoricalClassDescription<?> catClassDescr = dataDescription.getCategoricalClassDescription();

				CategoricalTable cTable;
				switch (catClassDescr.getRawDataType()) {
				case INTEGER:
					cTable = new CategoricalTable<Integer>(dataDomain);
					break;
				case STRING:
					cTable = new CategoricalTable<String>(dataDomain);
					break;
				case FLOAT:
				default:
					throw new UnsupportedOperationException("Float not supported for categorical data");

				}
				cTable.setCategoryDescritions(catClassDescr);

				table = cTable;
			} else {
				throw new IllegalStateException("DataDescription must contain categorical or numerical definitions"
						+ dataDescription);
			}
		}
		dataDomain.setTable(table);

		if (!dataSetDescription.isLinearSource()) {
			TabularDataParser parser = new TabularDataParser(dataDomain, dataSetDescription);
			parser.loadData();
		} else {
			LinearDataParser parser = new LinearDataParser(dataDomain, dataSetDescription);
			parser.loadData();
		}

		table.normalize();

		if (createDefaultDimensionPerspectives)
			table.createDefaultDimensionPerspectives();

		if (createDefaultRecordPerspective)
			table.createDefaultRecordPerspectives();
	}

	/**
	 * cleans the input data transformation to known one if possible
	 *
	 * @param dataTransformation
	 * @return
	 */
	private static String sanitize(String dataTransformation) {
		if (Table.Transformation.NONE.equalsIgnoreCase(dataTransformation))
			return Table.Transformation.NONE;
		if (NumericalTable.Transformation.LOG10.equalsIgnoreCase(dataTransformation))
			return NumericalTable.Transformation.LOG10;
		if (NumericalTable.Transformation.LOG2.equalsIgnoreCase(dataTransformation))
			return NumericalTable.Transformation.LOG2;
		return dataTransformation;
	}

	/**
	 * Exports the dataset identified through the perspectives to the file specified.
	 *
	 * @param dataDomain
	 * @param fileName
	 * @param recordPerspective
	 * @param dimensionPerspective
	 * @param targetRecordIDType
	 *            the id type to be used in the file. If this is null the {@link IDCategory#getHumanReadableIDType()}
	 *            will be used.
	 * @param targetDimensionIDType
	 *            same as targetRecordIDType for dimensions
	 * @param includeClusterInfo
	 *            true if you want to add information about the clustering to the file, else false
	 *
	 * @return true if export was successful, else false.
	 */
	public static boolean export(ATableBasedDataDomain dataDomain, String fileName, Perspective recordPerspective,
			Perspective dimensionPerspective, IDType targetRecordIDType, IDType targetDimensionIDType,
			boolean includeClusterInfo) {

		if (targetRecordIDType == null)
			targetRecordIDType = dataDomain.getRecordIDCategory().getHumanReadableIDType();

		if (targetDimensionIDType == null)
			targetDimensionIDType = dataDomain.getDimensionIDCategory().getHumanReadableIDType();

		IDType rowTargetIDType;
		IDType rowSourceIDType;

		IDType colTargetIDType;
		IDType colSourceIDType;

		VirtualArray rowVA;
		VirtualArray colVA;

		if (dataDomain.isColumnDimension()) {
			rowVA = recordPerspective.getVirtualArray();
			colVA = dimensionPerspective.getVirtualArray();
			rowTargetIDType = targetRecordIDType;
			colTargetIDType = targetDimensionIDType;

			rowSourceIDType = dataDomain.getRecordIDType();
			colSourceIDType = dataDomain.getDimensionIDType();

		} else {
			rowVA = dimensionPerspective.getVirtualArray();
			colVA = recordPerspective.getVirtualArray();
			rowTargetIDType = targetDimensionIDType;
			colTargetIDType = targetRecordIDType;

			rowSourceIDType = dataDomain.getDimensionIDType();
			colSourceIDType = dataDomain.getRecordIDType();
		}

		if (rowVA == null || colVA == null)
			throw new IllegalArgumentException("VAs in perspectives were null");

		IDMappingManager rowIDMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(rowSourceIDType);
		IDMappingManager colIDMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(colSourceIDType);

		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));

			// Writing dimension labels

			// first cell
			out.print("Identifier \t");

			for (Integer colID : colVA) {
				Set<Object> colTargetIDs = colIDMappingManager.getIDAsSet(colSourceIDType, colTargetIDType, colID);
				String id = "";
				for (Object colTargetID : colTargetIDs) {
					id = colTargetID.toString();
					// here we only use the first id
					break;
				}
				out.print(id + "\t");
			}

			if (includeClusterInfo && rowVA.getGroupList() != null)
				out.print("Cluster_Number\tCluster_Repr\t");

			out.println();

			int cnt = -1;
			int cluster = 0;
			int example = 0;
			int offset = 0;
			String id;
			for (Integer rowID : rowVA) {

				Set<Object> rowTargetIDs = rowIDMappingManager.getIDAsSet(rowSourceIDType, rowTargetIDType, rowID);
				id = "";
				for (Object rowTargetID : rowTargetIDs) {
					id = rowTargetID.toString();
					// here we only use the first id
					break;
				}
				out.print(id + "\t");

				for (Integer colID : colVA) {
					if (dataDomain.isColumnDimension()) {
						out.print(dataDomain.getTable().getRawAsString(colID, rowID));
					} else {
						out.print(dataDomain.getTable().getRawAsString(rowID, colID));
					}

					out.print("\t");
				}

				if (includeClusterInfo) {
					// export cluster info for rows
					if (rowVA.getGroupList() != null) {
						if (cnt == rowVA.getGroupList().get(cluster).getSize() - 1) {
							offset = offset + rowVA.getGroupList().get(cluster).getSize();
							cluster++;
							cnt = 0;
						} else {
							cnt++;
						}

						example = rowVA.getGroupList().get(cluster).getRepresentativeElementIndex();

						out.print(cluster + "\t" + example + "\t");
					}
				}
				out.println();
			}

			if (!includeClusterInfo) {
				out.close();
				return true;
			}

			// export cluster info for cols
			if (colVA.getGroupList() != null) {

				String clusterNr = "Cluster\t";
				String clusterRep = "Representative Element\t";

				cluster = 0;
				cnt = -1;

				for (@SuppressWarnings("unused")
				Integer colIndex : colVA) {
					if (cnt == colVA.getGroupList().get(cluster).getSize() - 1) {
						offset = offset + colVA.getGroupList().get(cluster).getSize();
						cluster++;
						cnt = 0;
					} else {
						cnt++;
					}

					example = colVA.getGroupList().get(cluster).getRepresentativeElementIndex();

					clusterNr += cluster + "\t";
					clusterRep += example + "\t";
				}

				clusterNr += "\n";
				clusterRep += "\n";

				out.print(clusterNr);
				out.print(clusterRep);

			}

			out.close();

		} catch (IOException e) {
			Logger.log(new Status(IStatus.ERROR, "TableUtils", "Failed to export data.", e));
			return false;
		}
		return true;

	}

}
