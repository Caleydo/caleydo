package cerberus.command.view.swt;

import cerberus.command.CommandQueueSaxType;
import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate_IdTargetLabelParentXY;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.view.gui.swt.pathway.Pathway2DViewRep;
import cerberus.xml.parser.parameter.IParameterHandler;

/**
 * Class implementes the command for creating a pathway view.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class CmdViewCreatePathway 
extends ACmdCreate_IdTargetLabelParentXY 
implements ICommand {
	
	protected int iHTMLBrowserId = 0;
	
	protected int iPathwaySetId = 0;
	
	/**
	 * Constructor
	 * 
	 * @param refGeneralManager
	 */
	public CmdViewCreatePathway( 
			final IGeneralManager refGeneralManager) {
		
		super(refGeneralManager);
	}

	/**
	 * Method creates a pathway view, sets the attributes 
	 * and calls the init and draw method.
	 */
	public void doCommand() throws CerberusRuntimeException {	
		
		IViewManager viewManager = ((IViewManager) refGeneralManager
				.getManagerByBaseType(ManagerObjectType.VIEW));
		
		Pathway2DViewRep pathwayView = (Pathway2DViewRep)viewManager
				.createView(ManagerObjectType.VIEW_SWT_PATHWAY,
						iUniqueTargetId,
						iParentContainerId, 
						sLabel);
		
		viewManager.registerItem(
				pathwayView, 
				iUniqueTargetId, 
				ManagerObjectType.VIEW);

		pathwayView.setAttributes(iWidthX, iHeightY, iPathwaySetId, iHTMLBrowserId);
		pathwayView.retrieveGUIContainer();
		pathwayView.initView();
		pathwayView.drawView();
		
		refCommandManager.runDoCommand(this);
	}
	
	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
				
		assert refParameterHandler != null: "ParameterHandler object is null!";	
		
		super.setParameterHandler(refParameterHandler);	
		
		refParameterHandler.setValueAndTypeAndDefault("iHTMLBrowserId",
				refParameterHandler.getValueString( 
						CommandQueueSaxType.TAG_DETAIL.getXmlKey() ),
				IParameterHandler.ParameterHandlerType.INT,
				"-1");
		
		iHTMLBrowserId = refParameterHandler.getValueInt("iHTMLBrowserId");
		
		// Read SET ID 
		refParameterHandler.setValueAndTypeAndDefault("iPathwaySetId",
				refParameterHandler.getValueString( 
						CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey() ),
				IParameterHandler.ParameterHandlerType.INT,
				"-1");
		
		iPathwaySetId = refParameterHandler.getValueInt("iPathwaySetId");
	}

	public void undoCommand() throws CerberusRuntimeException {
		
		refCommandManager.runUndoCommand(this);
		
	}
}
