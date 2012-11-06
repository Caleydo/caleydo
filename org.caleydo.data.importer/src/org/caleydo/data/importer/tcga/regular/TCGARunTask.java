package org.caleydo.data.importer.tcga.regular;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RecursiveAction;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.data.importer.tcga.EDataSetType;
import org.caleydo.data.importer.tcga.utils.IOUtils;

public class TCGARunTask extends RecursiveAction {

	private final String analysisRun;
	private final String dataRun;
	private TCGASettings settings;

	public TCGARunTask(String analysisRun, String dataRun, TCGASettings settings) {
		this.analysisRun = analysisRun;
		this.dataRun = dataRun;
		this.settings = settings;
	}

	@Override
	protected void compute() {
		Collection<TCGATask> tasks = new ArrayList<>();
		for (String tumorType : settings.getTumorTypes()) {
			tasks.add(new TCGATask(tumorType, analysisRun, dataRun, settings));
		}
		invokeAll(tasks);

		StringBuilder b = new StringBuilder();
		for (TCGATask task : tasks) {
			try {
				b.append(task.get()).append("\n,");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (b.length() != 0) // remove last ,
			b.setLength(b.length()-1);
		generateJSONReport(b, analysisRun, dataRun, settings.getDataDirectory(analysisRun));
	}

	protected void generateJSONReport(StringBuilder report, String analysisRun, String dataRun,
			String runSpecificOutputPath) {

		String reportJSONOutputPath = analysisRun + ".json";

		String dataSetColors = "";
		for (EDataSetType dataSetType : EDataSetType.values()) {
			dataSetColors += "{\"" + dataSetType.getName() + "\":\"#" + dataSetType.getColor().getHEX() + "\"}, ";
		}
		dataSetColors = dataSetColors.substring(0, dataSetColors.length() - 2);

		String reportJSONGenomicData = report.toString().replace("\"null\"", "null");

		reportJSONGenomicData = "{\"analysisRun\":\"" + analysisRun + "\",\"dataRun\":\"" + dataRun
				+ "\",\"details\":[" + reportJSONGenomicData + "],\"caleydoVersion\":\"" + GeneralManager.VERSION
				+ "\", \"dataSetColors\":[" + dataSetColors + "]}\n";

		IOUtils.dumpToFile(reportJSONGenomicData, new File(runSpecificOutputPath, reportJSONOutputPath));
	}
}
