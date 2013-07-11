/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.io.gui.dataimport;

import java.util.List;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.io.GroupingParseSpecification;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Christian
 *
 */
public class DataImportStatusDialogs {

	public static DataImportStatusDialog createDatasetImportStatusDialog(Shell shell, ATableBasedDataDomain dataDomain) {
		DataImportStatusDialog dialog = new DataImportStatusDialog(shell, "Dataset Loading Finished", dataDomain
				.getDataSetDescription().getDataSourcePath());

		int numColumns = dataDomain.getTable().getColumnIDList().size();
		int numRecords = dataDomain.getTable().getRowIDList().size();

		dialog.addAttribute("Number of " + dataDomain.getDimensionDenomination(true, true) + " loaded :", ""
				+ (dataDomain.isColumnDimension() ? numColumns : numRecords));
		dialog.addAttribute("Number of " + dataDomain.getRecordDenomination(true, true) + " loaded:",
				"" + (dataDomain.isColumnDimension() ? numRecords : numColumns));
		return dialog;
	}

	public static DataImportStatusDialog createGroupingImportStatusDialog(Shell shell, GroupingParseSpecification spec,
			List<Perspective> persectives, ATableBasedDataDomain dataDomain, boolean isDimensionGrouping,
			int numRowsInFile) {
		DataImportStatusDialog dialog = new DataImportStatusDialog(shell, "Grouping Loading Finished",
				spec.getDataSourcePath());
		String idCategoryName = isDimensionGrouping ? dataDomain.getDimensionDenomination(true, true) : dataDomain
				.getRecordDenomination(true, true);

		dialog.addAttribute("Number of groupings loaded :", "" + persectives.size());
		dialog.addAttribute("Number of " + idCategoryName + " detected :",
				"" + (numRowsInFile - spec.getNumberOfHeaderLines()));
		dialog.addAttribute("Number of " + idCategoryName + " matched :", ""
				+ persectives.get(0).getVirtualArray().size());
		return dialog;
	}
}
