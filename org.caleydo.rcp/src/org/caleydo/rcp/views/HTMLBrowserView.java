package org.caleydo.rcp.views;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.view.swt.browser.HTMLBrowserViewRep;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class HTMLBrowserView
	extends ViewPart
{

	public static final String ID = "HTMLBrowserView.view";

	@Override
	public void createPartControl(Composite parent)
	{
//		parent.setLayout(new RowLayout(SWT.VERTICAL));
		
		HTMLBrowserViewRep browserView = (HTMLBrowserViewRep) GeneralManager.get().getViewGLCanvasManager().createView(
				EManagedObjectType.VIEW_SWT_BROWSER_GENOME, -1, "Browser");

		browserView.initViewRCP(parent);
		browserView.drawView();

		GeneralManager.get().getViewGLCanvasManager().registerItem(browserView);
	}

	@Override
	public void setFocus()
	{

	}
}
