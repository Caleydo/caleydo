package org.caleydo.testing.applications.base;

import java.io.BufferedReader;
import java.io.FileReader;

import weka.clusterers.Cobweb;
import weka.core.Instances;

// Code from: http://weka.wiki.sourceforge.net/Use+Weka+in+your+Java+code

public class WekaTest {

	public static void main(String[] args) throws Exception {

		// load data
		Instances data = new Instances(new BufferedReader(new FileReader(
				"./src/org/caleydo/testing/applications/base/weather.arff")));

		// class index negative --> no class defined --> unsupervised learning
		data.setClassIndex(-1);

//		System.out.println(data.toString());
		
		Cobweb clusterer = new Cobweb();

		// train the clusterer
		clusterer.buildClusterer(data);

		System.out.println(clusterer);
		
	}
}
