package org.caleydo.core.util.mapping;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.SetType;
import org.caleydo.core.data.graph.item.vertex.EPathwayVertexType;
import org.caleydo.core.data.graph.item.vertex.PathwayVertexGraphItem;
import org.caleydo.core.data.graph.item.vertex.PathwayVertexGraphItemRep;
import org.caleydo.core.data.mapping.EGenomeMappingType;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.data.IGenomeIdManager;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.util.system.StringConversionTool;
import org.caleydo.util.graph.EGraphItemProperty;

/**
 * Class is responsible for mapping gene expression data
 * from one entity to another. 
 * 
 * @author Marc Streit
 *
 */
public class GenomeMapper {

	protected IGeneralManager generalManager;
	
	protected IGenomeIdManager genomeIdManager;
	
	protected ArrayList<IStorage> alMappingStorage;
	
	protected ColorMapping expressionColorMapping;
	
	/**
	 * Constructor. 
	 * 
	 * @param generalManager
	 */
	public GenomeMapper(final IGeneralManager generalManager) {
	
		this.generalManager = generalManager;
		alMappingStorage = new ArrayList<IStorage>();
		genomeIdManager = generalManager.getGenomeIdManager();
		
		expressionColorMapping = new ColorMapping(0, 1);
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
	
	public final ArrayList<Vec3f> getMappingColorArrayByVertexRep(
			final PathwayVertexGraphItemRep pathwayVertexRep) {
		
		// Do nothing if picked node is invalid.
		if (pathwayVertexRep == null)
		{
			return new ArrayList<Vec3f>();
		}
		
		if (pathwayVertexRep.getPathwayVertexGraphItem().getType().equals(EPathwayVertexType.gene))
		{
			ArrayList<Vec3f> arMappingColor = new ArrayList<Vec3f>();
			
			if (pathwayVertexRep.getAllItemsByProp(EGraphItemProperty.ALIAS_PARENT).size() > 1)
			{
				arMappingColor.add(new Vec3f(0,1,1)); // cyan
			}
			else
			{
				int iDavidId = pathwayVertexRep.getPathwayVertexGraphItem().getId();
				
				int iExpressionStorageIndex = genomeIdManager.getIdIntFromIntByMapping(iDavidId,
						EGenomeMappingType.DAVID_2_EXPRESSION_STORAGE_ID);

				if (iExpressionStorageIndex == -1)
				{	
					arMappingColor.add(new Vec3f(-1, -1, -1)); // invalid color
					return arMappingColor;
				}
				
				Iterator<IStorage> iterMappingStorage = alMappingStorage.iterator();
				IStorage refExpressionStorage = null;
				
				while (iterMappingStorage.hasNext())
				{
					//Get expression value by MicroArrayID
					refExpressionStorage = iterMappingStorage.next();
					
					float[] bufferFloatArray = refExpressionStorage.getArrayFloat();
					
					if ( bufferFloatArray == null ) 
					{
						generalManager.getLogger().log(Level.SEVERE, 
							"Color mapping failed!.");
					}
								
					float fExpressionValue = bufferFloatArray[iExpressionStorageIndex];	
					arMappingColor.add(expressionColorMapping.colorMappingLookup(fExpressionValue));
				}
			}
			
			return arMappingColor;
		}
		else if (pathwayVertexRep.getPathwayVertexGraphItem().getType().equals(EPathwayVertexType.enzyme))
		{
			ArrayList<Vec3f> arMappingColor = new ArrayList<Vec3f>();
			arMappingColor.add(new Vec3f(-1, -1, -1)); // invalid color
			return arMappingColor;
		}
		
		return new ArrayList<Vec3f>();
	}
}
