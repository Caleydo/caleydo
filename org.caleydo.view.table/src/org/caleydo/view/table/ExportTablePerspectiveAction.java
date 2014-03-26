/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.table;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.collection.table.TableUtils;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.gui.util.WithinSWTThread;
import org.caleydo.core.id.IDType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * action for exporting a table perspective
 *
 * @author Samuel Gratzl
 *
 */
public class ExportTablePerspectiveAction implements Runnable {
	private final TablePerspective tablePerspective;
	private final EDimension limitToIdentifiersOf; // null = both, otherwise just a single dimension
	private final EDimension exportGroupingsOf;

	public ExportTablePerspectiveAction(TablePerspective tablePerspective, EDimension limitToIdentifiersOf,
			EDimension exportGroupingsOf) {
		this.tablePerspective = tablePerspective;
		this.limitToIdentifiersOf = limitToIdentifiersOf;
		this.exportGroupingsOf = exportGroupingsOf;
	}

	@Override
	@WithinSWTThread
	public void run() {
		String fileName = selectFile();

		if (fileName == null)
			return;

		final ATableBasedDataDomain dataDomain = tablePerspective.getDataDomain();

		Perspective r = tablePerspective.getRecordPerspective();
		Perspective d = tablePerspective.getDimensionPerspective();
		if (limitToIdentifiersOf == EDimension.RECORD)
			d = createDummy(d.getIdType(), dataDomain);
		else if (limitToIdentifiersOf == EDimension.DIMENSION)
			r = createDummy(r.getIdType(), dataDomain);
		boolean exportRecordGrouping = exportGroupingsOf != EDimension.DIMENSION && hasGrouping(r);
		boolean exportDimensionGrouping = exportGroupingsOf != EDimension.RECORD && hasGrouping(d);

		TableUtils.export(dataDomain, fileName, r, d, null, null, exportRecordGrouping, exportDimensionGrouping);
	}

	/**
	 * @param r
	 * @return
	 */
	private static boolean hasGrouping(Perspective r) {
		GroupList g = r.getVirtualArray().getGroupList();
		return g != null && g.size() > 1;
	}

	public static boolean hasGrouping(TablePerspective p, EDimension dim) {
		return hasGrouping(dim.select(p.getDimensionPerspective(), p.getRecordPerspective()));
	}

	private String selectFile() {
		FileDialog fileDialog = new FileDialog(new Shell(), SWT.SAVE);
		fileDialog.setText("Save");
		String[] filterExt = { "*.csv", "*.txt", "*.*" };
		fileDialog.setFilterExtensions(filterExt);

		fileDialog.setFileName("caleydo_export_" + new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date()) + ".csv");
		String fileName = fileDialog.open();
		return fileName;
	}

	/**
	 * @param idType
	 * @param dataDomain
	 * @return
	 */
	private static Perspective createDummy(IDType idType, ATableBasedDataDomain dataDomain) {
		Perspective p = new Perspective(dataDomain, idType);
		p.init(null);
		return p;
	}
}
