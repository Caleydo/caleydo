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
	private float fCoefficient;
	
	public HierarchyGraph()
	{
		super(GeneralManager.get().getIDManager().createID(EManagedObjectType.HIERARCHYGRAPH));
	}
	
	public HierarchyGraph(final String sNodeName, final int iClusterNr, final float fCoefficient)
	{
		super(GeneralManager.get().getIDManager().createID(EManagedObjectType.HIERARCHYGRAPH));
		this.setNodeName(sNodeName);
		this.setClusterNr(iClusterNr);
		this.setCoefficient(fCoefficient);
	}

	@Override
	public int getID()
	{
		return super.getId();
	}

	public void setNodeName(String sNodeName)
	{
		this.sNodeName = sNodeName;
	}

	public String getNodeName()
	{
		return sNodeName;
	}

	public void setClusterNr(int iClusterNr)
	{
		this.iClusterNr = iClusterNr;
	}

	public int getClusterNr()
	{
		return iClusterNr;
	}

	public void setCoefficient(float fCoefficient)
	{
		this.fCoefficient = fCoefficient;
	}

	public float getCoefficient()
	{
		return fCoefficient;
	}
}