package org.caleydo.core.command.view.swt;

import java.util.ArrayList;
import java.util.StringTokenizer;
import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelParentXY;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.system.StringConversionTool;

/**
 * Class implements the command for creating a pathway view.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdViewCreatePathway
	extends ACmdCreate_IdTargetLabelParentXY
{

	protected ArrayList<Integer> iArSetIDs;

	/**
	 * Constructor
	 */
	public CmdViewCreatePathway(final IGeneralManager generalManager,
			final ICommandManager commandManager, final CommandQueueSaxType commandQueueSaxType)
	{

		super(generalManager, commandManager, commandQueueSaxType);

		iArSetIDs = new ArrayList<Integer>();
	}

	/**
	 * Method creates a pathway view, sets the attributes and calls the init and
	 * draw method.
	 */
	public void doCommand() throws CaleydoRuntimeException
	{

		// IViewManager viewManager = ((IViewManager) generalManager
		// .getManagerByObjectType(ManagerObjectType.VIEW));
		//		
		// Pathway2DViewRep pathwayView = (Pathway2DViewRep)viewManager
		// .createView(ManagerObjectType.VIEW_SWT_PATHWAY,
		// iUniqueId,
		// iParentContainerId,
		// sLabel);
		//		
		// viewManager.registerItem(
		// pathwayView,
		// iUniqueId);
		//
		// int[] iArTmp = new int[iArSetIDs.size()];
		// for(int index = 0; index < iArSetIDs.size(); index++)
		// iArTmp[index] = iArSetIDs.get(index);
		//		
		// pathwayView.setAttributes(iHTMLBrowserId);
		// pathwayView.addSetId(iArTmp);
		// pathwayView.initView();
		// pathwayView.drawView();
		//		
		// commandManager.runDoCommand(this);
	}

	public void setParameterHandler(final IParameterHandler parameterHandler)
	{

		assert parameterHandler != null : "ParameterHandler object is null!";

		super.setParameterHandler(parameterHandler);

		// TODO: load browser ID dynamically
		// parameterHandler.setValueAndTypeAndDefault("iHTMLBrowserId",
		// parameterHandler.getValueString(
		// CommandQueueSaxType.TAG_DETAIL.getXmlKey() ),
		// IParameterHandler.ParameterHandlerType.INT,
		// "-1");
		//		
		// iHTMLBrowserId = parameterHandler.getValueInt("iHTMLBrowserId");

		// Read SET IDs (Data and Selection)
		String sPathwaySets = "";
		parameterHandler.setValueAndTypeAndDefault("sPathwaySets", parameterHandler
				.getValueString(CommandQueueSaxType.TAG_DETAIL.getXmlKey()),
				IParameterHandler.ParameterHandlerType.STRING, "-1");

		sPathwaySets = parameterHandler.getValueString("sPathwaySets");

		StringTokenizer setToken = new StringTokenizer(sPathwaySets,
				IGeneralManager.sDelimiter_Parser_DataItems);

		while (setToken.hasMoreTokens())
		{
			iArSetIDs.add(StringConversionTool.convertStringToInt(setToken.nextToken(), -1));
		}
	}

	public void undoCommand() throws CaleydoRuntimeException
	{

		commandManager.runUndoCommand(this);
	}
}
