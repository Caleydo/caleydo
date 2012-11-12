package org.caleydo.data.importer.tcga.regular;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.data.importer.tcga.Settings;
import org.kohsuke.args4j.Option;


public class TCGASettings extends Settings {
	@Option(name = "s", aliases = { "server" }, usage = "TCGA Server URL that hosts TCGA Caleydo project files")
	private String tcgaServerURL = "http://compbio.med.harvard.edu/tcga/stratomex/data/";

	@Option(name = "g", aliases = { "sampleGenes" })
	private boolean sampleGenes = true;

	public static String CALEYDO_WEBSTART_URL = "http://data.icg.tugraz.at/caleydo/download/webstart_"
			+ GeneralManager.VERSION + "/";


	public String getTcgaServerURL() {
		return tcgaServerURL;
	}

	public boolean isSampleGenes() {
		return sampleGenes;
	}
}
