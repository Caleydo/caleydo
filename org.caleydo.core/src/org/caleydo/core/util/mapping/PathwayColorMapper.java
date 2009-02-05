package org.caleydo.core.util.mapping;

import java.util.ArrayList;
import java.util.Iterator;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.graph.pathway.item.vertex.EPathwayVertexType;
import org.caleydo.core.data.graph.pathway.item.vertex.PathwayVertexGraphItemRep;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.manager.IIDMappingManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;
import org.caleydo.util.graph.EGraphItemProperty;

/**
 * Class is responsible for mapping gene expression data to color from one
 * entity to another.
 * 
 * @author Marc Streit
 */
public class PathwayColorMapper
{
//	protected IIDMappingManager genomeIdManager;
//
//	protected ArrayList<IStorage> alMappingStorage;
//
//	protected ColorMapping expressionColorMapper;
//
//	/**
//	 * Constructor.
//	 * 
//	 * @param generalManager
//	 */
//	public PathwayColorMapper()
//	{
//		alMappingStorage = new ArrayList<IStorage>();
//		genomeIdManager = GeneralManager.get().getIDMappingManager();
//
//		expressionColorMapper = ColorMappingManager.get().getColorMapping(
//				EColorMappingType.GENE_EXPRESSION);
//	}
//
//	public void setMappingData(final ArrayList<ISet> alSetData)
//	{
//
//		if (alSetData == null)
//			return;
//
//		// TODO better iterator
//		for (ISet tmpSet : alSetData)
//		{
//			for (int iStorageCount = 0; iStorageCount < tmpSet.size(); iStorageCount++)
//			{
//				alMappingStorage.add(tmpSet.get(iStorageCount));
//			}
//		}
//	}
//
//	public final ArrayList<float[]> getMappingColorArrayByVertexRep(
//			final PathwayVertexGraphItemRep pathwayVertexRep)
//	{
//
//		// Do nothing if picked node is invalid.
//		if (pathwayVertexRep == null)
//		{
//			return new ArrayList<float[]>();
//		}
//
//		ArrayList<float[]> arMappingColor = new ArrayList<float[]>();
//
//		if (pathwayVertexRep.getType().equals(EPathwayVertexType.gene))
//		{
//
//			if (pathwayVertexRep.getAllItemsByProp(EGraphItemProperty.ALIAS_PARENT).size() > 1)
//			{
//				arMappingColor.add(new float[] { 0, 1, 1 }); // cyan
//			}
//			else
//			{
//				int iDavidId = GeneralManager.get().getPathwayItemManager()
//						.getDavidIdByPathwayVertexGraphItemId(
//								pathwayVertexRep.getAllItemsByProp(
//										EGraphItemProperty.ALIAS_PARENT).get(0).getId());
//
//				Integer iExpressionStorageIndex = genomeIdManager.getID(
//						EMappingType.DAVID_2_EXPRESSION_INDEX, iDavidId);
//
//				if (iExpressionStorageIndex == null || iExpressionStorageIndex == -1)
//				{
//					// FIXME, color from render style here
//					arMappingColor.add(new float[] { 0.5f, 0.5f, 0.5f }); // invalid
//					// color
//					return arMappingColor;
//				}
//
//				Iterator<IStorage> iterMappingStorage = alMappingStorage.iterator();
//				IStorage expressionStorage = null;
//
//				while (iterMappingStorage.hasNext())
//				{
//					// Get expression value by MicroArrayID
//					expressionStorage = iterMappingStorage.next();
//
//					float fExpressionValue = expressionStorage.getFloat(
//							EDataRepresentation.NORMALIZED, iExpressionStorageIndex);
//
//					arMappingColor.add(expressionColorMapper.getColor(fExpressionValue));
//				}
//			}
//
//			return arMappingColor;
//		}
//		else if (pathwayVertexRep.getType().equals(EPathwayVertexType.enzyme))
//		{
//
//			arMappingColor.add(new float[] { 0.5f, 0.5f, 0.5f }); // invalid
//			// color
//			return arMappingColor;
//		}
//
//		throw new IllegalStateException("Unkonw pathway node type");
//	}
//
//	public ColorMapping getColorMapper()
//	{
//
//		return expressionColorMapper;
//	}
}
