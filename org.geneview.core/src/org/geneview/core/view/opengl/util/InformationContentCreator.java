package org.geneview.core.view.opengl.util;

import java.util.ArrayList;

import org.geneview.core.data.mapping.EGenomeMappingType;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.data.IGenomeIdManager;
import org.geneview.core.util.mapping.GeneAnnotationMapper;
import org.geneview.core.view.opengl.canvas.parcoords.EInputDataTypes;

/**
 * 
 * Creates the content for eg the InfoArea
 * Just pass it an ID and an Inputdatatype,
 * it returns an AL of Relevant data
 * 
 * @author Alexander Lex
 *
 */

public class InformationContentCreator 
{
	
	private ArrayList<String> sContent;
	
	private IGenomeIdManager IDManager;
	private GeneAnnotationMapper mapper;
	private IGeneralManager generalManager;
	
	/**
	 * Constructor
	 * @param generalManager
	 */
	public InformationContentCreator(final IGeneralManager generalManager) 
	{
		this.generalManager = generalManager;
		sContent = new ArrayList<String>();
		IDManager = this.generalManager.getSingelton().getGenomeIdManager();
		// TODO Auto-generated constructor stub
		mapper = new GeneAnnotationMapper(generalManager);
	}
	
	/**
	 * Returns an AL of Strings when you pass it an ID and a data type
	 * The list is in such order that the first element is suitable for a title
	 * 
	 * @param iGeneViewID
	 * @param eInputDataTypes
	 * @return
	 */
	ArrayList<String> getStringContentForID(final int iGeneViewID, final EInputDataTypes eInputDataTypes)
	{
		sContent.clear();
		switch (eInputDataTypes)
		{
		case GENES:
			String sAccessionNumber;
			String sGeneName;
			
			if (iGeneViewID == -1)
			{
				sAccessionNumber = "unknown";
				sGeneName = "unknown";
			}
			else
			{
				sAccessionNumber = getAccessionNumberFromAccessionID(iGeneViewID);							
				sGeneName = mapper.getGeneShortNameByAccession(iGeneViewID);
			}
			sContent.add("Type: Gene");
			sContent.add("Name: " + sGeneName);			
			sContent.add("Acc.: " + sAccessionNumber);
		
		
			break;
		case EXPERIMENTS:
			sContent.add("Type: Experiment");
			break;
		default:
			sContent.add("No Data");
		}
		
		return sContent;
	}
	
	
	private String getAccessionNumberFromAccessionID(final int iGeneViewID)
	{
		String sAccessionNumber = IDManager.getIdStringFromIntByMapping(iGeneViewID, EGenomeMappingType.ACCESSION_2_ACCESSION_CODE);
		if(sAccessionNumber == "")
			return "Unkonwn Gene";
		else
			return sAccessionNumber;		
	}

}
