package org.caleydo.rcp;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

public class TrimTest
extends WorkbenchWindowControlContribution
{

	@Override
	protected Control createControl(Composite parent)
	{	
		Button test = new Button(parent, SWT.NONE);
		test.setText("TEST");

		return test;
	}
	
	

}
