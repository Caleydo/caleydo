package org.geneview.core.util.mapping;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.geneview.util.graph.EGraphItemProperty;

import org.geneview.core.data.collection.ISet;
import org.geneview.core.data.collection.IStorage;
import org.geneview.core.data.collection.SetType;
import org.geneview.core.data.graph.item.vertex.EPathwayVertexType;
import org.geneview.core.data.graph.item.vertex.PathwayVertexGraphItem;
import org.geneview.core.data.graph.item.vertex.PathwayVertexGraphItemRep;
import org.geneview.core.data.mapping.GenomeMappingType;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.ILoggerManager.LoggerType;
import org.geneview.core.manager.data.IGenomeIdManager;


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
	public EnzymeToExpressionColorMapper(
			final IGeneralManager refGeneralManager,
			final ArrayList<ISet> alSetData) {
		
		this.refGeneralManager = refGeneralManager;
		
		alMappingStorage = new ArrayList<IStorage>();
		
		refGenomeIdManager = refGeneralManager.getSingelton().getGenomeIdManager();
		
		extractMappingData(alSetData);
		
		// Create Color Lookup Table
		expressionColorMapping = new ColorMapping(0, 60000);
	}
	
	protected void extractMappingData(final ArrayList<ISet> alSetData) {
		
		if (alSetData == null)
			return;
		
		Iterator<ISet> iterSetData = alSetData.iterator();
		
		while (iterSetData.hasNext())
		{
			ISet tmpSet = iterSetData.next();
			
			if (tmpSet.getSetType().equals(SetType.SET_GENE_EXPRESSION_DATA))
			{
				alMappingStorage.add(tmpSet.getStorageByDimAndIndex(0, 0));
			}
		}
	}
	
	public final ArrayList<Color> getMappingColorArrayByVertexRep(
			final PathwayVertexGraphItemRep pathwayVertexRep) {
		
		// Do nothing if picked node is invalid.
		if (pathwayVertexRep == null)
		{
			return new ArrayList<Color>();
		}
		
		if (pathwayVertexRep.getPathwayVertexGraphItem().getType().equals(EPathwayVertexType.gene))
		{
			return getMappingColorArrayByGeneVertexRep(pathwayVertexRep);
		}
		else if (pathwayVertexRep.getPathwayVertexGraphItem().getType().equals(EPathwayVertexType.enzyme))
		{
			return getMappingColorArrayByEnzymeVertex(pathwayVertexRep.getPathwayVertexGraphItem());
		}
		
		return new ArrayList<Color>();
	}
	
	private final ArrayList<Color> getMappingColorArrayByGeneVertexRep(
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
		
//		String sGeneID = pathwayVertex.getName();
//		
//		// Check for multiple genes per enzyme
//		if (sGeneID.contains(" "))
//		{	
//			arMappingColor.add(Color.YELLOW);
//			return arMappingColor;
//			//sGeneID = sGeneID.substring(0, sGeneID.indexOf(' '));
//		}
//	
//		return getMappingColorArrayByGeneID(sGeneID);
	}
	
	public final ArrayList<Color> getMappingColorArrayByGeneID(
			String sGeneID) {
		
		// Remove prefix ("hsa:")
		sGeneID = sGeneID.substring(4);
		
		ArrayList<Color> arMappingColor = new ArrayList<Color>();
		int iCummulatedExpressionValue = 0;
		int iNumberOfExpressionValues = 0;
		
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
						"] does not contain int[]!",LoggerType.ERROR);
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
	
	private final ArrayList<Color> getMappingColorArrayByEnzymeVertex(
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
							"] does not contain int[]!",LoggerType.ERROR);
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

	public final ArrayList<Integer> getExpressionValueArrayByGeneID(
			String sGeneID) {
		
		// Remove prefix ("hsa:")
		sGeneID = sGeneID.substring(4);
		
		ArrayList<Integer> arMappingColor = new ArrayList<Integer>();
		
		int iCummulatedExpressionValue = 0;
		int iNumberOfExpressionValues = 0;
		
		int iGeneID = refGenomeIdManager.getIdIntFromStringByMapping(sGeneID, 
				GenomeMappingType.NCBI_GENEID_CODE_2_NCBI_GENEID);
				
		if (iGeneID == -1)
		{	
			return arMappingColor;
		}
		
		int iAccessionID = refGenomeIdManager.getIdIntFromIntByMapping(iGeneID, 
				GenomeMappingType.NCBI_GENEID_2_ACCESSION);
	
		if (iAccessionID == -1)
		{	
			return arMappingColor;
		}
		
		Collection<Integer> iArTmpMicroArrayId = null;
		iArTmpMicroArrayId = refGenomeIdManager.getIdIntListByType(iAccessionID, 
					GenomeMappingType.ACCESSION_2_MICROARRAY);
		
		if (iArTmpMicroArrayId == null)
		{	
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
						"] does not contain int[]!",LoggerType.ERROR);
			}
			
			while (iterTmpMicroArrayId.hasNext())
			{
				int iMicroArrayId = iterTmpMicroArrayId.next();
								
				int iExpressionStorageIndex = refGenomeIdManager.getIdIntFromIntByMapping(
						iMicroArrayId, GenomeMappingType.MICROARRAY_2_MICROARRAY_EXPRESSION);
				
				// Get rid of 770 internal ID identifier
				iExpressionStorageIndex = (int)(((float)iExpressionStorageIndex - 770.0f) / 1000.0f);
				
					int iExpressionValue = bufferIntArray[iExpressionStorageIndex];
					
					arMappingColor.add(iExpressionValue);
					
					iCummulatedExpressionValue += iExpressionValue;
					iNumberOfExpressionValues++;					
				
			}
			
