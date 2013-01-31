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
package org.caleydo.data.importer.tcga.regular;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

import org.caleydo.data.importer.tcga.EDataSetType;
import org.caleydo.data.importer.tcga.FirehoseProvider;
import org.caleydo.data.importer.tcga.TCGADataSetBuilder;
import org.caleydo.data.importer.tcga.model.TCGADataSet;
import org.caleydo.data.importer.tcga.model.TCGADataSets;
import org.caleydo.data.importer.tcga.model.TumorType;


/**
 * Generator class that writes the loading information of a series of TCGA data sets to an XML file.
 *
 * @author Nils Gehlenborg
 * @author Alexander Lex
 * @author Marc Streit
 */
public class TCGADataSetGenerator extends RecursiveTask<TCGADataSets> {
	private static final long serialVersionUID = 7866075803605970224L;

	private final TCGASettings settings;
	private final FirehoseProvider fileProvider;

	private final TumorType tumorAbbreviation;

	public TCGADataSetGenerator(TumorType tumorAbbreviation, FirehoseProvider fileProvider, TCGASettings settings) {
		this.tumorAbbreviation = tumorAbbreviation;
		this.fileProvider = fileProvider;
		this.settings = settings;
	}

	@Override
	protected TCGADataSets compute() {
		Collection<ForkJoinTask<TCGADataSet>> tasks = new ArrayList<>();

		for (EDataSetType type : EDataSetType.values()) {
			tasks.add(TCGADataSetBuilder.create(tumorAbbreviation, type, fileProvider, settings.isSampleGenes(),
					settings));
		}

		invokeAll(tasks); // fork and wait

		TCGADataSets result = new TCGADataSets(tumorAbbreviation.getLabel());
		for (ForkJoinTask<TCGADataSet> task : tasks) {
			try {
				TCGADataSet ds = task.get();
				if (ds == null)
					continue;
				result.add(ds);
			} catch (InterruptedException e) {
				System.err.println(e.getMessage());
			} catch (ExecutionException e) {
				System.err.println(e.getMessage());
			}
		}

		return result;
	}
}
