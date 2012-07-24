/**
 * 
 */
package org.caleydo.core.io.gui.dataimport.wizard;

import org.caleydo.core.io.DataSetDescription;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * Page that offers the possibility to transform the dataset, such as data
 * logarithmation or table transpose.
 * 
 * @author Christian Partl
 * 
 */
public class TransformDataPage extends AImportDataPage {

	public static final String PAGE_NAME = "Transform Data";

	public static final String PAGE_DESCRIPTION = "Specify the data transformations to be performed.";

	private Text minTextField;
	private Text maxTextField;
	private Button buttonSwapRowsWithColumns;
	private String scalingMode = "Log2";

	/**
	 * @param pageName
	 * @param dataSetDescription
	 */
	// protected TransformDataPage(String pageName, DataSetDescription
	// dataSetDescription) {
	// super(pageName, dataSetDescription);
	// }

	public TransformDataPage(DataSetDescription dataSetDescription) {
		super(PAGE_NAME, dataSetDescription);
		setDescription(PAGE_DESCRIPTION);
	}

	@Override
	public void createControl(Composite parent) {

		Composite parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayout(new GridLayout(1, true));

		createScalingGroup(parentComposite);

		createClippingGroup(parentComposite);

		createTranspositionGroup(parentComposite);

		setControl(parentComposite);
	}

	private void createTranspositionGroup(Composite parent) {
		Group dataTranspositionGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		dataTranspositionGroup.setText("Data Transposition");
		dataTranspositionGroup.setLayout(new GridLayout(2, false));
		dataTranspositionGroup
				.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		Label transpositionExplanationLabel = new Label(dataTranspositionGroup, SWT.WRAP);
		transpositionExplanationLabel
				.setText("Specify whether the table should be transposed, i.e., whether the rows should become columns and vice versa.");
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gridData.widthHint = 200;
		transpositionExplanationLabel.setLayoutData(gridData);
		buttonSwapRowsWithColumns = new Button(dataTranspositionGroup, SWT.CHECK);
		buttonSwapRowsWithColumns.setText("Swap Rows and Columns");
		buttonSwapRowsWithColumns.setEnabled(true);
		buttonSwapRowsWithColumns.setSelection(false);
	}

	private void createClippingGroup(Composite parent) {

		Group clippingGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		clippingGroup.setText("Data Clipping");
		clippingGroup.setLayout(new GridLayout(2, false));
		clippingGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		Label clippingExplanationLabel = new Label(clippingGroup, SWT.WRAP);
		clippingExplanationLabel
				.setText("Specify the value range for the dataset. Every data point exceeding this range will be clipped to the lower and upper limits respectively.");
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gridData.widthHint = 200;
		clippingExplanationLabel.setLayoutData(gridData);
		
		
		final Button buttonMax = new Button(clippingGroup, SWT.CHECK);
		buttonMax.setText("Max");
		buttonMax.setEnabled(true);
		buttonMax.setSelection(false);
		buttonMax.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				maxTextField.setEnabled(buttonMax.getSelection());
			}
		});

		maxTextField = new Text(clippingGroup, SWT.BORDER);
		maxTextField.setEnabled(false);
		maxTextField.addListener(SWT.Verify, new Listener() {
			@Override
			public void handleEvent(Event e) {
				// Only allow digits
				String string = e.text;
				char[] chars = new char[string.length()];
				string.getChars(0, chars.length, chars, 0);
			}
		});
		
		final Button buttonMin = new Button(clippingGroup, SWT.CHECK);
		buttonMin.setText("Min");
		buttonMin.setEnabled(true);
		buttonMin.setSelection(false);
		buttonMin.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				minTextField.setEnabled(buttonMin.getSelection());
			}
		});

		minTextField = new Text(clippingGroup, SWT.BORDER);
		minTextField.setEnabled(false);
		minTextField.addListener(SWT.Verify, new Listener() {
			@Override
			public void handleEvent(Event e) {
				// Only allow digits
				String string = e.text;
				char[] chars = new char[string.length()];
				string.getChars(0, chars.length, chars, 0);
			}
		});

	}

	private void createScalingGroup(Composite parent) {
		Group scalingGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		scalingGroup.setText("Data Scale");
		scalingGroup.setLayout(new GridLayout(2, false));
		scalingGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		Label scalingExplanationLabel = new Label(scalingGroup, SWT.WRAP);
		scalingExplanationLabel
				.setText("Specify the way every data point should be scaled.");
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gridData.widthHint = 200;
		scalingExplanationLabel.setLayoutData(gridData);

		Label scalingMethodLabel = new Label(scalingGroup, SWT.NONE);
		scalingMethodLabel.setText("Scaling Method");

		final Combo scalingCombo = new Combo(scalingGroup, SWT.DROP_DOWN);
		String[] scalingOptions = { "None", "Log10", "Log2" };
		scalingCombo.setItems(scalingOptions);
		scalingCombo.setEnabled(true);
		scalingCombo.select(2);
		scalingCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				scalingMode = scalingCombo.getText();
			}
		});
	}

	@Override
	public void fillDataSetDescription() {
		if (minTextField.getEnabled() && !minTextField.getText().isEmpty()) {
			float fMin = Float.parseFloat(minTextField.getText());
			if (!Float.isNaN(fMin)) {
				dataSetDescription.setMin(fMin);
			}
		}
		if (maxTextField.getEnabled() && !maxTextField.getText().isEmpty()) {
			float fMax = Float.parseFloat(maxTextField.getText());
			if (!Float.isNaN(fMax)) {
				dataSetDescription.setMax(fMax);
			}
		}

		dataSetDescription.setMathFilterMode(scalingMode);
		dataSetDescription.setTransposeMatrix(buttonSwapRowsWithColumns.getSelection());

	}

}
