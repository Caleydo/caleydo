package org.caleydo.core.data.collection.export;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.caleydo.core.data.collection.dimension.ADimension;
import org.caleydo.core.data.collection.dimension.DataRepresentation;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.graph.tree.TreePorter;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.mapping.IDMappingManager;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.ATableBasedDataDomain;
import org.caleydo.core.util.clusterer.ClusterNode;

/**
 * Exports data so a CSV file.
 * 
 * @author Alexander Lex
 */
public class DataTableExporter {

	public enum WhichViewToExport {
		BUCKET,
		WHOLE_DATA
	}

	public void exportGroups(DataTable dataTable, String sFileName, ArrayList<Integer> alGenes,
		ArrayList<Integer> alExperiments, IDType targetIDType) {

		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(sFileName)));
			// Writing dimension labels
			out.print("Identifier \t");
			for (Integer iDimensionIndex : alExperiments) {
				out.print(dataTable.get(iDimensionIndex).getLabel());
				out.print("\t");
			}

			out.println();

			// IUseCase useCase = GeneralManager.get().getUseCase(dataTable.getDataTableType().getDataDomain());
			//
			String identifier;
			IDMappingManager iDMappingManager = GeneralManager.get().getIDMappingManager();
			for (Integer recordIndex : alGenes) {
				if (dataTable.getDataDomain().getDataDomainID().equals("org.caleydo.datadomain.genetic")) {
					java.util.Set<String> setRefSeqIDs =
						iDMappingManager.getIDAsSet(dataTable.getDataDomain().getRecordIDType(), targetIDType,
							recordIndex);

					if ((setRefSeqIDs != null && !setRefSeqIDs.isEmpty())) {
						identifier = (String) setRefSeqIDs.toArray()[0];
					}
					else {
						continue;
					}
				}
				else {
					identifier =
						iDMappingManager.getID(dataTable.getDataDomain().getRecordIDType(), targetIDType,
							recordIndex);
				}
				out.print(identifier + "\t");
				for (Integer iDimensionIndex : alExperiments) {
					ADimension dimension = dataTable.get(iDimensionIndex);
					out.print(dimension.getFloat(DataRepresentation.RAW, recordIndex));
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

	@SuppressWarnings("unused")
	public void export(DataTable dataTable, String sFileName, WhichViewToExport eWhichViewToExport,
		IDType targetIDType) {
		RecordVirtualArray recordVA = null;
		DimensionVirtualArray dimensionVA = null;

		ATableBasedDataDomain dataDomain = dataTable.getDataDomain();

		if (eWhichViewToExport == WhichViewToExport.BUCKET) {

			recordVA = dataDomain.getRecordVA(DataTable.RECORD_CONTEXT);
			dimensionVA = dataDomain.getDimensionVA(DataTable.DIMENSION);
		}
		else if (eWhichViewToExport == WhichViewToExport.WHOLE_DATA) {
			recordVA = dataDomain.getRecordVA(DataTable.RECORD);
			dimensionVA = dataDomain.getDimensionVA(DataTable.DIMENSION);
		}

		if (recordVA == null || dimensionVA == null)
			throw new IllegalStateException("Not sure which VA to take.");

		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(sFileName)));

			// Writing dimension labels
			out.print("Identifier \t");
			for (Integer iDimensionIndex : dimensionVA) {
				ADimension dimension = dataTable.get(iDimensionIndex);
				out.print(dimension.getLabel());
				out.print("\t");

				if (dimension.containsDataRepresentation(DataRepresentation.UNCERTAINTY_RAW)) {
					out.print("Uncertainty\t");
				}
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
			IDMappingManager iDMappingManager = GeneralManager.get().getIDMappingManager();
			for (Integer recordID : recordVA) {
				if (dataDomain.getDataDomainID().equals("org.caleydo.datadomain.genetic")) {

					// FIXME: Due to new mapping system, a mapping involving expression index can return a Set
					// of
					// values, depending on the IDType that has been specified when loading expression data.
					// Possibly a different handling of the Set is required.
					// java.util.Set<String> setRefSeqIDs =
					// iDMappingManager.getIDAsSet(dataTable.getDataDomain().getContentIDType(), targetIDType,
					// recordID);

					java.util.Set<String> setRefSeqIDs =
						iDMappingManager.getIDAsSet(dataTable.getDataDomain().getRecordIDType(),
							IDType.getIDType("REFSEQ_MRNA"), recordID);

					if ((setRefSeqIDs != null && !setRefSeqIDs.isEmpty())) {
						identifier = (String) setRefSeqIDs.toArray()[0];
					}
					else {
						continue;
					}
					// Integer iRefseqMrnaInt =
					// iDMappingManager.getID(EIDType.EXPRESSION_INDEX, EIDType.REFSEQ_MRNA_INT,
					// recordIndex);
					// if (iRefseqMrnaInt == null) {
					// continue;
					// }
					//
					// identifier =
					// iDMappingManager.getID(EIDType.REFSEQ_MRNA_INT, EIDType.REFSEQ_MRNA, iRefseqMrnaInt);
				}
				else {
					identifier =
						iDMappingManager.getID(dataTable.getDataDomain().getRecordIDType(), targetIDType,
							recordID);
				}
				out.print(identifier + "\t");
				for (Integer iDimensionIndex : dimensionVA) {
					ADimension dimension = dataTable.get(iDimensionIndex);
					out.print(dimension.getFloat(DataRepresentation.RAW, recordID));
					out.print("\t");

					if (dimension.containsDataRepresentation(DataRepresentation.UNCERTAINTY_RAW)) {
						out.print(dimension.getFloat(DataRepresentation.UNCERTAINTY_RAW, recordID));
						out.print("\t");
					}
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

	// FIXME: implement different content data / dimension data instances
	public void exportTrees(DataTable dataTable, String directory) {
		try {
			// export gene cluster tree to own xml file
			Tree<ClusterNode> tree = dataTable.getRecordData(DataTable.RECORD).getRecordTree();
			if (tree != null) {
				TreePorter treePorter = new TreePorter();
				treePorter.setDataDomain(dataTable.getDataDomain());
				treePorter.exportTree(directory + "/horizontal_gene.xml", tree);
			}
			// export experiment cluster tree to own xml file
			tree = dataTable.getDimensionData(DataTable.DIMENSION).getDimensionTree();
			if (tree != null) {
				TreePorter treePorter = new TreePorter();
				treePorter.setDataDomain(dataTable.getDataDomain());
				treePorter.exportTree(directory + "/vertical_experiments.xml", tree);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
