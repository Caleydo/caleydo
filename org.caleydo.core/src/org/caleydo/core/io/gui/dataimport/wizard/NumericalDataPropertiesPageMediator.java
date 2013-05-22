/**
 *
 */
package org.caleydo.core.io.gui.dataimport.wizard;

import java.util.ArrayList;

import org.caleydo.core.id.IDCategory;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.NumericalProperties;

/**
 * Mediator for {@link NumericalDataPropertiesPage}. This class is responsible for setting the states of all widgets of
 * the page and triggering actions according to different events that occur in the page.
 *
 *
 * @author Christian Partl
 *
 */
public class NumericalDataPropertiesPageMediator {

	/**
	 * Mode that specifies how the data is scaled.
	 */
	private String dataTransformation = "None";

	private DataSetDescription dataSetDescription;

	/**
	 * Matrix that stores the data for {@link #MAX_PREVIEW_TABLE_ROWS} rows and all columns of the data file.
	 */
	protected ArrayList<ArrayList<String>> dataMatrix;

	/**
	 * Page this class serves as mediator for.
	 */
	private NumericalDataPropertiesPage page;

	/**
	 * File name of the current dataset.
	 */
	private String dataSourcePath = "";

	public NumericalDataPropertiesPageMediator(NumericalDataPropertiesPage page, DataSetDescription dataSetDescription) {
		this.page = page;
		this.dataSetDescription = dataSetDescription;
	}

	/**
	 * Initializes all widgets of the {@link #page}. This method should be called after all widgets of the dialog were
	 * created.
	 */
	public void guiCreated() {

		NumericalProperties numericalProperties = dataSetDescription.getDataDescription().getNumericalProperties();
		if (numericalProperties == null) {
			numericalProperties = new NumericalProperties();
			dataSetDescription.getDataDescription().setNumericalProperties(numericalProperties);
		}

		String previousMathFiltermode = numericalProperties.getDataTransformation();
		String[] scalingOptions = { "None", "Log10", "Log2" };
		page.scalingCombo.setItems(scalingOptions);
		page.scalingCombo.setEnabled(true);

		if (previousMathFiltermode.equals("None")) {
			page.scalingCombo.select(0);
		} else if (previousMathFiltermode.equals("Log10"))
			page.scalingCombo.select(1);
		else if (previousMathFiltermode.equals("Log2"))
			page.scalingCombo.select(2);
		else
			page.scalingCombo.select(0);

		dataTransformation = page.scalingCombo.getText();

		boolean maxDefined = numericalProperties.getMax() != null;
		page.maxButton.setEnabled(true);
		page.maxButton.setSelection(maxDefined);
		page.maxTextField.setEnabled(maxDefined);
		if (maxDefined)
			page.maxTextField.setText(numericalProperties.getMax().toString());

		boolean minDefined = numericalProperties.getMin() != null;
		page.minButton.setEnabled(true);
		page.minButton.setSelection(minDefined);
		page.minTextField.setEnabled(minDefined);
		if (minDefined)
			page.minTextField.setText(numericalProperties.getMin().toString());

		page.swapRowsWithColumnsButton.setEnabled(true);
		page.swapRowsWithColumnsButton.setSelection(dataSetDescription.isTransposeMatrix());

		page.useDataCenterButton.setSelection(false);
		page.dataCenterTextField.setEnabled(false);
		page.dataCenterTextField.setText("0");
	}

	/**
	 * Enables or disables maxTextField.
	 */
	public void maxButtonSelected() {
		page.maxTextField.setEnabled(page.maxButton.getSelection());
	}

	/**
	 * Enables or disables minTextField.
	 */
	public void minButtonSelected() {
		page.minTextField.setEnabled(page.minButton.getSelection());
	}

	/**
	 * Sets the {@link #dataTransformation}.
	 */
	public void scalingComboSelected() {
		dataTransformation = page.scalingCombo.getText();
	}

	/**
	 * Sets the column count warning visible or not, depending on the current count of columns.
	 */
	public void swapRowsWithColumnsButtonSelected() {
		updateColumnCountWarning();
	}

	public void useDataCenterButtonSelected() {
		page.dataCenterTextField.setEnabled(page.useDataCenterButton.getSelection());
	}

