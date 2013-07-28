/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.genetic;

import java.util.Arrays;
import java.util.regex.Pattern;

import org.caleydo.core.io.IDSpecification;
import org.caleydo.core.io.IDTypeParsingRules;

/**
 *
 * Known source ID formats covered by correctly applying these expressions:
 *
 * Known source ID formats not covered correctly:
 *
 * <li>OV_20_0990 - known expression: "^[a-z]+\\-",
 * setReplacementExpression("\\_", "-");</li>
 *
 * @author Alexander Lex
 *
 */
public class TCGADefinitions {

	public static final String[] KNOWN_ID_EXAMPLES = { "TCGA-06-0171-02",
			"tcga-06-0125-02", "TCGA-02-0003-01A-01R-0177-01",
 "TCGA-02-0004-01A-21-1898-20", "OV_20_0990", "tcga-a2-a04r",
			"tcga-ab-1234-01a" };

	// tcga\\-|TCGA\\-|^[a-zA-Z]|\\-..\\z
	public static final String TCGA_ID_SUBSTRING_REGEX = "^[a-zA-Z]*\\-|\\-..\\z|\\-...\\-|\\-...\\z";
	public static final String[] TCGA_REPLACING_EXPRESSIONS = { "\\.", "\\_" };
	public static final String TCGA_REPLACEMENT_STRING = "-";

	public static IDSpecification createSampleIDSpecification(boolean isDefault) {
		IDTypeParsingRules rule = new IDTypeParsingRules();
		// split by that expression and take the first element
		rule.setSubStringExpression(TCGA_ID_SUBSTRING_REGEX);
		// replace all . and _ with -
		rule.setReplacementExpression(TCGA_REPLACEMENT_STRING, TCGA_REPLACING_EXPRESSIONS);
		rule.setToUpperCase(true);
		rule.setDefault(isDefault);

		IDSpecification id = new IDSpecification("TCGA_SAMPLE", "TCGA_SAMPLE");
		id.setIdTypeParsingRules(rule);
		return id;
	}

	public static IDSpecification createGeneIDSpecificiation() {
		IDTypeParsingRules rule = new IDTypeParsingRules();
		rule.setSubStringExpression(Pattern.quote("|")); // using the first element before the | separator
		rule.setDefault(false);

		IDSpecification geneIDSpecification = new IDSpecification();
		geneIDSpecification.setIDTypeGene(true);
		geneIDSpecification.setIdType("GENE_SYMBOL");
		geneIDSpecification.setIdTypeParsingRules(rule);
		return geneIDSpecification;
	}

	public static void main(String[] args) {
		System.out.println(Arrays.toString("PITX2|5308".split(Pattern.quote("|"))));
	}
}
