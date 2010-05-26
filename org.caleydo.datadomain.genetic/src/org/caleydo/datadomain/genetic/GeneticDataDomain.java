package org.caleydo.datadomain.genetic;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.graph.pathway.item.vertex.PathwayVertexGraphItem;
import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.data.selection.StorageVirtualArray;
import org.caleydo.core.data.selection.delta.ContentVADelta;
import org.caleydo.core.data.selection.delta.DeltaConverter;
import org.caleydo.core.data.selection.delta.StorageVADelta;
import org.caleydo.core.manager.datadomain.ADataDomain;
import org.caleydo.core.manager.datadomain.EDataDomain;
import org.caleydo.core.manager.datadomain.EDataFilterLevel;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.preferences.PreferenceConstants;

/**
 * Use case specialized to genetic data.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
@XmlType
@XmlRootElement
public class GeneticDataDomain
	extends ADataDomain {

	/**
	 * <code>TRUE</code>if only pathways can be displayed (no gene-expression data), <code>FALSE</code>
	 * otherwise
	 */
	private boolean pathwayViewerMode;



	/**
	 * Constructor.
	 */
	public GeneticDataDomain() {

		super();
		pathwayViewerMode = false;
		useCaseMode = EDataDomain.GENETIC_DATA;
		contentLabelSingular = "gene";
		contentLabelPlural = "genes";

		possibleViews = new ArrayList<String>();
		possibleViews.add("org.caleydo.view.parcoords");
		possibleViews.add("org.caleydo.view.heatmap.hierarchical");

		possibleIDCategories = new HashMap<EIDCategory, String>();
		possibleIDCategories.put(EIDCategory.GENE, null);
		possibleIDCategories.put(EIDCategory.EXPERIMENT, null);
		contentIDType = EIDType.EXPRESSION_INDEX;
		storageIDType = EIDType.EXPERIMENT_INDEX;

	}

	@Override
	public void setSet(ISet set) {
		super.setSet(set);

	}

	/**
	 * Initializes a virtual array with all elements, according to the data filters, as defined in
	 * {@link EDataFilterLevel}.
	 */

	@Override
	protected void initFullVA() {

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
		ContentVirtualArray contentVA = new ContentVirtualArray(ContentVAType.CONTENT, alTempList);
//		removeDuplicates(contentVA);
		set.setContentVA(ContentVAType.CONTENT, contentVA);
	}

//	public ContentVirtualArray removeDuplicates(ContentVirtualArray contentVirtualArray) {
//		Map<Object, Object> idMap =
//			GeneralManager.get().getIDMappingManager().getMap(EMappingType.REFSEQ_MRNA_INT_2_DAVID);
//		for (Object idObject : idMap.keySet()) {
//			Integer id = (Integer) idObject;
//			ArrayList<Integer> indices = contentVirtualArray.indicesOf(id);
//			if (indices.size() > 1) {
//				for (int count = 1; count < indices.size(); count++) {
//					contentVirtualArray.remove(indices.get(count));
//				}
//			}
//
//		}
//		return contentVirtualArray;
//	}

	public boolean isPathwayViewerMode() {
		return pathwayViewerMode;
	}

	public void setPathwayViewerMode(boolean pathwayViewerMode) {
		this.pathwayViewerMode = pathwayViewerMode;
	}





	@Override
	public void handleContentVAUpdate(ContentVADelta vaDelta, String info) {
		EIDCategory targetCategory = vaDelta.getIDType().getCategory();
		if (targetCategory != EIDCategory.GENE)
			return;

		if (targetCategory == EIDCategory.GENE && vaDelta.getIDType() != EIDType.EXPRESSION_INDEX)
			vaDelta = DeltaConverter.convertDelta(EIDType.EXPRESSION_INDEX, vaDelta);
		ContentVirtualArray va = set.getContentVA(vaDelta.getVAType());

		va.setDelta(vaDelta);
	}

	@Override
	public void handleStorageVAUpdate(StorageVADelta vaDelta, String info) {
		EIDCategory targetCategory = vaDelta.getIDType().getCategory();
		if (targetCategory != EIDCategory.EXPERIMENT)
			return;

		StorageVirtualArray va = set.getStorageVA(vaDelta.getVAType());

		va.setDelta(vaDelta);
	}

}
