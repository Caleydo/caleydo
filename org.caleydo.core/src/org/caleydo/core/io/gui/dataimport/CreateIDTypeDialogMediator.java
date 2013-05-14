/**
 *
 */
package org.caleydo.core.io.gui.dataimport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.caleydo.core.data.collection.EDataClass;
import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.core.io.IDTypeParsingRules;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * Mediator for {@link CreateIDTypeDialog}. This class is responsible for setting the states of all widgets of the
 * dialog and triggering actions according to different events that occur in the dialog.
 *
 * @author Christian Partl
 *
 */
public class CreateIDTypeDialogMediator {

	/**
	 * {@link IDCategory} created or used for the creation of the {@link #idType} by the {@link #dialog}.
	 */
	private IDCategory idCategory;

	/**
	 * The {@link IDType} created by the {@link #dialog}.
	 */
	private IDType idType;

	/**
	 * Determines whether an {@link IDCategory} should be created.
	 */
	private boolean createIDCategory = false;

	/**
	 * Maps the index of {@link #dataTypeCombo} to the {@link EDataClass}.
	 */
	private Map<Integer, EDataType> dataTypeMap = new HashMap<Integer, EDataType>();

	/**
	 * The dialog this class serves as mediator for.
	 */
	private CreateIDTypeDialog dialog;

	/**
	 * Parsing rules for the id type.
	 */
	IDTypeParsingRules idTypeParsingRules;

	public CreateIDTypeDialogMediator(CreateIDTypeDialog dialog) {
		this.dialog = dialog;
		createIDCategory = true;
	}

	public CreateIDTypeDialogMediator(CreateIDTypeDialog dialog, IDCategory idCategory) {
		this.dialog = dialog;
		this.idCategory = idCategory;
		createIDCategory = false;
	}

	public void categoryNameTextFieldModified() {
		// if (dialog.typeNameTextField.getText().isEmpty()
		// || dialog.categoryNameTextField.getText().contains(
		// dialog.typeNameTextField.getText())) {
		dialog.typeNameTextField.setText(dialog.categoryNameTextField.getText());
		// }
	}

	/**
	 * Initializes all widgets of the {@link #dialog}. This method should be called after all widgets of the dialog were
	 * created.
	 */
	public void guiCreated() {
		if (!createIDCategory) {
			dialog.categoryNameTextField.setText(idCategory.getCategoryName());
			dialog.categoryNameTextField.setEnabled(false);
		}

		dataTypeMap.put(0, EDataType.INTEGER);
		dataTypeMap.put(1, EDataType.STRING);
		dialog.dataTypeCombo.add("Number");
		dialog.dataTypeCombo.add("Text");

		dialog.idParsingRulesWidget.setEnabled(false);
	}

	/**
	 * Checks if all required fields of the {@link #dialog} are filled. If so the {@link #idCategory} and
	 * {@link #idType} are created.
	 *
	 * @return True, when all required fields were filled, false otherwise.
	 */
	public boolean okPressed() {
		String typeName = dialog.typeNameTextField.getText();
		String categoryName = dialog.categoryNameTextField.getText();

		if (createIDCategory) {
			if (categoryName.isEmpty()) {
				MessageDialog.openError(new Shell(), "Invalid Class Name", "Please specify a class name");
				return false;
			}

			for (IDCategory category : IDCategory.getAllRegisteredIDCategories()) {
				if (category.getCategoryName().equals(categoryName)) {
					MessageDialog.openError(new Shell(), "Class Name Already Exists",
							"Please specify a different class name");
					return false;
				}
			}
		}

		if (typeName.isEmpty()) {
			MessageDialog.openError(new Shell(), "Invalid ID Type Name", "Please specify an ID type name");
			return false;
		}

		if (dialog.dataTypeCombo.getSelectionIndex() == -1) {
			MessageDialog.openError(new Shell(), "Invalid Data Type", "Please select a data type");
			return false;
		}

		for (IDCategory category : IDCategory.getAllRegisteredIDCategories()) {
			List<IDType> idTypes = category.getIdTypes();
			for (IDType idType : idTypes) {
				if (idType.getTypeName().equals(typeName)) {

					MessageDialog.openError(new Shell(), "ID Type Name Already Exists",
							"Please specify a different ID type name");
					return false;
				}
			}
		}

		if (createIDCategory && typeName.equals(categoryName + "_INT")) {
			MessageDialog.openError(new Shell(), "ID Type Name Already Exists",
					"Please specify a different ID type name");
			return false;
		}

		if (dialog.idParsingRulesWidget.isEnabled()) {
			// if (dialog.idParsingRulesWidget.getReplacingExpression() == null
			// && dialog.idParsingRulesWidget.getSubStringExpression() == null) {
			// MessageDialog.openError(new Shell(), "Incomplete Parsing Definition",
			// "At least one expression (replacing or substring) must be specified.");
			// return false;
			// }

			idTypeParsingRules = dialog.idParsingRulesWidget.getIDTypeParsingRules();
			// idTypeParsingRules.setReplacementExpression(dialog.idParsingRulesWidget.getReplacementString(),
			// dialog.idParsingRulesWidget.getReplacingExpression());
			// idTypeParsingRules.setSubStringExpression(dialog.idParsingRulesWidget.getSubStringExpression());
			idTypeParsingRules.setDefault(true);
		}

		if (createIDCategory) {
			idCategory = IDCategory.registerCategory(categoryName);

			// Create primary IDType
			IDType primaryIDType = IDType.registerType(categoryName + "_INT", idCategory, EDataType.INTEGER);
			primaryIDType.setInternalType(true);
			idCategory.setPrimaryMappingType(primaryIDType);
		}

		idType = IDType.registerType(typeName, idCategory, dataTypeMap.get(dialog.dataTypeCombo.getSelectionIndex()));

		if (idTypeParsingRules != null) {
			idType.setIdTypeParsingRules(idTypeParsingRules);
		}

		return true;
	}

	/**
	 * @return the idCategory, see {@link #idCategory}
	 */
	public IDCategory getIdCategory() {
		return idCategory;
	}

	/**
	 * @return the idType, see {@link #idType}
	 */
	public IDType getIdType() {
		return idType;
	}

	/**
	 * @return the createIDCategory, see {@link #createIDCategory}
	 */
	public boolean isCreateIDCategory() {
		return createIDCategory;
	}

	/**
	 * @return the idTypeParsingRules, see {@link #idTypeParsingRules}, if defined, null otherwise.
	 */
	public IDTypeParsingRules getIdTypeParsingRules() {
		return idTypeParsingRules;
	}

}
