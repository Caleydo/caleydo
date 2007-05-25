package org.jgraph.plugins.gpgraph;

import org.jgraph.pad.coreframework.GPAbstractActionDefault;

public abstract class GPGraphAction extends GPAbstractActionDefault {

	public GPGraph getCurrentGPGraph() {
		try {
			return (GPGraph) getCurrentGraph();
		} catch (Exception ex) {
			System.err.print("Your graph base class isn't a GPGraph!");
			ex.printStackTrace();
			return null;
		}
	}

}
