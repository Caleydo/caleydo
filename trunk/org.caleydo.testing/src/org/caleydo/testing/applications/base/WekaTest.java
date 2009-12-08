package org.caleydo.testing.applications.base;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import weka.clusterers.ClusterEvaluation;
import weka.clusterers.Cobweb;
import weka.core.Instances;

// Code from: http://weka.wiki.sourceforge.net/Use+Weka+in+your+Java+code

public class WekaTest {

	public static void main(String[] args) throws Exception {

		// load data
		Instances data = new Instances(new BufferedReader(new FileReader(
				"./src/org/caleydo/testing/applications/base/splice.arff")));

		// class index negative --> no class defined --> unsupervised learning
		data.setClassIndex(-1);

//		System.out.println(data.toString());
				
//		double[] array = {3.2, 4, 5, 7, 8.6, 6.00};
//		data.add(new Instance(1.0, array));	
//		Instance tempIn = new Instance(1.0, array);
//		System.out.println(tempIn);
		
		Cobweb clusterer = new Cobweb();

		// train the clusterer
		clusterer.buildClusterer(data);

		// System.out.println(clusterer.numberOfClusters());

		ClusterEvaluation eval = new ClusterEvaluation();
		eval.setClusterer(clusterer); // the cluster to evaluate
		eval.evaluateClusterer(data);

		// System.out.print("eval.getNumClusters():  ");
		// System.out.println(eval.getNumClusters());
		//		
		// System.out.print("eval.clusterResultsToString():  ");
		// System.out.println(eval.clusterResultsToString());

		double[] test = eval.getClusterAssignments();
		int nrclusters = eval.getNumClusters();

		// System.out.println(test.length);
		// for (int i = 0; i < test.length; i++) {
		// System.out.println(test[i]);
		// }
		
		ArrayList<Integer> temp = new ArrayList<Integer>();
		
		// Arraylist holding # of elements per cluster
		ArrayList<Integer> count = new ArrayList<Integer>();
		
		for (int i = 0; i < nrclusters; i++)
			temp.add(0);

		// Arraylist with indexes according to the clusters
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		
		for (int cluster = 0; cluster < nrclusters; cluster++) {
			for (int i = 0; i < data.numInstances(); i++) {
				if (test[i] == cluster) {
					indexes.add(i);
					temp.set(cluster, temp.get(cluster) + 1);
				}
			}
		}

//		for (Integer iter : indexes) {
//			System.out.print(iter + " ");
//		}
//		System.out.println(" ");

		for (Integer iter : temp) {
			if (iter > 0)
				count.add(iter);
		}

//		int i = 0;
//		for (Integer iter : count) {
//			System.out.println("cluster Nr:" + i + " has " + iter + " elements");
//			i++;
//		}
//		IVirtualArray va = new VirtualArray(count.size(), count);
		
	}
}
