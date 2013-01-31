package org.caleydo.data.importer.tcga;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.data.importer.tcga.model.TumorType;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * container for all settings of the TCGA generator
 *
 * @author Samuel Gratzl
 *
 */
public class Settings {
	private static String CALEYDO_WEBSTART_URL = "http://data.icg.tugraz.at/caleydo/download/webstart_"
			+ GeneralManager.VERSION + "/";

	private static final String DATA_PATTERN = "http://gdac.broadinstitute.org/runs/stddata__{0}/data/{2}/{1}/gdac.broadinstitute.org_{2}.{3}.Level_{4}.{1}00.0.0.tar.gz";
	private static final String ANALYSIS_PATTERN = "http://gdac.broadinstitute.org/runs/analyses__{0}/data/{2}/{1}/gdac.broadinstitute.org_{2}.{3}.Level_{4}.{1}00.0.0.tar.gz";
	private static final String REPORT_PATTERN = "http://gdac.broadinstitute.org/runs/analyses__{0}/reports/cancer/{2}/";

	@Option(name = "-t", required = false, aliases = { "--tumortypes" }, usage = "the tumor types to export default: \"all known\"")
	private List<String> tumorTypes = null;

	@Option(name = "-cl", required = false, aliases = { "--clinical" }, usage = "the clinical variables to export default: \"all known\"")
	private List<String> clinicalVariables = null;

	@Argument(required = true, usage = "the dates on the analysis runs as argument list separated by spaces")
	private List<String> analysisRuns = null;
	@Option(name = "-d", required = false, usage = "the dates of the data runs to use default the same as the analysis runs")
	private List<String> dataRuns = null;

	@Option(name = "-o", aliases = { "--output" }, usage = "the directory to store the generated files and temporary files default: \".\"")
	private File outputPath = new File("."); // default current directory

	@Option(name = "-f", aliases = { "--flatOutput" }, usage = "whether to create a flat output directory structure default: \"false\"")
	private boolean flatOutput = false;
	@Option(name = "-c", aliases = { "--clean" }, usage = "don't use the cache default: \"false\"")
	private boolean cleanCache = false;

	@Option(name = "-p", aliases = { "--threads" }, usage = "number of parallel threads, <= 0 -> available processors, default \"1\"")
	private int numThreads = 1;

	@Option(name = "-b", aliases = { "--batch" }, usage = "batch size of parallel top tasks default \"4\"")
	private int batchSize = 4;

	// create path of archive search directory
	// 0 .. analysisRun
	// 1 .. cleaned analysisRun
	// 2 .. tumor
	// 3 .. piplelineName
	// 4 .. level
	@Option(name = "-ap", aliases = { "--analysisPattern" }, usage = "the pattern used to locate analysis run archives, where {0} .. analysisRun {1} .. cleaned analysisRun, {2} .. tumor, {3} .. pipelineName, {4} .. level, default \""
			+ DATA_PATTERN + "\"")
	private String analysisPattern = ANALYSIS_PATTERN;
	@Option(name = "-dp", aliases = { "--dataPattern" }, usage = "the pattern used to locate data run archives, where {0} .. dataRun {1} .. cleaned dataRun, {2} .. tumor, {3} .. pipelineName, {4} .. level, default \""
			+ DATA_PATTERN + "\"")
	private String dataPattern = DATA_PATTERN;

	@Option(name = "-rp", aliases = { "--reportPattern" }, usage = "the pattern used to locate run reports, where {0} .. analysisRun {1} .. cleaned analysisRun, {2} .. tumor, default \""
			+ REPORT_PATTERN + "\"")
	private String reportPattern = REPORT_PATTERN;

	private Gson gson = new GsonBuilder().setPrettyPrinting().create();



	public boolean validate() {
		if (dataRuns == null)
			dataRuns = analysisRuns;
		if (dataRuns.size() != analysisRuns.size()) {
			System.err
					.println("Error during parsing of program arguments. You need to provide a corresponding data run for each analysis run. Closing program.");
			return false;
		}

		if (numThreads <= 0)
			numThreads = Runtime.getRuntime().availableProcessors();
		return true;
	}

	public String getJNLPOutputDirectory() {
		if (this.flatOutput)
			return ensureExistingDir(outputPath);
		else
			return ensureExistingDir(new File(outputPath, "jnlp"));
	}

	public String getJNLPURL(String fileName) {
		return CALEYDO_WEBSTART_URL + fileName;
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

	/**
	 * @return the analysisRuns
	 */
	public List<String> getAnalysisRuns() {
		return analysisRuns;
	}

	/**
	 * @return the dataRuns
	 */
	public List<String> getDataRuns() {
		return dataRuns;
	}

	/**
	 * @return the tumorTypes
	 */
	public Collection<TumorType> getTumorTypes() {
		if (this.tumorTypes == null || this.tumorTypes.isEmpty())
			return TumorType.values();
		List<TumorType> types = new ArrayList<>();
		for(String t : tumorTypes) {
			TumorType type = TumorType.byName(t);
			if (type == null) {
				System.err.println("unknown tumor type: "+t);
			} else
				types.add(type);
		}
		return types;
	}

	/**
	 * @return the clinicalVariables, see {@link #clinicalVariables}
	 */
	public Collection<String> getClinicalVariables() {
		if (this.clinicalVariables == null || this.clinicalVariables.isEmpty())
			return Collections.emptyList();
		List<String> r = new ArrayList<>();
		for (String c : this.clinicalVariables)
			r.add(c.toLowerCase());
		return r;
	}

	public FirehoseProvider createFirehoseProvider(TumorType tumor, String analysisRunIdentifier,
			String dataRunIdentifier) {
		return new FirehoseProvider(tumor, analysisRunIdentifier, dataRunIdentifier, this);
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

	static String clean(String id) {
		return id == null ? null : id.replace("_", "");
	}

	public URL getDataURL(String runId, TumorType tumor, String pipelineName, int level)
			throws MalformedURLException {
		return new URL(MessageFormat.format(dataPattern, runId, clean(runId), tumor, pipelineName, level));
	}

	public URL getAnalysisURL(String runId, TumorType tumor, String pipelineName, int level)
			throws MalformedURLException {
		return new URL(MessageFormat.format(analysisPattern, runId, clean(runId), tumor, pipelineName,
				level));
	}

	public String getReportUrl(String analysisRun, TumorType tumor) {
		return MessageFormat.format(reportPattern, analysisRun, clean(analysisRun), tumor);
	}

	/**
	 * @return the batchSize
	 */
	public int getBatchSize() {
		return batchSize;
	}

	/**
	 * @return the gson, see {@link #gson}
	 */
	public Gson getGson() {
		return gson;
	}
}
