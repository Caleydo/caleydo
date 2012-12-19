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
package org.caleydo.core.data.collection.export;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import org.caleydo.core.data.collection.dimension.DataRepresentation;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.DimensionPerspective;
import org.caleydo.core.data.perspective.variable.RecordPerspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;

/**
 * Exports data so a CSV file.
 *
 * @author Alexander Lex
 */
public class DataTableExporter {

	/** Fixme this needs to be re-written */
	// public void exportGroups(ATableBasedDataDomain dataDomain, String
	// sFileName,
	// ArrayList<Integer> recordsToExport, ArrayList<Integer>
	// dimensionsToExport,
	// IDType targetIDType) {
	//
	// try {
	// PrintWriter out = new PrintWriter(new BufferedWriter(
	// new FileWriter(sFileName)));
	// // Writing dimension labels
	// out.print("Identifier \t");
	// for (Integer dimensionID : dimensionsToExport) {
	// dataDomain.getDimensionLabel(dimensionID);
	// }
	//
	// out.println();
	//
	// // IUseCase useCase =
	// // GeneralManager.get().getUseCase(table.getTableType().getDataDomain());
	// //
	// String identifier;
	// IDMappingManager idMappingManager =
	// dataDomain.getRecordIDMappingManager();
	// for (Integer recordIndex : recordsToExport) {
	// if
	// (dataDomain.getDataDomainID().equals("org.caleydo.datadomain.genetic")) {
	// java.util.Set<String> setRefSeqIDs = idMappingManager.getIDAsSet(
	// dataDomain.getRecordIDType(), targetIDType, recordIndex);
	//
	// if ((setRefSeqIDs != null && !setRefSeqIDs.isEmpty())) {
	// identifier = (String) setRefSeqIDs.toArray()[0];
	// } else {
	// continue;
	// }
	// } else {
	// identifier = idMappingManager.getID(dataDomain.getRecordIDType(),
	// targetIDType, recordIndex);
	// }
	// out.print(identifier + "\t");
	// for (Integer dimensionIndex : dimensionsToExport) {
	// out.print(dataDomain.getTable().getFloat(DataRepresentation.RAW,
	// recordIndex, dimensionIndex));
	// out.print("\t");
	// }
	// out.println();
	// }
	//
	// out.close();
	//
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }

	/**
	 * Exports the dataset identified through the perspectives to the file
	 * specified.
	 *
	 * @param dataDomain
	 * @param fileName
	 * @param recordPerspective
	 * @param dimensionPerspective
	 * @param targetRecordIDType
	 *            the id type to be used in the file. If this is null the
	 *            {@link IDCategory#getHumanReadableIDType()} will be used.
	 * @param targetDimensionIDType
	 *            same as targetRecordIDType for dimensions
	 * @param includeClusterInfo
	 *            true if you want to add information about the clustering to
	 *            the file, else false
	 */
	public static void export(ATableBasedDataDomain dataDomain, String fileName,
			RecordPerspective recordPerspective,
			DimensionPerspective dimensionPerspective, IDType targetRecordIDType,
			IDType targetDimensionIDType, boolean includeClusterInfo) {

		if (targetRecordIDType == null)
			targetRecordIDType = dataDomain.getRecordIDCategory()
					.getHumanReadableIDType();

		if (targetDimensionIDType == null)
			targetDimensionIDType = dataDomain.getDimensionIDCategory()
					.getHumanReadableIDType();

		IDType rowTargetIDType;
		IDType rowSourceIDType;

		IDType colTargetIDType;
		IDType colSourceIDType;

		VirtualArray<?, ?, ?> rowVA;
		VirtualArray<?, ?, ?> colVA;

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

		IDMappingManager rowIDMappingManager = IDMappingManagerRegistry.get()
				.getIDMappingManager(rowSourceIDType);
		IDMappingManager colIDMappingManager = IDMappingManagerRegistry.get()
				.getIDMappingManager(colSourceIDType);

		try {
			PrintWriter out = new PrintWriter(
					new BufferedWriter(new FileWriter(fileName)));

			// Writing dimension labels

			// first cell
			out.print("Identifier \t");

			for (Integer colID : colVA) {
				Set<Object> colTargetIDs = colIDMappingManager.getIDAsSet(
						colSourceIDType, colTargetIDType, colID);
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

				Set<Object> rowTargetIDs = rowIDMappingManager.getIDAsSet(
						rowSourceIDType, rowTargetIDType, rowID);
				id = "";
				for (Object rowTargetID : rowTargetIDs) {
					id = rowTargetID.toString();
					// here we only use the first id
					break;
				}
				out.print(id + "\t");

				for (Integer colID : colVA) {
					if (dataDomain.isColumnDimension()) {
						out.print(dataDomain.getTable().getFloat(DataRepresentation.RAW,
								rowID, colID));
					} else {
						out.print(dataDomain.getTable().getFloat(DataRepresentation.RAW,
								colID, rowID));
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

						example = rowVA.getGroupList().get(cluster)
								.getRepresentativeElementIndex();

						out.print(cluster + "\t" + example + "\t");
					}
				}
				out.println();
			}

			if (!includeClusterInfo) {
				out.close();
				return;
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

					example = colVA.getGroupList().get(cluster)
							.getRepresentativeElementIndex();

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
			e.printStackTrace();
		}

	}
}
