/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.data.importer;

import org.caleydo.core.io.IDTypeParsingRules;
import org.caleydo.core.io.parser.ascii.ATextParser;
import org.caleydo.datadomain.genetic.TCGADefinitions;

/**
 * Test class for regular expressions applied to IDs. Uses the actual code which
 * is also used in caleydo. Can be used for replacement and substring
 * expressions
 *
 * @see{http://docs.oracle.com/javase/6/docs/api/java/util/regex/Pattern.html
 *
 * @author Alexander Lex
 */
public class RegExTester {

	// public static final String TCGA_ID_SUBSTRING_REGEX = "TCGA\\-|\\-01";

	public static void main(String[] args) {



		IDTypeParsingRules idTypeParsingRules = new IDTypeParsingRules();
		idTypeParsingRules.setReplacementExpression(
				TCGADefinitions.TCGA_REPLACEMENT_STRING,
				TCGADefinitions.TCGA_REPLACING_EXPRESSIONS);

		idTypeParsingRules
				.setSubStringExpression(TCGADefinitions.TCGA_ID_SUBSTRING_REGEX);

		int count = 1;
		for (String id : TCGADefinitions.KNOWN_ID_EXAMPLES) {
			String outputString = ATextParser.convertID(id, idTypeParsingRules);

			System.out.println(count++ + ") source : " + id + ",\t converted: "
					+ outputString);
		}
	}
}
