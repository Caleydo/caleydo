package org.caleydo.core.command;

import org.caleydo.core.command.base.ACmdCreational;
import org.caleydo.core.data.mapping.IDCategory;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Command creates a new ID category.
 * 
 * @author Alexander Lex
 */
public class CmdCreateIDCategory
	extends ACmdCreational<IDCategory> {

	private String categoryName;

	/**
	 * Constructor.
	 */
	public CmdCreateIDCategory(final ECommandType cmdType) {
		super(cmdType);

	}

	/**
	 * Load data from file using a token pattern.
	 */
	public void doCommand() {

		createdObject = IDCategory.registerCategory(categoryName);

		generalManager.getLogger().log(
			new Status(IStatus.INFO, IGeneralManager.PLUGIN_ID, "Created ID Type " + createdObject));

		commandManager.runDoCommand(this);
	}

	@Override
	public void undoCommand() {
		commandManager.runUndoCommand(this);
	}

	@Override
	public void setParameterHandler(final IParameterHandler parameterHandler) {
		super.setParameterHandler(parameterHandler);

		categoryName = detail;

	}

	public void setAttributes(String categoryName) {
		this.categoryName = categoryName;
	}
}
