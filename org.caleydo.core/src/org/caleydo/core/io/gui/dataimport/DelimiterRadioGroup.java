/**
 * 
 */
package org.caleydo.core.io.gui.dataimport;

import org.caleydo.core.io.MatrixDefinition;
import org.caleydo.core.io.gui.dataimport.wizard.LoadDataSetPageMediator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

/**
 * Class that creates a group of radio buttons for data file delimiter
 * specification.
 * 
 * @author Christian Partl
 * 
 */
public class DelimiterRadioGroup {

	protected Button[] delimiterButtons = new Button[6];

	protected Text customizedDelimiterTextField;

	/**
	 * 
	 */
	public DelimiterRadioGroup() {
	}

	// FIXME: just a temporary solution
	public void create(Composite parent, final ImportGroupingDialogMediator mediator) {
		Group delimiterGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		delimiterGroup.setText("Separated by (delimiter)");
		delimiterGroup.setLayout(new RowLayout());

		delimiterButtons[0] = new Button(delimiterGroup, SWT.RADIO);
		delimiterButtons[0].setSelection(true);
		delimiterButtons[0].setText("TAB");
		delimiterButtons[0].setData("\t");
		delimiterButtons[0].setBounds(10, 5, 75, 30);

		delimiterButtons[1] = new Button(delimiterGroup, SWT.RADIO);
		delimiterButtons[1].setText(";");
		delimiterButtons[1].setData(";");
		delimiterButtons[1].setBounds(10, 30, 75, 30);

		delimiterButtons[2] = new Button(delimiterGroup, SWT.RADIO);
		delimiterButtons[2].setText(",");
		delimiterButtons[2].setData(",");
		delimiterButtons[2].setBounds(10, 55, 75, 30);

		delimiterButtons[3] = new Button(delimiterGroup, SWT.RADIO);
		delimiterButtons[3].setText(".");
		delimiterButtons[3].setData(".");
		delimiterButtons[3].setBounds(10, 55, 75, 30);

		delimiterButtons[4] = new Button(delimiterGroup, SWT.RADIO);
		delimiterButtons[4].setText("SPACE");
		delimiterButtons[4].setData(" ");
		delimiterButtons[4].setBounds(10, 55, 75, 30);

		delimiterButtons[5] = new Button(delimiterGroup, SWT.RADIO);
		delimiterButtons[5].setText("Other");
		delimiterButtons[5].setBounds(10, 55, 75, 30);

		customizedDelimiterTextField = new Text(delimiterGroup, SWT.BORDER);
		customizedDelimiterTextField.setBounds(0, 0, 75, 30);
		customizedDelimiterTextField.setTextLimit(1);
		customizedDelimiterTextField.setEnabled(false);
		customizedDelimiterTextField.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				mediator.customizedDelimiterTextFieldModified();
//				matrixDefinition.setDelimiter(customizedDelimiterTextField.getText());
//				dataImporter.createDataPreviewTableFromFile();
				// composite.pack();
			}

		});

		SelectionAdapter radioGroupSelectionListener = new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Button selectedButton = (Button) e.getSource();
				mediator.delimiterRadioButtonSelected(selectedButton);
//				if (selectedButton != delimiterButtons[5]) {
//					customizedDelimiterTextField.setEnabled(false);
//					matrixDefinition.setDelimiter((String) selectedButton.getData());
//					dataImporter.createDataPreviewTableFromFile();
//				} else {
//					customizedDelimiterTextField.setEnabled(true);
//					matrixDefinition.setDelimiter(" ");
//					dataImporter.createDataPreviewTableFromFile();
//				}
			}
		};

		for (int i = 0; i < delimiterButtons.length; i++) {
			delimiterButtons[i].addSelectionListener(radioGroupSelectionListener);
		}

	}
	
	// FIXME: just a temporary solution
		public void create(Composite parent, final LoadDataSetPageMediator mediator) {
			Group delimiterGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
			delimiterGroup.setText("Separated by (delimiter)");
			delimiterGroup.setLayout(new RowLayout());

			delimiterButtons[0] = new Button(delimiterGroup, SWT.RADIO);
			delimiterButtons[0].setSelection(true);
			delimiterButtons[0].setText("TAB");
			delimiterButtons[0].setData("\t");
			delimiterButtons[0].setBounds(10, 5, 75, 30);

			delimiterButtons[1] = new Button(delimiterGroup, SWT.RADIO);
			delimiterButtons[1].setText(";");
			delimiterButtons[1].setData(";");
			delimiterButtons[1].setBounds(10, 30, 75, 30);

			delimiterButtons[2] = new Button(delimiterGroup, SWT.RADIO);
			delimiterButtons[2].setText(",");
			delimiterButtons[2].setData(",");
			delimiterButtons[2].setBounds(10, 55, 75, 30);

			delimiterButtons[3] = new Button(delimiterGroup, SWT.RADIO);
			delimiterButtons[3].setText(".");
			delimiterButtons[3].setData(".");
			delimiterButtons[3].setBounds(10, 55, 75, 30);

			delimiterButtons[4] = new Button(delimiterGroup, SWT.RADIO);
			delimiterButtons[4].setText("SPACE");
			delimiterButtons[4].setData(" ");
			delimiterButtons[4].setBounds(10, 55, 75, 30);

			delimiterButtons[5] = new Button(delimiterGroup, SWT.RADIO);
			delimiterButtons[5].setText("Other");
			delimiterButtons[5].setBounds(10, 55, 75, 30);

			customizedDelimiterTextField = new Text(delimiterGroup, SWT.BORDER);
			customizedDelimiterTextField.setBounds(0, 0, 75, 30);
			customizedDelimiterTextField.setTextLimit(1);
			customizedDelimiterTextField.setEnabled(false);
			customizedDelimiterTextField.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					mediator.customizedDelimiterTextFieldModified();
//					matrixDefinition.setDelimiter(customizedDelimiterTextField.getText());
//					dataImporter.createDataPreviewTableFromFile();
					// composite.pack();
				}

			});

			SelectionAdapter radioGroupSelectionListener = new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					Button selectedButton = (Button) e.getSource();
					mediator.delimiterRadioButtonSelected(selectedButton);
