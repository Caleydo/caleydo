package org.caleydo.testing.applications.base;

import java.io.BufferedReader;
import java.io.FileReader;

import weka.clusterers.ClusterEvaluation;
import weka.clusterers.EM;
import weka.core.Instances;
import weka.filters.Filter;

// Code from: http://weka.wiki.sourceforge.net/Use+Weka+in+your+Java+code

public class WekaTest {

	public static void main(String[] args) throws Exception {

		// load data
		Instances data = new Instances(new BufferedReader(new FileReader(
				"./src/org/caleydo/testing/applications/base/splice.arff")));

		data.setClassIndex(data.numAttributes() - 1);

		// generate the class-less data to train the clusterer with
		weka.filters.unsupervised.attribute.Remove filter = new weka.filters.unsupervised.attribute.Remove();
		filter.setAttributeIndices("" + (data.classIndex() + 1));
		filter.setInputFormat(data);
		Instances dataClusterer = Filter.useFilter(data, filter);

		// train the clusterer, e.g., EM
		EM clusterer = new EM();
		// set further options for EM, if necessary...
		clusterer.buildClusterer(dataClusterer);

		// evaluate the clusterer with the data containing still the class
		// attribute
		ClusterEvaluation eval = new ClusterEvaluation();
		eval.setClusterer(clusterer);
		eval.evaluateClusterer(data);

		// print the results of the evaluation to stdout
		System.out.println(eval.clusterResultsToString());

	}
}
