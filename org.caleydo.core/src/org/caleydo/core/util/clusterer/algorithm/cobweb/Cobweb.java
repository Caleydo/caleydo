/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/

package org.caleydo.core.util.clusterer.algorithm.cobweb;

import java.util.Random;

import weka.clusterers.RandomizableClusterer;
import weka.clusterers.UpdateableClusterer;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.RevisionUtils;

/**
 * Cobweb cluster algorithm
 */
public class Cobweb
	extends RandomizableClusterer
	implements UpdateableClusterer {

	/** for serialization */
	static final long serialVersionUID = 928406656495092318L;

	/**
	 * Holds the root of the Cobweb tree.
	 */
	protected CNode m_cobwebTree = null;

	/**
	 * Number of clusters (nodes in the tree). Must never be queried directly, only via the method
	 * numberOfClusters(). Otherwise it's not guaranteed that it contains the correct value.
	 * 
	 * @see #numberOfClusters()
	 * @see #m_numberOfClustersDetermined
	 */
	protected int m_numberOfClusters = -1;

	/** whether the number of clusters was already determined */
	protected boolean m_numberOfClustersDetermined = false;

	/**
	 * Output instances in graph representation of Cobweb tree (Allows instances at nodes in the tree to be
	 * visualized in the Explorer).
	 */
	protected boolean m_saveInstances = false;

	/**
	 * default constructor
	 */
	public Cobweb() {
		super();

		m_SeedDefault = 42;
		setSeed(m_SeedDefault);
	}

	/**
	 * Builds the clusterer.
	 * 
	 * @param data
	 *            the training instances.
	 * @throws Exception
	 *             if something goes wrong.
	 */
	@Override
	public void buildClusterer(Instances data) throws Exception {
		m_numberOfClusters = -1;
		m_cobwebTree = null;

		// can clusterer handle the data?
		getCapabilities().testWithFail(data);

		// randomize the instances
		data = new Instances(data);
		data.randomize(new Random(getSeed()));

		for (int i = 0; i < data.numInstances(); i++) {
			updateClusterer(data.instance(i));
		}

		updateFinished();
	}

	/**
	 * Signals the end of the updating.
	 */
	@Override
	public void updateFinished() {
		determineNumberOfClusters();
	}

	/**
	 * Classifies a given instance.
	 * 
	 * @param instance
	 *            the instance to be assigned to a cluster
	 * @return the number of the assigned cluster as an interger if the class is enumerated, otherwise the
	 *         predicted value
	 * @throws Exception
	 *             if instance could not be classified successfully
	 */
	@Override
	public int clusterInstance(Instance instance) throws Exception {
		CNode host = m_cobwebTree;
		CNode temp = null;

		determineNumberOfClusters();

		do {
			if (host.getChilds() == null) {
				temp = null;
				break;
			}

			host.updateStats(instance, false);
			temp = host.findHost(instance, true);
			host.updateStats(instance, true);

			if (temp != null) {
				host = temp;
			}
		} while (temp != null);

		return host.getClusterNum();
	}

	/**
	 * determines the number of clusters if necessary
	 * 
	 * @see #m_numberOfClusters
	 * @see #m_numberOfClustersDetermined
	 */
	protected void determineNumberOfClusters() {
		if (!m_numberOfClustersDetermined && m_cobwebTree != null) {
			int[] numClusts = new int[1];
			numClusts[0] = 0;
			try {
				m_cobwebTree.assignClusterNums(numClusts);
			}
			catch (Exception e) {
				e.printStackTrace();
				numClusts[0] = 0;
			}
			m_numberOfClusters = numClusts[0];

			m_numberOfClustersDetermined = true;
		}
	}

	/**
	 * Returns the number of clusters.
	 * 
	 * @return the number of clusters
	 */
	@Override
	public int numberOfClusters() {
		determineNumberOfClusters();
		return m_numberOfClusters;
	}

	/**
	 * Returns default capabilities of the clusterer.
	 * 
	 * @return the capabilities of this clusterer
	 */
	@Override
	public Capabilities getCapabilities() {
		Capabilities result = super.getCapabilities();

		// attributes
		result.enable(Capability.NOMINAL_ATTRIBUTES);
		result.enable(Capability.NUMERIC_ATTRIBUTES);
		result.enable(Capability.DATE_ATTRIBUTES);
		result.enable(Capability.MISSING_VALUES);

		// other
		result.setMinimumNumberInstances(0);

		return result;
	}

	/**
	 * Returns the graph, for drawing dendrogram
	 * 
	 * @return CNode
	 */
	public CNode getGraph() {
		determineNumberOfClusters();
		return m_cobwebTree;
	}

	/**
	 * Adds an instance to the clusterer.
	 * 
	 * @param newInstance
	 *            the instance to be added
	 * @throws Exception
	 *             if something goes wrong
	 */
	@Override
	public void updateClusterer(Instance newInstance) throws Exception {
		m_numberOfClustersDetermined = false;

		if (m_cobwebTree == null) {
			m_cobwebTree = new CNode(newInstance.numAttributes(), newInstance);
		}
		else {
			m_cobwebTree.addInstance(newInstance);
		}
	}

	/**
	 * Adds an instance to the Cobweb tree.
	 * 
	 * @param newInstance
	 *            the instance to be added
	 * @throws Exception
	 *             if something goes wrong
	 * @deprecated updateClusterer(Instance) should be used instead
	 * @see #updateClusterer(Instance)
	 */
	@Deprecated
	public void addInstance(Instance newInstance) throws Exception {
		updateClusterer(newInstance);
	}

	/**
	 * Returns the revision string.
	 * 
	 * @return the revision
	 */
	@Override
	public String getRevision() {
		return RevisionUtils.extract("$Revision: 1.25 $");
	}

}
