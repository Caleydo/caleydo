package org.caleydo.data.importer.tcga.regular;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RecursiveAction;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.data.importer.tcga.EDataSetType;
import org.caleydo.data.importer.tcga.model.TumorType;

import com.google.common.io.Files;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class TCGARunTask extends RecursiveAction {
	private static final long serialVersionUID = 1903427073511950319L;

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
		for (TumorType tumorType : settings.getTumorTypes()) {
			tasks.add(new TCGATask(tumorType, analysisRun, dataRun, settings));
		}
		invokeAll(tasks);

		JsonArray b = new JsonArray();
		for (TCGATask task : tasks) {
			try {
				JsonElement t = task.get();
				if (t == null)
					continue;
				b.add(t);
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			generateJSONReport(b, analysisRun, dataRun, settings.getDataDirectory(analysisRun));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void generateJSONReport(JsonArray detailedReports, String analysisRun, String dataRun,
			String runSpecificOutputPath) throws IOException {

		String reportJSONOutputPath = analysisRun + ".json";

		JsonArray dataSetColors = new JsonArray();
		for (EDataSetType dataSetType : EDataSetType.values()) {
			JsonObject o = new JsonObject();
			o.addProperty(dataSetType.getName(), "#" + dataSetType.getColor().getHEX());
			dataSetColors.add(o);
		}

		JsonObject report = new JsonObject();
		report.addProperty("analysisRun", analysisRun);
		report.addProperty("dataRun", dataRun);
		report.add("details", detailedReports);
		report.addProperty("caleydoVersion", GeneralManager.VERSION);
		report.add("dataSetColors", dataSetColors);

		String r = settings.getGson().toJson(report);
		Files.write(r, new File(runSpecificOutputPath, reportJSONOutputPath), Charset.defaultCharset());
	}
}
