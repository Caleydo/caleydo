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
package org.caleydo.core.util.clusterer;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.event.IListenerOwner;
import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;

/**
 * Interface class for all clustering algorithms.
 * 
 * @author Bernhard Schlegl
 */
public interface IClusterer
	extends IListenerOwner {

	/**
	 * Clusters a given set and returns the Id of the new generated virtual array with sorted indexes
	 * according to the cluster result. If an error occurs or an user aborts the cluster process a negative
	 * value will be returned.
	 * 
	 * @param set
	 *            Set
	 * @param clusterState
	 *            Container for cluster info (algo, type, ...)
	 * @param iProgressBarOffsetValue
	 *            Offset value needed for overall progress bar while bi clustering. During the first run the
	 *            value is 0 and during the second run 50.
	 * @param iProgressBarMultiplier
	 *            multiplier needed for overall progress bar. In case of bi clustering the value is 1. In case
	 *            of normal clustering the value is 2.
	 * @return Sorted VirtualArray.
	 */
	public PerspectiveInitializationData getSortedVA(ATableBasedDataDomain dataDomain,
		ClusterConfiguration clusterState, int iProgressBarOffsetValue, int iProgressBarMultiplier);

	public void cancel();

}
