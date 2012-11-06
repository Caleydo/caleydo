package org.caleydo.data.importer.tcga;

import java.io.File;

import org.caleydo.data.importer.tcga.provider.AFirehoseProvider;
import org.caleydo.data.importer.tcga.provider.RemoteFirehoseProvider;

public class Settings {
	private String[] tumorTypes = null;

	private File outputPath = new File("."); // default current directory
	private boolean flatOutput = false;
	private boolean cleanCache = false;

	private int numThreads = 1;

	public String getJNLPOutputDirectory() {
		if (this.flatOutput)
			return ensureExistingDir(outputPath);
		else
			return ensureExistingDir(new File(outputPath, "jnlp"));
	}

	private static String ensureExistingDir(File f) {
		if (f.exists())
			return f.getAbsolutePath() + "/";
		f.mkdirs();
		return f.getAbsolutePath() + "/";
	}

	public String getDataDirectory(String subDir) {
		if (this.flatOutput)
			return ensureExistingDir(outputPath);
		else
			return ensureExistingDir(new File(new File(outputPath, "data"), subDir));
	}

	public String getTemporaryDirectory() {
		return ensureExistingDir(new File(outputPath, "tmp"));
	}

	public void setTumorTypes(String[] tumorTypes) {
		this.tumorTypes = tumorTypes;
	}

	public void setOutput(String outputPath, boolean flatOutput, boolean cleanCache) {
		this.outputPath = new File(outputPath);
		this.flatOutput = flatOutput;
		this.cleanCache = cleanCache;
	}

	public String[] getTumorTypes() {
		return tumorTypes;
	}

	public AFirehoseProvider createFirehoseProvider(String tumorAbbreviation, String analysisRunIdentifier, String dataRunIdentifier) {
		return new RemoteFirehoseProvider(tumorAbbreviation, analysisRunIdentifier, dataRunIdentifier, this);
	}

	public AFirehoseProvider createFirehoseProvider(String tumorAbbreviation, String analysisRunIdentifier) {
		return new RemoteFirehoseProvider(tumorAbbreviation, analysisRunIdentifier, null, this);
	}

	public void setNumThreads(int numThreads) {
		if (numThreads <= 0)
			numThreads = Runtime.getRuntime().availableProcessors();
		this.numThreads = numThreads;
	}

	public int getNumThreads() {
		return numThreads;
	}

	public boolean isCleanCache() {
		return cleanCache;
	}

	public boolean isFlatOutput() {
		return flatOutput;
	}
}
