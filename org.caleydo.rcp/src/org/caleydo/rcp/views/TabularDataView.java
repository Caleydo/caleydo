package org.caleydo.rcp.views;

import org.caleydo.rcp.action.file.FileLoadDataAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class TabularDataView
	extends ViewPart
{
	public static final String ID = "org.caleydo.rcp.views.TabularDataView";

	@Override
	public void createPartControl(Composite parent)
	{	
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		
		GridData gridData = new GridData(GridData.FILL_BOTH);

		Composite upperComposite = new Composite(composite, SWT.NONE);
		upperComposite.setLayout(new FillLayout());
		upperComposite.setLayoutData(gridData);

		final FileLoadDataAction fileLoadDataAction = new FileLoadDataAction(upperComposite);
		fileLoadDataAction.run();
		
		Button applyButton = new Button(composite, SWT.PUSH);
		applyButton.setText("Apply");

		gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gridData.heightHint = 30;
		gridData.widthHint = 500;
		
		applyButton.setLayoutData(gridData);
		
		applyButton.addSelectionListener(new SelectionAdapter() {		
			
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				fileLoadDataAction.execute();
			}
		});
	}

	@Override
	public void setFocus()
	{

	}
	
	@Override
	public void dispose()
	{
		super.dispose();
	}
}
