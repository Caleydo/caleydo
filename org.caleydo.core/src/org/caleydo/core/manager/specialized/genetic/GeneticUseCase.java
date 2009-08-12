package org.caleydo.core.manager.specialized.genetic;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.graph.pathway.item.vertex.PathwayVertexGraphItem;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.usecase.AUseCase;
import org.caleydo.core.manager.usecase.EUseCaseMode;
import org.caleydo.core.util.preferences.PreferenceConstants;
import org.caleydo.core.view.opengl.canvas.storagebased.EDataFilterLevel;
import org.caleydo.core.view.opengl.canvas.storagebased.EVAType;

/**
 * Use case specialized to genetic data.
 * 
 * @author Marc Streit
 */
@XmlType
@XmlRootElement
public class GeneticUseCase
	extends AUseCase {

	/** <code>TRUE</code>if only pathways can be displayed (no gene-expression data), <code>FALSE</code>otherwise */
	private boolean pathwayViewerMode;
	
	/**
	 * Constructor.
	 */
	public GeneticUseCase() {

		super();
		pathwayViewerMode = false;
		useCaseMode = EUseCaseMode.GENETIC_DATA;
		contentLabelSingular = "gene";
		contentLabelPlural = "genes";
	}

	/**
	 * Initializes a virtual array with all elements, according to the data filters, as defined in
	 * {@link EDataFilterLevel}.
	 */
	@Override
	protected final void initFullVA() {

		// TODO preferences seem not to be initialized here either in XML case
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
			// TODO we need to wait for the pathways to be loaded here!
			if (GeneralManager.get().getPathwayManager().size() > 100) {
				dataFilterLevel = EDataFilterLevel.ONLY_CONTEXT;
			}
			else {
				dataFilterLevel = EDataFilterLevel.ONLY_MAPPING;
			}
		}
		else
			dataFilterLevel = EDataFilterLevel.COMPLETE;

		// initialize virtual array that contains all (filtered) information
		ArrayList<Integer> alTempList = new ArrayList<Integer>(set.depth());

		for (int iCount = 0; iCount < set.depth(); iCount++) {
			if (dataFilterLevel != EDataFilterLevel.COMPLETE
				&& set.getSetType() == ESetType.GENE_EXPRESSION_DATA) {

				// Here we get mapping data for all values
				Integer iDavidID =
					GeneralManager.get().getIDMappingManager().getID(EIDType.EXPRESSION_INDEX, EIDType.DAVID,
						iCount);
				// GeneticIDMappingHelper.get().getDavidIDFromStorageIndex(iCount);

				if (iDavidID == null) {
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
		int iVAID = set.createVA(EVAType.CONTENT, alTempList);
		mapVAIDs.put(EVAType.CONTENT, iVAID);

	}

	public boolean isPathwayViewerMode() {
		return pathwayViewerMode;
	}

	public void setPathwayViewerMode(boolean pathwayViewerMode) {
		this.pathwayViewerMode = pathwayViewerMode;
	}


}
