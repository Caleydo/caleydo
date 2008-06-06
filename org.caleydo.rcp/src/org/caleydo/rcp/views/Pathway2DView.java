package org.caleydo.rcp.views;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.caleydo.rcp.Application;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.event.CmdEventCreateMediator;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.IEventPublisher.MediatorType;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.view.swt.pathway.Pathway2DViewRep;

public class Pathway2DView 
extends ViewPart {

	public static final String ID = "org.caleydo.rcp.views.Pathway2DView";
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {

//		IViewManager viewManager = ((IViewManager) Application.generalManager
//				.getManagerByObjectType(ManagerObjectType.VIEW));
//		
//		int iUniqueId = 80401;
//		
//		Pathway2DViewRep pathwayView = (Pathway2DViewRep)viewManager
//				.createView(ManagerObjectType.VIEW_SWT_PATHWAY,
//						iUniqueId,
//						-1, 
//						"Pathway 2D");
//		
//		viewManager.registerItem(
//				pathwayView, 
//				iUniqueId, 
//				ManagerObjectType.VIEW);
//
//		
//		ArrayList<Integer> iArSetIDs = new ArrayList<Integer>();
//		iArSetIDs.add(49101);
//		iArSetIDs.add(95101);
//		
//		int[] iArTmp = new int[iArSetIDs.size()];
//		for(int index = 0; index < iArSetIDs.size(); index++)
//			iArTmp[index] = iArSetIDs.get(index);
//		
//		pathwayView.setAttributes(85401);
//		pathwayView.addSetId(iArTmp);
//		pathwayView.initViewRCP(parent);
//		pathwayView.drawView();	
		
//		ArrayList<Integer> iAlSender = new ArrayList<Integer>();
//		ArrayList<Integer> iAlReceiver = new ArrayList<Integer>();
//		iAlSender.add(iUniqueId);
//		iAlReceiver.add(82401); // GLJukeboxPathway3D
//		iAlReceiver.add(83401);
		
//		// Connect 2D pathway with 3D pathway
//		CmdEventCreateMediator cmd = (CmdEventCreateMediator)Application.generalManager.getSingelton().getCommandManager()
//		 	.createCommandByType(CommandQueueSaxType.CREATE_EVENT_MEDIATOR);
//		cmd.setAttributes(-1, iAlSender, iAlReceiver, MediatorType.SELECTION_MEDIATOR);
//		cmd.doCommand();
//
//		CmdEventCreateMediator cmdReverse = (CmdEventCreateMediator)Application.generalManager.getSingelton().getCommandManager()
//	 		.createCommandByType(CommandQueueSaxType.CREATE_EVENT_MEDIATOR);
//		cmdReverse.setAttributes(-1, iAlReceiver, iAlSender, MediatorType.SELECTION_MEDIATOR);
//		cmdReverse.doCommand();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
