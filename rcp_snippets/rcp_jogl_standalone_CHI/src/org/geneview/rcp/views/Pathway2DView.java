package org.geneview.rcp.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.geneview.rcp.Application;

import cerberus.view.IView;

public class Pathway2DView extends ViewPart {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {

		IView pathway2DView = 
			(IView)Application.refGeneralManager.getSingelton().getViewGLCanvasManager().getItem(29);

		//pathway2DView.setParentContainerId(iParentContainerId)
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
