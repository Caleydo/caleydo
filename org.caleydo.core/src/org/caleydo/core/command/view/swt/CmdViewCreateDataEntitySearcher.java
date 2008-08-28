package org.caleydo.core.command.view.swt;

import java.util.ArrayList;
import java.util.StringTokenizer;
import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdExternalAttributes;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.system.StringConversionTool;
import org.caleydo.core.view.swt.data.search.DataEntitySearcherViewRep;

public class CmdViewCreateDataEntitySearcher
	extends ACmdExternalAttributes
{

	private ArrayList<Integer> iAlViewReceiverID;

	/**
	 * Constructor.
	 */
	public CmdViewCreateDataEntitySearcher(final ECommandType cmdType)
	{

		super(cmdType);

		iAlViewReceiverID = new ArrayList<Integer>();
	}

	@Override
	public void setParameterHandler(final IParameterHandler parameterHandler)
	{
		super.setParameterHandler(parameterHandler);

		StringTokenizer receiverToken = new StringTokenizer(sDetail,
				IGeneralManager.sDelimiter_Parser_DataItems);

		int iReceiverID = -1;
		while (receiverToken.hasMoreTokens())
		{
			iReceiverID = StringConversionTool.convertStringToInt(receiverToken.nextToken(),
					-1);

			if (iReceiverID != -1)
				iAlViewReceiverID.add(generalManager.getIDManager().getInternalFromExternalID(
						iReceiverID));
		}
	}

	@Override
	public void doCommand() throws CaleydoRuntimeException
	{
		IViewManager viewManager = generalManager.getViewGLCanvasManager();

		DataEntitySearcherViewRep dataEntitySearcherView = (DataEntitySearcherViewRep) viewManager
				.createView(EManagedObjectType.VIEW_SWT_DATA_ENTITY_SEARCHER, -1, sLabel);

		viewManager.registerItem(dataEntitySearcherView);
		viewManager.addViewRep(dataEntitySearcherView);

		dataEntitySearcherView.setAttributes(iAlViewReceiverID);

		commandManager.runDoCommand(this);
	}

	@Override
	public void undoCommand() throws CaleydoRuntimeException
	{
		commandManager.runUndoCommand(this);
	}
}
