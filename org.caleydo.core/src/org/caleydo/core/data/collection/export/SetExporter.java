package org.caleydo.core.data.collection.export;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import javax.xml.bind.JAXBException;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.graph.tree.TreePorter;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.data.selection.IVirtualArray;
import org.caleydo.core.manager.IIDMappingManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.usecase.EUseCaseMode;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.storagebased.GLHeatMap;
import org.caleydo.core.view.opengl.canvas.storagebased.GLHierarchicalHeatMap;
import org.caleydo.core.view.opengl.canvas.storagebased.GLParallelCoordinates;

/**
 * Exports data so a CSV file.
 * 
 * @author Alexander Lex
 */
public class SetExporter {

	public enum EWhichViewToExport {
		BUCKET,
		HEAT_MAP,
		PARALLEL_COORDINATES
	}

	public void export(ISet set, String sFileName, EWhichViewToExport eWhichViewToExport) {
		IVirtualArray contentVA = null;
		IVirtualArray storageVA = null;

		Collection<AGLEventListener> views =
			GeneralManager.get().getViewGLCanvasManager().getAllGLEventListeners();
		for (AGLEventListener view : views) {
			if (eWhichViewToExport == EWhichViewToExport.BUCKET) {
				if ((view instanceof GLParallelCoordinates || view instanceof GLHeatMap)
					&& view.isRenderedRemote()) {
					contentVA = set.getVA(view.getContentVAID());
					storageVA = set.getVA(view.getStorageVAID());
					break;
				}
			}
			else if (eWhichViewToExport == EWhichViewToExport.PARALLEL_COORDINATES) {
				if (!view.isRenderedRemote() && view instanceof GLParallelCoordinates) {
					contentVA = set.getVA(view.getContentVAID());
					storageVA = set.getVA(view.getStorageVAID());
					break;
				}
			}
			else if (eWhichViewToExport == EWhichViewToExport.HEAT_MAP) {
				if (!view.isRenderedRemote() && view instanceof GLHierarchicalHeatMap) {
					contentVA = set.getVA(view.getContentVAID());
					storageVA = set.getVA(view.getStorageVAID());
					break;
				}
			}
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
			int index = 0;
			int offset = 0;
			String identifier;
			IIDMappingManager iDMappingManager = GeneralManager.get().getIDMappingManager();
			for (Integer iContentIndex : contentVA) {
				if (GeneralManager.get().getUseCase().getUseCaseMode() == EUseCaseMode.GENETIC_DATA) {

					Integer iRefseqMrnaInt =
						iDMappingManager
							.getID(EMappingType.EXPRESSION_INDEX_2_REFSEQ_MRNA_INT, iContentIndex);
					if (iRefseqMrnaInt == null) {
						continue;
					}

					identifier =
						iDMappingManager.getID(EMappingType.REFSEQ_MRNA_INT_2_REFSEQ_MRNA, iRefseqMrnaInt);
				}
				else {
					identifier =
						iDMappingManager.getID(EMappingType.EXPRESSION_INDEX_2_UNSPECIFIED, iContentIndex);
				}
				out.print(identifier + "\t");
				for (Integer iStorageIndex : storageVA) {
					IStorage storage = set.get(iStorageIndex);
					out.print(storage.getFloat(EDataRepresentation.RAW, iContentIndex));
					out.print("\t");
				}
				if (contentVA.getGroupList() != null) {
					if (cnt == contentVA.getGroupList().get(cluster).getNrElements() - 1) {
						offset = offset + contentVA.getGroupList().get(cluster).getNrElements();
						cluster++;
						cnt = 0;
					}
					else {
						cnt++;
					}

					if (cnt == 0)
						out.print(cluster + "\t" + 1 + "\t");
					else
						out.print(cluster + "\t" + 0 + "\t");
					// if (cluster < contentVA.getGroupList().size()) {
					// if (index == offset + contentVA.getGroupList().get(cluster).getIdxExample())
					// out.print(cluster + "\t" + 1 + "\t");
					// else
					// out.print(cluster + "\t" + 0 + "\t");
					// }
					// else
					// out.print(cluster + "\t" + 0 + "\t");
					index++;
				}
				out.println();
			}

			out.close();

			 // export gene cluster tree to own xml file
			 Tree<ClusterNode> tree = set.getClusteredTreeGenes();
			 if (tree != null) {
			 TreePorter treePorter = new TreePorter();
			 if (treePorter.exportTree(sFileName + "_horizontal_gene.xml", tree) == false)
			 System.out.println("Problem during gene tree export!");
			 }
			 // export experiment cluster tree to own xml file
			 tree = set.getClusteredTreeExps();
			 if (tree != null) {
			 TreePorter treePorter = new TreePorter();
			 if (treePorter.exportTree(sFileName + "_vertical_experiments.xml", tree) == false)
			 System.out.println("Problem during experiments tree export!");
			 }

		}
		catch (IOException e) {

		}
		catch (JAXBException e) {
			e.printStackTrace();
		}
	}
}
