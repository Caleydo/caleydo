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
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RecursiveAction;
import java.util.logging.Level;
import java.util.logging.Logger;

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
	private static final Logger log = Logger.getLogger(TCGARunTask.class.getName());
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
		List<TCGATask> tasks = new ArrayList<>();
		List<TumorType> tumorTypes = new ArrayList<>(settings.getTumorTypes());
		String id = Settings.format(analysisRun);
		log.info(id + " start tumor types: " + tumorTypes);
		for (TumorType tumorType : tumorTypes) {
			tasks.add(new TCGATask(tumorType, analysisRun, dataRun, settings));
		}
		invokeAll(tasks);

		log.fine(id + " created");
		JsonArray b = new JsonArray();
		int i = -1;
		for (TCGATask task : tasks) {
			i++;
			try {
				JsonElement t = task.get();
				if (t == null) {
					log.warning(id + " " + tumorTypes.get(i) + " delivered no result");
					continue;
				}
				b.add(t);
			} catch (InterruptedException | ExecutionException e) {
				log.log(Level.SEVERE, id + " " + tumorTypes.get(i) + " execution error: " + e.getMessage(), e);
			}
		}
		try {
			generateJSONReport(b, analysisRun, dataRun, settings.getDataDirectory(id), tumorTypes);
		} catch (IOException e) {
			log.log(Level.SEVERE, id + " can't generate json report: " + e.getMessage(), e);
		}
		log.info(id + " done (" + b.size() + " tumor types)");
	}

	protected void generateJSONReport(JsonArray detailedReports, Date analysisRun, Date dataRun,
			String runSpecificOutputPath, List<TumorType> tumorTypes) throws IOException {

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
		for (TumorType t : tumorTypes) {
			Files.write(r, new File(runSpecificOutputPath, t.getName() + '_' + reportJSONOutputPath),
					Charset.forName("UTF-8"));
		}
	}
}
