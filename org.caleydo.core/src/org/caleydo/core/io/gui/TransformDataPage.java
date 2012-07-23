/**
 * 
 */
package org.caleydo.core.io.gui;

import org.caleydo.core.io.DataSetDescription;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
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
	
	public static final String PAGE_NAME = "Specify Data Transformation";
	
	public static final String PAGE_DESCRIPTION = "Specify the data transformations to be performed.";

	private Text minTextField;
	private Text maxTextField;
	private Button buttonSwapRowsWithColumns;
	private String mathFilterMode = "Log2";

	/**
	 * @param pageName
	 * @param dataSetDescription
	 */
//	protected TransformDataPage(String pageName, DataSetDescription dataSetDescription) {
//		super(pageName, dataSetDescription);
//	}
	
	public TransformDataPage(DataSetDescription dataSetDescription) {
		super(PAGE_NAME, dataSetDescription);
		setDescription(PAGE_DESCRIPTION);
	}

	@Override
	public void createControl(Composite parent) {

		Composite parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayout(new GridLayout(2, true));
		
		createFilterGroup(parentComposite);
		
		buttonSwapRowsWithColumns = new Button(parentComposite, SWT.CHECK);
		buttonSwapRowsWithColumns.setText("Swap rows and columns");
		buttonSwapRowsWithColumns.setEnabled(true);
		buttonSwapRowsWithColumns.setSelection(false);

		setControl(parentComposite);
	}

	private void createFilterGroup(Composite parent) {
		Group filterGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		filterGroup.setText("Apply filter");
		filterGroup.setLayout(new RowLayout());
		filterGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Combo mathFilterCombo = new Combo(filterGroup, SWT.DROP_DOWN);
		String[] filterOptions = { "Normal", "Log10", "Log2" };
		mathFilterCombo.setItems(filterOptions);
		mathFilterCombo.setEnabled(true);
		mathFilterCombo.select(2);
		mathFilterCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mathFilterMode = mathFilterCombo.getText();
			}
		});

		final Button buttonMin = new Button(filterGroup, SWT.CHECK);
		buttonMin.setText("Min");
		buttonMin.setEnabled(true);
		buttonMin.setSelection(false);
		buttonMin.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				minTextField.setEnabled(buttonMin.getSelection());
			}
		});

		minTextField = new Text(filterGroup, SWT.BORDER);
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

		final Button buttonMax = new Button(filterGroup, SWT.CHECK);
		buttonMax.setText("Max");
		buttonMax.setEnabled(true);
		buttonMax.setSelection(false);
		buttonMax.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				maxTextField.setEnabled(buttonMax.getSelection());
			}
		});

		maxTextField = new Text(filterGroup, SWT.BORDER);
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
		
		dataSetDescription.setMathFilterMode(mathFilterMode);
		dataSetDescription.setTransposeMatrix(buttonSwapRowsWithColumns.getSelection());

	}

}
