package org.caleydo.view.glyph.command;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdExternalAttributes;
import org.caleydo.core.manager.ViewManager;
import org.caleydo.view.glyph.GlyphMappingConfigurationViewRep;

/**
 * Class implements the command for creating a view.
 * 
 * @author Sauer Stefan
 */
public class CmdViewCreateGlyphConfiguration
	extends ACmdExternalAttributes {
	int iNumberOfSliders = 1;

	/**
	 * Constructor.
	 */
	public CmdViewCreateGlyphConfiguration(final ECommandType cmdType) {
		super(cmdType);
	}

	@Override
	public void doCommand() {

		ViewManager viewManager = generalManager.getViewGLCanvasManager();

		if (externalID != -1) {
			parentContainerID = generalManager.getIDManager().getInternalFromExternalID(parentContainerID);
		}

		GlyphMappingConfigurationViewRep view =
			(GlyphMappingConfigurationViewRep) viewManager.createView(
				"org.caleydo.view.glyph.mappingconfiguration", parentContainerID, label);

		viewManager.registerItem(view);

		// view.setAttributes(iWidthX, iHeightY, iNumberOfSliders);
		view.initView();
		view.drawView();

		if (externalID != -1) {
			generalManager.getIDManager().mapInternalToExternalID(view.getID(), externalID);
		}

		commandManager.runDoCommand(this);
	}

	@Override
	public void undoCommand() {
		commandManager.runUndoCommand(this);
	}
}
