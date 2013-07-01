/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.data.mapping;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

/**
 * Helper class converts enzyme codes that are given space separated. EXAMPLE:
 * Input 2;1.2.3 2.4.5 Output: 2;1.2.3 2;2.4.5
 * 
 * @author Marc Streit
 */
public class EnzymeCodeConverter {

	protected static char cEzymeBeginDelimiter = ';';

	protected static String sEnzymeSeparator = " ";

	protected PrintWriter writer_GENE_ID_2_ENZYME_CODE;

	public EnzymeCodeConverter() throws IOException {

		writer_GENE_ID_2_ENZYME_CODE = new PrintWriter(
				"data/MicroarrayData/mapping/gene_id_2_enzyme_code.map");
	}

	protected void convertData() throws IOException {

		// Reading input by lines
		BufferedReader in = new BufferedReader(new FileReader(
				"data/MicroarrayData/mapping/gene_id_2_enzyme_code_ORIG.map"));

		String sInputLine = "";
		String sConvertedInputLine = "";
		int iStartIndex = 0;

		while ((sInputLine = in.readLine()) != null) {
			iStartIndex = sInputLine.lastIndexOf(cEzymeBeginDelimiter);
			sConvertedInputLine = sInputLine.substring(iStartIndex + 1);

			StringTokenizer strTokenText = new StringTokenizer(sConvertedInputLine,
					sEnzymeSeparator);

			// Nothing todo because there is only one or none enzyme
			if (strTokenText.countTokens() <= 1) {
				// Write out original input line without modification
				writer_GENE_ID_2_ENZYME_CODE.println(sInputLine);
			} else {
				sConvertedInputLine = sInputLine.substring(0, iStartIndex + 1);

				while (strTokenText.hasMoreTokens()) {
					writer_GENE_ID_2_ENZYME_CODE.println(sConvertedInputLine
							+ strTokenText.nextToken());
				}
			}
		}

		in.close();

		writer_GENE_ID_2_ENZYME_CODE.flush();

	}

	public static void main(String[] args) {

		try {
			EnzymeCodeConverter enzymeCodeConverter = new EnzymeCodeConverter();

			enzymeCodeConverter.convertData();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
