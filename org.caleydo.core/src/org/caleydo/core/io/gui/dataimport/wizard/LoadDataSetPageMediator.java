/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 *
 */
package org.caleydo.core.io.gui.dataimport.wizard;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.core.io.ColumnDescription;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.FileUtil;
import org.caleydo.core.io.GroupingParseSpecification;
import org.caleydo.core.io.IDSpecification;
import org.caleydo.core.io.IDTypeParsingRules;
import org.caleydo.core.io.gui.dataimport.CreateIDTypeDialog;
import org.caleydo.core.io.gui.dataimport.DefineIDParsingDialog;
import org.caleydo.core.io.gui.dataimport.FilePreviewParser;
import org.caleydo.core.io.parser.ascii.ATextParser;
import org.caleydo.core.serialize.SerializationManager;
import org.caleydo.core.util.collection.Pair;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Shell;

/**
 * Mediator for {@link LoadDataSetPage}. This class is responsible for setting the states of all widgets of the page and
 * triggering actions according to different events that occur in the page.
 *
 *
 * @author Christian Partl
 *
 */
public class LoadDataSetPageMediator extends ALoadDataPageMediator {

	protected static final int UNMAPPED_INDEX = 0;

	/**
	 * Maximum number of previewed rows in {@link #previewTable}.
	 */
	protected static final int MAX_PREVIEW_TABLE_ROWS = 50;

	/**
	 * Maximum number of previewed columns in {@link #previewTable}.
	 */
	// protected static final int MAX_PREVIEW_TABLE_COLUMNS = 10;

	/**
	 * The maximum number of ids that are tested in order to determine the {@link IDType}.
	 */
	protected static final int MAX_CONSIDERED_IDS_FOR_ID_TYPE_DETERMINATION = 10;

	/**
	 * Page this class serves as mediator for.
	 */
	private LoadDataSetPage page;

	/**
	 * Parser used to parse data files.
	 */
	private FilePreviewParser parser = new FilePreviewParser();

	/**
	 * Matrix that stores the data for {@link #MAX_PREVIEW_TABLE_ROWS} rows and all columns of the data file.
	 */
	protected List<List<String>> dataMatrix;

	/**
	 * The total number of columns of the input file.
	 */
	protected int totalNumberOfColumns;

	/**
	 * The total number of rows of the input file.
	 */
	protected int totalNumberOfRows;

	/**
	 * The IDTypes available for {@link #rowIDCategory}.
	 */
	protected List<IDType> rowIDTypes = new ArrayList<IDType>();

	/**
	 * The IDTypes available for {@link #columnIDCategory}.
	 */
	protected List<IDType> columnIDTypes = new ArrayList<IDType>();

	/**
	 * The current row id category.
	 */
	protected IDCategory rowIDCategory;

	/**
	 * The current column id category.
	 */
	protected IDCategory columnIDCategory;

	/**
	 * The {@link DataSetDescription} for which data is defined in subclasses.
	 */
	protected DataSetDescription dataSetDescription;

	/**
	 * Parsing rules for the row id.
	 */
	protected IDTypeParsingRules rowIDTypeParsingRules;

	/**
	 * Parsing rules for the column id.
	 */
	protected IDTypeParsingRules columnIDTypeParsingRules;

	/**
	 * Determines whether the file was transposed.
	 */
	protected boolean isTransposed = false;

	/**
	 * The transposed version of the input file.
	 */
	protected File transposedDataFile;

	/**
	 * Determines whether a config file was loaded that shall be initialized from
	 */
	protected boolean initFromConfiguration = false;

	protected boolean datasetChanged = true;

	public LoadDataSetPageMediator(LoadDataSetPage page, DataSetDescription dataSetDescription) {
		this.page = page;
		this.dataSetDescription = dataSetDescription;
		dataSetDescription.setDelimiter("\t");
		dataSetDescription.setNumberOfHeaderLines(1);
		dataSetDescription.setRowOfColumnIDs(0);
		dataSetDescription.setColumnOfRowIds(0);
	}

	/**
	 * Initializes all widgets of the {@link #page}. This method should be called after all widgets of the dialog were
	 * created.
	 */
	@Override
	public void guiCreated() {
		initWidgets();
	}

	public void onSelectFile(String inputFileName) {
		dataSetDescription.setDataSourcePath(inputFileName);
		dataSetDescription.setRowIDSpecification(null);
		dataSetDescription.setColumnIDSpecification(null);
		initWidgets();
		isTransposed = false;
		transposedDataFile = null;
		setDataSetChanged(true);
		initFromConfiguration = false;
	}