//			if (iNumberOfExpressionValues != 0)
//			{
//				arMappingColor.add(iCummulatedExpressionValue);
//			}
		}
		
		return arMappingColor;
	}
	
//	/**
//	 * Method is for testing the mapping from enzymes to genes. It takes a
//	 * selected vertex and looks up the IDs in the proper order.
//	 * 
//	 * @param refTmpVertexRep
//	 *            Selected vertex
//	 */
//	protected void expressionMappingTest(
//			final PathwayVertexRep refTmpVertexRep) {
//
//		// Check if vertex is an enzyme.
//		// If not leave. Because only enzymes needs to be mapped.
//		if (!refTmpVertexRep.getVertex().getVertexType().equals(
//				PathwayVertexType.enzyme))
//		{
//			return;
//		}
//
//		String sEnzymeCode = refTmpVertexRep.getVertex().getElementTitle()
//				.substring(3);
//		String sAccessionCode = "";
//		String sTmpGeneName = "";
//		int iAccessionID = 0;
//		Collection<Integer> iArTmpAccessionId = null;
//
//		// Just for testing mapping!
//		IGenomeIdManager refGenomeIdManager = refGeneralManager.getSingelton()
//				.getGenomeIdManager();
//
//		int iEnzymeID = refGenomeIdManager.getIdIntFromStringByMapping(
//				sEnzymeCode, GenomeMappingType.ENZYME_CODE_2_ENZYME);
//
//		if (iEnzymeID == -1)
//			return;
//
//		Collection<Integer> iTmpGeneId = refGenomeIdManager.getIdIntListByType(
//				iEnzymeID, GenomeMappingType.ENZYME_2_NCBI_GENEID);
//
//		if (iTmpGeneId == null)
//			return;
//
//		Iterator<Integer> iterTmpGeneId = iTmpGeneId.iterator();
//		Iterator<Integer> iterTmpAccessionId = null;
//		while (iterTmpGeneId.hasNext())
//		{
//			iAccessionID = refGenomeIdManager.getIdIntFromIntByMapping(
//					iterTmpGeneId.next(),
//					GenomeMappingType.NCBI_GENEID_2_ACCESSION);
//
//			if (iAccessionID == -1)
//				break;
//
//			sAccessionCode = refGenomeIdManager.getIdStringFromIntByMapping(
//					iAccessionID, GenomeMappingType.ACCESSION_2_ACCESSION_CODE);
//
//			System.out.println("Accession Code for Enzyme " + sEnzymeCode
//					+ ": " + sAccessionCode);
//
//			sTmpGeneName = refGenomeIdManager.getIdStringFromIntByMapping(
//					iAccessionID, GenomeMappingType.ACCESSION_2_GENE_NAME);
//
//			System.out.println("Gene name for Enzyme " + sEnzymeCode + ": "
//					+ sTmpGeneName);
//
//			iArTmpAccessionId = refGenomeIdManager.getIdIntListByType(
//					iAccessionID, GenomeMappingType.ACCESSION_2_MICROARRAY);
//
//			if (iArTmpAccessionId == null)
//				continue;
//
//			iterTmpAccessionId = iArTmpAccessionId.iterator();
//			while (iterTmpAccessionId.hasNext())
//			{
//				String sMicroArrayCode = refGenomeIdManager
//						.getIdStringFromIntByMapping(iterTmpAccessionId.next(),
//								GenomeMappingType.MICROARRAY_2_MICROARRAY_CODE);
//
//				System.out.println("MicroArray Code for Enzyme " + sEnzymeCode
//						+ ": " + sMicroArrayCode);
//			}
//		}
//	}
}
