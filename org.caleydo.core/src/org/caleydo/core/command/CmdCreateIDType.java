package org.caleydo.core.command;

import org.caleydo.core.command.base.ACmdCreational;
import org.caleydo.core.data.collection.EStorageType;
import org.caleydo.core.data.id.IDCategory;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.parser.parameter.ParameterHandler;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Command creates a new ID type.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public class CmdCreateIDType
	extends ACmdCreational<IDType> {

	private String typeName;
	private EStorageType storageType;
	private IDCategory idCategory;

	/**
	 * Constructor.
	 */
	public CmdCreateIDType() {
		super(CommandType.CREATE_ID_TYPE);
	}

	/**
	 * Load data from file using a token pattern.
	 */
	@Override
	public void doCommand() {

		createdObject = IDType.registerType(typeName, idCategory, storageType);

		Logger.log(new Status(IStatus.INFO, this.toString(), "Created ID Type " + createdObject));
	}

	@Override
	public void undoCommand() {
	}

	@Override
	public void setParameterHandler(final ParameterHandler parameterHandler) {
		super.setParameterHandler(parameterHandler);

		typeName = detail;
		idCategory = IDCategory.getIDCategory(attrib1);
		storageType = EStorageType.valueOf(attrib2);

	}

	public void setAttributes(String typeName, IDCategory idCategory, EStorageType storageType) {
		this.typeName = typeName;
		this.idCategory = idCategory;
		this.storageType = storageType;
	}
}
