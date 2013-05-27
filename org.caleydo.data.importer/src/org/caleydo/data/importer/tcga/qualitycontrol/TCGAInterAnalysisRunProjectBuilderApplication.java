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
package org.caleydo.data.importer.tcga.qualitycontrol;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinTask;

import org.caleydo.data.importer.tcga.AProjectBuilderApplication;
import org.caleydo.data.importer.tcga.EDataSetType;
import org.eclipse.equinox.app.IApplication;

public class TCGAInterAnalysisRunProjectBuilderApplication extends AProjectBuilderApplication<TCGAQCSettings> implements
		IApplication {

	@Override
	protected TCGAQCSettings createSettings() {
		return new TCGAQCSettings();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.caleydo.data.importer.tcga.AProjectBuilderApplication#createTasks(org.caleydo.data.importer.tcga.Settings)
	 */
	@Override
	protected List<ForkJoinTask<Void>> createTasks(TCGAQCSettings settings) {
		List<ForkJoinTask<Void>> tasks = new ArrayList<>();
		for (EDataSetType dataSetType : EDataSetType.values()) {
			tasks.add(new TCGAQCDataSetTypeTask(dataSetType, settings));
		}
		return tasks;
	}

	@Override
	public void stop() {
		// nothing to do
	}
}
