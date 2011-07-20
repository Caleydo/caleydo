package org.caleydo.core.data.collection.export;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.caleydo.core.data.collection.storage.AStorage;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.graph.tree.TreePorter;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.mapping.IDMappingManager;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.StorageVirtualArray;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.ATableBasedDataDomain;
import org.caleydo.core.util.clusterer.ClusterNode;

/**
 * Exports data so a CSV file.
 * 
 * @author Alexander Lex
 */
public class SetExporter {

	public enum EWhichViewToExport {
		BUCKET,
		WHOLE_DATA
	}

	public void exportGroups(DataTable set, String sFileName, ArrayList<Integer> alGenes,
		ArrayList<Integer> alExperiments, IDType targetIDType) {

		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(sFileName)));
			// Writing storage labels
			out.print("Identifier \t");
			for (Integer iStorageIndex : alExperiments) {
				out.print(set.get(iStorageIndex).getLabel());
				out.print("\t");
			}

			out.println();

			// IUseCase useCase = GeneralManager.get().getUseCase(set.getSetType().getDataDomain());
			//
			String identifier;
			IDMappingManager iDMappingManager = GeneralManager.get().getIDMappingManager();
			for (Integer iContentIndex : alGenes) {
				if (set.getDataDomain().getDataDomainID().equals("org.caleydo.datadomain.genetic")) {
					java.util.Set<String> setRefSeqIDs =
						iDMappingManager.getIDAsSet(set.getDataDomain().getContentIDType(), targetIDType,
							iContentIndex);

					if ((setRefSeqIDs != null && !setRefSeqIDs.isEmpty())) {
						identifier = (String) setRefSeqIDs.toArray()[0];
					}
					else {
						continue;
					}
				}
				else {
					identifier =
						iDMappingManager.getID(set.getDataDomain().getContentIDType(), targetIDType,
							iContentIndex);
				}
				out.print(identifier + "\t");
				for (Integer iStorageIndex : alExperiments) {
					AStorage storage = set.get(iStorageIndex);
					out.print(storage.getFloat(EDataRepresentation.RAW, iContentIndex));
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
	public void export(DataTable set, String sFileName, EWhichViewToExport eWhichViewToExport, IDType targetIDType) {
		ContentVirtualArray contentVA = null;
		StorageVirtualArray storageVA = null;

		ATableBasedDataDomain dataDomain = set.getDataDomain();

		if (eWhichViewToExport == EWhichViewToExport.BUCKET) {

			contentVA = dataDomain.getContentVA(DataTable.CONTENT_CONTEXT);
			storageVA = dataDomain.getStorageVA(DataTable.STORAGE);
		}
		else if (eWhichViewToExport == EWhichViewToExport.WHOLE_DATA) {
			contentVA = dataDomain.getContentVA(DataTable.CONTENT);
			storageVA = dataDomain.getStorageVA(DataTable.STORAGE);
		}

		if (contentVA == null || storageVA == null)
			throw new IllegalStateException("Not sure which VA to take.");

		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(sFileName)));

			// Writing storage labels
			out.print("Identifier \t");
			for (Integer iStorageIndex : storageVA) {
				AStorage storage = set.get(iStorageIndex);
				out.print(storage.getLabel());
				out.print("\t");
				
				if (storage.containsUncertaintyData()) {
					out.print("Uncertainty\t");
				}
			}

			if (contentVA.getGroupList() != null)
				out.print("Cluster_Number\tCluster_Repr\t");

			out.println();

			int cnt = -1;
			int cluster = 0;
			int iExample = 0;
			int index = 0;
			int offset = 0;
			String identifier;
			IDMappingManager iDMappingManager = GeneralManager.get().getIDMappingManager();
			for (Integer contentID : contentVA) {
				if (dataDomain.getDataDomainID().equals("org.caleydo.datadomain.genetic")) {

					// FIXME: Due to new mapping system, a mapping involving expression index can return a Set
					// of
					// values, depending on the IDType that has been specified when loading expression data.
					// Possibly a different handling of the Set is required.
					// java.util.Set<String> setRefSeqIDs =
					// iDMappingManager.getIDAsSet(set.getDataDomain().getContentIDType(), targetIDType,
					// contentID);

					java.util.Set<String> setRefSeqIDs =
						iDMappingManager.getIDAsSet(set.getDataDomain().getContentIDType(),
							IDType.getIDType("REFSEQ_MRNA"), contentID);

					if ((setRefSeqIDs != null && !setRefSeqIDs.isEmpty())) {
						identifier = (String) setRefSeqIDs.toArray()[0];
					}
					else {
						continue;
					}
					// Integer iRefseqMrnaInt =
					// iDMappingManager.getID(EIDType.EXPRESSION_INDEX, EIDType.REFSEQ_MRNA_INT,
					// iContentIndex);
					// if (iRefseqMrnaInt == null) {
					// continue;
					// }
					//
					// identifier =
					// iDMappingManager.getID(EIDType.REFSEQ_MRNA_INT, EIDType.REFSEQ_MRNA, iRefseqMrnaInt);
				}
				else {
					identifier =
						iDMappingManager.getID(set.getDataDomain().getContentIDType(), targetIDType,
							contentID);
				}
				out.print(identifier + "\t");
				for (Integer iStorageIndex : storageVA) {
					AStorage storage = set.get(iStorageIndex);
					out.print(storage.getFloat(EDataRepresentation.RAW, contentID));
					out.print("\t");

					if (storage.containsUncertaintyData()) {
						out.print(storage.getFloat(EDataRepresentation.UNCERTAINTY_RAW, contentID));
						out.print("\t");
					}
				}

				// export partitional cluster info for genes/entities
				if (contentVA.getGroupList() != null) {
					if (cnt == contentVA.getGroupList().get(cluster).getSize() - 1) {
						offset = offset + contentVA.getGroupList().get(cluster).getSize();
						cluster++;
						cnt = 0;
					}
					else {
						cnt++;
					}

					iExample = contentVA.getGroupList().get(cluster).getRepresentativeElementIndex();

					out.print(cluster + "\t" + iExample + "\t");

					index++;
				}
				out.println();
			}

			// export partitional cluster info for experiments
			if (storageVA.getGroupList() != null) {

				String stClusterNr = "Cluster_Number\t";
				String stClusterRep = "Cluster_Repr\t";

				cluster = 0;
				cnt = -1;

				for (Integer iStorageIndex : storageVA) {
					if (cnt == storageVA.getGroupList().get(cluster).getSize() - 1) {
						offset = offset + storageVA.getGroupList().get(cluster).getSize();
						cluster++;
						cnt = 0;
					}
					else {
						cnt++;
					}

					iExample = storageVA.getGroupList().get(cluster).getRepresentativeElementIndex();

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

	// FIXME: implement different content data / storage data instances
	public void exportTrees(DataTable set, String directory) {
		try {
			// export gene cluster tree to own xml file
			Tree<ClusterNode> tree = set.getContentData(DataTable.CONTENT).getContentTree();
			if (tree != null) {
				TreePorter treePorter = new TreePorter();
				treePorter.setDataDomain(set.getDataDomain());
				treePorter.exportTree(directory + "/horizontal_gene.xml", tree);
			}
			// export experiment cluster tree to own xml file
			tree = set.getStorageData(DataTable.STORAGE).getStorageTree();
			if (tree != null) {
				TreePorter treePorter = new TreePorter();
				treePorter.setDataDomain(set.getDataDomain());
				treePorter.exportTree(directory + "/vertical_experiments.xml", tree);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
