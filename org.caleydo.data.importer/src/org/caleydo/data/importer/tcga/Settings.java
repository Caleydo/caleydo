/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.data.importer.tcga;

import java.io.File;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.data.importer.tcga.model.TumorType;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * container for all settings of the TCGA generator
 *
 * @author Samuel Gratzl
 *
 */
public class Settings {

	public static enum ClusterOptions {
		NONE, AFFINITY, KMEANS, TREE;
	}

	private static String CALEYDO_JNLP_GENERATOR_URL = "http://data.icg.tugraz.at/caleydo/download/webstart_"
			+ GeneralManager.VERSION + "/{0}_{1}.jnlp"; // jnlpgenerator.php?date={0}&tumor={1}";

	private static final String BASE_URL = "http://gdac.broadinstitute.org/runs/";
	/**
	 * 0..run (a date), 1..tumor, 2..tumorSample (e.g. -TP), 3..pipelineName, 4..level
	 */
	private static final String FILE_PATTERN = "gdac.broadinstitute.org_{2}.{3}.Level_{4}.{0,date,yyyyMMdd}00.0.0.tar.gz";
	/**
	 * 0..run (a date), 1..tumor, 2..tumorSample (e.g. -TP), 3..pipelineName, 4..level
	 */
	private static final String DATAFILE_PATTERN = "gdac.broadinstitute.org_{1}.{3}.Level_{4}.{0,date,yyyyMMdd}00.0.0.tar.gz";
	/**
	 * 0..run (a date), 1..tumor, 2..tumorSample (e.g -TP), 3..file
	 */
	private static final String DATA_PATTERN = BASE_URL + "stddata__{0,date,yyyy_MM_dd}/data/{1}/{0,date,yyyyMMdd}/{3}";
	/**
	 * 0..run (a date), 1..tumor, 2..tumorSample (e.g -TP), 3..file
	 */
	private static final String ANALYSIS_PATTERN = BASE_URL
			+ "analyses__{0,date,yyyy_MM_dd}/data/{1}/{0,date,yyyyMMdd}/{3}";

	/**
	 * 0..run (date), 1..tumor
	 */
	private static final String REPORT_PATTERN = BASE_URL + "analyses__{0,date,yyyy_MM_dd}/reports/cancer/{1}/";

	@Option(name = "-awg", required = false, usage = "indicate an AWG run, i.e. use exact tumor type labels and automatically set specific urls")
	private String awgGroup = null;

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

	@Option(name = "-cg", required = false, aliases = { "--cluster" }, usage = "cluster genes with options {tree|kmeans|none|affinity}, default \"tree\"")
	private ClusterOptions cluster = ClusterOptions.TREE;

	// create path of archive search directory
	// 0 .. analysisRun
	// 1 .. cleaned analysisRun
	// 2 .. tumor
	// 3 .. piplelineName
	// 4 .. level
	@Option(name = "-ap", aliases = { "--analysisPattern" }, usage = "the pattern used to locate analysis run archives, where {0} .. run (date) {1} .. tumor, {2} .. tumor sample, {3} .. file name, default \""
			+ ANALYSIS_PATTERN + "\"")
	private String analysisPattern = ANALYSIS_PATTERN;
	@Option(name = "-fp", aliases = { "--filePattern" }, usage = "the pattern used to create a archive file name, where {0} .. run (date) {1} .. tumor, {2} .. tumor sample, {3} .. pipelineName, {4} .. level, default \""
			+ FILE_PATTERN + "\"")
	private String filePattern = FILE_PATTERN;
	@Option(name = "-dp", aliases = { "--dataPattern" }, usage = "the pattern used to locate data run archives, where {0} .. run (date) {1} .. tumor, {2} .. tumor sample, {3} .. file name, default \""
			+ DATA_PATTERN + "\"")
	private String dataPattern = DATA_PATTERN;
	@Option(name = "-dfp", aliases = { "--dataFilePattern" }, usage = "the pattern used to create a archive file name, where {0} .. run (date) {1} .. tumor, {2} .. tumor sample, {3} .. pipelineName, {4} .. level, default \""
			+ DATAFILE_PATTERN + "\"")
	private String dataFilePattern = DATAFILE_PATTERN;

	@Option(name = "-rp", aliases = { "--reportPattern" }, usage = "the pattern used to locate run reports, where {0} .. run (date), {1} .. tumor, {2} .. tumor sample, default \""
			+ REPORT_PATTERN + "\"")
	private String reportPattern = REPORT_PATTERN;

	@Option(name = "--downloadOnly", usage = "if enabled only the files will be downloaded")
	private boolean downloadOnly = false;

