package org.geneview.core.util.mapping;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.geneview.core.data.collection.ISet;
import org.geneview.core.data.collection.IStorage;
import org.geneview.core.data.graph.item.vertex.EPathwayVertexType;
import org.geneview.core.data.graph.item.vertex.PathwayVertexGraphItem;
import org.geneview.core.data.graph.item.vertex.PathwayVertexGraphItemRep;
import org.geneview.core.data.mapping.EGenomeMappingType;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.ILoggerManager.LoggerType;
import org.geneview.util.graph.EGraphItemProperty;


public class GenomeMapper_ENZYME_2_NCBI_GENEID_2_ACCESSION_2_MICROARRAY_EXPRESSION_STORAGE_INDEX 
extends AGenomeMapper{

	/**
	 * Constructor.
	 * 
	 * @param refGeneralManager
	 * @param alSetData
	 */
	public GenomeMapper_ENZYME_2_NCBI_GENEID_2_ACCESSION_2_MICROARRAY_EXPRESSION_STORAGE_INDEX(
			final IGeneralManager refGeneralManager) {

		super(refGeneralManager);
	}
	
	protected ArrayList<Color> getMappingColorArrayByGeneVertexRep(
			final PathwayVertexGraphItemRep pathwayVertexRep) {
		
		ArrayList<Color> arMappingColor = new ArrayList<Color>();
		
		if (pathwayVertexRep.getAllItemsByProp(EGraphItemProperty.ALIAS_PARENT).size() > 1)
		{
			arMappingColor.add(Color.CYAN);
		}
		else
		{
			arMappingColor = getMappingColorArrayByGeneID(
					pathwayVertexRep.getPathwayVertexGraphItem().getName());
		}
		
		return arMappingColor;
	}
	
	protected ArrayList<Color> getMappingColorArrayByEnzymeVertex(
			final PathwayVertexGraphItem pathwayVertex) {
		
		int iCummulatedExpressionValue = 0;
		int iNumberOfExpressionValues = 0;
		
		ArrayList<Color> arMappingColor = new ArrayList<Color>();
		
		String sEnzymeCode = pathwayVertex.getName().substring(3);
		int iAccessionID = 0;
		int iGeneID = 0;
		Collection<Integer> iArTmpAccessionId = null;
		Iterator<IStorage> iterMappingStorage = alMappingStorage.iterator();
		
		int iEnzymeID = refGenomeIdManager.getIdIntFromStringByMapping(sEnzymeCode, 
				EGenomeMappingType.ENZYME_CODE_2_ENZYME);
		
		if (iEnzymeID == -1)
		{	
			arMappingColor.add(Color.BLACK);
			return arMappingColor;
		}
		
		Collection<Integer> iTmpGeneId = refGenomeIdManager.getIdIntListByType(iEnzymeID, 
				EGenomeMappingType.ENZYME_2_NCBI_GENEID);
		
		if(iTmpGeneId == null)
		{	
			arMappingColor.add(Color.BLACK);
			return arMappingColor;
		}
		
		Iterator<Integer> iterTmpGeneId = iTmpGeneId.iterator();
		Iterator<Integer> iterTmpAccessionId = null;
		while (iterTmpGeneId.hasNext())
		{
			iGeneID = iterTmpGeneId.next();
						
			iAccessionID = refGenomeIdManager.getIdIntFromIntByMapping(iGeneID, 
					EGenomeMappingType.NCBI_GENEID_2_ACCESSION);
	
			if (iAccessionID == -1)
				break;
							
			iArTmpAccessionId = refGenomeIdManager.getIdIntListByType(iAccessionID, 
					EGenomeMappingType.ACCESSION_2_MICROARRAY);
			
			if(iArTmpAccessionId == null)
				continue;
			
			while (iterMappingStorage.hasNext())
			{
				//Get expression value by MicroArrayID
				IStorage refExpressionStorage = iterMappingStorage.next();
				
				iterTmpAccessionId = iArTmpAccessionId.iterator();
				
				int [] bufferIntArray = refExpressionStorage.getArrayInt();
				
				if ( bufferIntArray == null ) {
					this.refGeneralManager.getSingelton().logMsg("color mapping failed, Storage=[" +
							refExpressionStorage.getLabel() + "][" +
							refExpressionStorage.toString() +
							"] does not contain int[]!",LoggerType.ERROR);
				}
				
				while (iterTmpAccessionId.hasNext())
				{
					int iMicroArrayId = iterTmpAccessionId.next();
									
					int iExpressionStorageIndex = refGenomeIdManager.getIdIntFromIntByMapping(
							iMicroArrayId, EGenomeMappingType.MICROARRAY_2_MICROARRAY_EXPRESSION);
					
					// Get rid of 770 internal ID identifier
					iExpressionStorageIndex = (int)(((float)iExpressionStorageIndex - 770.0f) / 1000.0f);
					
						int iExpressionValue = bufferIntArray[iExpressionStorageIndex];
						
						iCummulatedExpressionValue += iExpressionValue;
						iNumberOfExpressionValues++;					
					
				}
				
				if (iNumberOfExpressionValues != 0)
				{
					arMappingColor.add(expressionColorMapping.colorMappingLookup(iCummulatedExpressionValue 
							/ iNumberOfExpressionValues));
				}
			}
		}
		
		return arMappingColor;
	}
}
