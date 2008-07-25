package org.caleydo.rcp.wizard.project;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * 1st wizard page: The user have to choose if she
 * wants to create a new project or load an existing one.
 * 
 * @author Marc Streit
 *
 */
public class NewOrExistingProjectPage 
extends WizardPage {
	
	public static final String PAGE_NAME = "New or existing project?";

	private Label textLabel;

	/**
	 * Constructor.
	 */
	public NewOrExistingProjectPage() {
		
		super(PAGE_NAME, PAGE_NAME, null);
		
		this.setImageDescriptor(ImageDescriptor.createFromImageData(
				new ImageData("splash.png")));
	}

	public void createControl(Composite parent) {
		Composite topLevel = new Composite(parent, SWT.NONE);
		topLevel.setLayout(new FillLayout());
		
//		textLabel = new Label(topLevel, SWT.CENTER);
//		textLabel.setText("HUHU");

		setControl(topLevel);
		setPageComplete(true);
	}
}
