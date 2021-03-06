/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.data.importer.tcga.regular;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.data.importer.tcga.Settings;
import org.kohsuke.args4j.Option;


public class TCGASettings extends Settings {
	@Option(name = "-s", aliases = { "--server" }, usage = "TCGA Server URL that hosts TCGA Caleydo project files default: \"http://compbio.med.harvard.edu/tcga/stratomex/data/\"")
	private String tcgaServerURL = GeneralManager.DATA_URL_PREFIX + "tcga/";

	@Option(name = "-g", aliases = { "--sampledGenes" }, usage = "whether to use the sampled genes or not default: \"true\"")
	private String sampleGenes = "true";

	@Option(name = "--fullGenes", usage = "inverse of --sampledGenes")
	private boolean fullGenes = false;

	public String getTcgaServerURL() {
		return tcgaServerURL;
	}

	public boolean isSampleGenes() {
		return Boolean.parseBoolean(sampleGenes) && !fullGenes;
	}

	@Override
	public String toString() {
		return super.toString() + (isSampleGenes() ? "FULLGENES" : "SAMPLEGENES");
	}
}