	@Option(name = "--username", usage = "the username to use for authentication, use PROMPT for interactive prompting")
	private String username = null;

	@Option(name = "--password", usage = "the password to use for authentication, use PROMPT for interactive prompting")
	private String password = null;

	private Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().setDateFormat("yyyy_MM_dd")
			.create();

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

		if (username != null && password != null) {
			username = fixPrompt(username, "Enter the username: ");
			password = fixPrompt(password, "Enter the password: ");
			// set Authenticator for following urls
			Authenticator.setDefault(new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password.toCharArray());
				}
			});
		}

		if (awgGroup != null) {
			String tumor = awgGroup.toUpperCase();
			// check if not manually overridden
			if (analysisPattern.equals(ANALYSIS_PATTERN))
				analysisPattern = BASE_URL + "awg_" + awgGroup + "__{0,date,yyyy_MM_dd}/data/{2}/{0,date,yyyyMMdd}/{3}";
			if (filePattern.equals(FILE_PATTERN))
				filePattern = "gdac.broadinstitute.org_{1}.{3}.Level_{4}.{0,date,yyyyMMdd}00.0.0.tar.gz";
			if (dataPattern.equals(DATA_PATTERN))
				dataPattern = BASE_URL + "/stddata__{0,date,yyyy_MM_dd}/data/" + tumor
					+ "/{0,date,yyyyMMdd}/{3}";
			if (dataFilePattern.equals(DATAFILE_PATTERN))
				dataFilePattern = "gdac.broadinstitute.org_" + tumor + ".{3}.Level_{4}.{0,date,yyyyMMdd}00.0.0.tar.gz";
			if (reportPattern.equals(REPORT_PATTERN))
				reportPattern = BASE_URL + "awg_" + awgGroup
					+ "__{0,date,yyyy_MM_dd}/reports/cancer/{1}/";
		}

		return true;
	}

	/**
	 * @param username2
	 * @param string
	 * @return
	 */
	private static String fixPrompt(String var, String prompt) {
		if ("PROMPT".equals(var)) {
			System.out.print(prompt);
			try (Scanner s = new Scanner(System.in)) {
				String l = s.nextLine();
				if (StringUtils.isBlank(l)) {
					System.err.println("Aborting");
					System.exit(1);
				}
				return l;
			}
		}
		return var;
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

	public String getJNLPURL(String declare, TumorType tumor) {
		return MessageFormat.format(CALEYDO_JNLP_GENERATOR_URL, declare, tumor);
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
	public Collection<TumorType> getTumorTypes() {
		if (this.tumorTypes == null || this.tumorTypes.isEmpty())
			return TumorType.getNormalTumorTypes();
		List<TumorType> types = new ArrayList<>();
		for (String t : tumorTypes) {
			if (t.contains("*"))
				types.addAll(TumorType.byNameMatches(Pattern.compile("\\Q" + t.replace("*", "\\E.*\\Q") + "\\E",
						Pattern.CASE_INSENSITIVE)));
			TumorType type = TumorType.byName(t);
			if (type == null) {
				System.err.println("unknown tumor type: " + t + " creating dummy");
				type = TumorType.createDummy(t);
			}
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

	public FirehoseProvider createFirehoseProvider(TumorType tumor, Date analysisRunIdentifier, Date dataRunIdentifier) {
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

	public boolean isAwgRun() {
		return awgGroup != null;
	}

	public int getBatchSize() {
		return batchSize;
	}

	/**
	 * @return the cluster, see {@link #cluster}
	 */
	public ClusterOptions getCluster() {
		return cluster;
	}

	public URL getDataURL(Date run, TumorType tumor, String tumorSample, String pipelineName, int level)
			throws MalformedURLException {
		String file = MessageFormat.format(dataFilePattern, run, tumor, tumorSample, pipelineName, level);
		String url = MessageFormat.format(dataPattern, run, tumor, tumorSample, file);
		return new URL(url);
	}

	public URL getAnalysisURL(Date run, TumorType tumor, String tumorSample, String pipelineName, int level)
			throws MalformedURLException {
		String file = MessageFormat.format(filePattern, run, tumor, tumorSample, pipelineName, level);
		String url = MessageFormat.format(analysisPattern, run, tumor, tumorSample, file);
		return new URL(url);
	}

	public String getReportUrl(Date run, TumorType tumor) {
		return MessageFormat.format(reportPattern, run, tumor);
	}

	/**
	 * @return the gson, see {@link #gson}
	 */
	public Gson getGson() {
		return gson;
	}
}
