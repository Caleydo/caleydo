package org.caleydo.data.importer.tcga.qualitycontrol;

import org.caleydo.data.importer.tcga.Settings;


public class TCGAQCSettings extends Settings {
	private String[] analysisRuns = null;
	private String tcgaServerURL = "";
	private boolean sampleGenes = true;


	public int getNumRuns() {
		return analysisRuns.length;
	}

	public String[] getAnalysisRuns() {
		return analysisRuns;
	}

	public String getAnalysisRun(int i) {
		return analysisRuns[i];
	}

	public void setRuns(String[] analysisRuns) {
		this.analysisRuns = analysisRuns;
	}


	public String getTcgaServerURL() {
		return tcgaServerURL;
	}

	public void setTcgaServerURL(String tcgaServerURL) {
		this.tcgaServerURL = tcgaServerURL;
	}

	public boolean isSampleGenes() {
		return sampleGenes;
	}

	public void setSampleGenes(boolean sampleGenes) {
		this.sampleGenes = sampleGenes;
	}
}
