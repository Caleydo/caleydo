/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.collection;

import org.caleydo.core.io.parser.ascii.TabularDataParser;

/**
 * The data types currently supported for {@link EDataClass}es trough the {@link TabularDataParser}
 *
 * @author Alexander Lex
 *
 */
public enum EDataType {
	/** Natural Numbers */
	INTEGER,
	/** Single precision real numbers */
	FLOAT,
	/** Text */
	STRING
}
