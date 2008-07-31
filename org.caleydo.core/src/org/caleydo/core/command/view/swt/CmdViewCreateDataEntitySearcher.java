package org.caleydo.core.command.view.swt;

import java.util.ArrayList;
import java.util.StringTokenizer;
import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelAttrDetail;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.type.EManagerObjectType;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.system.StringConversionTool;
import org.caleydo.core.view.swt.data.search.DataEntitySearcherViewRep;

public class CmdViewCreateDataEntitySearcher
	extends ACmdCreate_IdTargetLabelAttrDetail
{

	private ArrayList<Integer> iAlViewReceiverID;

	public CmdViewCreateDataEntitySearcher(final IGeneralManager generalManager,
			final ICommandManager commandManager, final CommandQueueSaxType commandQueueSaxType)
	{

		super(generalManager, commandManager, commandQueueSaxType);

		iAlViewReceiverID = new ArrayList<Integer>();
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.caleydo.core.command.base.ACmdCreate_IdTargetLabelAttrDetail#
	 * setParameterHandler(org.caleydo.core.parser.parameter.IParameterHandler)
	 */
	public void setParameterHandler(final IParameterHandler parameterHandler)
	{

		assert parameterHandler != null : "ParameterHandler object is null!";

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
				.createView(EManagerObjectType.VIEW_SWT_DATA_ENTITY_SEARCHER, iUniqueId, -1,
						sLabel);

		viewManager.registerItem(dataEntitySearcherView, iUniqueId);

		viewManager.addViewRep(dataEntitySearcherView);

		dataEntitySearcherView.setAttributes(iAlViewReceiverID);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CaleydoRuntimeException
	{

		// TODO Auto-generated method stub

	}

}
