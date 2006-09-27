package cerberus.command.view.swt;

import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate_IdTargetLabelParentXY;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.view.gui.swt.progressbar.ProgressBarViewRep;
import cerberus.xml.parser.command.CommandQueueSaxType;
import cerberus.xml.parser.parameter.IParameterHandler;

/**
 * Class implementes the command for creating a progress bar view.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class CmdViewCreateProgressBar extends ACmdCreate_IdTargetLabelParentXY implements ICommand {
	/**
	 * Constructor
	 * 
	 * @param refGeneralManager
	 * @param listAttributes List of attributes
	 */
	public CmdViewCreateProgressBar( IGeneralManager refGeneralManager,
			final IParameterHandler refParameterHandler ) 
	{
		super(refGeneralManager, refParameterHandler);
		
		setAttributes(refParameterHandler);
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
							iUniqueTargetId, 
							iParentContainerId, 
							sLabel);
		
		viewManager.registerItem(
				progressBarView, 
				iUniqueTargetId, 
				ManagerObjectType.VIEW);

		progressBarView.readInAttributes(refParameterHandler);
		
		progressBarView.extractAttributes();
		progressBarView.retrieveGUIContainer();
		progressBarView.initView();
		progressBarView.drawView();
	}
	
	protected void setAttributes( final IParameterHandler refParameterHandler ) {
		
		refParameterHandler.setValueAndTypeAndDefault( "iProgressBarCurrentValue",
				refParameterHandler.getValueString( 
						CommandQueueSaxType.TAG_DETAIL.getXmlKey() ),
				IParameterHandler.ParameterHandlerType.INT,
				"0");
	}

	public void undoCommand() throws CerberusRuntimeException
	{
		// TODO Auto-generated method stub
		
	}
}
