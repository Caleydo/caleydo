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
package org.caleydo.core.startup;

import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.collection.table.TableUtils;
import org.caleydo.core.data.datadomain.ADataDomain;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.DimensionPerspective;
import org.caleydo.core.data.perspective.variable.RecordPerspective;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.DataDomainSerializationData;
import org.caleydo.core.serialize.ISerializationAddon;
import org.caleydo.core.serialize.ProjectManager;
import org.caleydo.core.serialize.SerializationData;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class SerializationStartupProcedure
	extends AStartupProcedure {

	public static final String SAMPLE_PROJECT_LOCATION = "data/sample_project/sample_project.cal";

	private String projectLocation = null;

	private boolean loadSampleProject = false;
	private boolean loadRecentProject = false;

	private SerializationData serializationDataList;

	@Override
	public void initPreWorkbenchOpen() {
		super.initPreWorkbenchOpen();

		if (this.loadSampleProject) {
			this.projectLocation = SAMPLE_PROJECT_LOCATION;
			ProjectManager.loadProjectFromZIP(this.projectLocation);
			ProjectManager.loadWorkbenchData(ProjectManager.TEMP_PROJECT_ZIP_FOLDER);
		}
		else if (this.loadRecentProject) {
			this.projectLocation = ProjectManager.RECENT_PROJECT_FOLDER;
			ProjectManager.loadWorkbenchData(this.projectLocation);
		}
		else if (this.projectLocation != null && !this.projectLocation.isEmpty()) {
			ProjectManager.loadProjectFromZIP(this.projectLocation);
			ProjectManager.loadWorkbenchData(ProjectManager.TEMP_PROJECT_ZIP_FOLDER);
		}
		else {
			throw new IllegalArgumentException("encountered unknown project-load-type");
		}

		ApplicationWorkbenchWindowAdvisor.setWindowTitle("Caleydo - "
				+ this.projectLocation.substring(this.projectLocation.lastIndexOf("/") + 1));
	}

	@Override
	public void postWorkbenchOpen() {
	}

	@Override
	public void init() {

		// not calling super.init() on purpose

		Logger.log(new Status(IStatus.INFO, this.toString(), "Load serialized project"));

		if (this.loadSampleProject) {
			this.serializationDataList = ProjectManager.loadProjectData(ProjectManager.TEMP_PROJECT_ZIP_FOLDER);
		}
		else {
			if (this.loadRecentProject) {
				this.serializationDataList = ProjectManager.loadProjectData(ProjectManager.RECENT_PROJECT_FOLDER);
			}
			else if (this.projectLocation != null || this.projectLocation.isEmpty()) {
				this.serializationDataList = ProjectManager.loadProjectData(ProjectManager.TEMP_PROJECT_ZIP_FOLDER);
			}
			else {
				throw new IllegalArgumentException("encoutnered unknown project-load-type");
			}
		}

		this.deserializeData(this.serializationDataList);
	}

	private void deserializeData(SerializationData serializationDataList) {

		for (DataDomainSerializationData dataSerializationData : serializationDataList
				.getDataDomainSerializationDataList()) {
			ADataDomain dataDomain = dataSerializationData.getDataDomain();

			if (dataDomain instanceof ATableBasedDataDomain) {
				ATableBasedDataDomain tDataDomain = (ATableBasedDataDomain) dataDomain;

				DataSetDescription dataSetDescription = dataDomain.getDataSetDescription();

				TableUtils.loadData(tDataDomain, dataSetDescription, false, false);
				Table table = tDataDomain.getTable();
				for (RecordPerspective perspective : dataSerializationData
						.getRecordPerspectiveMap().values()) {
					table.registerRecordPerspective(perspective);
				}
				for (DimensionPerspective perspective : dataSerializationData
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

	public void loadSampleProject(boolean loadSampleProject) {
		this.loadSampleProject = loadSampleProject;
	}

	public void setProjectLocation(String projectLocation) {
		this.projectLocation = projectLocation;
	}

	public void setLoadRecentProject(boolean loadRecentProject) {
		this.loadRecentProject = loadRecentProject;
	}
}
