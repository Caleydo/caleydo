package org.geneview.core.util.mapping;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

import org.geneview.core.data.collection.ISet;
import org.geneview.core.data.collection.IStorage;
import org.geneview.core.data.collection.SetType;
import org.geneview.core.data.graph.item.vertex.EPathwayVertexType;
import org.geneview.core.data.graph.item.vertex.PathwayVertexGraphItem;
import org.geneview.core.data.graph.item.vertex.PathwayVertexGraphItemRep;
import org.geneview.core.data.mapping.EGenomeMappingType;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.ILoggerManager.LoggerType;
import org.geneview.core.manager.data.IGenomeIdManager;
import org.geneview.core.util.mapping.color.ColorMapping;

/**
 * Class is responsible for mapping gene expression data
 * from one entity to another. 
 * 
 * @author Marc Streit
 *
 */
public abstract class AGenomeMapper {

	protected IGeneralManager refGeneralManager;
	
	protected IGenomeIdManager refGenomeIdManager;
	
	protected ArrayList<IStorage> alMappingStorage;
	
	protected ColorMapping expressionColorMapping;
	
	/**
	 * Constructor. 
	 * 
	 * @param refGeneralManager
	 */
	public AGenomeMapper(final IGeneralManager refGeneralManager) {
	
		this.refGeneralManager = refGeneralManager;
		alMappingStorage = new ArrayList<IStorage>();
		refGenomeIdManager = refGeneralManager.getSingelton().getGenomeIdManager();
		
		// Create Color Lookup Table
		// FIXME: take MIN MAX out of storage!!
		expressionColorMapping = new ColorMapping(0, 60000);
	}
	
	public void setMappingData(final ArrayList<ISet> alSetData) {
		
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
	
	public ArrayList<Color> getMappingColorArrayByGeneID(
			String sGeneID) {
		
		// Remove prefix ("hsa:")
		sGeneID = sGeneID.substring(4);
		
		ArrayList<Color> arMappingColor = new ArrayList<Color>();
		
		int iGeneID = refGenomeIdManager.getIdIntFromStringByMapping(sGeneID, 
				EGenomeMappingType.NCBI_GENEID_CODE_2_NCBI_GENEID);
				
		if (iGeneID == -1)
		{	
			arMappingColor.add(Color.BLACK);
			return arMappingColor;
		}
		
		int iAccessionID = refGenomeIdManager.getIdIntFromIntByMapping(iGeneID, 
				EGenomeMappingType.NCBI_GENEID_2_ACCESSION);
	
		if (iAccessionID == -1)
		{	
			arMappingColor.add(Color.BLACK);
			return arMappingColor;
		}

		//---------------------------------------
		// Just for testing kashofer data mapping
		int iExpressionStorageIndex = refGenomeIdManager.getIdIntFromIntByMapping(iAccessionID,
				EGenomeMappingType.ACCESSION_2_MICROARRAY_EXPRESSION);

		// Get rid of 770 internal ID identifier
		iExpressionStorageIndex = (int)(((float)iExpressionStorageIndex - 770.0f) / 1000.0f);

		Iterator<IStorage> iterMappingStorage = alMappingStorage.iterator();
		IStorage refExpressionStorage = null;
		
		while (iterMappingStorage.hasNext())
		{
			//Get expression value by MicroArrayID
			refExpressionStorage = iterMappingStorage.next();
			
			float[] bufferFloatArray = refExpressionStorage.getArrayFloat();
			
			if ( bufferFloatArray == null ) {
				this.refGeneralManager.getSingelton().logMsg("color mapping failed, Storage=[" +
						refExpressionStorage.getLabel() + "][" +
						refExpressionStorage.toString() +
						"] does not contain float[]!",LoggerType.ERROR);
			}
						
			float fExpressionValue = bufferFloatArray[iExpressionStorageIndex];
			
			arMappingColor.add(expressionColorMapping.colorMappingLookup((int)fExpressionValue));			
		}
		
		return arMappingColor;
	}
	
	protected abstract ArrayList<Color> getMappingColorArrayByGeneVertexRep(
			final PathwayVertexGraphItemRep pathwayVertexRep);
	
	protected abstract ArrayList<Color> getMappingColorArrayByEnzymeVertex(
			final PathwayVertexGraphItem pathwayVertex);
}
