package org.caleydo.core.command.view.swt;

import java.util.ArrayList;
import java.util.StringTokenizer;
import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelAttrDetail;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.system.StringConversionTool;
import org.caleydo.core.view.swt.data.search.DataEntitySearcherViewRep;

public class CmdViewCreateDataEntitySearcher
	extends ACmdCreate_IdTargetLabelAttrDetail
{

	private ArrayList<Integer> iAlViewReceiverID;

	/**
	 * Constructor.
	 */
	public CmdViewCreateDataEntitySearcher(final CommandType cmdType)
	{

		super(cmdType);

		iAlViewReceiverID = new ArrayList<Integer>();
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.base.ACmdCreate_IdTargetLabelAttrDetail#setParameterHandler(org.caleydo.core.parser.parameter.IParameterHandler)
	 */
	public void setParameterHandler(final IParameterHandler parameterHandler)
	{
		super.setParameterHandler(parameterHandler);

		StringTokenizer receiverToken = new StringTokenizer(sDetail,
				IGeneralManager.sDelimiter_Parser_DataItems);

		while (receiverToken.hasMoreTokens())
		{
			iAlViewReceiverID.add(StringConversionTool.convertStringToInt(receiverToken
					.nextToken(), -1));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws CaleydoRuntimeException
	{
		IViewManager viewManager = generalManager.getViewGLCanvasManager();

		DataEntitySearcherViewRep dataEntitySearcherView = (DataEntitySearcherViewRep) viewManager
				.createView(EManagedObjectType.VIEW_SWT_DATA_ENTITY_SEARCHER, -1,
						sLabel);

		viewManager.registerItem(dataEntitySearcherView);

		viewManager.addViewRep(dataEntitySearcherView);

		dataEntitySearcherView.setAttributes(iAlViewReceiverID);
		
		commandManager.runDoCommand(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CaleydoRuntimeException
	{
		commandManager.runUndoCommand(this);
	}
}
