package org.caleydo.core.command;

import org.caleydo.core.command.base.ACmdCreational;
import org.caleydo.core.data.mapping.IDCategory;
import org.caleydo.core.manager.GeneralManager;
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
	@Override
	public void doCommand() {

		createdObject = IDCategory.registerCategory(categoryName);

		generalManager.getLogger().log(
			new Status(IStatus.INFO, GeneralManager.PLUGIN_ID, "Created ID Type " + createdObject));
	}

	@Override
	public void undoCommand() {
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
