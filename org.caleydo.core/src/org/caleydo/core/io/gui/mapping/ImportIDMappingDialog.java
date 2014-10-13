/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.io.gui.mapping;

import org.caleydo.core.gui.util.AHelpButtonDialog;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.core.io.gui.dataimport.widget.DelimiterWidget;
import org.caleydo.core.io.gui.dataimport.widget.LoadFileWidget;
import org.caleydo.core.util.base.ICallback;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

/**
 * @author Christian
 *
 */
public class ImportIDMappingDialog extends AHelpButtonDialog {

	private Composite parentComposite;
	private LoadFileWidget loadFile;
	private Combo idCategoryCombo;
	private DelimiterWidget delimiters;
	private Label mappingTypeLabel;
	private Label mappingTypeExplanation;
	private Group idCategoryGroup;
	private Label mappingExplanation;
	private Button newIDCategoryButton;
	private Group numHeaderRowsGroup;
	private Spinner numHeaderRowsSpinner;

	private Group[] identifierGroups = new Group[2];
	private Label[] identifierLabels = new Label[2];
	private Combo[] identifierCombos = new Combo[2];
	private Button[] newIdentifierButtons = new Button[2];
	private Button[] defineParsingButtons = new Button[2];
	private Label[] columnLabels = new Label[2];
	private Spinner[] columnSpinners = new Spinner[2];

	private IDCategory selectedIDCategory;

