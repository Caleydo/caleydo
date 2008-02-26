package org.geneview.core.view.opengl.util;

import java.util.ArrayList;

import org.geneview.core.data.mapping.EGenomeMappingType;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.data.IGenomeIdManager;
import org.geneview.core.view.opengl.canvas.parcoords.EInputDataTypes;


public class InformationContentCreator 
{
	
	private ArrayList<String> sContent;
	
	private IGenomeIdManager IDManager;
	
	private IGeneralManager generalManager;
	
	
	public InformationContentCreator(final IGeneralManager generalManager) 
	{
		this.generalManager = generalManager;
		sContent = new ArrayList<String>();
		IDManager = this.generalManager.getSingelton().getGenomeIdManager();
		// TODO Auto-generated constructor stub
	}
	
	ArrayList<String> getStringContentForID(final int iGeneViewID, final EInputDataTypes eInputDataTypes)
	{
		sContent.clear();
		switch (eInputDataTypes)
		{
		case GENES:
			String sAccessionNumber = getAccessionNumberFromAccessionID(iGeneViewID);							
			String sGeneName = IDManager.getIdStringFromIntByMapping(iGeneViewID, EGenomeMappingType.ACCESSION_2_GENE_NAME);						
			//String sGeneShortName = IDManager.getIdStringFromIntByMapping(iGeneViewID, EGenomeMappingType.)
		
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
