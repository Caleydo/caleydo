package org.caleydo.data.importer.tcga.provider;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.caleydo.data.importer.tcga.Settings;

public class RemoteFirehoseProvider extends AFirehoseProvider {

	private final String analysisBaseUrl;
	private final String dataBaseUrl;

	public RemoteFirehoseProvider(String tumorAbbreviation, String analysisRunId, String dataRunId, Settings settings) {
		super(tumorAbbreviation, analysisRunId, dataRunId, settings);
		// create path of archive search directory
		this.analysisBaseUrl = FIREHOSE_URL_PREFIX + "analyses__" + analysisRunId + "/data/" + tumorAbbreviation + "/" + clean(analysisRunId) + "/";
		this.dataBaseUrl = FIREHOSE_URL_PREFIX + "stddata__" + dataRunId + "/data/" + tumorAbbreviation + "/" + clean(dataRunId) + "/";
	}

	@Override
	public File extractAnalysisRunFile(String fileName, String pipelineName, int level) {
		return extractFile(fileName, pipelineName, level, true);
	}

	@Override
	public File extractDataRunFile(String fileName, String pipelineName, int level) {
		return extractFile(fileName, pipelineName, level, false);
	}

	protected File extractFile(String fileName, String pipelineName, int level, boolean analysisRun) {
		String archiveName = getArchiveName(pipelineName, level, analysisRun);

		File outputDir = new File(analysisRun ? tmpAnalysisDir : tmpDataDir, archiveName);
		outputDir.mkdirs();

		// extract file to temp directory and return path to file
		String baseUrl = analysisRun ? analysisBaseUrl : dataBaseUrl;
		URL url;
		try {
			url = new URL(baseUrl + archiveName);
			return extractFileFromTarGzArchive(url, fileName, outputDir, archiveName);
		} catch (MalformedURLException e) {
			throw new RuntimeException("can't extract " + fileName + " from " + archiveName, e);
		}
	}
}
