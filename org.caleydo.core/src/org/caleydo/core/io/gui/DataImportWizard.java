/**
 * 
 */
package org.caleydo.core.io.gui;

import org.eclipse.jface.wizard.Wizard;

/**
 * Wizard that guides the user through the different steps of importing a
 * dataset: 1. Dataset Specification, 2. Dataset Transformation, 3. Loading of
 * groupings. The user may finish the import after completing the first step.
 * 
 * @author Christian Partl
 * 
 */
public class DataImportWizard extends Wizard {
	
	@Override
	public void addPages() {
		addPage(new LoadDataSetPage("Specify Dataset"));
	}

	@Override
	public boolean performFinish() {
		
		return true;
	}

}
