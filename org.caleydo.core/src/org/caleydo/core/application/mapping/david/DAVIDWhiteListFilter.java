package org.caleydo.core.application.mapping.david;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.caleydo.core.util.system.StringConversionTool;

/**
 * @author Marc Streit
 */
public class DAVIDWhiteListFilter
{

	protected PrintWriter writer;

	// private static final String FILE_NAME = "DAVID2ENTREZ_GENE_ID.txt";
	// private static final String FILE_NAME = "DAVID2REFSEQ_MRNA.txt";
	// private static final String FILE_NAME = "DAVID2GENE_NAME.txt";
	private static final String FILE_NAME = "DAVID2GOTERM_CC_ALL.txt";

	public DAVIDWhiteListFilter()
		throws IOException
	{

		writer = new PrintWriter("data/genome/mapping/david/HOMO_SAPIENS_" + FILE_NAME);
	}

	protected void convertData() throws IOException
	{

		// Reading input by lines
		BufferedReader in = new BufferedReader(new FileReader("data/genome/mapping/david/"
				+ FILE_NAME));

		String sInputLine = "";
		String sFilter = "";

		BufferedReader whitelist = new BufferedReader(new FileReader(
				"data/genome/mapping/david/DAVID_HOMO_SAPIENS.txt"));

		ArrayList<Integer> alFilter = new ArrayList<Integer>();

		while ((sFilter = whitelist.readLine()) != null)
		{
			alFilter.add(StringConversionTool.convertStringToInt(sFilter, -1));
		}
		whitelist.close();

		StringTokenizer tokenizer;
		while ((sInputLine = in.readLine()) != null)
		{
			tokenizer = new StringTokenizer(sInputLine, "\t");

			if (alFilter.contains(StringConversionTool.convertStringToInt(tokenizer
					.nextToken(), -1)))// &&
			// sInputLine.contains("\tEG"))
			{
				writer.println(sInputLine);
				continue;
			}
		}

		in.close();
		writer.flush();
		writer.close();
	}

	public static void main(String[] args)
	{

		try
		{
			DAVIDWhiteListFilter whitelistFilter = new DAVIDWhiteListFilter();

			whitelistFilter.convertData();

		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
}