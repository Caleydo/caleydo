package org.caleydo.core.view.opengl.util.infoarea;

import java.util.ArrayList;
import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.mapping.GeneAnnotationMapper;

/**
 * Creates the content for eg the InfoArea Just pass it an ID and an
 * Inputdatatype, it returns an AL of Relevant data
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */

public class InformationContentCreator
{
	private ArrayList<String> sContent;

	private GeneAnnotationMapper mapper;

	private IGeneralManager generalManager;

	/**
	 * Constructor
	 * 
	 * @param generalManager
	 */
	public InformationContentCreator()
	{
		sContent = new ArrayList<String>();
		mapper = new GeneAnnotationMapper();

		this.generalManager = GeneralManager.get();
	}

	/**
	 * Returns an AL of Strings when you pass it an ID and a data type The list
	 * is in such order that the first element is suitable for a title
	 * 
	 * @param iUniqueID
	 * @param eInputDataTypes
	 * @return
	 */
	ArrayList<String> getStringContentForID(final int iUniqueID, final EIDType eInputDataTypes)
	{

		sContent.clear();
		switch (eInputDataTypes)
		{
			case EXPRESSION_INDEX:

				String sRefSeq = "unknown";
				String sGeneName = "unknown";
				String sGeneSymbol = "unknown";

				if (iUniqueID != -1)
				{
					sRefSeq = generalManager.getGenomeIdManager().getIdStringFromIntByMapping(
							iUniqueID, EMappingType.DAVID_2_REFSEQ_MRNA);
					sGeneName = generalManager.getGenomeIdManager()
							.getIdStringFromIntByMapping(iUniqueID,
									EMappingType.DAVID_2_GENE_NAME);
					sGeneSymbol = generalManager.getGenomeIdManager()
							.getIdStringFromIntByMapping(iUniqueID,
									EMappingType.DAVID_2_GENE_SYMBOL);
				}

				// Cut too long gene names
				if (sGeneName.length() >= 50)
					sGeneName = sGeneName.substring(0, 50) + "...";

				sContent.add("Type: Gene");
				sContent.add("RefSeq: " + sRefSeq);
				sContent.add("Symbol:" + sGeneSymbol);
				sContent.add("Name: " + sGeneName);

				break;

			case PATHWAY:

				PathwayGraph pathway = (generalManager.getPathwayManager().getItem(iUniqueID));

				if (pathway == null)
				{
					break;
				}

				String sPathwayTitle = pathway.getTitle();

				sContent.add("Type: " + pathway.getType().getName() + "Pathway");
				sContent.add("PW: " + sPathwayTitle);
				break;

			case EXPRESSION_EXPERIMENT:

				sContent.add("Type: Experiment");
				break;

			default:
				sContent.add("No Data");
		}

		return sContent;
	}
}
