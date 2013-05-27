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
package org.caleydo.data.mapping;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

/**
 * Helper class that extracts multiple accession numbers mapped to the same
 * dimension index. separated. EXAMPLE: Input NM_012156.2;NM_177996.1 1770
 * Output: NM_012156.2;1770 NM_177996.1;1770
 * 
 * @author Marc Streit
 */
public class MultipleAccessionCodeExtractor {

	private static final String FILE_EXT = ".csv";

	private static final String INPUT_FILE_NAME = "data/genome/mapping/MUS_MUSCULUS_REFSEQ_MRNA2PANTHER_BP";

	private static final String OUTPUT_FILE_NAME = INPUT_FILE_NAME + "_out";

	protected static char delimiter = ';';

	protected static String multiFieldSeparator = ",";

	protected PrintWriter writer;

	public MultipleAccessionCodeExtractor() throws IOException {

		writer = new PrintWriter(OUTPUT_FILE_NAME + FILE_EXT);
	}

	protected void convertData() throws IOException {

		// Reading input by lines
		BufferedReader in = new BufferedReader(new FileReader(INPUT_FILE_NAME + FILE_EXT));

		String sInputLine = "";
		String sAccessionCodes = "";
		String sMicroarrayExpressionDimensionIndex = "";

		while ((sInputLine = in.readLine()) != null) {
			sAccessionCodes = sInputLine.substring(0, sInputLine.indexOf(delimiter));

			StringTokenizer strTokenText = new StringTokenizer(sAccessionCodes,
					multiFieldSeparator);

			// Nothing todo because there is only one or none accession
			if (strTokenText.countTokens() <= 1) {
				// Write out original input line without modification
				writer.println(sInputLine);
			} else {
				sMicroarrayExpressionDimensionIndex = sInputLine.substring(
						sInputLine.indexOf(delimiter) + 1, sInputLine.length());

				while (strTokenText.hasMoreTokens()) {
					writer.println(strTokenText.nextToken() + delimiter
							+ sMicroarrayExpressionDimensionIndex);
				}
			}
		}

		in.close();

		writer.flush();

	}

	public static void main(String[] args) {

		try {
			MultipleAccessionCodeExtractor enzymeCodeConverter = new MultipleAccessionCodeExtractor();

			enzymeCodeConverter.convertData();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}