//					if (selectedButton != delimiterButtons[5]) {
//						customizedDelimiterTextField.setEnabled(false);
//						matrixDefinition.setDelimiter((String) selectedButton.getData());
//						dataImporter.createDataPreviewTableFromFile();
//					} else {
//						customizedDelimiterTextField.setEnabled(true);
//						matrixDefinition.setDelimiter(" ");
//						dataImporter.createDataPreviewTableFromFile();
//					}
				}
			};

			for (int i = 0; i < delimiterButtons.length; i++) {
				delimiterButtons[i].addSelectionListener(radioGroupSelectionListener);
			}

		}

	public void create(Composite parent, final MatrixDefinition matrixDefinition,
			final ITabularDataImporter dataImporter) {
		Group delimiterGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		delimiterGroup.setText("Separated by (delimiter)");
		delimiterGroup.setLayout(new RowLayout());

		delimiterButtons[0] = new Button(delimiterGroup, SWT.RADIO);
		delimiterButtons[0].setSelection(true);
		delimiterButtons[0].setText("TAB");
		delimiterButtons[0].setData("\t");
		delimiterButtons[0].setBounds(10, 5, 75, 30);

		delimiterButtons[1] = new Button(delimiterGroup, SWT.RADIO);
		delimiterButtons[1].setText(";");
		delimiterButtons[1].setData(";");
		delimiterButtons[1].setBounds(10, 30, 75, 30);

		delimiterButtons[2] = new Button(delimiterGroup, SWT.RADIO);
		delimiterButtons[2].setText(",");
		delimiterButtons[2].setData(",");
		delimiterButtons[2].setBounds(10, 55, 75, 30);

		delimiterButtons[3] = new Button(delimiterGroup, SWT.RADIO);
		delimiterButtons[3].setText(".");
		delimiterButtons[3].setData(".");
		delimiterButtons[3].setBounds(10, 55, 75, 30);

		delimiterButtons[4] = new Button(delimiterGroup, SWT.RADIO);
		delimiterButtons[4].setText("SPACE");
		delimiterButtons[4].setData(" ");
		delimiterButtons[4].setBounds(10, 55, 75, 30);

		delimiterButtons[5] = new Button(delimiterGroup, SWT.RADIO);
		delimiterButtons[5].setText("Other");
		delimiterButtons[5].setBounds(10, 55, 75, 30);

		customizedDelimiterTextField = new Text(delimiterGroup, SWT.BORDER);
		customizedDelimiterTextField.setBounds(0, 0, 75, 30);
		customizedDelimiterTextField.setTextLimit(1);
		customizedDelimiterTextField.setEnabled(false);
		customizedDelimiterTextField.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				matrixDefinition.setDelimiter(customizedDelimiterTextField.getText());
				dataImporter.createDataPreviewTableFromFile();
				// composite.pack();
			}

		});

		SelectionAdapter radioGroupSelectionListener = new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Button selectedButton = (Button) e.getSource();
				if (selectedButton != delimiterButtons[5]) {
					customizedDelimiterTextField.setEnabled(false);
					matrixDefinition.setDelimiter((String) selectedButton.getData());
					dataImporter.createDataPreviewTableFromFile();
				} else {
					customizedDelimiterTextField.setEnabled(true);
					matrixDefinition.setDelimiter(" ");
					dataImporter.createDataPreviewTableFromFile();
				}
			}
		};

		for (int i = 0; i < delimiterButtons.length; i++) {
			delimiterButtons[i].addSelectionListener(radioGroupSelectionListener);
		}

	}
	
	/**
	 * @return the delimiterButtons, see {@link #delimiterButtons}
	 */
	public Button[] getDelimiterButtons() {
		return delimiterButtons;
	}
	
	/**
	 * @return the customizedDelimiterTextField, see {@link #customizedDelimiterTextField}
	 */
	public Text getCustomizedDelimiterTextField() {
		return customizedDelimiterTextField;
	}

}
