package org.caleydo.rcp.dialog.search;

import org.caleydo.core.manager.general.GeneralManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Marc Streit
 */
public class OpenSearchDataEntityDialog
	extends Dialog
{
	private Text searchText;

	private Label resultLabel;

	/**
	 * Constructor.
	 * 
	 * @param parentShell
	 */
	public OpenSearchDataEntityDialog(Shell parentShell)
	{

		super(parentShell);
	}

	@Override
	protected void configureShell(Shell newShell)
	{

		super.configureShell(newShell);
		newShell.setText("Search Dialog");
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{

		Composite composite = new Composite(parent, SWT.NONE);
		RowLayout rowLayout = new RowLayout();
		// rowLayout.fill = true;
		// rowLayout.justify = true;
		// rowLayout.pack = false;
		rowLayout.type = SWT.VERTICAL;
		// rowLayout.wrap = false;
		composite.setLayout(rowLayout);

		Composite searchInputComposite = new Composite(composite, SWT.NONE);
		searchInputComposite.setLayout(new GridLayout(2, true));

		Label searchInputLabel = new Label(searchInputComposite, SWT.NULL);
		searchInputLabel.setText("Enter search phrease:");

		searchText = new Text(searchInputComposite, SWT.BORDER | SWT.SINGLE);
		searchText.setLayoutData(new GridData(GridData.FILL_BOTH));

		resultLabel = new Label(composite, SWT.CENTER);

		composite.pack();

		return composite;
	}

	@Override
	protected void okPressed()
	{

		boolean bFound = GeneralManager.get().getViewGLCanvasManager().getDataEntitySearcher()
				.searchForEntity(searchText.getText());

		if (bFound)
			super.okPressed();
		else
		{
			resultLabel.setText(" NOT FOUND! Try again...");
			resultLabel.setForeground(resultLabel.getDisplay().getSystemColor(SWT.COLOR_RED));
			resultLabel.pack();
		}
	}
}
