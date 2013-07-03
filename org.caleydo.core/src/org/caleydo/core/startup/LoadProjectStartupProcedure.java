/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.startup;

import static org.caleydo.core.manager.GeneralManager.CALEYDO_HOME_PATH;

import java.io.File;

import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.collection.table.TableUtils;
import org.caleydo.core.data.datadomain.ADataDomain;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.DataDomainSerializationData;
import org.caleydo.core.serialize.ISerializationAddon;
import org.caleydo.core.serialize.ProjectManager;
import org.caleydo.core.serialize.SerializationData;
import org.caleydo.core.serialize.ZipUtils;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.util.system.FileOperations;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;

import com.google.common.base.Function;

public class LoadProjectStartupProcedure implements IStartupProcedure {
	/**
	 * full path to directory to temporarily store the projects file before zipping
	 */
	public static final String TEMP_PROJECT_ZIP_FOLDER = CALEYDO_HOME_PATH + "temp_load" + File.separator;

	private final String packedProjectLocation;
	private final String unpackedProjectLocation;

	public LoadProjectStartupProcedure(String project, boolean isAlreadyUnpacked) {
		if (isAlreadyUnpacked) {
			this.packedProjectLocation = null;
			this.unpackedProjectLocation = project;
		} else {
			this.packedProjectLocation = project;
			this.unpackedProjectLocation = TEMP_PROJECT_ZIP_FOLDER;
		}
	}

	@Override
	public boolean preWorkbenchOpen() {
		if (this.packedProjectLocation != null) {
			// unzip data
			FileOperations.deleteDirectory(this.unpackedProjectLocation);
			ZipUtils.unzipToDirectory(this.packedProjectLocation, this.unpackedProjectLocation);
		}

		if (!ProjectManager.checkCompatibility(this.unpackedProjectLocation)){
			Logger.create(LoadProjectStartupProcedure.class).error(
					"incompatible project: " + this.packedProjectLocation);
			MessageDialog.openError(null, "Error incompatible project", "the project file:\n"+this.packedProjectLocation+"\n\nis not compatible with the this Caleydo version");
			return false;
		}

		ProjectManager.loadWorkbenchData(this.unpackedProjectLocation);
		return true;
	}

	@Override
	public void postWorkbenchOpen() {
	}

	private static void deserializeData(SerializationData serializationDataList) {

		for (DataDomainSerializationData dataSerializationData : serializationDataList
				.getDataDomainSerializationDataList()) {
			ADataDomain dataDomain = dataSerializationData.getDataDomain();

			if (dataDomain instanceof ATableBasedDataDomain) {
				ATableBasedDataDomain tDataDomain = (ATableBasedDataDomain) dataDomain;

				DataSetDescription dataSetDescription = dataDomain.getDataSetDescription();

				TableUtils.loadData(tDataDomain, dataSetDescription, false, false);
				Table table = tDataDomain.getTable();
				for (Perspective perspective : dataSerializationData
						.getRecordPerspectiveMap().values()) {
					table.registerRecordPerspective(perspective);
				}
				for (Perspective perspective : dataSerializationData
						.getDimensionPerspectiveMap().values()) {
					table.registerDimensionPerspective(perspective);
				}
				for (TablePerspective container : tDataDomain.getTablePerspectives().values()) {
					container.postDesirialize();
				}
			}

		}

		for (ISerializationAddon addon : GeneralManager.get().getSerializationManager().getAddons())
			addon.load(serializationDataList);

	}

	@Override
	public boolean run(Function<String, Void> setTitle) {
		SerializationData serializationDataList;

		// not calling super.init() on purpose

		Logger.log(new Status(IStatus.INFO, this.toString(), "Load serialized project"));

		serializationDataList = ProjectManager.loadProjectData(unpackedProjectLocation);

		if (packedProjectLocation != null) {
			String normalized = this.packedProjectLocation.replace(File.separatorChar, '/');
			setTitle.apply("Caleydo - " + normalized.substring(normalized.lastIndexOf('/') + 1));
		}

		deserializeData(serializationDataList);
		return true;
	}
}
