package org.caleydo.core.data.collection.export;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.graph.tree.TreePorter;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.IVirtualArray;
import org.caleydo.core.manager.IIDMappingManager;
import org.caleydo.core.manager.IUseCase;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.usecase.EUseCaseMode;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.view.opengl.canvas.storagebased.EVAType;

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

	public void export(ISet set, String sFileName, EWhichViewToExport eWhichViewToExport) {
		IVirtualArray contentVA = null;
		IVirtualArray storageVA = null;

		IUseCase useCase = GeneralManager.get().getUseCase();

		if (eWhichViewToExport == EWhichViewToExport.BUCKET) {

			contentVA = useCase.getVA(EVAType.CONTENT_CONTEXT);
			storageVA = useCase.getVA(EVAType.STORAGE);
		}
		else if (eWhichViewToExport == EWhichViewToExport.WHOLE_DATA) {
			contentVA = useCase.getVA(EVAType.CONTENT);
			storageVA = useCase.getVA(EVAType.STORAGE);
		}

		if (contentVA == null || storageVA == null)
			throw new IllegalStateException("Not sure which VA to take.");

		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(sFileName)));

			// Writing storage labels
			out.print("Identifier \t");
			for (Integer iStorageIndex : storageVA) {
				out.print(set.get(iStorageIndex).getLabel());
				out.print("\t");
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
			IIDMappingManager iDMappingManager = GeneralManager.get().getIDMappingManager();
			for (Integer iContentIndex : contentVA) {
				if (GeneralManager.get().getUseCase().getUseCaseMode() == EUseCaseMode.GENETIC_DATA) {
					
					// FIXME: Due to new mapping system, a mapping involving expression index can return a Set of
					// values, depending on the IDType that has been specified when loading expression data.
					// Possibly a different handling of the Set is required.
					Set<String> setRefSeqIDs =
						iDMappingManager.getIDAsSet(EIDType.EXPRESSION_INDEX, EIDType.REFSEQ_MRNA,
							iContentIndex);

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
						iDMappingManager.getID(EIDType.EXPRESSION_INDEX, EIDType.UNSPECIFIED, iContentIndex);
				}
				out.print(identifier + "\t");
				for (Integer iStorageIndex : storageVA) {
					IStorage storage = set.get(iStorageIndex);
					out.print(storage.getFloat(EDataRepresentation.RAW, iContentIndex));
					out.print("\t");
				}

				// export partitional cluster info for genes/entities
				if (contentVA.getGroupList() != null) {
					if (cnt == contentVA.getGroupList().get(cluster).getNrElements() - 1) {
						offset = offset + contentVA.getGroupList().get(cluster).getNrElements();
						cluster++;
						cnt = 0;
					}
					else {
						cnt++;
					}

					iExample = contentVA.getGroupList().get(cluster).getIdxExample();

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
					if (cnt == storageVA.getGroupList().get(cluster).getNrElements() - 1) {
						offset = offset + storageVA.getGroupList().get(cluster).getNrElements();
						cluster++;
						cnt = 0;
					}
					else {
						cnt++;
					}

					iExample = storageVA.getGroupList().get(cluster).getIdxExample();

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

	public void exportTrees(ISet set, String directory) {
		try {
			// export gene cluster tree to own xml file
			Tree<ClusterNode> tree = set.getClusteredTreeGenes();
			if (tree != null) {
				TreePorter treePorter = new TreePorter();
				treePorter.exportTree(directory + "/horizontal_gene.xml", tree);
			}
			// export experiment cluster tree to own xml file
			tree = set.getClusteredTreeExps();
			if (tree != null) {
				TreePorter treePorter = new TreePorter();
				treePorter.exportTree(directory + "/vertical_experiments.xml", tree);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