	/**
	 * Reinitializes all widgets according to the {@link #dataSetDescription}.
	 */
	public void pageActivated() {
		if (!dataSourcePath.equals(dataSetDescription.getDataSourcePath())) {
			guiCreated();
			// Guess transposal
			DataImportWizard wizard = (DataImportWizard) page.getWizard();
			int totalNumberOfColumns = wizard.getTotalNumberOfColumns();
			int totalNumberOfRows = wizard.getTotalNumberOfRows();
			IDCategory tcgaSampleCategory = IDCategory.getIDCategory("TCGA_SAMPLE");
			if (totalNumberOfColumns > 100
					&& totalNumberOfColumns > totalNumberOfRows
					|| (tcgaSampleCategory != null && (dataSetDescription.getColumnIDSpecification().getIdCategory()
							.equals(tcgaSampleCategory.getCategoryName())))) {
				page.swapRowsWithColumnsButton.setSelection(true);

			} else {
				page.swapRowsWithColumnsButton.setSelection(false);
			}

			updateColumnCountWarning();

		}
		dataSourcePath = dataSetDescription.getDataSourcePath();
		// parser.parse(dataSetDescription.getDataSourcePath(), dataSetDescription.getDelimiter(), true, -1);
		// List<List<String>> matrix = parser.getDataMatrix();
		// List<List<String>> filteredMatrix = new ArrayList<>(matrix.size());
		// List<ColumnDescription> parsingPattern = dataSetDescription.getOrCreateParsingPattern();
		// for (int i = 0; i < matrix.size(); i++) {
		// if (i < dataSetDescription.getNumberOfHeaderLines() - 1)
		// continue;
		// List<String> row = matrix.get(i);
		// List<String> filteredRow = new ArrayList<>(parsingPattern.size());
		// for (ColumnDescription columnDescription : parsingPattern) {
		// filteredRow.add(row.get(columnDescription.getColumn()));
		// }
		// filteredMatrix.add(filteredRow);
		// }

		// page.columnConfigTable.createTableFromMatrix(filteredMatrix, parsingPattern.size());
	}

	private void updateColumnCountWarning() {
		DataImportWizard wizard = (DataImportWizard) page.getWizard();
		int totalNumberOfRows = wizard.getTotalNumberOfRows();

		int numColumns = page.swapRowsWithColumnsButton.getSelection() ? totalNumberOfRows : (dataSetDescription
				.getOrCreateParsingPattern().size() + 1);

		if (page.warningIconLabel1 != null && !page.warningIconLabel1.isDisposed()) {
			page.warningIconLabel1.dispose();
			page.warningDescriptionLabel1.dispose();
		}

		if (page.warningIconLabel2 != null && !page.warningIconLabel2.isDisposed()) {
			page.warningIconLabel2.dispose();
			page.warningDescriptionLabel2.dispose();
		}

		page.parentComposite.layout(true);

		if (numColumns > 50) {
			String warningText1 = "Attention: the large number of columns (" + numColumns
					+ ") may lead to an impaired visualization quality in some views";

			if (page.warningIconLabel1 == null || page.warningIconLabel1.isDisposed()) {
				page.warningIconLabel1 = page.createWarningIconLabel(page.dataTranspositionGroup);
				page.warningDescriptionLabel1 = page.createWarningDescriptionLabel(page.dataTranspositionGroup,
						warningText1);
			}
		}

		if (totalNumberOfRows > 50 && dataSetDescription.getOrCreateParsingPattern().size() > 50) {
			if (page.warningIconLabel2 == null || page.warningIconLabel2.isDisposed()) {
				page.warningIconLabel2 = page.createWarningIconLabel(page.dataTranspositionGroup);
				page.warningDescriptionLabel2 = page
						.createWarningDescriptionLabel(
								page.dataTranspositionGroup,
								"Attention: In your dataset the choice of dimensions is not obvious. Please choose whether you want to keep the columns in the file as dimensions (do not check) or whether you want to use the rows as dimensions.");
			}
		}

		// page.parentComposite.pack(true);
		page.parentComposite.layout(true);
	}

	/**
	 * Fills the {@link #dataSetDescription} according to the widgets.
	 */
	public void fillDataSetDescription() {
		NumericalProperties numericalProperties = dataSetDescription.getDataDescription().getNumericalProperties();
		if (page.minTextField.getEnabled() && !page.minTextField.getText().isEmpty()) {
			float min = Float.parseFloat(page.minTextField.getText());
			if (!Float.isNaN(min)) {
				numericalProperties.setMin(min);
			}
		}
		if (page.maxTextField.getEnabled() && !page.maxTextField.getText().isEmpty()) {
			float max = Float.parseFloat(page.maxTextField.getText());
			if (!Float.isNaN(max)) {
				numericalProperties.setMax(max);
			}
		}
		if (page.useDataCenterButton.getSelection()) {
			numericalProperties.setDataCenter(Double.parseDouble(page.dataCenterTextField.getText()));
		}

		numericalProperties.setDataTransformation(dataTransformation);
		dataSetDescription.setTransposeMatrix(page.swapRowsWithColumnsButton.getSelection());
	}

	public boolean isDataValid() {
		if (page.maxButton.getSelection()) {
			try {
				Float.parseFloat(page.maxTextField.getText());
			} catch (NumberFormatException e) {
				return false;
			}
		}
		if (page.minButton.getSelection()) {
			try {
				Float.parseFloat(page.minTextField.getText());
			} catch (NumberFormatException e) {
				return false;
			}
		}
		if (page.useDataCenterButton.getSelection()) {
			try {
				Double.parseDouble(page.dataCenterTextField.getText());
			} catch (NumberFormatException e) {
				return false;
			}
		}

		return true;
	}

}
