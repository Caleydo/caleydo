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
package org.caleydo.core.io.gui.dataimport;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.gui.util.FontUtil;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Christian
 *
 */
public class DatasetImportStatusDialog extends Dialog {

	private ATableBasedDataDomain dataDomain;

	/**
	 * @param parentShell
	 */
	public DatasetImportStatusDialog(Shell parentShell, ATableBasedDataDomain dataDomain) {
		super(parentShell);
		this.dataDomain = dataDomain;
	}

	@Override
	protected void setShellStyle(int newShellStyle) {
		super.setShellStyle(SWT.TITLE);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Dataset Loading Finished");
	}

	@Override
	protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
		if (id == IDialogConstants.CANCEL_ID)
			return null;
		return super.createButton(parent, id, label, defaultButton);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		int numColumns = dataDomain.getTable().getColumnIDList().size();
		int numRecords = dataDomain.getTable().getRowIDList().size();

		Composite parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayout(new GridLayout(2, false));
		parentComposite.setLayoutData(new GridData(400, 200));
		Label statusLabel = new Label(parentComposite, SWT.NONE | SWT.WRAP);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
		gd.widthHint = 400;
		statusLabel.setLayoutData(gd);
		statusLabel.setText("The file " + dataDomain.getDataSetDescription().getDataSourcePath()
				+ " was imported successfully!");
		Label numDimensionsLabel = new Label(parentComposite, SWT.NONE);
		numDimensionsLabel.setText("Number of " + dataDomain.getDimensionDenomination(true, true) + " loaded :");
		FontUtil.makeBold(numDimensionsLabel);

		Label dimensionsLabel = new Label(parentComposite, SWT.NONE);
		dimensionsLabel.setText("" + (dataDomain.isColumnDimension() ? numColumns : numRecords));

		Label numRecordsLabel = new Label(parentComposite, SWT.NONE);
		numRecordsLabel.setText("Number of " + dataDomain.getRecordDenomination(true, true) + " loaded:");
		FontUtil.makeBold(numRecordsLabel);

		Label recordsLabel = new Label(parentComposite, SWT.NONE);
		recordsLabel.setText("" + (dataDomain.isColumnDimension() ? numRecords : numColumns));
		return super.createDialogArea(parent);
	}

	@Override
	protected void okPressed() {
		// TODO Auto-generated method stub
		super.okPressed();
	}
}
