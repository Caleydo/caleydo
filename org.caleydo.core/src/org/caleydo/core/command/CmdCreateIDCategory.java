package org.caleydo.core.command;

import org.caleydo.core.command.base.ACmdCreational;
import org.caleydo.core.data.id.IDCategory;
import org.caleydo.core.parser.parameter.ParameterHandler;
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
	public CmdCreateIDCategory() {
		super(CommandType.CREATE_ID_CATEGORY);
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
	public void setParameterHandler(final ParameterHandler parameterHandler) {
		super.setParameterHandler(parameterHandler);

		categoryName = detail;
	}

	public void setAttributes(String categoryName) {
		this.categoryName = categoryName;
	}
}
