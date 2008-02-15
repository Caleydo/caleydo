package org.geneview.rcp.views;

//import java.awt.FlowLayout;
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.layout.FillLayout;
//import org.eclipse.swt.layout.RowLayout;
import java.util.ArrayList;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.geneview.rcp.Application;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.command.event.CmdEventCreateMediator;
import org.geneview.core.manager.IViewManager;
import org.geneview.core.manager.IEventPublisher.MediatorType;
import org.geneview.core.manager.type.ManagerObjectType;
import org.geneview.core.view.swt.browser.HTMLBrowserViewRep;

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
						"Browser");
		
		viewManager.registerItem(
				browserView, 
				iUniqueId, 
				ManagerObjectType.VIEW);

		browserView.setAttributes(1000, 800);
		browserView.initViewRCP(parent);
		browserView.drawView();	
		
		ArrayList<Integer> iAlSender = new ArrayList<Integer>();
		ArrayList<Integer> iAlReceiver = new ArrayList<Integer>();
		iAlSender.add(iUniqueId);
		//iAlReceiver.add(85401); 
		iAlReceiver.add(84401);
		iAlReceiver.add(82401);
		
		// Connect browser to 2D pathway
		CmdEventCreateMediator cmd = (CmdEventCreateMediator)Application.refGeneralManager.getSingelton().getCommandManager()
		 	.createCommandByType(CommandQueueSaxType.CREATE_EVENT_MEDIATOR);
		cmd.setAttributes(-1, iAlSender, iAlReceiver, MediatorType.SELECTION_MEDIATOR);
		cmd.doCommand();

		CmdEventCreateMediator cmdReverse = (CmdEventCreateMediator)Application.refGeneralManager.getSingelton().getCommandManager()
	 		.createCommandByType(CommandQueueSaxType.CREATE_EVENT_MEDIATOR);
		cmdReverse.setAttributes(-1, iAlReceiver, iAlSender, MediatorType.SELECTION_MEDIATOR);
		cmdReverse.doCommand();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
