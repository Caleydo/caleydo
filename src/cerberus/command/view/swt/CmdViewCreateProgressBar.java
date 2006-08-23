package cerberus.command.view.swt;

import java.util.LinkedList;

import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.view.gui.swt.progressbar.ProgressBarViewRep;

/**
 * Class implementes the command for creating a progress bar view.
 * 
 * @author Marc Streit
 *
 */
public class CmdViewCreateProgressBar 
extends ACmdCreate 
implements ICommand 
{
	/**
	 * Constructor
	 * 
	 * @param refGeneralManager
	 * @param listAttributes List of attributes
	 */
	public CmdViewCreateProgressBar( IGeneralManager refGeneralManager,
		final LinkedList <String> listAttributes ) 
	{
		super(refGeneralManager, listAttributes);
	}

	/**
	 * Method creates a progress bar view, sets the attributes 
	 * and calls the init and draw method.
	 */
	public void doCommand() throws CerberusRuntimeException
	{
		IViewManager viewManager = ((IViewManager) refGeneralManager
				.getManagerByBaseType(ManagerObjectType.VIEW));
		
		ProgressBarViewRep progressBarView = (ProgressBarViewRep)viewManager
				.createView(ManagerObjectType.VIEW_SWT_PROGRESS_BAR,
							iCreatedObjectId, 
							iParentContainerId, 
							sLabel);
		
		viewManager.registerItem(
				progressBarView, 
				iCreatedObjectId, 
				ManagerObjectType.VIEW);

		progressBarView.setAttributes(refVecAttributes);
		progressBarView.extractAttributes();
		progressBarView.retrieveGUIContainer();
		progressBarView.initView();
		progressBarView.drawView();
	}
}
