package org.caleydo.core.command;

import org.caleydo.core.command.base.ACmdCreational;
import org.caleydo.core.data.mapping.IDCategory;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.logging.Logger;
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

		Logger.log(new Status(IStatus.INFO, this.toString(), "Created ID Type " + createdObject));
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
