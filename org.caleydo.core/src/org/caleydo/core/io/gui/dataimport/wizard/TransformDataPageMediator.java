/**
 * 
 */
package org.caleydo.core.io.gui.dataimport.wizard;

import org.caleydo.core.io.DataSetDescription;

/**
 * Mediator for {@link TransformDataPage}. This class is responsible for setting
 * the states of all widgets of the page and triggering actions according to
 * different events that occur in the page.
 * 
 * 
 * @author Christian Partl
 * 
 */
public class TransformDataPageMediator {

	/**
	 * Mode that specifies how the data is scaled.
	 */
	private String scalingMode = "None";

	private DataSetDescription dataSetDescription;

	/**
	 * Page this class serves as mediator for.
	 */
	private TransformDataPage page;

	/**
	 * Determines whether the page has been activated the first time.
	 */
	private boolean firstTimeActivation = true;

	public TransformDataPageMediator(TransformDataPage page,
			DataSetDescription dataSetDescription) {
		this.page = page;
		this.dataSetDescription = dataSetDescription;
	}

	/**
	 * Initializes all widgets of the {@link #page}. This method should be
	 * called after all widgets of the dialog were created.
	 */
	public void guiCreated() {

		String previousMathFiltermode = dataSetDescription.getMathFilterMode();
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

		scalingMode = page.scalingCombo.getText();

		boolean maxDefined = dataSetDescription.getMax() != null;
		page.maxButton.setEnabled(true);
		page.maxButton.setSelection(maxDefined);
		page.maxTextField.setEnabled(maxDefined);
		if (maxDefined)
			page.maxTextField.setText(dataSetDescription.getMax().toString());

		boolean minDefined = dataSetDescription.getMin() != null;
		page.minButton.setEnabled(true);
		page.minButton.setSelection(minDefined);
		page.minTextField.setEnabled(minDefined);
		if (minDefined)
			page.minTextField.setText(dataSetDescription.getMin().toString());

		page.buttonSwapRowsWithColumns.setEnabled(true);
		page.buttonSwapRowsWithColumns.setSelection(dataSetDescription
				.isTransposeMatrix());

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

	public void verifyClippingTextField(String text) {
		char[] chars = new char[text.length()];
		text.getChars(0, chars.length, chars, 0);
	}

	/**
	 * Sets the {@link #scalingMode}.
	 */
	public void scalingComboSelected() {
		scalingMode = page.scalingCombo.getText();
	}

	/**
	 * Reinitializes all widgets according to the {@link #dataSetDescription}.
	 */
	public void pageActivated() {
		if (firstTimeActivation) {
			guiCreated();
			// Guess transposal
			DataImportWizard wizard = (DataImportWizard) page.getWizard();
			int totalNumberOfColumns = wizard.getTotalNumberOfColumns();
			int totalNumberOfRows = wizard.getTotalNumberOfRows();
			if (totalNumberOfColumns > 100 && totalNumberOfColumns > totalNumberOfRows)
				page.buttonSwapRowsWithColumns.setSelection(true);
			else
				page.buttonSwapRowsWithColumns.setSelection(false);
		}
		firstTimeActivation = false;

	}

	/**
	 * Fills the {@link #dataSetDescription} according to the widgets.
	 */
	public void fillDataSetDescription() {
		if (page.minTextField.getEnabled() && !page.minTextField.getText().isEmpty()) {
			float min = Float.parseFloat(page.minTextField.getText());
			if (!Float.isNaN(min)) {
				dataSetDescription.setMin(min);
			}
		}
		if (page.maxTextField.getEnabled() && !page.maxTextField.getText().isEmpty()) {
			float max = Float.parseFloat(page.maxTextField.getText());
			if (!Float.isNaN(max)) {
				dataSetDescription.setMax(max);
			}
		}

		dataSetDescription.setMathFilterMode(scalingMode);
		dataSetDescription.setTransposeMatrix(page.buttonSwapRowsWithColumns
				.getSelection());
	}

}
