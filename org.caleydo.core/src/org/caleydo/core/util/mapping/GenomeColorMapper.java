package org.caleydo.core.util.mapping;

import gleem.linalg.Vec3f;
import java.util.ArrayList;
import java.util.Iterator;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.graph.pathway.item.vertex.EPathwayVertexType;
import org.caleydo.core.data.graph.pathway.item.vertex.PathwayVertexGraphItem;
import org.caleydo.core.data.graph.pathway.item.vertex.PathwayVertexGraphItemRep;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.specialized.genome.IGenomeIdManager;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.util.graph.EGraphItemProperty;

/**
 * Class is responsible for mapping gene expression data to color from one
 * entity to another.
 * 
 * @author Marc Streit
 */
public class GenomeColorMapper
{

	protected IGeneralManager generalManager;

	protected IGenomeIdManager genomeIdManager;

	protected ArrayList<IStorage> alMappingStorage;

	protected ColorMapping expressionColorMapper;

	/**
	 * Constructor.
	 * 
	 * @param generalManager
	 */
	public GenomeColorMapper(final IGeneralManager generalManager)
	{

		this.generalManager = generalManager;
		alMappingStorage = new ArrayList<IStorage>();
		genomeIdManager = generalManager.getGenomeIdManager();

		expressionColorMapper = new ColorMapping(0, 1);
	}

	public void setMappingData(final ArrayList<ISet> alSetData)
	{

		if (alSetData == null)
			return;

		// TODO better iterator
		for (ISet tmpSet : alSetData)
		{
			for (int iStorageCount = 0; iStorageCount < tmpSet.size(); iStorageCount++)
			{
				alMappingStorage.add(tmpSet.get(iStorageCount));
			}
		}
	}

	public final ArrayList<Vec3f> getMappingColorArrayByVertexRep(
			final PathwayVertexGraphItemRep pathwayVertexRep)
	{

		// Do nothing if picked node is invalid.
		if (pathwayVertexRep == null)
		{
			return new ArrayList<Vec3f>();
		}
		
		if (pathwayVertexRep.getType().equals(EPathwayVertexType.gene))
		{
			ArrayList<Vec3f> arMappingColor = new ArrayList<Vec3f>();

			if (pathwayVertexRep.getAllItemsByProp(EGraphItemProperty.ALIAS_PARENT).size() > 1)
			{
				arMappingColor.add(new Vec3f(0, 1, 1)); // cyan
			}
			else
			{
				int iDavidId = generalManager.getPathwayItemManager()
						.getDavidIdByPathwayVertexGraphItemId(
								pathwayVertexRep.getAllItemsByProp(EGraphItemProperty.ALIAS_PARENT).get(0).getId());

				int iExpressionStorageIndex = genomeIdManager.getIdIntFromIntByMapping(
						iDavidId, EMappingType.DAVID_2_EXPRESSION_STORAGE_ID);

				if (iExpressionStorageIndex == -1)
				{
					arMappingColor.add(new Vec3f(-1, -1, -1)); // invalid color
					return arMappingColor;
				}

				Iterator<IStorage> iterMappingStorage = alMappingStorage.iterator();
				IStorage expressionStorage = null;

				while (iterMappingStorage.hasNext())
				{
					// Get expression value by MicroArrayID
					expressionStorage = iterMappingStorage.next();

					float fExpressionValue = expressionStorage.getFloat(
							EDataRepresentation.NORMALIZED, iExpressionStorageIndex);

					arMappingColor.add(expressionColorMapper
							.colorMappingLookup(fExpressionValue));
				}
			}

			return arMappingColor;
		}
		else if (pathwayVertexRep.getType().equals(EPathwayVertexType.enzyme))
		{
			ArrayList<Vec3f> arMappingColor = new ArrayList<Vec3f>();
			arMappingColor.add(new Vec3f(-1, -1, -1)); // invalid color
			return arMappingColor;
		}

		return new ArrayList<Vec3f>();
	}

	public ColorMapping getColorMapper()
	{

		return expressionColorMapper;
	}
}