	public void onLoadDatasetDescription(String inputFileName) {
		Unmarshaller unmarshaller;
		try {
			
			unmarshaller = SerializationManager.get().getProjectContext().createUnmarshaller();

			File descriptionFile = new File(inputFileName);
			if (descriptionFile.exists()) {
				DataSetDescription desc = (DataSetDescription) unmarshaller.unmarshal(descriptionFile);
				desc.setDataSourcePath(dataSetDescription.getDataSourcePath());
				page.getWizard().setDataSetDescription(desc);
				initFromConfiguration = true;
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param dataSetDescription
	 *            setter, see {@link dataSetDescription}
	 */
	public void setDataSetDescription(DataSetDescription dataSetDescription) {
		this.dataSetDescription = dataSetDescription;
		updateToDatasetDescription();
		setDataSetChanged(true);
	}

	public void transposeFile() {
		isTransposed = !isTransposed;
		if (isTransposed && transposedDataFile == null) {
			loadTransposedFile();
		}
		initWidgets();
		setDataSetChanged(true);
	}

	private void loadTransposedFile() {
		try {
			transposedDataFile = File.createTempFile("tmptransposed", "txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FileUtil.transposeCSV(page.loadFile.getFileName(), transposedDataFile.getAbsolutePath(),
				page.delimiterRadioGroup.getDelimeter());
	}

	private void initWidgets() {

		String inputFileName = dataSetDescription.getDataSourcePath();
		boolean fileSpecified = dataSetDescription.getDataSourcePath() != null;

		if (!isTransposed) {
			page.loadFile.setFileName(fileSpecified ? inputFileName : "");

			page.label.setText(fileSpecified ? inputFileName.substring(inputFileName.lastIndexOf(File.separator) + 1,
					inputFileName.lastIndexOf(".")) : "");
			page.label.setEnabled(fileSpecified);
		}

		fillIDCategoryCombo(page.rowIDCategoryCombo);
		page.rowIDCategoryCombo.setEnabled(fileSpecified);

		fillIDCategoryCombo(page.columnIDCategoryCombo);
		page.columnIDCategoryCombo.setEnabled(fileSpecified);

		fillIDTypeCombo(rowIDCategory, rowIDTypes, page.rowIDCombo);
		page.rowIDCombo.setEnabled(fileSpecified && (page.rowIDCategoryCombo.getSelectionIndex() != -1));

		fillIDTypeCombo(columnIDCategory, columnIDTypes, page.columnIDCombo);
		page.columnIDCombo.setEnabled(fileSpecified && (page.columnIDCategoryCombo.getSelectionIndex() != -1));

		page.homogeneousDatasetButton.setEnabled(fileSpecified);
		page.homogeneousDatasetButton.setSelection(true);
		page.inhomogeneousDatasetButton.setEnabled(fileSpecified);
		page.inhomogeneousDatasetButton.setSelection(false);

		page.loadDatasetDescriptionButton.setEnabled(fileSpecified);

		page.createRowIDCategoryButton.setEnabled(fileSpecified);

		page.createColumnIDCategoryButton.setEnabled(fileSpecified);

		page.createRowIDTypeButton.setEnabled(fileSpecified && (page.rowIDCategoryCombo.getSelectionIndex() != -1));

		page.createColumnIDTypeButton.setEnabled(fileSpecified
				&& (page.columnIDCategoryCombo.getSelectionIndex() != -1));

		page.defineRowIDParsingButton.setEnabled(fileSpecified && (page.rowIDCategoryCombo.getSelectionIndex() != -1));

		page.defineColumnIDParsingButton.setEnabled(fileSpecified
				&& (page.columnIDCategoryCombo.getSelectionIndex() != -1));

		page.numHeaderRowsSpinner.setSelection(1);
		page.numHeaderRowsSpinner.setEnabled(fileSpecified);

		page.rowOfColumnIDSpinner.setSelection(1);
		page.rowOfColumnIDSpinner.setEnabled(fileSpecified);

		page.columnOfRowIDSpinner.setSelection(1);
		page.columnOfRowIDSpinner.setEnabled(fileSpecified);

		// page.buttonHomogeneous.setEnabled(fileSpecified);

		page.delimiterRadioGroup.setEnabled(fileSpecified);
		page.selectAllNone.setEnabled(fileSpecified);

		page.previewTable.setEnabled(fileSpecified);

		if (fileSpecified)
			createDataPreviewTableFromFile(true);
	}

	private void updateToDatasetDescription() {
		String inputFileName = dataSetDescription.getDataSourcePath();
		if (inputFileName == null)
			return;

		page.loadFile.setFileName(inputFileName);

		String datasetName = dataSetDescription.getDataSetName();
		page.label.setText(datasetName == null ? "" : datasetName);
		page.label.setEnabled(true);

		IDSpecification rowIDSpecification = dataSetDescription.getRowIDSpecification();
		if (rowIDSpecification != null) {
			updateToIDSpecification(rowIDSpecification, true);
		}

		IDSpecification columnIDSpecification = dataSetDescription.getColumnIDSpecification();
		if (columnIDSpecification != null) {
			updateToIDSpecification(columnIDSpecification, false);
		}

		boolean isHomogeneous = dataSetDescription.getDataDescription() != null;

		page.createRowIDCategoryButton.setEnabled(true);

		page.createColumnIDCategoryButton.setEnabled(isHomogeneous);

		page.createRowIDTypeButton.setEnabled(true);

		page.createColumnIDTypeButton.setEnabled(isHomogeneous);

		page.defineRowIDParsingButton.setEnabled(true);

		page.defineColumnIDParsingButton.setEnabled(isHomogeneous);

		onHomogeneousDatasetSelected(isHomogeneous);

		page.homogeneousDatasetButton.setEnabled(true);
		page.homogeneousDatasetButton.setSelection(isHomogeneous);
		page.inhomogeneousDatasetButton.setEnabled(true);
		page.inhomogeneousDatasetButton.setSelection(!isHomogeneous);

		Integer numHeaderLines = dataSetDescription.getNumberOfHeaderLines();
		page.numHeaderRowsSpinner.setSelection(numHeaderLines == null ? 1 : numHeaderLines);
		page.numHeaderRowsSpinner.setEnabled(true);

		Integer rowOfColumnID = dataSetDescription.getNumberOfHeaderLines();
		page.rowOfColumnIDSpinner.setSelection(rowOfColumnID == null ? 1 : rowOfColumnID);
		page.rowOfColumnIDSpinner.setEnabled(true);

		Integer columnOfRowID = dataSetDescription.getNumberOfHeaderLines();
		page.columnOfRowIDSpinner.setSelection(columnOfRowID == null ? 1 : columnOfRowID);
		page.columnOfRowIDSpinner.setEnabled(true);

		// page.buttonHomogeneous.setEnabled(fileSpecified);

		page.delimiterRadioGroup.setDelimeter(dataSetDescription.getDelimiter());
		page.delimiterRadioGroup.setEnabled(true);
		page.selectAllNone.setEnabled(true);

		page.previewTable.setEnabled(true);

		createDataPreviewTableFromFile(false);

		List<ColumnDescription> parsingPattern = dataSetDescription.getOrCreateParsingPattern();
		List<Integer> columns = new ArrayList<>(parsingPattern.size() + 1);
		columns.add(page.columnOfRowIDSpinner.getSelection() - 1);
		for (ColumnDescription desc : parsingPattern) {
			columns.add(desc.getColumn());
		}

		page.previewTable.setSelectedColumns(columns);
	}

	private void updateToIDSpecification(IDSpecification spec, boolean isRowIDSpec) {
		IDCategory idCategory = IDCategory.registerCategory(spec.getIdCategory());
		EDataType dataType = spec.getDataType();
		IDType idType = IDType.registerType(spec.getIdType(), idCategory, dataType == null ? EDataType.STRING
				: dataType);
		if (spec.getIdTypeParsingRules() != null)
			idType.setIdTypeParsingRules(spec.getIdTypeParsingRules());
		Combo idCategoryCombo = null;
		Combo idCombo = null;
		List<IDType> idTypes = null;
		boolean enable = true;

		if (isRowIDSpec) {
			idCategoryCombo = page.rowIDCategoryCombo;
			idCombo = page.rowIDCombo;
			idTypes = rowIDTypes;
			rowIDCategory = idCategory;
		} else {
			idCategoryCombo = page.columnIDCategoryCombo;
			idCombo = page.columnIDCombo;
			idTypes = columnIDTypes;
			columnIDCategory = idCategory;
			if (dataSetDescription.getDataDescription() == null)
				enable = false;
		}

		refreshRegisteredCategories();
		fillIDCategoryCombo(idCategoryCombo);

		idCategoryCombo.select(idCategoryCombo.indexOf(idCategory.getCategoryName()));
		idCategoryCombo.setEnabled(enable);

		fillIDTypeCombo(idCategory, idTypes, idCombo);
		idCombo.select(idCombo.indexOf(idType.getTypeName()));
		idCombo.setEnabled(enable);
	}

	private String getRowIDSample() {
		return page.previewTable.getValue(page.numHeaderRowsSpinner.getSelection(),
				page.columnOfRowIDSpinner.getSelection() - 1);
	}

	private String getColumnIDSample() {
		List<Integer> selectedColumns = page.previewTable.getSelectedColumns();
		Integer sampleColumn = 0;
		for (Integer candidate : selectedColumns) {
			if (candidate != page.columnOfRowIDSpinner.getSelection() - 1) {
				sampleColumn = candidate;
				break;
			}

		}

		return page.previewTable.getValue(page.rowOfColumnIDSpinner.getSelection() - 1, sampleColumn);
	}

	public void onHomogeneousDatasetSelected(boolean isHomogeneous) {
		page.columnIDCategoryCombo.setEnabled(isHomogeneous);
		page.columnIDCombo.setEnabled(isHomogeneous);
		page.createColumnIDCategoryButton.setEnabled(isHomogeneous);
		page.createColumnIDTypeButton.setEnabled(isHomogeneous);
		page.defineColumnIDParsingButton.setEnabled(isHomogeneous);
		page.columnIDCategoryLabel.setEnabled(isHomogeneous);
		page.columnIDTypeLabel.setEnabled(isHomogeneous);
		if (!isHomogeneous) {
			setColumnIDTypeParsingRules(null);
			page.columnIDCategoryCombo.clearSelection();
			page.columnIDCombo.clearSelection();
		}
	}

	public void onDefineRowIDParsing() {
		// IDType idType = getRowIDType();
		// IDTypeParsingRules templateIdTypeParsingRules = rowIDTypeParsingRules;
		// if (idType != null && templateIdTypeParsingRules == null) {
		// templateIdTypeParsingRules = idType.getIdTypeParsingRules();
		// }
		DefineIDParsingDialog dialog = new DefineIDParsingDialog(new Shell(), rowIDTypeParsingRules, getRowIDSample());
		int status = dialog.open();

		if (status == Window.OK) {
			setRowIDTypeParsingRules(dialog.getIdTypeParsingRules());
		}
	}

	public void onDefineColumnIDParsing() {
		// IDType idType = getColumnIDType();
		// IDTypeParsingRules templateIdTypeParsingRules = columnIDTypeParsingRules;
		// if (idType != null && templateIdTypeParsingRules == null) {
		// templateIdTypeParsingRules = idType.getIdTypeParsingRules();
		// }
		DefineIDParsingDialog dialog = new DefineIDParsingDialog(new Shell(), columnIDTypeParsingRules,
				getColumnIDSample());
		int status = dialog.open();

		if (status == Window.OK) {
			setColumnIDTypeParsingRules(dialog.getIdTypeParsingRules());
		}
	}

	private IDType getRowIDType() {
		int i = page.rowIDCombo.getSelectionIndex();
		if (i < 0)
			return null;
		String type = page.rowIDCombo.getItem(i);
		return IDType.getIDType(type);
	}

	private IDType getColumnIDType() {
		int i = page.columnIDCombo.getSelectionIndex();
		if (i < 0)
			return null;
		String type = page.columnIDCombo.getItem(i);
		return IDType.getIDType(type);
	}

	public void idTypeComboModified(boolean isColumnIDType) {
		if (isColumnIDType) {
			if (page.columnIDCombo.getSelectionIndex() != -1) {
				page.defineColumnIDParsingButton.setEnabled(true);
				setColumnIDTypeParsingRules(getColumnIDType().getIdTypeParsingRules());
			} else {
				page.defineColumnIDParsingButton.setEnabled(false);
				setColumnIDTypeParsingRules(null);
			}

		} else {
			if (page.rowIDCombo.getSelectionIndex() != -1) {
				page.defineRowIDParsingButton.setEnabled(true);
				setRowIDTypeParsingRules(getRowIDType().getIdTypeParsingRules());
			} else {
				page.defineRowIDParsingButton.setEnabled(false);
				setRowIDTypeParsingRules(null);
			}

		}
	}

	private void setRowIDTypeParsingRules(IDTypeParsingRules rowIDTypeParsingRules) {
		this.rowIDTypeParsingRules = rowIDTypeParsingRules;
		page.previewTable.setRowIDTypeParsingRules(rowIDTypeParsingRules);
	}

	/**
	 * @param columnIDTypeParsingRules
	 *            setter, see {@link columnIDTypeParsingRules}
	 */
	public void setColumnIDTypeParsingRules(IDTypeParsingRules columnIDTypeParsingRules) {
		this.columnIDTypeParsingRules = columnIDTypeParsingRules;
		page.previewTable.setColumnIDTypeParsingRules(columnIDTypeParsingRules);
	}

	/**
	 * Fills the idTypeCombos according to the IDCategory selected by the idCategoryCombo.
	 *
	 * @param isColumnCategory
	 *            Determines whether the column or row combo is affected.
	 */
	public void idCategoryComboModified(boolean isColumnCategory) {

		if (isColumnCategory) {
			if (page.columnIDCategoryCombo.getSelectionIndex() == -1)
				return;
			if (page.columnIDCategoryCombo.getSelectionIndex() == UNMAPPED_INDEX) {
				columnIDCategory = null;
				page.columnIDCombo.clearSelection();
				page.columnIDCombo.removeAll();
				page.columnIDCombo.setText("<Unmapped>");
				page.columnIDCombo.setEnabled(false);
				page.defineColumnIDParsingButton.setEnabled(false);
				setColumnIDTypeParsingRules(null);
				page.createColumnIDTypeButton.setEnabled(false);
				return;
			} else {
				columnIDCategory = IDCategory.getIDCategory(page.columnIDCategoryCombo
						.getItem(page.columnIDCategoryCombo.getSelectionIndex()));
				fillIDTypeCombo(columnIDCategory, columnIDTypes, page.columnIDCombo);
				page.columnIDCombo.setEnabled(true);
				page.createColumnIDTypeButton.setEnabled(true);
			}

			if (dataSetDescription.getColumnGroupingSpecifications() != null) {
				ArrayList<GroupingParseSpecification> columnGroupingSpecifications = new ArrayList<GroupingParseSpecification>(
						dataSetDescription.getColumnGroupingSpecifications());

				boolean groupingParseSpecificationsRemoved = false;
				for (GroupingParseSpecification groupingParseSpecification : columnGroupingSpecifications) {
					String categoryString = groupingParseSpecification.getRowIDSpecification().getIdCategory();
					if (IDCategory.getIDCategory(categoryString) != columnIDCategory) {
						dataSetDescription.getColumnGroupingSpecifications().remove(groupingParseSpecification);
						groupingParseSpecificationsRemoved = true;
					}
				}

				if (groupingParseSpecificationsRemoved) {
					MessageDialog.openInformation(new Shell(), "Grouping Removed",
							"At least one column grouping was removed due to the change of the column type.");
				}
			}
		} else {

			if (page.rowIDCategoryCombo.getSelectionIndex() == -1)
				return;
			if (page.rowIDCategoryCombo.getSelectionIndex() == UNMAPPED_INDEX) {
				rowIDCategory = null;
				page.rowIDCombo.clearSelection();
				page.rowIDCombo.removeAll();
				page.rowIDCombo.setText("<Unmapped>");
				page.rowIDCombo.setEnabled(false);
				page.defineRowIDParsingButton.setEnabled(false);
				setRowIDTypeParsingRules(null);
				page.createRowIDTypeButton.setEnabled(false);
				return;
			} else {
				rowIDCategory = IDCategory.getIDCategory(page.rowIDCategoryCombo.getItem(page.rowIDCategoryCombo
						.getSelectionIndex()));
				fillIDTypeCombo(rowIDCategory, rowIDTypes, page.rowIDCombo);
				page.rowIDCombo.setEnabled(true);
				page.createRowIDTypeButton.setEnabled(true);
			}

			if (dataSetDescription.getRowGroupingSpecifications() != null) {
				ArrayList<GroupingParseSpecification> rowGroupingSpecifications = new ArrayList<GroupingParseSpecification>(
						dataSetDescription.getRowGroupingSpecifications());

				boolean groupingParseSpecificationsRemoved = false;
				for (GroupingParseSpecification groupingParseSpecification : rowGroupingSpecifications) {
					String categoryString = groupingParseSpecification.getRowIDSpecification().getIdCategory();
					if (IDCategory.getIDCategory(categoryString) != rowIDCategory) {
						dataSetDescription.getRowGroupingSpecifications().remove(groupingParseSpecification);
						groupingParseSpecificationsRemoved = true;
					}
				}

				if (groupingParseSpecificationsRemoved) {
					MessageDialog.openInformation(new Shell(), "Grouping Removed",
							"At least one row grouping was removed due to the change of the row type.");
				}
			}
		}
	}

	/**
	 * Updates the preview table according to the number of the spinner.
	 */
	public void numHeaderRowsSpinnerModified() {
		int numHeaderRows = page.numHeaderRowsSpinner.getSelection();
		int idRowIndex = page.rowOfColumnIDSpinner.getSelection();
		if (idRowIndex > numHeaderRows) {
			page.rowOfColumnIDSpinner.setSelection(numHeaderRows);
			dataSetDescription.setRowOfColumnIDs(numHeaderRows - 1);
		}
		dataSetDescription.setNumberOfHeaderLines(numHeaderRows);
		page.previewTable.updateTableColors(dataSetDescription.getNumberOfHeaderLines(),
				dataSetDescription.getRowOfColumnIDs(), dataSetDescription.getColumnOfRowIds());
		setDataSetChanged(true);
	}

	protected void setDataSetChanged(boolean datasetChanged) {
		page.getWizard().getDataSetTypePage().setDatasetChanged(datasetChanged);
		this.datasetChanged = datasetChanged;
	}

	/**
	 * Updates the preview table according to the number of the spinner.
	 */
	public void columnOfRowIDSpinnerModified() {
		dataSetDescription.setColumnOfRowIds(page.columnOfRowIDSpinner.getSelection() - 1);
		page.previewTable.updateTableColors(dataSetDescription.getNumberOfHeaderLines(),
				dataSetDescription.getRowOfColumnIDs(), dataSetDescription.getColumnOfRowIds());
		setDataSetChanged(true);
	}

	/**
	 * Opens a dialog to create a new {@link IDCategory}. The value of rowIDCategoryCombo is set to the newly created
	 * category.
	 */
	public void createRowIDCategoryButtonSelected() {
		createIDCategory(false);
	}

	/**
	 * Opens a dialog to create a new {@link IDCategory}. The value of columnIDCategoryCombo is set to the newly created
	 * category.
	 */
	public void createColumnIDCategoryButtonSelected() {
		createIDCategory(true);
	}

	private void createIDCategory(boolean isColumnCategory) {
		CreateIDTypeDialog dialog = new CreateIDTypeDialog(new Shell(), isColumnCategory ? getColumnIDSample()
				: getRowIDSample());
		int status = dialog.open();

		if (status == Window.OK) {
			IDCategory newIDCategory = dialog.getIdCategory();
			registeredIDCategories.add(newIDCategory);

			// registeredIDCategories = new ArrayList<IDCategory>();
			//
			// for (IDCategory idCategory :
			// IDCategory.getAllRegisteredIDCategories()) {
			// if (!idCategory.isInternaltCategory())
			// registeredIDCategories.add(idCategory);
			// }

			fillIDCategoryCombo(page.rowIDCategoryCombo);
			fillIDCategoryCombo(page.columnIDCategoryCombo);
			if (isColumnCategory) {
				columnIDCategory = newIDCategory;
				page.columnIDCategoryCombo
						.select(page.columnIDCategoryCombo.indexOf(columnIDCategory.getCategoryName()));
				fillIDTypeCombo(columnIDCategory, columnIDTypes, page.columnIDCombo);
				// columnIDTypeParsingRules = dialog.getIdTypeParsingRules();
			} else {
				rowIDCategory = newIDCategory;
				page.rowIDCategoryCombo.select(page.rowIDCategoryCombo.indexOf(rowIDCategory.getCategoryName()));
				fillIDTypeCombo(rowIDCategory, rowIDTypes, page.rowIDCombo);
				// rowIDTypeParsingRules = dialog.getIdTypeParsingRules();
			}
		}
	}

	/**
	 * Opens a dialog to create a new {@link IDType}. The value of rowIDCombo is set to the newly created category.
	 */
	public void createRowIDTypeButtonSelected() {
		createIDType(false);
	}

	/**
	 * Opens a dialog to create a new {@link IDType}. The value of columnIDCombo is set to the newly created category.
	 */
	public void createColumnIDTypeButtonSelected() {
		createIDType(true);
	}

	private void createIDType(boolean isColumnIDType) {

		CreateIDTypeDialog dialog = new CreateIDTypeDialog(new Shell(), isColumnIDType ? columnIDCategory
				: rowIDCategory, isColumnIDType ? getColumnIDSample() : getRowIDSample());
		int status = dialog.open();

		if (status == Window.OK) {

			IDType newIDType = dialog.getIdType();

			fillIDTypeCombo(rowIDCategory, rowIDTypes, page.rowIDCombo);
			fillIDTypeCombo(columnIDCategory, columnIDTypes, page.columnIDCombo);
			if (isColumnIDType) {
				int selectionIndex = page.columnIDCombo.indexOf(newIDType.getTypeName());
				if (selectionIndex != -1) {
					page.columnIDCombo.select(selectionIndex);
				}
				// columnIDTypeParsingRules = dialog.getIdTypeParsingRules();
			} else {
				int selectionIndex = page.rowIDCombo.indexOf(newIDType.getTypeName());
				if (selectionIndex != -1) {
					page.rowIDCombo.select(selectionIndex);
				}
				// rowIDTypeParsingRules = dialog.getIdTypeParsingRules();
			}
		}
	}

	/**
	 * Updates the preview table according to the number of the spinner.
	 */
	public void rowOfColumnIDSpinnerModified() {
		int numHeaderRows = page.numHeaderRowsSpinner.getSelection();
		int idRowIndex = page.rowOfColumnIDSpinner.getSelection();
		dataSetDescription.setRowOfColumnIDs(idRowIndex - 1);
		if (idRowIndex > numHeaderRows) {
			page.numHeaderRowsSpinner.setSelection(idRowIndex);
			dataSetDescription.setNumberOfHeaderLines(idRowIndex);
		}
		page.previewTable.updateTableColors(dataSetDescription.getNumberOfHeaderLines(),
				dataSetDescription.getRowOfColumnIDs(), dataSetDescription.getColumnOfRowIds());
		setDataSetChanged(true);
	}

	public void onDelimiterChanged(String delimiter) {
		dataSetDescription.setDelimiter(delimiter);
		if (isTransposed) {
			loadTransposedFile();
		}
		createDataPreviewTableFromFile(true);

		setDataSetChanged(true);
	}

	public void onSelectAllNone(boolean selectAll) {
		page.previewTable.selectColumns(selectAll, dataSetDescription.getColumnOfRowIds());
		page.getWizard().getDataSetTypePage().setDatasetChanged(true);
		setDataSetChanged(true);
	}

	// public void onShowAllColumns(boolean showAllColumns) {
	// page.previewTable.createDataPreviewTableFromDataMatrix(dataMatrix, totalNumberOfColumns);
	// page.previewTable.updateTableColors(dataSetDescription.getNumberOfHeaderLines(),
	// dataSetDescription.getRowOfColumnIDs(), dataSetDescription.getColumnOfRowIds());
	// updateWidgetsAccordingToTableChanges();
	// }

	public void createDataPreviewTableFromFile(boolean guessIDTypes) {
		parser.parseWithProgress(page.getShell(), isTransposed ? transposedDataFile.getAbsolutePath()
				: dataSetDescription.getDataSourcePath(), dataSetDescription.getDelimiter(), true, -1);
		dataMatrix = parser.getDataMatrix();
		totalNumberOfColumns = parser.getTotalNumberOfColumns();
		totalNumberOfRows = parser.getTotalNumberOfRows();
		page.previewTable.createTableFromMatrix(dataMatrix, totalNumberOfColumns);
		updateWidgetsAccordingToTableChanges();
		if (guessIDTypes) {
			guessNumberOfHeaderRows();
			determineIDTypes();
		}

		page.previewTable.updateTableColors(dataSetDescription.getNumberOfHeaderLines(),
				dataSetDescription.getRowOfColumnIDs(), dataSetDescription.getColumnOfRowIds());
		// page.parentComposite.pack();
		// page.parentComposite.setSize(800, 600);
		// page.parentComposite.setSize(page.parentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		// page.parentComposite.pack(true);
		// page.parentComposite.redraw();

	}

	private void guessNumberOfHeaderRows() {
		// In grouping case we can have 0 header rows as there does not have to
		// be an id row
		if (dataSetDescription.getDataSourcePath().endsWith(".gct") && dataMatrix.size() >= 3) {
			page.numHeaderRowsSpinner.setSelection(3);
			page.rowOfColumnIDSpinner.setSelection(3);
			return;
		}

		int numHeaderRows = 1;
		for (int i = 1; i < dataMatrix.size(); i++) {
			List<String> row = dataMatrix.get(i);
			int numFloatsFound = 0;
			for (int j = 0; j < row.size(); j++) {
				String text = row.get(j);
				try {
					// This currently only works for numerical values
					Float.parseFloat(text);
					numFloatsFound++;
					if (numFloatsFound >= 3) {
						page.numHeaderRowsSpinner.setSelection(numHeaderRows);
						return;
					}
				} catch (Exception e) {

				}
			}
			numHeaderRows++;
		}
	}

	private void determineIDTypes() {

		IDType mostProbableRowIDType = null;
		IDType mostProbableColumnIDType = null;

		IDSpecification rowIDSpec = dataSetDescription.getRowIDSpecification();
		IDSpecification columnIDSpec = dataSetDescription.getColumnIDSpecification();
		if (rowIDSpec != null) {
			mostProbableRowIDType = IDType.getIDType(rowIDSpec.getIdType());
		}
		if (columnIDSpec != null) {
			mostProbableColumnIDType = IDType.getIDType(columnIDSpec.getIdType());
		}

		List<String> rowIDList = new ArrayList<String>();
		for (int i = 0; i < dataMatrix.size() && i < MAX_CONSIDERED_IDS_FOR_ID_TYPE_DETERMINATION; i++) {
			List<String> row = dataMatrix.get(i);
			rowIDList.add(row.get(dataSetDescription.getColumnOfRowIds()));
		}

		List<String> columnIDList = new ArrayList<String>();
		List<String> idRow = dataMatrix.get(dataSetDescription.getRowOfColumnIDs());
		for (int i = 0; i < idRow.size() && i < MAX_CONSIDERED_IDS_FOR_ID_TYPE_DETERMINATION; i++) {
			columnIDList.add(idRow.get(i));
		}

		if (mostProbableRowIDType == null)
			mostProbableRowIDType = determineMostProbableIDType(rowIDList);
		if (mostProbableColumnIDType == null)
			mostProbableColumnIDType = determineMostProbableIDType(columnIDList);

		setMostProbableIDTypes(mostProbableRowIDType, mostProbableColumnIDType);
	}

	protected void setMostProbableIDTypes(IDType mostProbableRowIDType, IDType mostProbableColumnIDType) {

		if (mostProbableRowIDType != null && mostProbableColumnIDType == null
				&& mostProbableRowIDType.getIDCategory() == IDCategory.getIDCategory("GENE")) {
			mostProbableColumnIDType = IDType.getIDType("SAMPLE");
		}

		if (mostProbableColumnIDType != null && mostProbableRowIDType == null
				&& mostProbableColumnIDType.getIDCategory() == IDCategory.getIDCategory("GENE")) {
			mostProbableRowIDType = IDType.getIDType("SAMPLE");
		}

		setMostProbableIDType(mostProbableRowIDType, page.rowIDCategoryCombo, page.rowIDCombo, rowIDTypes, false);
		setMostProbableIDType(mostProbableColumnIDType, page.columnIDCategoryCombo, page.columnIDCombo, columnIDTypes,
				true);
	}

	private void setMostProbableIDType(IDType mostProbableIDType, Combo idCategoryCombo, Combo idTypeCombo,
			List<IDType> idTypes, boolean isColumnIDType) {
		if (mostProbableIDType != null) {
			int index = registeredIDCategories.indexOf(mostProbableIDType.getIDCategory());
			idCategoryCombo.select(index + 1);
			if (isColumnIDType) {
				columnIDCategory = mostProbableIDType.getIDCategory();
				fillIDTypeCombo(columnIDCategory, idTypes, idTypeCombo);
			} else {
				rowIDCategory = mostProbableIDType.getIDCategory();
				fillIDTypeCombo(rowIDCategory, idTypes, idTypeCombo);
			}

			idTypeCombo.select(idTypes.indexOf(mostProbableIDType));
		} else {
			if (isColumnIDType) {
				columnIDCategory = null;
				page.createColumnIDTypeButton.setEnabled(false);
				page.defineColumnIDParsingButton.setEnabled(false);
			} else {
				rowIDCategory = null;
				page.createRowIDTypeButton.setEnabled(false);
				page.defineRowIDParsingButton.setEnabled(false);
			}
			idCategoryCombo.deselectAll();
			idTypeCombo.deselectAll();
			idTypeCombo.setEnabled(false);
			// fillIDTypeCombo(isColumnIDType ? columnIDCategory :
			// rowIDCategory, idTypes,
			// idTypeCombo);
		}
	}

	protected List<IDCategory> getAvailableIDCategories() {
		return registeredIDCategories;
	}

	protected void updateWidgetsAccordingToTableChanges() {
		page.columnOfRowIDSpinner.setMaximum(totalNumberOfColumns);
		page.rowOfColumnIDSpinner.setMaximum(totalNumberOfRows);
		page.numHeaderRowsSpinner.setMaximum(totalNumberOfRows);
		// page.previewTable.updateVisibleColumns(totalNumberOfColumns);
		// page.parentComposite.pack(true);
		// page.parentComposite.layout(true);
	}

	private IDType determineMostProbableIDType(List<String> idList) {
		float maxProbability = 0;
		IDType mostProbableIDType = null;
		for (IDCategory idCategory : getAvailableIDCategories()) {
			List<Pair<Float, IDType>> probabilityList = idCategory.getListOfIDTypeAffiliationProbabilities(idList,
					false);
			if (probabilityList.size() > 0) {
				Pair<Float, IDType> pair = probabilityList.get(0);
				if (pair.getFirst() > maxProbability) {
					maxProbability = pair.getFirst();
					mostProbableIDType = pair.getSecond();
				}
			}
		}

		if (maxProbability <= 1.0f / MAX_CONSIDERED_IDS_FOR_ID_TYPE_DETERMINATION)
			mostProbableIDType = null;

		return mostProbableIDType;
	}

	/**
	 * Reads the min and max values (if set) from the dialog
	 */
	public void fillDataSetDescription() {

		dataSetDescription.setDataSetName(page.label.getText());

		if (page.rowIDCategoryCombo.getSelectionIndex() != UNMAPPED_INDEX) {
			IDSpecification rowIDSpecification = new IDSpecification();
			IDType rowIDType = IDType.getIDType(page.rowIDCombo.getItem(page.rowIDCombo.getSelectionIndex()));
			rowIDSpecification.setIdType(rowIDType.getTypeName());
			rowIDSpecification.setDataType(rowIDType.getDataType());
			if (rowIDType.getIDCategory().getCategoryName().equals("GENE"))
				rowIDSpecification.setIDTypeGene(true);
			rowIDSpecification.setIdCategory(rowIDType.getIDCategory().getCategoryName());
			// if (rowIDTypeParsingRules != null) {
			rowIDSpecification.setIdTypeParsingRules(rowIDTypeParsingRules);
			dataSetDescription.setRowIDSpecification(rowIDSpecification);
		} else {
			IDSpecification rowIDSpecification = new IDSpecification();
			IDCategory idCategory = IDCategory.registerCategoryIfAbsent(dataSetDescription.getDataSetName() + "_row");
			idCategory.setInternalCategory(true);
			IDType idType = IDType.registerType(dataSetDescription.getDataSetName() + "_row", idCategory,
					EDataType.STRING);
			rowIDSpecification.setIDSpecification(idCategory.getCategoryName(), idType.getTypeName());
			dataSetDescription.setRowIDSpecification(rowIDSpecification);
		}
		// } else if (rowIDType.getIdTypeParsingRules() != null) {
		// rowIDSpecification.setIdTypeParsingRules(rowIDType.getIdTypeParsingRules());
		// } else if (rowIDType.getTypeName().equalsIgnoreCase("REFSEQ_MRNA")) {
		// // for REFSEQ_MRNA we ignore the .1, etc.
		// IDTypeParsingRules parsingRules = new IDTypeParsingRules();
		// parsingRules.setSubStringExpression("\\.");
		// parsingRules.setDefault(true);
		// rowIDSpecification.setIdTypeParsingRules(parsingRules);
		// }

		if (!page.inhomogeneousDatasetButton.getSelection()
				&& page.columnIDCategoryCombo.getSelectionIndex() != UNMAPPED_INDEX) {
			IDSpecification columnIDSpecification = new IDSpecification();
			IDType columnIDType = IDType.getIDType(page.columnIDCombo.getItem(page.columnIDCombo.getSelectionIndex()));
			// columnIDTypes.get(page.columnIDCombo.getSelectionIndex());
			columnIDSpecification.setIdType(columnIDType.getTypeName());
			columnIDSpecification.setDataType(columnIDType.getDataType());
			if (columnIDType.getIDCategory().getCategoryName().equals("GENE"))
				columnIDSpecification.setIDTypeGene(true);
			columnIDSpecification.setIdCategory(columnIDType.getIDCategory().getCategoryName());
			// if (columnIDTypeParsingRules != null) {
			columnIDSpecification.setIdTypeParsingRules(columnIDTypeParsingRules);
			// } else if (columnIDType.getIdTypeParsingRules() != null) {
			// columnIDSpecification.setIdTypeParsingRules(columnIDType.getIdTypeParsingRules());
			// }
			dataSetDescription.setColumnIDSpecification(columnIDSpecification);
		} else {
			IDSpecification columnIDSpecification = new IDSpecification();
			IDCategory idCategory = IDCategory
					.registerCategoryIfAbsent(dataSetDescription.getDataSetName() + "_column");
			idCategory.setInternalCategory(true);
			IDType idType = IDType.registerType(dataSetDescription.getDataSetName() + "_column", idCategory,
					EDataType.STRING);
			columnIDSpecification.setIDSpecification(idCategory.getCategoryName(), idType.getTypeName());
			dataSetDescription.setColumnIDSpecification(columnIDSpecification);
		}

		// TODO check buttonHomogeneous
		// dataSetDescription.setDataHomogeneous(page.buttonHomogeneous.getSelection());

		dataSetDescription.setDataSourcePath(isTransposed ? transposedDataFile.getAbsolutePath() : page.loadFile
				.getFileName());
		// readDimensionDefinition();

		List<List<String>> matrix = parser.getDataMatrix();
		List<List<String>> filteredMatrix = new ArrayList<>(matrix.size());
		List<Integer> selectedColumns = page.previewTable.getSelectedColumns();
		DataImportWizard wizard = page.getWizard();
		List<String> columnOfRowIDs = new ArrayList<>(matrix.size() - dataSetDescription.getNumberOfHeaderLines());

		for (int i = 0; i < matrix.size(); i++) {

			boolean isRowOfColumnID = false;
			if (i == dataSetDescription.getRowOfColumnIDs()) {
				isRowOfColumnID = true;
			} else if (i < dataSetDescription.getNumberOfHeaderLines())
				continue;

			List<String> row = matrix.get(i);
			List<String> filteredRow = filterRowByIndices(row, selectedColumns);
			if (isRowOfColumnID) {
				List<String> convertedFilteredRow = new ArrayList<>(filteredRow.size());
				for (String id : filteredRow) {
					convertedFilteredRow.add(ATextParser.convertID(id, columnIDTypeParsingRules));
				}
				wizard.setFilteredRowOfColumnIDs(convertedFilteredRow);
			} else {
				filteredMatrix.add(filteredRow);
				columnOfRowIDs.add(ATextParser.convertID(row.get(dataSetDescription.getColumnOfRowIds()),
						rowIDTypeParsingRules));
			}
		}

		wizard.setFilteredDataMatrix(filteredMatrix);
		wizard.setSelectedColumns(selectedColumns);
		wizard.setColumnOfRowIDs(columnOfRowIDs);

		if (page.inhomogeneousDatasetButton.getSelection()) {
			if (datasetChanged || dataSetDescription.getDataDescription() != null) {
				page.getWizard().getInhomogeneousDataPropertiesPage().setInitColumnDescriptions(!initFromConfiguration);
				// No global data description for inhomogeneous
				dataSetDescription.setDataDescription(null);
			}
		}
		datasetChanged = false;
	}

	private List<String> filterRowByIndices(List<String> row, List<Integer> indices) {
		List<String> filteredRow = new ArrayList<>(indices.size());
		for (int selectedColumn : indices) {
			if (selectedColumn != dataSetDescription.getColumnOfRowIds())
				filteredRow.add(row.get(selectedColumn));
		}
		return filteredRow;
	}

	/**
	 * prepares the dimension creation definition from the preview table. The dimension creation definition consists of
	 * the definition which columns in the data-CSV-file should be read, which should be skipped and the
	 * dimension-labels.
	 *
	 * @return <code>true</code> if the preparation was successful, <code>false</code> otherwise
	 */
	// private void readDimensionDefinition() {
	// // ArrayList<String> dimensionLabels = new ArrayList<String>();
	//
	// ArrayList<ColumnDescription> inputPattern = new ArrayList<ColumnDescription>();
	// // inputPattern = new StringBuffer("SKIP" + ";");
	//
	// // the columnIndex here is the columnIndex of the previewTable. This is
	// // different by one from the index in the source csv.
	// for (Integer selected : page.previewTable.getSelectedColumns()) {
	// int columnIndex = selected.intValue();
	// if (columnIndex == dataSetDescription.getColumnOfRowIds())
	// continue;
	// if (columnIndex >= 0)
	// inputPattern.add(createColumnDescription(columnIndex));
	// else {
	// // wildcard creating multiple column descriptions at once, i.e til the end
	// int from = page.previewTable.getColumnCount(); // everything before was directly selected
	// int to = this.totalNumberOfColumns; // all possible
	// for (int i = from; i < to; ++i) {
	// // TODO how to handle different automatically detected types for unknown
	// inputPattern.add(new ColumnDescription(i, dataSetDescription.getDataDescription()));
	// }
	// }
	//
	// // String labelText = dataMatrix.get(0).get(columnIndex);
	// // dimensionLabels.add(labelText);
	// }
	// dataSetDescription.setParsingPattern(inputPattern);
	// dataSetDescription.setDataSourcePath(page.loadFile.getFileName());
	// // dataSetDescripton.setColumnLabels(dimidMappingManagerensionLabels);
	//
	// }

	/**
	 * Creates a {@link ColumnDescription} for the specified column.
	 *
	 * @param columnIndex
	 *            Index of the column in the file.
	 * @return The ColumnDescription.
	 */
	// private ColumnDescription createColumnDescription(int columnIndex) {
	//
	// // TODO: This is just a temporary solution to the problem of detecting
	// // NaN values: now we expect the column to be float, if one float is
	// // found, otherwise it is a string.
	// int testSize = page.previewTable.getRowCount();
	// for (int rowIndex = dataSetDescription.getNumberOfHeaderLines(); rowIndex < testSize; rowIndex++) {
	// if (rowIndex != dataSetDescription.getRowOfColumnIDs()) {
	// String testString = dataMatrix.get(rowIndex).get(columnIndex);
	// try {
	// if (!testString.isEmpty()) {
	// Float.parseFloat(testString);
	// return new ColumnDescription(columnIndex, dataSetDescription.getDataDescription());
	// }
	// } catch (NumberFormatException nfe) {
	// }
	// }
	// }
	// throw new UnsupportedOperationException("Not implemented for non-numerica data");
	// // return new ColumnDescription(columnIndex, EDataClass.CATEGORICAL, EDataType.STRING);
	// }

	/**
	 * @return the dataSetDescription, see {@link #dataSetDescription}
	 */
	public DataSetDescription getDataSetDescription() {
		return dataSetDescription;
	}

	public boolean isValidConfiguration() {
		if (page.loadFile.getFileName().isEmpty()) {
			page.getWizard().setRequiredDataSpecified(false);
			return false;
		}

		if (page.rowIDCombo.getSelectionIndex() == -1 && page.rowIDCategoryCombo.getSelectionIndex() != UNMAPPED_INDEX) {
			page.getWizard().setRequiredDataSpecified(false);
			return false;
		}

		if (page.columnIDCombo.getSelectionIndex() == -1 && !page.inhomogeneousDatasetButton.getSelection()
				&& page.columnIDCategoryCombo.getSelectionIndex() != UNMAPPED_INDEX) {
			page.getWizard().setRequiredDataSpecified(false);
			return false;
		}
		// if (page.columnIDCombo.getSelectionIndex() == -1 && page.rowIDCombo.getSelectionIndex() == -1) {
		// page.getWizard().setRequiredDataSpecified(false);
		// return false;
		// }

		page.getWizard().setRequiredDataSpecified(true);

		return true;
	}
}
