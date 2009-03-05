package org.caleydo.core.util.clusterer;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.util.graph.core.Graph;

import weka.core.Instances;
import weka.core.RevisionUtils;

public class HierarchicalClusterer
	extends weka.clusterers.RandomizableClusterer
{

	/** for serialization */
	private static final long serialVersionUID = 4353530254946938099L;

	/**
	 * Number of clusters (nodes in the tree). Must never be queried directly,
	 * only via the method numberOfClusters(). Otherwise it's not guaranteed
	 * that it contains the correct value.
	 * 
	 * @see #numberOfClusters()
	 * @see #m_numberOfClustersDetermined
	 */
	protected int iNumberOfClusters = -1;

	/** whether the number of clusters was already determined */
	protected boolean bNumberOfClustersDetermined = false;

	/**
	 * Holds the graph (dendrogram)
	 */
	protected Graph ClusterGraph = null;

	/**
	 * Returns the number of clusters.
	 * 
	 * @return the number of clusters
	 */
	public int numberOfClusters()
	{
		if (bNumberOfClustersDetermined == true)
			return iNumberOfClusters;
		else
			return -1;
	}

	public Graph cluster(ISet set, Integer iVAIdOriginal)
	{
		System.out.println("build hierarchical clusterer on VA with index: " + iVAIdOriginal);

		// ClusterGraph = ...;
		
		return ClusterGraph;
	}
	
	/**
	 * Returns the revision string.
	 * 
	 * @return the revision
	 */
	public String getRevision()
	{
		return RevisionUtils.extract("$Revision: 0.01 $");
	}

	@Override
	public void buildClusterer(Instances arg0) throws Exception
	{
		// TODO Auto-generated method stub

	}
}
