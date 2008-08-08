package org.caleydo.core.command.view.swt;

import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelParentXY;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.view.swt.glyph.GlyphMappingConfigurationViewRep;

/**
 * Class implements the command for creating a view.
 * 
 * @author Sauer Stefan
 */
public class CmdViewCreateGlyphConfiguration
	extends ACmdCreate_IdTargetLabelParentXY
{
	int iNumberOfSliders = 1;

	/**
	 * Constructor.
	 */
	public CmdViewCreateGlyphConfiguration(final CommandType cmdType)
	{
		super(cmdType);
	}

	/**
	 * Method creates a slider view, sets the attributes and calls the init and
	 * draw method.
	 */
	public void doCommand() throws CaleydoRuntimeException
	{

		IViewManager viewManager = generalManager.getViewGLCanvasManager();

		if (iExternalID != -1)
		{
			iParentContainerId = 
				generalManager.getIDManager().getInternalFromExternalID(iParentContainerId);
		}
		
		GlyphMappingConfigurationViewRep view = (GlyphMappingConfigurationViewRep) viewManager
				.createView(EManagedObjectType.VIEW_SWT_GLYPH_MAPPINGCONFIGURATION, 
						iParentContainerId, sLabel);

		viewManager.registerItem(view);

		// view.setAttributes(iWidthX, iHeightY, iNumberOfSliders);
		view.initView();
		view.drawView();
		
		if (iExternalID != -1)
		{
			generalManager.getIDManager().mapInternalToExternalID(view.getID(), iExternalID);
		}

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
