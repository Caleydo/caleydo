package org.caleydo.core.manager.specialized.genetic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.graph.pathway.item.vertex.PathwayVertexGraphItem;
import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.EVAType;
import org.caleydo.core.data.selection.IVirtualArray;
import org.caleydo.core.data.selection.delta.DeltaConverter;
import org.caleydo.core.data.selection.delta.IVirtualArrayDelta;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.usecase.AUseCase;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.manager.usecase.EDataFilterLevel;
import org.caleydo.core.util.preferences.PreferenceConstants;

/**
 * Use case specialized to genetic data.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
@XmlType
@XmlRootElement
public class GeneticUseCase
	extends AUseCase {

	/**
	 * <code>TRUE</code>if only pathways can be displayed (no gene-expression data), <code>FALSE</code>
	 * otherwise
	 */
	private boolean pathwayViewerMode;

	/**
	 * Organism on which the genetic analysis data bases on.
	 */
	private EOrganism eOrganism = EOrganism.HOMO_SAPIENS;

	/**
	 * Constructor.
	 */
	public GeneticUseCase() {

		super();
		pathwayViewerMode = false;
		useCaseMode = EDataDomain.GENETIC_DATA;
		contentLabelSingular = "gene";
		contentLabelPlural = "genes";

		possibleViews = new ArrayList<EManagedObjectType>();
		// possibleViews.add(EManagedObjectType.GL_HEAT_MAP);
		possibleViews.add(EManagedObjectType.GL_PARALLEL_COORDINATES);
		possibleViews.add(EManagedObjectType.GL_HIER_HEAT_MAP);

		possibleIDCategories = new HashMap<EIDCategory, Boolean>();
		possibleIDCategories.put(EIDCategory.GENE, null);
		possibleIDCategories.put(EIDCategory.EXPERIMENT, null);

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

				Integer iDavidID = null;
				// Here we get mapping data for all values
				// FIXME: Due to new mapping system, a mapping involving expression index can return a Set of
				// values, depending on the IDType that has been specified when loading expression data.
				// Possibly a different handling of the Set is required.
				Set<Integer> setDavidIDs =
					GeneralManager.get().getIDMappingManager().getIDAsSet(EIDType.EXPRESSION_INDEX,
						EIDType.DAVID, iCount);

				if ((setDavidIDs != null && !setDavidIDs.isEmpty())) {
					iDavidID = (Integer) setDavidIDs.toArray()[0];
				}
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

	public void setOrganism(EOrganism eOrganism) {
		this.eOrganism = eOrganism;
	}

	public EOrganism getOrganism() {
		return eOrganism;
	}

	@Override
	public void handleVirtualArrayUpdate(IVirtualArrayDelta vaDelta, String info) {
		EIDCategory targetCategory = vaDelta.getIDType().getCategory();
		if (!(targetCategory == EIDCategory.EXPERIMENT || targetCategory == EIDCategory.GENE))
			return;

		Integer vaID = mapVAIDs.get(vaDelta.getVAType());

		if (targetCategory == EIDCategory.GENE && vaDelta.getIDType() != EIDType.EXPRESSION_INDEX)
			vaDelta = DeltaConverter.convertDelta(EIDType.EXPRESSION_INDEX, vaDelta);
		IVirtualArray va = set.getVA(vaID);

		va.setDelta(vaDelta);
	}
}
