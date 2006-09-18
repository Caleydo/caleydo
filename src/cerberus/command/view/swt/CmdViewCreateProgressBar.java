package cerberus.command.view.swt;

import java.util.LinkedList;

import cerberus.command.ICommand;
import cerberus.command.base.AcmdCreate_IdTargetLabelParentXY;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.util.system.StringConversionTool;
import cerberus.view.gui.swt.progressbar.ProgressBarViewRep;
import cerberus.xml.parser.parameter.IParameterHandler;

/**
 * Class implementes the command for creating a progress bar view.
 * 
 * @author Marc Streit
 *
 */
public class CmdViewCreateProgressBar 
extends AcmdCreate_IdTargetLabelParentXY 
//ACmdCreateGui 
implements ICommand
{
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
		
		setAttributesProgressBar(refParameterHandler);
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

		progressBarView.setAttributes(refParameterHandler);
		
		progressBarView.extractAttributes();
		progressBarView.retrieveGUIContainer();
		progressBarView.initView();
		progressBarView.drawView();
	}
	
	protected void setAttributesProgressBar( final IParameterHandler refParameterHandler ) {
		
		refParameterHandler.setValueAndType( "iProgressBarCurrentValue",
				sAttribute1,
				IParameterHandler.ParameterHandlerType.INT);
	}

	public void undoCommand() throws CerberusRuntimeException
	{
		// TODO Auto-generated method stub
		
	}
}
