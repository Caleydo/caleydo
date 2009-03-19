package org.caleydo.core.command.view.swt;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdCreational;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.view.swt.data.search.DataEntitySearcherViewRep;

public class CmdViewCreateDataEntitySearcher
	extends ACmdCreational<DataEntitySearcherViewRep> {
	/**
	 * Constructor.
	 */
	public CmdViewCreateDataEntitySearcher(final ECommandType cmdType) {
		super(cmdType);
	}

	@Override
	public void setParameterHandler(final IParameterHandler parameterHandler) {
		super.setParameterHandler(parameterHandler);
	}

	@Override
	public void doCommand() {
		IViewManager viewManager = generalManager.getViewGLCanvasManager();
		createdObject =
			(DataEntitySearcherViewRep) viewManager.createView(
				EManagedObjectType.VIEW_SWT_DATA_ENTITY_SEARCHER, -1, sLabel);
		viewManager.registerItem(createdObject);

		commandManager.runDoCommand(this);
	}

	@Override
	public void undoCommand() {
		commandManager.runUndoCommand(this);
	}
}
