package org.caleydo.core.manager.specialized.genetic;

import java.util.ArrayList;

import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.graph.pathway.item.vertex.PathwayVertexGraphItem;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.usecase.AUseCase;
import org.caleydo.core.manager.usecase.EUseCaseMode;
import org.caleydo.core.util.clusterer.ClusterState;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.preferences.PreferenceConstants;
import org.caleydo.core.view.opengl.canvas.storagebased.EDataFilterLevel;
import org.caleydo.core.view.opengl.canvas.storagebased.EVAType;

/**
 * Use case specialized to genetic data.
 * 
 * @author Marc Streit
 */
public class GeneticUseCase
	extends AUseCase {

	/**
	 * Constructor.
	 */
	public GeneticUseCase() {

		super();
		eUseCaseMode = EUseCaseMode.GENETIC_DATA;
		sContentLabelSingular = "gene";
		sContentLabelPlural = "genes";
	}

	/**
	 * Initializes a virtual array with all elements, according to the data filters, as defined in
	 * {@link EDataFilterLevel}.
	 */
	protected final void initFullVA() {

		String sLevel =
			GeneralManager.get().getPreferenceStore().getString(PreferenceConstants.DATA_FILTER_LEVEL);
		if (sLevel.equals("complete")) {
			dataFilterLevel = EDataFilterLevel.COMPLETE;
		}
		else if (sLevel.equals("only_mapping")) {
			dataFilterLevel = EDataFilterLevel.ONLY_MAPPING;
		}
		else if (sLevel.equals("only_context")) {
			// Only apply only_context when pathways are loaded
			if (GeneralManager.get().getPathwayManager().size() > 100) {
				dataFilterLevel = EDataFilterLevel.ONLY_CONTEXT;
			}
			else {
				dataFilterLevel = EDataFilterLevel.ONLY_MAPPING;
			}
		}
		else
			// default
			dataFilterLevel = EDataFilterLevel.COMPLETE;

		// initialize virtual array that contains all (filtered) information
		ArrayList<Integer> alTempList = new ArrayList<Integer>(set.depth());

		for (int iCount = 0; iCount < set.depth(); iCount++) {
			if (dataFilterLevel != EDataFilterLevel.COMPLETE
				&& set.getSetType() == ESetType.GENE_EXPRESSION_DATA) {

				// Here we get mapping data for all values
				int iDavidID = GeneticIDMappingHelper.get().getDavidIDFromStorageIndex(iCount);

				if (iDavidID == -1) {
					// generalManager.getLogger().log(new Status(Status.WARNING, GeneralManager.PLUGIN_ID,
					// "Cannot resolve gene to DAVID ID!"));
					continue;
				}

				if (dataFilterLevel == EDataFilterLevel.ONLY_CONTEXT) {
					// Here all values are contained within pathways as well
					PathwayVertexGraphItem tmpPathwayVertexGraphItem =
						GeneralManager.get().getPathwayItemManager().getPathwayVertexGraphItemByDavidId(
							iDavidID);

					if (tmpPathwayVertexGraphItem == null) {
						continue;
					}
				}
			}

			alTempList.add(iCount);
		}

		// TODO: remove possible old virtual array
		int iVAID = set.createStorageVA(alTempList);
		mapVAIDs.put(EVAType.COMPLETE_SELECTION, iVAID);

	}


}
