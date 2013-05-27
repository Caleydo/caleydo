/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.startup.internal;

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
import org.caleydo.core.startup.IStartupProcedure;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class LoadProjectStartupProcedure implements IStartupProcedure {
	private final String projectLocation;
	private final boolean isDirectory;

	public LoadProjectStartupProcedure(String project, boolean isDirectory) {
		this.projectLocation = project;
		this.isDirectory = isDirectory;
	}

	@Override
	public void preWorkbenchOpen() {
		if (isDirectory)
			ProjectManager.loadWorkbenchData(this.projectLocation);
		else {
			ProjectManager.loadProjectFromZIP(this.projectLocation);
			ProjectManager.loadWorkbenchData(ProjectManager.TEMP_PROJECT_ZIP_FOLDER);
		}
		// FIXME
		// ApplicationWorkbenchWindowAdvisor.setWindowTitle("Caleydo - "
		// + this.projectLocation.substring(this.projectLocation.lastIndexOf("/") + 1));
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
	public boolean run() {
		SerializationData serializationDataList;

		// not calling super.init() on purpose

		Logger.log(new Status(IStatus.INFO, this.toString(), "Load serialized project"));

		if (isDirectory)
			serializationDataList = ProjectManager.loadProjectData(ProjectManager.RECENT_PROJECT_FOLDER);
		else {
			serializationDataList = ProjectManager.loadProjectData(ProjectManager.TEMP_PROJECT_ZIP_FOLDER);
		}

		deserializeData(serializationDataList);
		return true;
	}
}
