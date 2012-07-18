/**
 * 
 */
package org.caleydo.core.io.gui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.core.io.IDTypeParsingRules;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Dialog that allows the creation of ID types.
 * 
 * @author Christian Partl
 * 
 */
public class CreateIDTypeDialog extends Dialog {

	/**
	 * Combo box that allows the specification of the {@link IDCategory} the
	 * {@link IDType} to be created should be associated with.
	 */
	private Combo idCategoryCombo;

	/**
	 * Combo box that allows the specification of the {@link EDataType} the
	 * {@link IDType} should be associated with.
	 */
	private Combo dataTypeCombo;

	/**
	 * Text field where the user is supposed to specify the name of the
	 * {@link IDType} to be created.
	 */
	private Text typeNameTextField;

	/**
	 * Maps the index of {@link #idCategoryCombo} to the {@link IDCategory}.
	 */
	private Map<Integer, IDCategory> idCategoryMap = new HashMap<Integer, IDCategory>();

	/**
	 * Maps the index of {@link #dataTypeCombo} to the {@link EDataType}.
	 */
	private Map<Integer, EDataType> dataTypeMap = new HashMap<Integer, EDataType>();

	/**
	 * Text field where the user can specify a string that shall be replaced
	 * using regular expressions. This regular expression is applied when
	 * parsing ids of the {@link IDType} created using this dialog.
	 */
	private Text replacementRegExTextField;

	/**
	 * Text field where the user can specify a string that shall replace the
	 * string specified by {@link #replacementRegExTextField}. This regular
	 * expression is applied when parsing ids of the {@link IDType} created
	 * using this dialog.
	 */
	private Text replacementStringTextField;

	/**
	 * Text field where the user can specify a regular expression to define a
	 * substring. This regular expression is applied when parsing ids of the
	 * {@link IDType} created using this dialog.
	 */
	private Text substringRegExTextField;

	/**
	 * Button to specify whether to use regular expressions shall be used to
	 * parse ids for the {@link IDType} created in this dialog.
	 */
	private Button useRegExButton;

	/**
	 * @param parentShell
	 */
	protected CreateIDTypeDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Create ID Type");
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayout(new GridLayout(2, false));

		Label categoryIDLabel = new Label(parentComposite, SWT.NONE);
		categoryIDLabel.setText("ID Category");

		idCategoryCombo = new Combo(parentComposite, SWT.DROP_DOWN);
		idCategoryCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		idCategoryCombo.setText("<Please select>");
		int index = 0;
		for (IDCategory category : IDCategory.getAllRegisteredIDCategories()) {
			idCategoryCombo.add(category.getCategoryName());
			idCategoryMap.put(index, category);
			index++;
		}

		Label dataTypeLabel = new Label(parentComposite, SWT.NONE);
		dataTypeLabel.setText("Data type");

		dataTypeCombo = new Combo(parentComposite, SWT.DROP_DOWN);
		dataTypeCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		dataTypeCombo.setText("<Please select>");
		dataTypeMap.put(0, EDataType.INT);
		dataTypeMap.put(1, EDataType.STRING);
		dataTypeCombo.add("Number");
		dataTypeCombo.add("Text");

		Label idTypeLabel = new Label(parentComposite, SWT.NONE);
		idTypeLabel.setText("ID type name");

		typeNameTextField = new Text(parentComposite, SWT.BORDER);
		typeNameTextField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		final Group regExGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		regExGroup.setText("Regular expressions");
		regExGroup.setLayout(new GridLayout(4, false));
		regExGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		Composite useRegExComposite = new Composite(regExGroup, SWT.NONE);
		useRegExComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
		useRegExComposite.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false, 4, 1));
		useRegExButton = new Button(useRegExComposite, SWT.CHECK);
//		useRegExButton.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true, 2, 1));
		Label useRegExlabel = new Label(useRegExComposite, SWT.NONE);
		useRegExlabel.setText("Use regular expressions to convert IDs");
//		useRegExlabel.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true, 2, 1));

		final Label replacementRegExLabel = new Label(regExGroup, SWT.NONE);
		replacementRegExLabel.setText("Replace");
		replacementRegExLabel.setEnabled(false);
		replacementRegExLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true));

		replacementRegExTextField = new Text(regExGroup, SWT.BORDER);
		replacementRegExTextField.setEnabled(false);
		GridData replacementTextFieldsGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		replacementTextFieldsGridData.widthHint = 150;
		replacementRegExTextField.setLayoutData(replacementTextFieldsGridData);

		final Label replacementStringLabel = new Label(regExGroup, SWT.NONE);
		replacementStringLabel.setText("with");
		replacementStringLabel.setEnabled(false);

		replacementStringTextField = new Text(regExGroup, SWT.BORDER);
		replacementStringTextField.setEnabled(false);
		replacementStringTextField.setLayoutData(replacementTextFieldsGridData);

		final Label substringRegExLabel = new Label(regExGroup, SWT.NONE);
		substringRegExLabel.setText("Substring specification");
//		substringRegExLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2,
//				1));
		substringRegExLabel.setEnabled(false);

		substringRegExTextField = new Text(regExGroup, SWT.BORDER);
		substringRegExTextField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 3, 1));
		substringRegExTextField.setEnabled(false);

		useRegExButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// regExGroup.setEnabled(useRegExButton.getSelection());
				replacementRegExTextField.setEnabled(useRegExButton.getSelection());
				replacementStringTextField.setEnabled(useRegExButton.getSelection());
				substringRegExTextField.setEnabled(useRegExButton.getSelection());
				replacementRegExLabel.setEnabled(useRegExButton.getSelection());
				replacementStringLabel.setEnabled(useRegExButton.getSelection());
				substringRegExLabel.setEnabled(useRegExButton.getSelection());
			}
		});

		return parent;
	}

	@Override
	protected void okPressed() {
		String typeName = typeNameTextField.getText();

		if (typeName.isEmpty()) {
			MessageDialog.openError(new Shell(), "Invalid ID type name",
					"Please specify an ID type name");
			return;
		}

		if (idCategoryCombo.getSelectionIndex() == -1) {
			MessageDialog.openError(new Shell(), "Invalid ID category",
					"Please select an ID category");
			return;
		}

		if (dataTypeCombo.getSelectionIndex() == -1) {
			MessageDialog.openError(new Shell(), "Invalid data type",
					"Please select a data type");
			return;
		}

		for (IDCategory category : IDCategory.getAllRegisteredIDCategories()) {
			List<IDType> idTypes = category.getIdTypes();
			for (IDType idType : idTypes) {
				if (idType.getTypeName().equals(typeName)) {

					MessageDialog.openError(new Shell(), "ID type name already exists",
							"Please specify a different ID type name");
					return;
				}
			}
		}

		IDType idType = IDType.registerType(typeName,
				idCategoryMap.get(idCategoryCombo.getSelectionIndex()),
				dataTypeMap.get(dataTypeCombo.getSelectionIndex()));

		if (useRegExButton.getSelection()) {
			IDTypeParsingRules idTypeParsingRules = new IDTypeParsingRules();
			idTypeParsingRules.setReplacementExpression(
					replacementRegExTextField.getText(),
					replacementStringTextField.getText());
			idTypeParsingRules.setSubStringExpression(substringRegExTextField.getText());
			idType.setIdTypeParsingRules(idTypeParsingRules);
		}

		super.okPressed();
	}
}
