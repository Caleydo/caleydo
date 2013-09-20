/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.data.importer.tcga.regular;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RecursiveAction;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.data.importer.tcga.EDataSetType;
import org.caleydo.data.importer.tcga.Settings;
import org.caleydo.data.importer.tcga.model.TumorType;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class TCGARunTask extends RecursiveAction {
	private static final long serialVersionUID = 1903427073511950319L;

	private final Date analysisRun;
	private final Date dataRun;
	private TCGASettings settings;

	public TCGARunTask(Date analysisRun, Date dataRun, TCGASettings settings) {
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
			generateJSONReport(b, analysisRun, dataRun, settings.getDataDirectory(Settings.format(analysisRun)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void generateJSONReport(JsonArray detailedReports, Date analysisRun, Date dataRun,
			String runSpecificOutputPath) throws IOException {

		String reportJSONOutputPath = Settings.format(analysisRun) + ".json";

		JsonArray dataSetColors = new JsonArray();
		for (EDataSetType dataSetType : EDataSetType.values()) {
			JsonObject o = new JsonObject();
			o.addProperty(dataSetType.getName(), "#" + dataSetType.getColor().getHEX());
			dataSetColors.add(o);
		}

		Gson gson = settings.getGson();
		JsonObject report = new JsonObject();
		report.add("analysisRun", gson.toJsonTree(analysisRun));
		report.add("dataRun", gson.toJsonTree(dataRun));
		report.add("details", detailedReports);
		report.addProperty("caleydoVersion", GeneralManager.VERSION.toString());
		report.add("dataSetColors", dataSetColors);

		String r = gson.toJson(report);
		Files.write(r, new File(runSpecificOutputPath, reportJSONOutputPath), Charset.forName("UTF-8"));
	}
}
