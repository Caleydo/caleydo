package org.caleydo.core.util.mapping;

import gleem.linalg.Vec3f;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.graph.item.vertex.EPathwayVertexType;
import org.caleydo.core.data.graph.item.vertex.PathwayVertexGraphItem;
import org.caleydo.core.data.graph.item.vertex.PathwayVertexGraphItemRep;
import org.caleydo.core.data.mapping.EGenomeMappingType;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.ILoggerManager.LoggerType;
import org.caleydo.util.graph.EGraphItemProperty;


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
	
	protected ArrayList<Vec3f> getMappingColorArrayByGeneVertexRep(
			final PathwayVertexGraphItemRep pathwayVertexRep) {
		
		ArrayList<Vec3f> arMappingColor = new ArrayList<Vec3f>();
		
		if (pathwayVertexRep.getAllItemsByProp(EGraphItemProperty.ALIAS_PARENT).size() > 1)
		{
			arMappingColor.add(new Vec3f(0,1,1)); // cyan
		}
		else
		{
			arMappingColor = getMappingColorArrayByGeneID(
					pathwayVertexRep.getPathwayVertexGraphItem().getName());
		}
		
		return arMappingColor;
	}
	
	protected ArrayList<Vec3f> getMappingColorArrayByEnzymeVertex(
			final PathwayVertexGraphItem pathwayVertex) {
		
		int iCummulatedExpressionValue = 0;
		int iNumberOfExpressionValues = 0;
		
		ArrayList<Vec3f> arMappingColor = new ArrayList<Vec3f>();
		
		String sEnzymeCode = pathwayVertex.getName().substring(3);
		int iAccessionID = 0;
		int iGeneID = 0;
		Collection<Integer> iArTmpAccessionId = null;
		Iterator<IStorage> iterMappingStorage = alMappingStorage.iterator();
		
		int iEnzymeID = genomeIdManager.getIdIntFromStringByMapping(sEnzymeCode, 
				EGenomeMappingType.ENZYME_CODE_2_ENZYME);
		
		if (iEnzymeID == -1)
		{	
			arMappingColor.add(new Vec3f(0,0,0));
			return arMappingColor;
		}
		
		Collection<Integer> iTmpGeneId = genomeIdManager.getIdIntListByType(iEnzymeID, 
				EGenomeMappingType.ENZYME_2_NCBI_GENEID);
		
		if(iTmpGeneId == null)
		{	
			arMappingColor.add(new Vec3f(0,0,0));
			return arMappingColor;
		}
		
		Iterator<Integer> iterTmpGeneId = iTmpGeneId.iterator();
		Iterator<Integer> iterTmpAccessionId = null;
		while (iterTmpGeneId.hasNext())
		{
			iGeneID = iterTmpGeneId.next();
						
			iAccessionID = genomeIdManager.getIdIntFromIntByMapping(iGeneID, 
					EGenomeMappingType.NCBI_GENEID_2_ACCESSION);
	
			if (iAccessionID == -1)
				break;
							
			iArTmpAccessionId = genomeIdManager.getIdIntListByType(iAccessionID, 
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
					this.generalManager.logMsg("color mapping failed, Storage=[" +
							refExpressionStorage.getLabel() + "][" +
							refExpressionStorage.toString() +
							"] does not contain int[]!",LoggerType.ERROR);
				}
				
				while (iterTmpAccessionId.hasNext())
				{
					int iMicroArrayId = iterTmpAccessionId.next();
									
					int iExpressionStorageIndex = genomeIdManager.getIdIntFromIntByMapping(
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
