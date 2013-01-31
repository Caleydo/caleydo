package org.caleydo.data.importer.tcga.qualitycontrol;

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

public class TCGAQCDataSetTypeTask extends RecursiveAction {
	private static final long serialVersionUID = -5114565876888372304L;

	private final EDataSetType datasetType;
	private final TCGAQCSettings settings;

	public TCGAQCDataSetTypeTask(EDataSetType datasetType, TCGAQCSettings settings) {
		this.datasetType = datasetType;
		this.settings = settings;
	}

	@Override
	public void compute() {
		Collection<TCGAQCTask> tasks = new ArrayList<>();
		for (TumorType tumorType : settings.getTumorTypes()) {
			tasks.add(new TCGAQCTask(tumorType, datasetType, settings));
		}
		invokeAll(tasks); // fork and wait

		JsonArray b = new JsonArray();
		for (TCGAQCTask task : tasks) {
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
			generateJSONReport(b, datasetType, settings.getDataDirectory(datasetType.name()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void generateJSONReport(JsonArray detailedReports, EDataSetType dataSetType,
			String dataSetTypeSpecificOutputPath) throws IOException {
		JsonObject report = new JsonObject();
		report.addProperty("analysisRun", dataSetType.getName());
		report.add("details", detailedReports);
		report.addProperty("caleydoVersion", GeneralManager.VERSION);

		String r = settings.getGson().toJson(report);
		Files.write(r, new File(dataSetTypeSpecificOutputPath, dataSetType + ".json"), Charset.defaultCharset());
	}
}
