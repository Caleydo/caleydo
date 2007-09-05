package org.geneview.rcp.views;

import java.awt.FlowLayout;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.geneview.rcp.Application;

import cerberus.manager.IViewManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.swt.browser.HTMLBrowserViewRep;

public class HTMLBrowserView 
extends ViewPart {

	public static final String ID = "org.geneview.rcp.views.HTMLBrowserView";
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {

		//parent.setLayout(new RowLayout(SWT.VERTICAL));
		
		IViewManager viewManager = ((IViewManager) Application.refGeneralManager
				.getManagerByBaseType(ManagerObjectType.VIEW));
		
		int iUniqueId = 85401;
		
		HTMLBrowserViewRep browserView = (HTMLBrowserViewRep)viewManager
				.createView(ManagerObjectType.VIEW_SWT_BROWSER,
						iUniqueId,
						-1, 
						-1,
						"Browser");
		
		viewManager.registerItem(
				browserView, 
				iUniqueId, 
				ManagerObjectType.VIEW);

		browserView.setAttributes(1000, 800);
		browserView.initViewRCP(parent);
		browserView.drawView();	
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
