package org.caleydo.data.importer.tcga;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.caleydo.core.manager.GeneralManager;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class Settings {
	private static String CALEYDO_WEBSTART_URL = "http://data.icg.tugraz.at/caleydo/download/webstart_"
			+ GeneralManager.VERSION + "/";

	private static final String BASE_URL = "http://gdac.broadinstitute.org/runs/";
	private static final String DATA_FILE_PATTERN = "gdac.broadinstitute.org_{1}.{3}.Level_{4}.{0,date,yyyyMMdd}00.0.0.tar.gz";
	private static final String FILE_PATTERN = "gdac.broadinstitute.org_{2}.{3}.Level_{4}.{0,date,yyyyMMdd}00.0.0.tar.gz";
	private static final String DATA_PATTERN = BASE_URL + "stddata__{0,date,yyyy_MM_dd}/data/{1}/{0,date,yyyyMMdd}/{3}";
	private static final String ANALYSIS_PATTERN = BASE_URL
			+ "analyses__{0,date,yyyy_MM_dd}/data/{1}/{0,date,yyyyMMdd}/{3}";
	private static final String REPORT_PATTERN = BASE_URL + "analyses__{0,date,yyyy_MM_dd}/reports/cancer/{1}/";

	@Option(name = "-t", required = false, aliases = { "--tumortypes" }, usage = "the tumor types to export default: \"all known\"")
	private List<String> tumorTypes = null;

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
	@Option(name = "-fp", aliases = { "--filePattern" }, usage = "the pattern used to create a archive analysis file name, where {0} .. run (date) {1} .. tumor, {2} .. tumor sample, {3} .. pipelineName, {4} .. level, default \""
			+ FILE_PATTERN + "\"")
	private String filePattern = FILE_PATTERN;
	@Option(name = "-ap", aliases = { "--analysisPattern" }, usage = "the pattern used to locate analysis run archives, where {0} .. run (date) {1} .. tumor, {2} .. tumor sample, {3} .. file name, default \""
			+ ANALYSIS_PATTERN + "\"")
	private String analysisPattern = ANALYSIS_PATTERN;
	@Option(name = "-dp", aliases = { "--dataPattern" }, usage = "the pattern used to locate data run archives, where {0} .. run (date) {1} .. tumor, {2} .. tumor sample, {3} .. file name, default \""
			+ DATA_PATTERN + "\"")
	private String dataPattern = DATA_PATTERN;
	@Option(name = "-dfp", aliases = { "--dataFilePattern" }, usage = "the pattern used to create a archive data file name, where {0} .. run (date) {1} .. tumor, {2} .. tumor sample, {3} .. pipelineName, {4} .. level, default \""
			+ DATA_FILE_PATTERN + "\"")
	private String dataFilePattern = DATA_FILE_PATTERN;

	@Option(name = "-rp", aliases = { "--reportPattern" }, usage = "the pattern used to locate run reports, where {0} .. run (date), {1} .. tumor, {2} .. tumor sample, default \""
			+ REPORT_PATTERN + "\"")
	private String reportPattern = REPORT_PATTERN;

	@Option(name = "--downloadOnly", usage = "if enabled only the files will be downloaded")
	private boolean downloadOnly = false;



	public boolean validate() {
		if (dataRuns == null)
			dataRuns = analysisRuns;
		if (tumorTypes == null) {
			tumorTypes = new ArrayList<>();
			for (ETumorType type : ETumorType.values())
				tumorTypes.add(type.name());
		}
		if (dataRuns.size() != analysisRuns.size()) {
			System.err
					.println("Error during parsing of program arguments. You need to provide a corresponding data run for each analysis run. Closing program.");
			return false;
		}

		if (numThreads <= 0)
			numThreads = Runtime.getRuntime().availableProcessors();
		return true;
	}

	/**
	 * @return the downloadOnly, see {@link #downloadOnly}
	 */
	public boolean isDownloadOnly() {
		return downloadOnly;
	}

	public String getJNLPOutputDirectory() {
		if (this.flatOutput)
			return ensureExistingDir(outputPath);
		else
			return ensureExistingDir(new File(outputPath, "jnlp" + GeneralManager.VERSION));
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
			return ensureExistingDir(new File(new File(outputPath, "data" + GeneralManager.VERSION), subDir));
	}

	public String getTemporaryDirectory() {
		return ensureExistingDir(new File(outputPath, "tmp"));
	}

	/**
	 * @return the analysisRuns
	 */
	public List<Date> getAnalysisRuns() {
		return Lists.transform(analysisRuns, toDate);
	}

	public static String format(Date date) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd");
		return df.format(date);
	}

	public static String formatClean(Date date) {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		return df.format(date);
	}

	private static final Function<String, Date> toDate = new Function<String, Date>() {
		private final SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd");

		@Override
		public Date apply(String in) {
			try {
				return df.parse(in);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
	};

	/**
	 * @return the dataRuns
	 */
	public List<Date> getDataRuns() {
		return Lists.transform(dataRuns, toDate);
	}

	/**
	 * @return the tumorTypes
	 */
	public List<String> getTumorTypes() {
		return tumorTypes;
	}

	public FirehoseProvider createFirehoseProvider(String tumorAbbreviation, Date analysisRunIdentifier,
			Date dataRunIdentifier) {
		return new FirehoseProvider(tumorAbbreviation, analysisRunIdentifier, dataRunIdentifier, this);
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

	public int getBatchSize() {
		return batchSize;
	}

	public URL getDataURL(Date run, String tumor, String tumorSample, String pipelineName, int level)
			throws MalformedURLException {
		String file = MessageFormat.format(dataFilePattern, run, tumor, tumorSample, pipelineName, level);
		String url = MessageFormat.format(dataPattern, run, tumor, tumorSample, file);
		return new URL(url);
	}

	public URL getAnalysisURL(Date run, String tumor, String tumorSample, String pipelineName, int level)
			throws MalformedURLException {
		String file = MessageFormat.format(filePattern, run, tumor, tumorSample, pipelineName, level);
		String url = MessageFormat.format(analysisPattern, run, tumor, tumorSample, file);
		return new URL(url);
	}

	public String getReportUrl(Date run, String tumor) {
		return MessageFormat.format(reportPattern, run, tumor);
	}
}