	/**
	 * @param shell
	 */
	public ImportIDMappingDialog(Shell shell) {
		super(shell);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Import Mapping File");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		int numGridCols = 2;

		parentComposite = new Composite(parent, SWT.BORDER);
		GridLayout layout = new GridLayout(numGridCols, false);
		parentComposite.setLayout(layout);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		// gd.widthHint = 900;
		// gd.heightHint = 670;
		parentComposite.setLayoutData(gd);

		// mappingExplanation = new Label(parentComposite, SWT.WRAP);
		// mappingExplanation
		// .setText("Load mapping files to declare relationships between data with different identifiers.");
		// gd = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
		// gd.widthHint = 400;
		// mappingExplanation.setLayoutData(gd);

		loadFile = new LoadFileWidget(parentComposite, "Open Mapping File", new ICallback<String>() {
			@Override
			public void on(String data) {
				onSelectFile(data);
			}
		}, new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

		idCategoryGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		idCategoryGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
		idCategoryGroup.setLayout(new GridLayout(3, false));
		idCategoryGroup.setText("Mapping Type");
		idCategoryGroup.setEnabled(false);

		mappingTypeExplanation = new Label(idCategoryGroup, SWT.WRAP);
		mappingTypeExplanation.setText("Specify a mapping type that both identifiers are related to.");
		gd = new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1);
		gd.widthHint = 400;
		mappingTypeExplanation.setLayoutData(gd);
		mappingTypeExplanation.setEnabled(false);

		mappingTypeLabel = new Label(idCategoryGroup, SWT.WRAP);
		mappingTypeLabel.setText("Mapping Type");
		mappingTypeLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		mappingTypeLabel.setEnabled(false);

		idCategoryCombo = new Combo(idCategoryGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		idCategoryCombo.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		idCategoryCombo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				selectedIDCategory = IDCategory.getIDCategory(idCategoryCombo.getItem(idCategoryCombo
						.getSelectionIndex()));
				updateIdentifierCombo(identifierCombos[0]);
				updateIdentifierCombo(identifierCombos[1]);

				enableIdentifierWidgets(true);
			}
		});
		idCategoryCombo.setEnabled(false);
		updateIDCategoryCombo();

		newIDCategoryButton = new Button(idCategoryGroup, SWT.PUSH);
		newIDCategoryButton.setText("New");
		newIDCategoryButton.setEnabled(false);
		newIDCategoryButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});

		createIdentifierGroup(parentComposite, 0);
		createIdentifierGroup(parentComposite, 1);

		delimiters = new DelimiterWidget(parentComposite, new ICallback<String>() {
			@Override
			public void on(String data) {

			}
		});
		delimiters.setEnabled(false);

		numHeaderRowsGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		numHeaderRowsGroup.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
		numHeaderRowsGroup.setLayout(new GridLayout(1, false));
		numHeaderRowsGroup.setText("Number of Header Rows");
		numHeaderRowsGroup.setEnabled(false);

		numHeaderRowsSpinner = createSpinner(numHeaderRowsGroup, new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				// TODO Auto-generated method stub

			}
		});

		//
		// previewTable = new PreviewTable(parentComposite, this.spec, new IPreviewCallback() {
		// @Override
		// public void on(int numColumn, int numRow, List<? extends List<String>> dataMatrix) {
		// onPreviewChanged(numColumn, numRow, dataMatrix);
		// }
		// }, true);

		return parent;
	}

	private void updateIDCategoryCombo() {

		String selectedCategory = "";
		if (idCategoryCombo.getSelectionIndex() != -1)
			selectedCategory = idCategoryCombo.getItem(idCategoryCombo.getSelectionIndex());

		for (IDCategory idCategory : IDCategory.getAllRegisteredIDCategories()) {
			if (!idCategory.isInternaltCategory())
				idCategoryCombo.add(idCategory.getCategoryName());
		}

		if (idCategoryCombo.indexOf(selectedCategory) != -1) {
			idCategoryCombo.select(idCategoryCombo.indexOf(selectedCategory));
		}
	}

	private void updateIdentifierCombo(Combo combo) {
		if (selectedIDCategory == null)
			return;

		String selectedID = "";
		if (combo.getSelectionIndex() != -1)
			selectedID = combo.getItem(combo.getSelectionIndex());
		combo.clearSelection();
		for (IDType idType : selectedIDCategory.getIdTypes()) {
			if (!idType.isInternalType())
				combo.add(idType.getTypeName());
		}

		if (combo.indexOf(selectedID) != -1) {
			combo.select(combo.indexOf(selectedID));
		}
	}

	private void createIdentifierGroup(Composite parentComposite, int index) {
		identifierGroups[index] = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		identifierGroups[index].setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		identifierGroups[index].setLayout(new GridLayout(4, false));
		identifierGroups[index].setText((index == 0 ? "First" : "Second") + " Identifier");
		identifierGroups[index].setEnabled(false);

		identifierLabels[index] = new Label(identifierGroups[index], SWT.NONE);
		identifierLabels[index].setText("Identifier");
		identifierLabels[index].setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		identifierLabels[index].setEnabled(false);

		identifierCombos[index] = new Combo(identifierGroups[index], SWT.DROP_DOWN | SWT.READ_ONLY);
		identifierCombos[index].setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		identifierCombos[index].setEnabled(false);

		newIdentifierButtons[index] = new Button(identifierGroups[index], SWT.PUSH);
		newIdentifierButtons[index].setText("New");
		newIdentifierButtons[index].setEnabled(false);
		newIdentifierButtons[index].addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});

		defineParsingButtons[index] = new Button(identifierGroups[index], SWT.PUSH);
		defineParsingButtons[index].setText("Define Parsing");
		defineParsingButtons[index].setEnabled(false);
		defineParsingButtons[index].addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});

		columnLabels[index] = new Label(identifierGroups[index], SWT.NONE);
		columnLabels[index].setText("Column with IDs");
		columnLabels[index].setEnabled(false);
		columnLabels[index].setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));

		columnSpinners[index] = createSpinner(identifierGroups[index], new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				// TODO Auto-generated method stub

			}
		});

	}

	private Spinner createSpinner(Composite parentComposite, ModifyListener listener) {
		Spinner spinner = new Spinner(parentComposite, SWT.BORDER);
		spinner.setMinimum(1);
		spinner.setMaximum(Integer.MAX_VALUE);
		spinner.setIncrement(1);
		GridData gridData = new GridData(SWT.LEFT, SWT.FILL, false, false);
		gridData.widthHint = 70;
		spinner.setLayoutData(gridData);
		spinner.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {

			}
		});
		spinner.setEnabled(false);
		return spinner;
	}

	private void enableIdentifierWidgets(boolean enabled) {
		for (int i = 0; i < identifierCombos.length; i++) {
			identifierGroups[i].setEnabled(true);
			identifierCombos[i].setEnabled(true);
			identifierLabels[i].setEnabled(true);
			newIdentifierButtons[i].setEnabled(true);
			defineParsingButtons[i].setEnabled(true);
		}
	}

	private void onSelectFile(String fileName) {
		if (fileName != null && !fileName.isEmpty()) {
			idCategoryGroup.setEnabled(true);
			idCategoryCombo.setEnabled(true);
			mappingTypeExplanation.setEnabled(true);
			mappingTypeLabel.setEnabled(true);
			for (int i = 0; i < identifierGroups.length; i++) {
				identifierGroups[i].setEnabled(true);
				columnLabels[i].setEnabled(true);
				columnSpinners[i].setEnabled(true);
				delimiters.setEnabled(true);
				numHeaderRowsGroup.setEnabled(true);
				numHeaderRowsSpinner.setEnabled(true);
			}
		}
	}

	@Override
	protected void helpPressed() {
		// TODO Auto-generated method stub

	}

}
