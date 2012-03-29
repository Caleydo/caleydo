package org.caleydo.core.data.collection.export;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import org.caleydo.core.data.collection.dimension.DataRepresentation;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.mapping.IDMappingManager;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.perspective.RecordPerspective;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;

/**
 * Exports data so a CSV file.
 * 
 * @author Alexander Lex
 */
public class DataTableExporter {

	public void exportGroups(ATableBasedDataDomain dataDomain, String sFileName,
		ArrayList<Integer> recordsToExport, ArrayList<Integer> dimensionsToExport, IDType targetIDType) {

		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(sFileName)));
			// Writing dimension labels
			out.print("Identifier \t");
			for (Integer dimensionID : dimensionsToExport) {
				dataDomain.getDimensionLabel(dimensionID);
			}

			out.println();

			// IUseCase useCase = GeneralManager.get().getUseCase(table.getTableType().getDataDomain());
			//
			String identifier;
			IDMappingManager idMappingManager = dataDomain.getRecordIDMappingManager();
			for (Integer recordIndex : recordsToExport) {
				if (dataDomain.getDataDomainID().equals("org.caleydo.datadomain.genetic")) {
					java.util.Set<String> setRefSeqIDs =
						idMappingManager.getIDAsSet(dataDomain.getRecordIDType(), targetIDType, recordIndex);

					if ((setRefSeqIDs != null && !setRefSeqIDs.isEmpty())) {
						identifier = (String) setRefSeqIDs.toArray()[0];
					}
					else {
						continue;
					}
				}
				else {
					identifier =
						idMappingManager.getID(dataDomain.getRecordIDType(), targetIDType, recordIndex);
				}
				out.print(identifier + "\t");
				for (Integer dimensionIndex : dimensionsToExport) {
					out.print(dataDomain.getTable().getFloat(DataRepresentation.RAW, recordIndex,
						dimensionIndex));
					out.print("\t");
				}
				out.println();
			}

			out.close();

		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void export(ATableBasedDataDomain dataDomain, String sFileName,
		RecordPerspective recordPerspective, DimensionPerspective dimensionPerspective, IDType targetIDType) {
		RecordVirtualArray recordVA = null;
		DimensionVirtualArray dimensionVA = null;

		recordVA = recordPerspective.getVirtualArray();
		dimensionVA = dimensionPerspective.getVirtualArray();

		if (recordVA == null || dimensionVA == null)
			throw new IllegalStateException("Not sure which VA to take.");

		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(sFileName)));

			// Writing dimension labels
			out.print("Identifier \t");

			for (Integer dimensionID : dimensionVA) {
				out.print(dataDomain.getDimensionLabel(dimensionID) + "\t");
			}

			if (recordVA.getGroupList() != null)
				out.print("Cluster_Number\tCluster_Repr\t");

			out.println();

			int cnt = -1;
			int cluster = 0;
			int iExample = 0;
			int index = 0;
			int offset = 0;
			String identifier;
			IDMappingManager recordIDMappingManager = dataDomain.getRecordIDMappingManager();
			for (Integer recordID : recordVA) {
				if (dataDomain.getDataDomainType().equals("org.caleydo.datadomain.genetic")) {

					java.util.Set<String> setRefSeqIDs =
						recordIDMappingManager.getIDAsSet(dataDomain.getRecordIDType(),
							IDType.getIDType("REFSEQ_MRNA"), recordID);

					if ((setRefSeqIDs != null && !setRefSeqIDs.isEmpty())) {
						identifier = (String) setRefSeqIDs.toArray()[0];
					}
					else {
						continue;
					}
				}
				else {
					identifier =
						recordIDMappingManager.getID(dataDomain.getRecordIDType(), targetIDType, recordID);
				}
				out.print(identifier + "\t");
				for (Integer dimensionID : dimensionVA) {

					out.print(dataDomain.getTable().getFloat(DataRepresentation.RAW, recordID, dimensionID));

					out.print("\t");

					// if (dimension.containsDataRepresentation(DataRepresentation.UNCERTAINTY_RAW)) {
					// out.print(dimension.getFloat(DataRepresentation.UNCERTAINTY_RAW, recordID));
					// out.print("\t");
					// }
				}

				// export partitional cluster info for genes/entities
				if (recordVA.getGroupList() != null) {
					if (cnt == recordVA.getGroupList().get(cluster).getSize() - 1) {
						offset = offset + recordVA.getGroupList().get(cluster).getSize();
						cluster++;
						cnt = 0;
					}
					else {
						cnt++;
					}

					iExample = recordVA.getGroupList().get(cluster).getRepresentativeElementIndex();

					out.print(cluster + "\t" + iExample + "\t");

					index++;
				}
				out.println();
			}

			// export partitional cluster info for experiments
			if (dimensionVA.getGroupList() != null) {

				String stClusterNr = "Cluster_Number\t";
				String stClusterRep = "Cluster_Repr\t";

				cluster = 0;
				cnt = -1;

				for (Integer iDimensionIndex : dimensionVA) {
					if (cnt == dimensionVA.getGroupList().get(cluster).getSize() - 1) {
						offset = offset + dimensionVA.getGroupList().get(cluster).getSize();
						cluster++;
						cnt = 0;
					}
					else {
						cnt++;
					}

					iExample = dimensionVA.getGroupList().get(cluster).getRepresentativeElementIndex();

					stClusterNr += cluster + "\t";
					stClusterRep += iExample + "\t";
				}

				stClusterNr += "\n";
				stClusterRep += "\n";

				out.print(stClusterNr);
				out.print(stClusterRep);

			}

			out.close();

		}
		catch (IOException e) {
			e.printStackTrace();
		}

	}
}
