package org.geneview.core.command.view.swt;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.command.base.ACmdCreate_IdTargetLabelAttrDetail;
import org.geneview.core.manager.ICommandManager;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.IViewManager;
import org.geneview.core.manager.type.ManagerObjectType;
import org.geneview.core.parser.parameter.IParameterHandler;
import org.geneview.core.util.exception.GeneViewRuntimeException;
import org.geneview.core.util.system.StringConversionTool;
import org.geneview.core.view.swt.data.search.DataEntitySearcherViewRep;

public class CmdViewCreateDataEntitySearcher 
extends ACmdCreate_IdTargetLabelAttrDetail{

	private ArrayList<Integer> iAlViewReceiverID;
	
	public CmdViewCreateDataEntitySearcher(final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType) {

		super(refGeneralManager, refCommandManager, refCommandQueueSaxType);
		
		iAlViewReceiverID = new ArrayList<Integer>();
	}

	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.command.base.ACmdCreate_IdTargetLabelAttrDetail#setParameterHandler(org.geneview.core.parser.parameter.IParameterHandler)
	 */
	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		assert refParameterHandler != null: "ParameterHandler object is null!";	
		
		super.setParameterHandler(refParameterHandler);	

		StringTokenizer receiverToken = new StringTokenizer(
				sDetail,
				IGeneralManager.sDelimiter_Parser_DataItems);

		while (receiverToken.hasMoreTokens())
		{
			iAlViewReceiverID.add(StringConversionTool.convertStringToInt(
					receiverToken.nextToken(), -1));
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws GeneViewRuntimeException {

		IViewManager viewManager = ((IViewManager) generalManager
				.getManagerByBaseType(ManagerObjectType.VIEW));
		
		DataEntitySearcherViewRep dataEntitySearcherView = (DataEntitySearcherViewRep)viewManager
			.createView(ManagerObjectType.VIEW_SWT_DATA_ENTITY_SEARCHER,
					iUniqueId, 
					-1,
					sLabel);
		
		viewManager.registerItem(
				dataEntitySearcherView, 
				iUniqueId, 
				ManagerObjectType.VIEW);

		viewManager.addViewRep(dataEntitySearcherView);

		dataEntitySearcherView.setAttributes(iAlViewReceiverID);
	}

	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws GeneViewRuntimeException {

		// TODO Auto-generated method stub
		
	}

}
