/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
