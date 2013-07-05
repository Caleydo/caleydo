package org.caleydo.data.importer.tcga.regular;

import org.caleydo.data.importer.tcga.Settings;
import org.kohsuke.args4j.Option;


public class TCGASettings extends Settings {
	@Option(name = "-s", aliases = { "--server" }, usage = "TCGA Server URL that hosts TCGA Caleydo project files default: \"http://compbio.med.harvard.edu/tcga/stratomex/data/\"")
	private String tcgaServerURL = "http://compbio.med.harvard.edu/tcga/stratomex/data/";

	@Option(name = "-g", aliases = { "--sampledGenes" }, usage = "whether to use the sampled genes or not default: \"true\"")
	private String sampleGenes = "true";

	public String getTcgaServerURL() {
		return tcgaServerURL;
	}

	public boolean isSampleGenes() {
		return Boolean.parseBoolean(sampleGenes);
	}
}
