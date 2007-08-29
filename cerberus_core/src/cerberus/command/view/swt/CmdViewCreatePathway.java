package cerberus.command.view.swt;

import java.util.ArrayList;
import java.util.StringTokenizer;

import cerberus.command.CommandQueueSaxType;
import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate_IdTargetLabelParentXY;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.parser.parameter.IParameterHandler;
import cerberus.util.exception.GeneViewRuntimeException;
import cerberus.util.system.StringConversionTool;
import cerberus.view.swt.pathway.Pathway2DViewRep;

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

	protected ArrayList<Integer> iArSetIDs;

	/**
	 * Constructor
	 * 
	 */
	public CmdViewCreatePathway(
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType) {
		
		super(refGeneralManager, 
				refCommandManager,
				refCommandQueueSaxType);
		
		iArSetIDs = new ArrayList<Integer>();
	}

	/**
	 * Method creates a pathway view, sets the attributes 
	 * and calls the init and draw method.
	 */
	public void doCommand() throws GeneViewRuntimeException {	
		
		IViewManager viewManager = ((IViewManager) refGeneralManager
				.getManagerByBaseType(ManagerObjectType.VIEW));
		
		Pathway2DViewRep pathwayView = (Pathway2DViewRep)viewManager
				.createView(ManagerObjectType.VIEW_SWT_PATHWAY,
						iUniqueTargetId,
						iParentContainerId, 
						sLabel,
						iGLCanvasId,
						iGLEventListernerId);
		
		viewManager.registerItem(
				pathwayView, 
				iUniqueTargetId, 
				ManagerObjectType.VIEW);

		int[] iArTmp = new int[iArSetIDs.size()];
		for(int index = 0; index < iArSetIDs.size(); index++)
			iArTmp[index] = iArSetIDs.get(index);
		
		pathwayView.setAttributes(iWidthX, iHeightY, iHTMLBrowserId);
		pathwayView.addSetId(iArTmp);
		pathwayView.initView();
		pathwayView.drawView();
		
		refCommandManager.runDoCommand(this);
	}
	
	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
				
		assert refParameterHandler != null: "ParameterHandler object is null!";	
		
		super.setParameterHandler(refParameterHandler);	
	
		//TODO: load browser ID dynamically
//		refParameterHandler.setValueAndTypeAndDefault("iHTMLBrowserId",
//				refParameterHandler.getValueString( 
//						CommandQueueSaxType.TAG_DETAIL.getXmlKey() ),
//				IParameterHandler.ParameterHandlerType.INT,
//				"-1");
//		
//		iHTMLBrowserId = refParameterHandler.getValueInt("iHTMLBrowserId");
		
		// Read SET IDs (Data and Selection) 
		String sPathwaySets = "";
		refParameterHandler.setValueAndTypeAndDefault("sPathwaySets",
				refParameterHandler.getValueString( 
						CommandQueueSaxType.TAG_DETAIL.getXmlKey() ),
				IParameterHandler.ParameterHandlerType.STRING,
				"-1");
		
		sPathwaySets = refParameterHandler.getValueString("sPathwaySets");
		
		StringTokenizer setToken = new StringTokenizer(
				sPathwaySets,
				IGeneralManager.sDelimiter_Parser_DataItems);

		while (setToken.hasMoreTokens())
		{
			iArSetIDs.add(StringConversionTool.convertStringToInt(
					setToken.nextToken(), -1));
		}
	}

	public void undoCommand() throws GeneViewRuntimeException {
		
		refCommandManager.runUndoCommand(this);
		
	}
}
