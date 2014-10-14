/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.io.gui.mapping;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.caleydo.core.gui.util.AHelpButtonDialog;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingDescription;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDType;
import org.caleydo.core.io.IDTypeParsingRules;
import org.caleydo.core.io.gui.dataimport.CreateIDTypeDialog;
import org.caleydo.core.io.gui.dataimport.DefineIDParsingDialog;
import org.caleydo.core.io.gui.dataimport.FilePreviewParser;
import org.caleydo.core.io.gui.dataimport.widget.DelimiterWidget;
import org.caleydo.core.io.gui.dataimport.widget.LoadFileWidget;
import org.caleydo.core.io.gui.dataimport.widget.table.PreviewTableWidget;
import org.caleydo.core.io.gui.dataimport.widget.table.PreviewTableWidget.RowColDesc;
import org.caleydo.core.util.base.ICallback;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
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

import com.google.common.collect.Lists;

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
	private PreviewTableWidget previewTable;
	private FilePreviewParser parser = new FilePreviewParser();
	private String fileName;

	private Group[] identifierGroups = new Group[2];
	private Label[] identifierLabels = new Label[2];
	private Combo[] identifierCombos = new Combo[2];
	private Button[] newIdentifierButtons = new Button[2];
	private Button[] defineParsingButtons = new Button[2];
	private Label[] columnLabels = new Label[2];
	private Spinner[] columnSpinners = new Spinner[2];

	private IDTypeParsingRules[] parsingRules = new IDTypeParsingRules[2];

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
		gd.heightHint = 670;
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
				updateButtons();
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
				if (idCategoryCombo.getSelectionIndex() == -1)
					return;
				selectedIDCategory = IDCategory.getIDCategory(idCategoryCombo.getItem(idCategoryCombo
						.getSelectionIndex()));
				updateIdentifierCombo(identifierCombos[0]);
				updateIdentifierCombo(identifierCombos[1]);

				enableIdentifierWidgets(true);
				updateButtons();
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
				String sample = previewTable.getValue(numHeaderRowsSpinner.getSelection(),
						columnSpinners[0].getSelection() - 1);

				CreateIDTypeDialog dialog = new CreateIDTypeDialog(new Shell(), sample);
				int status = dialog.open();

				if (status == Window.OK) {
					IDCategory newIDCategory = dialog.getIdCategory();
					updateIDCategoryCombo();
					idCategoryCombo.select(idCategoryCombo.indexOf(newIDCategory.getCategoryName()));
				}
			}
		});

		createIdentifierGroup(parentComposite, 0);
		createIdentifierGroup(parentComposite, 1);

		delimiters = new DelimiterWidget(parentComposite, new ICallback<String>() {
			@Override
			public void on(String data) {
				updatePreviewTableContent(fileName);
				updateButtons();
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
				updateTable(false);
				updateButtons();
			}
		});
		numHeaderRowsSpinner.setMinimum(0);
		numHeaderRowsSpinner.setSelection(0);

		previewTable = new PreviewTableWidget(parentComposite, false, null, false, null);
		previewTable.setHeaderRowsInFront(true);

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

		idCategoryCombo.clearSelection();
		idCategoryCombo.removeAll();
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
		combo.removeAll();
		for (IDType idType : selectedIDCategory.getIdTypes()) {
			if (!idType.isInternalType())
				combo.add(idType.getTypeName());
		}

		if (combo.indexOf(selectedID) != -1) {
			combo.select(combo.indexOf(selectedID));
		}
	}

	private void createIdentifierGroup(Composite parentComposite, final int index) {
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
		identifierCombos[index].addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				updateTable(true);
				updateButtons();
			}
		});

		newIdentifierButtons[index] = new Button(identifierGroups[index], SWT.PUSH);
		newIdentifierButtons[index].setText("New");
		newIdentifierButtons[index].setEnabled(false);
		newIdentifierButtons[index].addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String sample = previewTable.getValue(numHeaderRowsSpinner.getSelection(),
						columnSpinners[index].getSelection() - 1);

				CreateIDTypeDialog dialog = new CreateIDTypeDialog(new Shell(), IDCategory
						.getIDCategory(idCategoryCombo.getText()), sample);
				int status = dialog.open();

				if (status == Window.OK) {
					IDType newIDType = dialog.getIdType();
					parsingRules[index] = dialog.getIdTypeParsingRules();
					updateIdentifierCombo(identifierCombos[index]);
					identifierCombos[index].select(identifierCombos[index].indexOf(newIDType.getTypeName()));
				}

			}
		});

		defineParsingButtons[index] = new Button(identifierGroups[index], SWT.PUSH);
		defineParsingButtons[index].setText("Define Parsing");
		defineParsingButtons[index].setEnabled(false);
		defineParsingButtons[index].addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String sample = previewTable.getValue(numHeaderRowsSpinner.getSelection(),
						columnSpinners[index].getSelection() - 1);
				DefineIDParsingDialog dialog = new DefineIDParsingDialog(new Shell(), parsingRules[index], sample);
				int status = dialog.open();

				if (status == Window.OK) {
					parsingRules[index] = dialog.getIdTypeParsingRules();
					updateTable(true);
				}
			}
		});

		columnLabels[index] = new Label(identifierGroups[index], SWT.NONE);
		columnLabels[index].setText("Column with IDs");
		columnLabels[index].setEnabled(false);
		columnLabels[index].setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));

		columnSpinners[index] = createSpinner(identifierGroups[index], new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				updateTable(false);
				updateButtons();
			}
		});
		columnSpinners[index].setSelection(index + 1);
		CLabel columnColor = new CLabel(identifierGroups[index], SWT.BORDER);
		GridData gridData = new GridData(SWT.LEFT, SWT.TOP, false, false);
		gridData.widthHint = 25;
		gridData.heightHint = 25;
		columnColor.setLayoutData(gridData);
		columnColor.setBackground(GUIHelper.getColor(index == 0 ? Color.GREEN.asRGB() : Color.CYAN.asRGB()));
		columnColor.update();

	}

	private Spinner createSpinner(Composite parentComposite, ModifyListener listener) {
		Spinner spinner = new Spinner(parentComposite, SWT.BORDER);
		spinner.setMinimum(1);
		spinner.setMaximum(Integer.MAX_VALUE);
		spinner.setIncrement(1);
		GridData gridData = new GridData(SWT.LEFT, SWT.FILL, false, false);
		gridData.widthHint = 70;
		spinner.setLayoutData(gridData);
		spinner.addModifyListener(listener);
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
			this.fileName = fileName;
			idCategoryGroup.setEnabled(true);
			idCategoryCombo.setEnabled(true);
			mappingTypeExplanation.setEnabled(true);
			mappingTypeLabel.setEnabled(true);
			newIDCategoryButton.setEnabled(true);
			for (int i = 0; i < identifierGroups.length; i++) {
				identifierGroups[i].setEnabled(true);
				columnLabels[i].setEnabled(true);
				columnSpinners[i].setEnabled(true);
				delimiters.setEnabled(true);
				numHeaderRowsGroup.setEnabled(true);
				numHeaderRowsSpinner.setEnabled(true);
			}

			updatePreviewTableContent(fileName);
		}
	}

	private void updatePreviewTableContent(String fileName) {
		if (fileName == null || fileName.isEmpty())
			return;
		parser.parseWithProgress(getShell(), fileName, delimiters.getDelimeter(), true, -1);
		previewTable.createTableFromMatrix(parser.getDataMatrix(), parser.getTotalNumberOfColumns());
		updateWidgetsToLoadedFile(parser.getTotalNumberOfRows(), parser.getTotalNumberOfColumns());
		updateTable(true);
	}

	private void updateTable(boolean reconfigureTable) {
		if (previewTable == null)
			return;
		previewTable.updateTable(Integer.parseInt(numHeaderRowsSpinner.getText()),
				new ArrayList<PreviewTableWidget.RowColDesc>(),
				Lists.newArrayList(new RowColDesc(Integer.parseInt(columnSpinners[0].getText()) - 1, Color.GREEN,
						parsingRules[0]), new RowColDesc(Integer.parseInt(columnSpinners[1].getText()) - 1, Color.CYAN,
						parsingRules[1])));
		if (reconfigureTable)
			previewTable.reconfigure();
	}

	private void updateWidgetsToLoadedFile(int numRows, int numColumns) {
		numHeaderRowsSpinner.setMaximum(numRows);
		columnSpinners[0].setMaximum(numColumns);
		columnSpinners[1].setMaximum(numColumns);
	}

	@Override
	protected void helpPressed() {
		// TODO Auto-generated method stub

	}

	@Override
	protected Control createButtonBar(Composite parent) {
		Control c = super.createButtonBar(parent);
		getButton(Window.OK).setEnabled(false);
		return c;
	}

	protected boolean isDataValid() {
		return loadFile.getFileName() != null && !loadFile.getFileName().isEmpty()
				&& idCategoryCombo.getSelectionIndex() != -1 && identifierCombos[0].getSelectionIndex() != -1
				&& identifierCombos[1].getSelectionIndex() != -1
				&& columnSpinners[0].getSelection() != columnSpinners[1].getSelection()
				&& delimiters.getDelimeter() != null && !delimiters.getDelimeter().isEmpty();
	}

	@Override
	protected void okPressed() {

		if (isDataValid()) {

			final IDMappingDescription desc = new IDMappingDescription();
			desc.setParsingStartLine(numHeaderRowsSpinner.getSelection());
			desc.setParsingStopLine(-1);
			desc.setFileName(loadFile.getFileName());
			desc.setDelimiter(delimiters.getDelimeter());
			desc.setMultiMapping(true);
			desc.setCreateReverseMapping(true);
			desc.setResolveCodeMappingUsingCodeToId_LUT(false);

			desc.setIdCategory(idCategoryCombo.getText());

			IDType fromIDType = IDType.getIDType(identifierCombos[0].getText());
			desc.setFromIDType(fromIDType.getTypeName());
			desc.setFromDataType(fromIDType.getDataType());

			IDType toIDType = IDType.getIDType(identifierCombos[1].getText());
			desc.setToIDType(toIDType.getTypeName());
			desc.setToDataType(toIDType.getDataType());

			ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell());

			try {

				dialog.run(true, false, new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						desc.addMapping();
						IDMappingManager.addIDMappingDescription(desc);
					}
				});
			} catch (Exception e) {
				Logger.log(new Status(IStatus.ERROR, this.toString(), "Dataset loading failed: " + e.getMessage()));
			}

			super.okPressed();
		}
	}

	// @Override
	// public void handleEvent(Event event) {
	// updateButtons();
	// }

	private void updateButtons() {
		if (getButton(Window.OK) != null)
			getButton(Window.OK).setEnabled(isDataValid());
	}
}
