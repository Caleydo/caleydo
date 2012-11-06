package org.caleydo.data.importer.tcga.provider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.zip.GZIPInputStream;

import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;
import org.caleydo.data.importer.tcga.Settings;

public abstract class AFirehoseProvider {
	protected static String FIREHOSE_URL_PREFIX = "http://gdac.broadinstitute.org/runs/";
	protected static String FIREHOSE_TAR_NAME_PREFIX = "gdac.broadinstitute.org_";

	protected final String tumorAbbreviation;

	protected final String analysisRunId;
	protected final String dataRunId;

	protected final File tmpAnalysisDir;
	protected final File tmpDataDir;
	protected final Settings settings;

	public AFirehoseProvider(String tumorAbbreviation, String analysisRunId, String dataRunId, Settings settings) {
		this.tumorAbbreviation = tumorAbbreviation;
		this.analysisRunId = analysisRunId;
		this.dataRunId = dataRunId;
		this.settings = settings;
		String tmpDir = settings.getTemporaryDirectory();
		this.tmpAnalysisDir = createTempDirectory(tmpDir, analysisRunId, tumorAbbreviation);
		this.tmpDataDir = createTempDirectory(tmpDir, dataRunId, tumorAbbreviation);
	}

	private File createTempDirectory(String tmpOutputDirectory, String runId, String tumor) {
		if (runId == null)
			runId = "unknown";
		return new File(tmpOutputDirectory + clean(runId) + System.getProperty("file.separator") + tumorAbbreviation + System.getProperty("file.separator"));
	}

	protected final String getArchiveName(String pipelineName, int level, boolean analysisRun) {
		String id = analysisRun ? analysisRunId : dataRunId;
		return FIREHOSE_TAR_NAME_PREFIX + tumorAbbreviation + "." + pipelineName + ".Level_" + level + "." + clean(id) + "00.0.0.tar.gz";
	}


	protected static String clean(String id) {
		return id.replace("_", "");
	}

	public abstract File extractAnalysisRunFile(String fileName, String pipelineName, int level);

	public abstract File extractDataRunFile(String fileName, String pipelineName, int level);

	public static String getReportUrl(String analysisRun, String tumortype) {
		return FIREHOSE_URL_PREFIX + "analyses__" + analysisRun + "/reports/cancer/" + tumortype + "/";
	}

	protected File extractFileFromTarGzArchive(URL in, String fileToExtract, File outputDirectory, String archiveName) {
		File targetFile = new File(outputDirectory, fileToExtract);

		// use cached
		if (targetFile.exists() && !settings.isCleanCache())
			return targetFile;

		TarInputStream tarIn = null;
		FileOutputStream out = null;
		try {
			tarIn = new TarInputStream(new GZIPInputStream(in.openStream()));

			// search the correct entry
			TarEntry act = tarIn.getNextEntry();
			while (act != null && !act.getName().endsWith(fileToExtract)) {
				act = tarIn.getNextEntry();
			}
			if (act == null) // no entry found
				return null;

			byte[] buf = new byte[4096];
			int n;
			String tmpFile = targetFile.getAbsolutePath() + ".tmp";
			out = new FileOutputStream(tmpFile);
			while ((n = tarIn.read(buf, 0, 4096)) > -1)
				out.write(buf, 0, n);
			out.close();
			Files.move(new File(tmpFile).toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			return targetFile;
		} catch (Exception e) {
			System.err.println("Unable to extract " + fileToExtract + " from " + archiveName + ". " + e.getMessage());
			return null;
		} finally {
			if (tarIn != null)
				try {
					tarIn.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (out != null)
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

}
