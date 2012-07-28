/**
 * 
 */
package org.caleydo.core.io.gui.dataimport;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Dialog that allows the creation of ID categories.
 * 
 * @author Christian Partl
 * 
 */
public class CreateIDCategoryDialog extends Dialog {

	/**
	 * Text field where the user is supposed to specify the name of the
	 * {@link IDCategory} to be created.
	 */
	private Text categoryNameTextField;

	/**
	 * {@link IDCategory} created by this dialog.
	 */
	private IDCategory idCategory;

	/**
	 * @param parentShell
	 */
	public CreateIDCategoryDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Create ID Category");
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayout(new GridLayout(2, false));

		Label categoryIDLabel = new Label(parentComposite, SWT.NONE);
		categoryIDLabel.setText("Category name");

		categoryNameTextField = new Text(parentComposite, SWT.BORDER);
		return parent;
	}

	@Override
	protected void okPressed() {

		String categoryName = categoryNameTextField.getText();
		if (categoryName.isEmpty()) {
			MessageDialog.openError(new Shell(), "Invalid category name",
					"Please specify a category name");
			return;
		}

		for (IDCategory category : IDCategory.getAllRegisteredIDCategories()) {
			if (category.getCategoryName().equals(categoryName)) {
				MessageDialog.openError(new Shell(), "Category name already exists",
						"Please specify a different category name");
				return;
			}
		}

		idCategory = IDCategory.registerCategory(categoryName);

		// Create primary IDType
		IDType primaryIDType = IDType.registerType(categoryName + "_INT", idCategory,
				EDataType.INT);
		primaryIDType.setInternalType(true);
		idCategory.setPrimaryMappingType(primaryIDType);
		
		super.okPressed();
	}

	/**
	 * @return the idCategory, see {@link #idCategory}
	 */
	public IDCategory getIdCategory() {
		return idCategory;
	}

}
