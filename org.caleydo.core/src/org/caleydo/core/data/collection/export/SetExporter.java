package org.caleydo.core.data.collection.export;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.data.selection.IVirtualArray;
import org.caleydo.core.manager.IIDMappingManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.storagebased.GLHierarchicalHeatMap;
import org.caleydo.core.view.opengl.canvas.storagebased.GLParallelCoordinates;

/**
 * Exports data so a CSV file.
 * 
 * @author Alexander Lex
 */
public class SetExporter {
	public void export(ISet set, String sFileName, boolean bExportBucketInternal) {
		IVirtualArray contentVA = null;
		IVirtualArray storageVA = null;

		Collection<AGLEventListener> views =
			GeneralManager.get().getViewGLCanvasManager().getAllGLEventListeners();
		for (AGLEventListener view : views) {
			if (view instanceof GLParallelCoordinates && view.isRenderedRemote() && bExportBucketInternal) {
				contentVA = set.getVA(view.getContentVAID());
				storageVA = set.getVA(view.getStorageVAID());
				break;
			}
			if ((view instanceof GLParallelCoordinates || view instanceof GLHierarchicalHeatMap)
				&& !bExportBucketInternal) {
				contentVA = set.getVA(view.getContentVAID());
				storageVA = set.getVA(view.getStorageVAID());
				break;
			}
		}

		if (contentVA == null || storageVA == null)
			throw new IllegalStateException("Not sure which VA to take.");

		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(sFileName)));

			// Writing storage labels
			out.print("RefSeq ID\t");
			for (Integer iStorageIndex : storageVA) {
				out.print(set.get(iStorageIndex).getLabel());
				out.print("\t");
			}
			out.println();

			for (Integer iContentIndex : contentVA) {
				IIDMappingManager iDMappingManager = GeneralManager.get().getIDMappingManager();
				Integer iRefseqMrnaInt =
					iDMappingManager.getID(EMappingType.EXPRESSION_INDEX_2_REFSEQ_MRNA_INT, iContentIndex);
				if (iRefseqMrnaInt == null)
					continue;

				String sRefseqMrna =
					iDMappingManager.getID(EMappingType.REFSEQ_MRNA_INT_2_REFSEQ_MRNA, iRefseqMrnaInt);
				out.print(sRefseqMrna + "\t");
				for (Integer iStorageIndex : storageVA) {
					IStorage storage = set.get(iStorageIndex);
					out.print(storage.getFloat(EDataRepresentation.RAW, iContentIndex));
					out.print("\t");
				}
				out.println();
			}

			out.close();
		}
		catch (IOException e) {

		}
	}
}
