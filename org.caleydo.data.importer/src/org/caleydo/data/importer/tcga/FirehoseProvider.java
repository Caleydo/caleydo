package org.caleydo.data.importer.tcga;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.zip.GZIPInputStream;

import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;

public final class FirehoseProvider {
	protected static String FIREHOSE_TAR_NAME_PREFIX = "gdac.broadinstitute.org_";

	protected final String tumorAbbreviation;

	protected final String analysisRunId;
	protected final String dataRunId;

	protected final File tmpAnalysisDir;
	protected final File tmpDataDir;
	protected final Settings settings;

	FirehoseProvider(String tumorAbbreviation, String analysisRunId, String dataRunId, Settings settings) {
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
		return new File(tmpOutputDirectory + Settings.clean(runId) + System.getProperty("file.separator")
				+ tumorAbbreviation + System.getProperty("file.separator"));
	}

	public File extractAnalysisRunFile(String fileName, String pipelineName, int level) {
		return extractFile(fileName, pipelineName, level, true);
	}

	public File extractDataRunFile(String fileName, String pipelineName, int level) {
		return extractFile(fileName, pipelineName, level, false);
	}

	protected final String getArchiveName(String pipelineName, int level, boolean analysisRun) {
		// up to now the real name, to use the existing cache
		String id = analysisRun ? analysisRunId : dataRunId;
		return FIREHOSE_TAR_NAME_PREFIX + tumorAbbreviation + "." + pipelineName + ".Level_" + level + "."
				+ Settings.clean(id)
				+ "00.0.0.tar.gz";
	}

	protected File extractFile(String fileName, String pipelineName, int level, boolean analysisRun) {
		String id = analysisRun ? analysisRunId : dataRunId;
		String label = getArchiveName(pipelineName, level, analysisRun);

		File outputDir = new File(analysisRun ? tmpAnalysisDir : tmpDataDir, label);
		outputDir.mkdirs();

		// extract file to temp directory and return path to file
		URL url;
		try {
			if (analysisRun)
				url = settings.getAnalysisURL(id, tumorAbbreviation, pipelineName, level);
			else
				url = settings.getDataURL(id, tumorAbbreviation, pipelineName, level);
			return extractFileFromTarGzArchive(url, fileName, outputDir);
		} catch (MalformedURLException e) {
			throw new RuntimeException("can't extract " + fileName + " from " + label, e);
		}
	}

	protected File extractFileFromTarGzArchive(URL inUrl, String fileToExtract, File outputDirectory) {
		File targetFile = new File(outputDirectory, fileToExtract);

		// use cached
		if (targetFile.exists() && !settings.isCleanCache())
			return targetFile;

		File notFound = new File(outputDirectory, fileToExtract + "-notfound");
		if (notFound.exists() && !settings.isCleanCache()) {
			System.err.println("Unable to extract " + fileToExtract + " from " + inUrl + ". "
					+ "file not found in a previous run");
			return null;
		}

		TarInputStream tarIn = null;
		FileOutputStream out = null;
		try {
			InputStream in = inUrl.openStream();

			// ok we have the file
			tarIn = new TarInputStream(new GZIPInputStream(in));

			// search the correct entry
			TarEntry act = tarIn.getNextEntry();
			while (act != null && !act.getName().endsWith(fileToExtract)) {
				act = tarIn.getNextEntry();
			}
			if (act == null) // no entry found
				return null;

			byte[] buf = new byte[4096];
			int n;
			targetFile.getParentFile().mkdirs();
			// use a temporary file to recognize if we have aborted between run
			String tmpFile = targetFile.getAbsolutePath() + ".tmp";
			out = new FileOutputStream(tmpFile);
			while ((n = tarIn.read(buf, 0, 4096)) > -1)
				out.write(buf, 0, n);
			out.close();
			Files.move(new File(tmpFile).toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			return targetFile;
		} catch (FileNotFoundException e) {
			System.err
.println("Unable to extract " + fileToExtract + " from " + inUrl + ". " + "file not found");
			// file was not found, create a marker to remember this for quicker checks
			notFound.getParentFile().mkdirs();
			try {
				notFound.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return null;
		} catch (Exception e) {
			System.err.println("Unable to extract " + fileToExtract + " from " + inUrl + ". " + e.getMessage());
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
