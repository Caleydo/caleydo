package org.caleydo.core.util.clusterer;

import java.io.Serializable;

import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.util.graph.core.Graph;

/**
 * Overall graph that holds all elements
 * 
 * @author Bernhard Schlegl
 */
public class HierarchyGraph
	extends Graph
	implements IUniqueObject, Serializable
{
	private static final long serialVersionUID = 1L;

	private String sNodeName;

	private int iClusterNr;

	private float fcoefficient;

	public HierarchyGraph(final String sNodeName, final int iClusterNr, final float fcoefficient)
	{
		super(GeneralManager.get().getIDManager().createID(EManagedObjectType.HIERARCHYGRAPH));

		this.sNodeName = sNodeName;
		this.iClusterNr = iClusterNr;
		this.fcoefficient = fcoefficient;
	}

	@Override
	public int getID()
	{
		return super.getId();
	}
}