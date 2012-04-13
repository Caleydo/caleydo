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
package org.caleydo.testing.applications.base;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import weka.clusterers.ClusterEvaluation;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;

public class KMeansClusterer {

	private static int NRCLUSTER = 5;

	public static void main(String[] args) throws Exception {

		// load data
		Instances data = new Instances(new BufferedReader(new FileReader(
				"./src/org/caleydo/testing/applications/base/splice.arff")));

		// class index negative --> no class defined --> unsupervised learning
		data.setClassIndex(-1);

		// System.out.println(data.toString());

		SimpleKMeans clusterer = new SimpleKMeans();
		clusterer.setNumClusters(NRCLUSTER);

		// train the clusterer
		clusterer.buildClusterer(data);

		ClusterEvaluation eval = new ClusterEvaluation();
		eval.setClusterer(clusterer); // the cluster to evaluate
		eval.evaluateClusterer(data);

		// System.out.print("eval.getNumClusters():  ");
		// System.out.println(eval.getNumClusters());

		// System.out.print("eval.clusterResultsToString():  ");
		// System.out.println(eval.clusterResultsToString());

		double[] ClusterAssignments = eval.getClusterAssignments();
		// System.out.println(test.length);
		// for (int i = 0; i < test.length; i++) {
		// System.out.println(test[i]);
		// }

		// Arraylist holding # of elements per cluster
		ArrayList<Integer> count = new ArrayList<Integer>();
		
		for (int i = 0; i < NRCLUSTER; i++)
			count.add(0);

		// Arraylist with ordered indexes according to the clusters
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		
		for (int cluster = 0; cluster < NRCLUSTER; cluster++) {
			for (int i = 0; i < data.numInstances(); i++) {
				if (ClusterAssignments[i] == cluster) {
					indexes.add(i);
					count.set(cluster, count.get(cluster) + 1);
				}
			}
		}

		for (Integer iter : indexes) {
			System.out.print(iter + " ");
		}
		System.out.println(" ");

		int i = 0;
		for (Integer iter : count) {
			System.out.println("cluster Nr:" + i + " has " + iter + " elements");
			i++;
		}
	}
}
