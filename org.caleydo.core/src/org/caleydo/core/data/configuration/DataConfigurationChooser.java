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
package org.caleydo.core.data.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.variable.AVariablePerspective;
import org.caleydo.core.data.perspective.variable.DimensionPerspective;
import org.caleydo.core.data.perspective.variable.RecordPerspective;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

/**
 * Helper class that chooses the data configuration based on parameters where possible, or opens a dialog when it's
 * unclear.
 *
 * @author Alexander Lex
 */
public class DataConfigurationChooser {

	/**
	 * <p>
	 * Chooses a {@link IDataDomain}, a {@link RecordPerspective} and a {@link DimensionPerspective} based on the list
	 * of dataDomains specified. If multiple matches are available a dialog to make the user choose is opened.
	 * </p>
	 *
	 * @param a
	 *            list of possible data domains
	 * @return a {@link DataConfiguration} object with all the data set.
	 */

	public static DataConfiguration determineDataConfiguration(List<ATableBasedDataDomain> possibleDataDomains,
			String viewName, boolean letUserChoose) {

		ATableBasedDataDomain chosenDataDomain = null;
		String recordPerspectiveID = null;
		RecordPerspective recordPerspective = null;
		String dimensionPerspectiveID = null;
		DimensionPerspective dimensionPerspective = null;

		if (possibleDataDomains == null || possibleDataDomains.size() == 0)
			throw new IllegalStateException("No datadomain for this view available");

		if (possibleDataDomains.size() == 1) {
			chosenDataDomain = possibleDataDomains.get(0);
			ATableBasedDataDomain tDataDomain = chosenDataDomain;
			Collection<String> recordPerspectiveCandidates = DataConfigurationChooser.getRecordPerspectiveCandidates(
					tDataDomain.getTable(), false);
			if (recordPerspectiveCandidates.size() == 1) {
				recordPerspectiveID = recordPerspectiveCandidates.iterator().next();
				recordPerspective = tDataDomain.getTable().getRecordPerspective(recordPerspectiveID);
			}
			Collection<String> dimensionPerspectiveCandidates = DataConfigurationChooser
					.getDimensionPerspectiveCandidates(tDataDomain.getTable(), false);
			if (dimensionPerspectiveCandidates.size() == 1) {
				dimensionPerspectiveID = dimensionPerspectiveCandidates.iterator().next();
				dimensionPerspective = tDataDomain.getTable().getDimensionPerspective(dimensionPerspectiveID);
			}
		}
		if ((chosenDataDomain == null || recordPerspectiveID == null || dimensionPerspectiveID == null)
				&& letUserChoose) {

			Shell shell = new Shell(SWT.APPLICATION_MODAL);
			ChooseDataConfigurationDialog dialog = new ChooseDataConfigurationDialog(shell, "Choose Data for "
					+ viewName);
			dialog.setSupportedDataDomains(possibleDataDomains);
			// dialog.setPossibleDataDomains(availableDomains);
			dialog.setBlockOnOpen(true);
			dialog.open();
			DataConfiguration config = dialog.getDataConfiguration();
			return config;
		} else {
			DataConfiguration config = new DataConfiguration();
			config.setDataDomain(chosenDataDomain);
			config.setDimensionPerspective(dimensionPerspective);
			config.setRecordPerspective(recordPerspective);
			return config;
		}
	}

	// ----------------- Private Implemetations ---------------------------

	/**
	 * <p>
	 * Checks if there is only one {@link RecordPerspective} from those registered with the {@link DataTable} a possible
	 * match considering the parameters. If so, this one match is returned, else null is returned.
	 * </p>
	 * <p>
	 * As {@link AVariablePerspective}s can be either private or public (see
	 * {@link AVariablePerspective#setIsPrivate(boolean)}, it is possible to let the chooser only chose from those that
	 * are public.
	 * </p>
	 *
	 * @param table
	 *            the data table from which to choose the available perspectives
	 * @param considerOnlyPublic
	 *            whether only public perspectives shall be used (true) or whether to also include private perspectives
	 * @return the chosen perspectiveID if a unique ID could be identified, or null
	 */

	private static Collection<String> getRecordPerspectiveCandidates(DataTable table, boolean considerOnlyPublic) {
		return getPerspectiveCandidates(table, considerOnlyPublic, true);
	}

	/**
	 * Same as {@link #getRecordPerspectiveCandidates(DataTable, boolean)} for dimensions.
	 *
	 * @param table
	 * @param considerOnlyPublic
	 * @return
	 */
	private static Collection<String> getDimensionPerspectiveCandidates(DataTable table, boolean considerOnlyPublic) {
		return getPerspectiveCandidates(table, considerOnlyPublic, false);
	}

	/**
	 * Generic method for {@link #getRecordPerspectiveCandidates(DataTable, boolean)} and
	 * {@link #getDimensionPerspectiveCandidates(DataTable, boolean)}
	 *
	 * @param table
	 * @param considerOnlyPublic
	 * @param isRecord
	 * @return
	 */
	private static ArrayList<String> getPerspectiveCandidates(DataTable table, boolean considerOnlyPublic,
			boolean isRecord) {
		Set<String> dataPerspectiveIDs;
		if (isRecord)
			dataPerspectiveIDs = table.getRecordPerspectiveIDs();
		else
			dataPerspectiveIDs = table.getDimensionPerspectiveIDs();

		ArrayList<String> candidates = new ArrayList<String>(dataPerspectiveIDs);

		if (!considerOnlyPublic)
			return candidates;
		else {
			Iterator<String> iterator = candidates.iterator();
			while (iterator.hasNext()) {
				String tempPerspectiveID = iterator.next();
				AVariablePerspective<?, ?, ?, ?> perspective;
				if (isRecord)
					perspective = table.getRecordPerspective(tempPerspectiveID);
				else
					perspective = table.getDimensionPerspective(tempPerspectiveID);

				if (!perspective.isPrivate()) {
					iterator.remove();
				}
			}
			return candidates;
		}
	}
}
