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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Helper tool to generate organism specific mapping files. Original david files
 * are filtered by using a gene whitelist specific to an organism.
 *
 * @author Marc Streit
 */
public class DAVIDWhiteListFilter {
	// private static final String ORGANISM = "HOMO_SAPIENS";
	private static final String ORGANISM = "MUS_MUSCULUS";

	// private static final String FILE_NAME = "DAVID2ENTREZ_GENE_ID.txt";
	// private static final String FILE_NAME = "DAVID2REFSEQ_MRNA.txt";
	// private static final String FILE_NAME = "DAVID2GENE_NAME.txt";
	// private static final String FILE_NAME = "DAVID2GENE_SYMBOL.txt";
	private static final String FILE_NAME = "DAVID2ENSEMBL_GENE_ID.txt";

	protected static void convertData() throws IOException {
		try (PrintWriter writer = new PrintWriter("data/genome/mapping/david/" + ORGANISM + "_" + FILE_NAME)) {

			// Reading input by lines
			BufferedReader in = new BufferedReader(new FileReader("data/genome/mapping/david/" + FILE_NAME));

			String line = "";

			Collection<Integer> alFilter = readAlFilter();

			StringTokenizer tokenizer;
			while ((line = in.readLine()) != null) {
				tokenizer = new StringTokenizer(line, "\t");

				if (alFilter.contains(Integer.valueOf(tokenizer.nextToken())))// &&
				// sInputLine.contains("\tEG"))
				{
					writer.println(line);
					continue;
				}
			}

			in.close();
			writer.flush();
		}
	}

	private static List<Integer> readAlFilter() throws FileNotFoundException, IOException {
		String sFilter;
		try (BufferedReader whitelist = new BufferedReader(new FileReader("data/genome/mapping/david/DAVID_" + ORGANISM
				+ ".txt"))) {
			ArrayList<Integer> alFilter = new ArrayList<Integer>();
			while ((sFilter = whitelist.readLine()) != null) {
				alFilter.add(Integer.valueOf(sFilter));
			}
			return alFilter;
		}
	}

	public static void main(String[] args) {
		try {
			convertData();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}