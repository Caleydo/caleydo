package org.caleydo.data.mapping;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MappingBuilder_BioCartaGeneID2Accession {

	private static String BIOCARTA_INPUT_FOLDER_PATH = System.getProperty("user.home")
			+ File.separator + ".caleydo_2.0" + File.separator + "cgap.nci.nih.gov/Genes";

	// private static String OUTPUT_FILE_PATH =
	// "data/genome/mapping/HOMO_SAPIENS_BIOCARTA_GENE_ID_2_REFSEQ_MRNA.txt";
	private static String OUTPUT_FILE_PATH = "data/genome/mapping/MUS_MUSCULUS_BIOCARTA_GENE_ID_2_REFSEQ_MRNA.txt";

	private static String SEARCH_SEQUENCE = "http://www.ncbi.nih.gov/entrez/query.fcgi?db=nucleotide&cmd=search&term=NM_";

	private PrintWriter outputWriter;

	public MappingBuilder_BioCartaGeneID2Accession() throws IOException {

		outputWriter = new PrintWriter(new BufferedWriter(
				new FileWriter(OUTPUT_FILE_PATH), 100000));
		// new PrintWriter(OUTPUT_FILE_PATH);
	}

	public void loadAllFilesInFolder(final String sFolderPath) {

		File folder = new File(sFolderPath);
		File[] arFiles = folder.listFiles();

		for (File arFile : arFiles) {
			searchForAccessionInFile(arFile);
		}
	}

	public void searchForAccessionInFile(final File file) {

		// Ignore mouse genes.
		// ORG=Mm | ORG=Hs
		if (file.getName().contains("ORG=Hs"))
			return;

		try {
			FileInputStream fis = new FileInputStream(file);

			FileChannel fc = fis.getChannel();

			MappedByteBuffer mbf = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());

			byte[] bArTmp = new byte[(int) fc.size()];
			mbf.get(bArTmp);

			String sFileText = new String(bArTmp); // one big string

			while (sFileText.contains(SEARCH_SEQUENCE)) {
				int iStartIndex = sFileText.indexOf(SEARCH_SEQUENCE)
						+ SEARCH_SEQUENCE.length();

				if (iStartIndex == -1)
					return;

				String sAccessionNumber = sFileText.substring(iStartIndex - 3,
						sFileText.indexOf('"', iStartIndex));

				String sBioCartaGeneId = file.getName().substring(
						file.getName().lastIndexOf("BCID=") + 5, file.getName().length());

				appendMappingToFile(sBioCartaGeneId, sAccessionNumber);

				// Remove already parsed file part
				sFileText = sFileText.substring(iStartIndex) + SEARCH_SEQUENCE.length();
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void appendMappingToFile(final String sBioCartaGeneID,
			final String sAccessionNumber) {

		outputWriter.println(sBioCartaGeneID + ";" + sAccessionNumber);
		outputWriter.flush();
	}

	public static void main(String[] args) {

		try {

			MappingBuilder_BioCartaGeneID2Accession mappingBuilder = new MappingBuilder_BioCartaGeneID2Accession();

			mappingBuilder.loadAllFilesInFolder(BIOCARTA_INPUT_FOLDER_PATH);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
