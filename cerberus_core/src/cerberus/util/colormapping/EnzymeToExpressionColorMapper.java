package cerberus.util.colormapping;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import cerberus.data.collection.ISet;
import cerberus.data.collection.IStorage;
import cerberus.data.collection.SetDetailedDataType;
import cerberus.data.mapping.GenomeMappingType;
import cerberus.data.pathway.element.PathwayVertex;
import cerberus.data.pathway.element.PathwayVertexType;
import cerberus.data.view.rep.pathway.IPathwayVertexRep;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.data.IGenomeIdManager;


public class EnzymeToExpressionColorMapper {
	
	protected IGeneralManager refGeneralManager;
	
	protected IGenomeIdManager refGenomeIdManager;
	
	protected ArrayList<IStorage> alMappingStorage;
	
	protected ColorMapping expressionColorMapping;
	
	/**
	 * Constructor.
	 * 
	 * @param refGeneralManager
	 */
	public EnzymeToExpressionColorMapper(IGeneralManager refGeneralManager,
			ArrayList<ISet> alSetData) {
		
		this.refGeneralManager = refGeneralManager;
		
		alMappingStorage = new ArrayList<IStorage>();
		
		refGenomeIdManager = refGeneralManager.getSingelton().getGenomeIdManager();
		
		extractMappingData(alSetData);
		
		// Create Color Lookup Table
		expressionColorMapping = new ColorMapping(0, 60000);
	}
	
	protected void extractMappingData(ArrayList<ISet> alSetData) {
		
		Iterator<ISet> iterSetData = alSetData.iterator();
		
		while (iterSetData.hasNext())
		{
			ISet tmpSet = iterSetData.next();
			
			if (tmpSet.getRawDataSetType().equals(SetDetailedDataType.GENE_EXPRESSION_DATA))
			{
				alMappingStorage.add(tmpSet.getStorageByDimAndIndex(0, 0));
			}
		}
	}
	
	public ArrayList<Color> getMappingColorArrayByVertex(
			PathwayVertex pathwayVertex) {
		
		// Do nothing if picked node is invalid.
		if (pathwayVertex == null)
		{
			return new ArrayList<Color>();
		}
		
		if (pathwayVertex.getVertexType().equals(PathwayVertexType.gene))
			return getMappingColorArrayByGene(pathwayVertex);
		else if (pathwayVertex.getVertexType().equals(PathwayVertexType.enzyme))
			return getMappingColorArrayByEnzyme(pathwayVertex);
		
		return new ArrayList<Color>();
	}
	
	private ArrayList<Color> getMappingColorArrayByGene(
			PathwayVertex pathwayVertex) {
		
		int iCummulatedExpressionValue = 0;
		int iNumberOfExpressionValues = 0;
		
		ArrayList<Color> arMappingColor = new ArrayList<Color>();
		
		String sGeneID = pathwayVertex.getElementTitle().substring(4);
		
		// Check for multiple genes per enzyme
		if (sGeneID.contains(" "))
		{	
			arMappingColor.add(Color.YELLOW);
			return arMappingColor;
			//sGeneID = sGeneID.substring(0, sGeneID.indexOf(' '));
		}
		
		int iGeneID = refGenomeIdManager.getIdIntFromStringByMapping(sGeneID, 
				GenomeMappingType.NCBI_GENEID_CODE_2_NCBI_GENEID);
				
		if (iGeneID == -1)
		{	
			arMappingColor.add(Color.BLACK);
			return arMappingColor;
		}
		
		int iAccessionID = refGenomeIdManager.getIdIntFromIntByMapping(iGeneID, 
				GenomeMappingType.NCBI_GENEID_2_ACCESSION);
	
		if (iAccessionID == -1)
		{	
			arMappingColor.add(Color.BLACK);
			return arMappingColor;
		}
		
		Collection<Integer> iArTmpMicroArrayId = null;
		iArTmpMicroArrayId = refGenomeIdManager.getIdIntListByType(iAccessionID, 
					GenomeMappingType.ACCESSION_2_MICROARRAY);
		
		if (iArTmpMicroArrayId == null)
		{	
			arMappingColor.add(Color.BLACK);
			return arMappingColor;
		}

		Iterator<IStorage> iterMappingStorage = alMappingStorage.iterator();
		Iterator<Integer> iterTmpMicroArrayId = null;
		IStorage refExpressionStorage = null;
		
		while (iterMappingStorage.hasNext())
		{
			//Get expression value by MicroArrayID
			refExpressionStorage = iterMappingStorage.next();
			
			iterTmpMicroArrayId = iArTmpMicroArrayId.iterator();
			
			int [] bufferIntArray = refExpressionStorage.getArrayInt();
			
			if ( bufferIntArray == null ) {
				this.refGeneralManager.getSingelton().logMsg("color mapping failed, Storage=[" +
						refExpressionStorage.getLabel() + "][" +
						refExpressionStorage.toString() +
						"] does not contain int[]!",LoggerType.ERROR_ONLY);
			}
			
			while (iterTmpMicroArrayId.hasNext())
			{
				int iMicroArrayId = iterTmpMicroArrayId.next();
								
				int iExpressionStorageIndex = refGenomeIdManager.getIdIntFromIntByMapping(
						iMicroArrayId, GenomeMappingType.MICROARRAY_2_MICROARRAY_EXPRESSION);
				
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
		
		return arMappingColor;
	}
	
	private ArrayList<Color> getMappingColorArrayByEnzyme(PathwayVertex pathwayVertex) {
		
		int iCummulatedExpressionValue = 0;
		int iNumberOfExpressionValues = 0;
		
		ArrayList<Color> arMappingColor = new ArrayList<Color>();
		
		String sEnzymeCode = pathwayVertex.getElementTitle().substring(3);
		int iAccessionID = 0;
		int iGeneID = 0;
		Collection<Integer> iArTmpAccessionId = null;
		Iterator<IStorage> iterMappingStorage = alMappingStorage.iterator();
		
		int iEnzymeID = refGenomeIdManager.getIdIntFromStringByMapping(sEnzymeCode, 
				GenomeMappingType.ENZYME_CODE_2_ENZYME);
		
		if (iEnzymeID == -1)
		{	
			arMappingColor.add(Color.BLACK);
			return arMappingColor;
		}
		
		Collection<Integer> iTmpGeneId = refGenomeIdManager.getIdIntListByType(iEnzymeID, 
				GenomeMappingType.ENZYME_2_NCBI_GENEID);
		
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
					GenomeMappingType.NCBI_GENEID_2_ACCESSION);
	
			if (iAccessionID == -1)
				break;
							
			iArTmpAccessionId = refGenomeIdManager.getIdIntListByType(iAccessionID, 
					GenomeMappingType.ACCESSION_2_MICROARRAY);
			
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
							"] does not contain int[]!",LoggerType.ERROR_ONLY);
				}
				
				while (iterTmpAccessionId.hasNext())
				{
					int iMicroArrayId = iterTmpAccessionId.next();
									
					int iExpressionStorageIndex = refGenomeIdManager.getIdIntFromIntByMapping(
							iMicroArrayId, GenomeMappingType.MICROARRAY_2_MICROARRAY_EXPRESSION);
					
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
