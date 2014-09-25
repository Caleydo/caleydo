/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.io.gui.dataimport.widget;

import org.caleydo.core.io.gui.dataimport.wizard.DataImportWizard;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

/**
 * Widget for the transposition of a dataset.
 *
 * @author Christian Partl
 *
 */
public class DataTranspositionWidget {

	/**
	 * Button to determine whether the dataset should be transposed.
	 */
	protected Button swapRowsWithColumnsButton;

	/**
	 * Label with a warning icon.
	 */
	protected Label warningIconLabel1;

	/**
	 * Label with a warning description.
	 */
	protected Label warningDescriptionLabel1;

	/**
	 * Label with a warning icon.
	 */
	protected Label warningIconLabel2;

	/**
	 * Label with a warning description.
	 */
	protected Label warningDescriptionLabel2;

	/**
	 * Group that contains widgets associated with data transposition.
	 */
	protected Group dataTranspositionGroup;

	protected Composite parentComposite;

	protected DataImportWizard wizard;

	public DataTranspositionWidget(Composite parent, DataImportWizard wizard, boolean transposeByDefault) {
		this.parentComposite = parent;
		this.wizard = wizard;
		dataTranspositionGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		dataTranspositionGroup.setText("Data Transposition");
		dataTranspositionGroup.setLayout(new GridLayout(2, false));
		dataTranspositionGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		Label transpositionExplanationLabel = new Label(dataTranspositionGroup, SWT.WRAP);
		transpositionExplanationLabel
				.setText("Caleydo assumes a limited number of dimensions and a lot of records.  You typically want to observe a variation in records over dimensions, where dimensions would be, for example points in time, and records expression values of genes. Dimensions do not necessarely map to columns in a source file and equally  records must not be the rows in your file. If you select this option you choose to show the rows in the file as dimensions and the columns in the file as records.");
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gridData.widthHint = 600;
		transpositionExplanationLabel.setLayoutData(gridData);
		swapRowsWithColumnsButton = new Button(dataTranspositionGroup, SWT.CHECK);
		swapRowsWithColumnsButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		swapRowsWithColumnsButton.setText("Swap Rows and Columns");
		swapRowsWithColumnsButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				update();
			}
		});
		swapRowsWithColumnsButton.setSelection(transposeByDefault);
	}

	protected Label createWarningIconLabel(Composite parent) {
		Label warningLabel = new Label(parent, SWT.NONE);
		warningLabel.setImage(JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_WARNING));
		return warningLabel;
	}

	protected Label createWarningDescriptionLabel(Composite parent, String text) {
		Label warningLabel = new Label(parent, SWT.WRAP);
		warningLabel.setText(text);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.widthHint = 200;
		warningLabel.setLayoutData(gridData);
		return warningLabel;
	}

	public void update() {
		int numRows = swapRowsWithColumnsButton.getSelection() ? wizard.getSelectedColumns().size() + 1 : wizard
				.getFilteredDataMatrix().size() + 1;
		int numColumns = swapRowsWithColumnsButton.getSelection() ? wizard.getFilteredDataMatrix().size() + 1 : wizard
				.getSelectedColumns().size() + 1;

		if (warningIconLabel1 != null && !warningIconLabel1.isDisposed()) {
			warningIconLabel1.dispose();
			warningDescriptionLabel1.dispose();
		}

		if (warningIconLabel2 != null && !warningIconLabel2.isDisposed()) {
			warningIconLabel2.dispose();
			warningDescriptionLabel2.dispose();
		}

		parentComposite.layout(true);

		if (numColumns > 50) {
			String warningText1 = "Attention: the large number of columns (" + numColumns
					+ ") may lead to an impaired visualization quality in some views";

			if (warningIconLabel1 == null || warningIconLabel1.isDisposed()) {
				warningIconLabel1 = createWarningIconLabel(dataTranspositionGroup);
				warningDescriptionLabel1 = createWarningDescriptionLabel(dataTranspositionGroup, warningText1);
			}
		}

		if (numRows > 50 && numColumns > 50) {
			if (warningIconLabel2 == null || warningIconLabel2.isDisposed()) {
				warningIconLabel2 = createWarningIconLabel(dataTranspositionGroup);
				warningDescriptionLabel2 = createWarningDescriptionLabel(
						dataTranspositionGroup,
						"Attention: In your dataset the choice of dimensions is not obvious. Please choose whether you want to keep the columns in the file as dimensions (do not check) or whether you want to use the rows as dimensions.");
			}
		}

		// page.parentComposite.pack(true);
		parentComposite.layout(true);
	}

	public boolean isTransposition() {
		return swapRowsWithColumnsButton.getSelection();
	}

	public void setTranspose(boolean transpose) {
		swapRowsWithColumnsButton.setSelection(transpose);
		update();
	}
}
