/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.command;

import org.caleydo.core.command.base.ACmdCreational;
import org.caleydo.core.data.collection.EColumnType;
import org.caleydo.core.data.id.IDCategory;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.io.parser.parameter.ParameterHandler;
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
	private EColumnType dimensionType;
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

		createdObject = IDType.registerType(typeName, idCategory, dimensionType);

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
		dimensionType = EColumnType.valueOf(attrib2);

	}

	public void setAttributes(String typeName, IDCategory idCategory, EColumnType dimensionType) {
		this.typeName = typeName;
		this.idCategory = idCategory;
		this.dimensionType = dimensionType;
	}
}
