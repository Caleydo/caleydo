package org.caleydo.rcp.util.info;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

public class InfoArea
	extends WorkbenchWindowControlContribution
{
	private ToolTip viewInfoToolTip;
	private ToolTip detailInfoToolTip;
	
	private Text txtViewInfo;
	private Text txtDetailInfo;
	
	@Override
	protected Control createControl(final Composite parent)
	{		
		Font font = new Font(parent.getDisplay(),"Arial",10, SWT.BOLD);
		
		Composite composite = new Composite(parent, SWT.NO_BACKGROUND);
		composite.setLayout(new FillLayout());
		
		Label lblViewInfo = new Label(composite, SWT.CENTER);
		lblViewInfo.setText("View Info:");
		lblViewInfo.setFont(font);   
		
		txtViewInfo = new Text(composite, SWT.NONE);
		txtViewInfo.setText("Some view information");
		txtViewInfo.setBackground(parent.getDisplay()
				.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		txtViewInfo.setEditable(false);

		viewInfoToolTip = new ToolTip(txtViewInfo, "View information.");

		new Label(composite, SWT.SEPARATOR | SWT.VERTICAL);
		
		Label lblDetailInfo = new Label(composite, SWT.NO_BACKGROUND);
		lblDetailInfo.setText("Detail Info:");
		lblDetailInfo.setFont(font);
		
		txtDetailInfo = new Text(composite, SWT.NONE);
		txtDetailInfo.setText("Some detail information");
		txtDetailInfo.setBackground(parent.getDisplay()
				.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		txtDetailInfo.setEditable(false);

		detailInfoToolTip = new ToolTip(txtDetailInfo, "Detailed information.");
		
		return composite;
	}
}
