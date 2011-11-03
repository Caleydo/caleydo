package org.caleydo.data.importer.tcga.startup;

import java.io.File;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.caleydo.core.util.mapping.color.EDefaultColorSchemes;

/**
 * Generator class that writes the loading information of a series of TCGA data sets to an XML file.
 * 
 * @author Marc Streit
 */
public class TCGATestDataXMLGenerator {

	public static final String DROPBOX_GBM_FOLDER = System.getProperty("user.home")
		+ System.getProperty("file.separator") + "Dropbox/TCGA GDAC/Omics Integration/testdata/20110728/gbm/";

	public static final String MRNA = DROPBOX_GBM_FOLDER + "mrna_cnmf/outputprefix.expclu.gct";
	public static final String MRNA_GROUPING = DROPBOX_GBM_FOLDER + "mrna_cnmf/cnmf.membership.txt";

	public static final String MI_RNA = DROPBOX_GBM_FOLDER + "mir_cnmf/cnmf.normalized.gct";
	public static final String MI_RNA_GROUPING = DROPBOX_GBM_FOLDER + "mir_cnmf/cnmf.membership.txt";

	public static final String METHYLATION = DROPBOX_GBM_FOLDER + "methylation_cnmf/cnmf.normalized.gct";
	public static final String METHYLATION_GROUPING = DROPBOX_GBM_FOLDER
		+ "methylation_cnmf/cnmf.membership.txt";

	public static final String OUTPUT_FILE_PATH = System.getProperty("user.home")
		+ System.getProperty("file.separator") + "tcga_test_data.xml";

	public static void main(String[] args) {
		DataTypeSet mrnaData = new DataTypeSet();
		mrnaData.setName("mRNA data");
		mrnaData.setDataDomainType("org.caleydo.datadomain.genetic");
		mrnaData.setDataPath(MRNA);
		mrnaData.setGroupingPath(MRNA_GROUPING);
		mrnaData.setColorScheme(EDefaultColorSchemes.BLUE_WHITE_RED.name());

		DataTypeSet mirnaData = new DataTypeSet();
		mirnaData.setName("miRNA data");
		mirnaData.setDataDomainType("org.caleydo.datadomain.generic");
		mirnaData.setDataPath(MI_RNA);
		mirnaData.setGroupingPath(MI_RNA_GROUPING);
		mirnaData.setColorScheme(EDefaultColorSchemes.GREEN_WHITE_BROWN.name());

		DataTypeSet methylationData = new DataTypeSet();
		methylationData.setName("Methylation data");
		methylationData.setDataDomainType("org.caleydo.datadomain.genetic");
		methylationData.setDataPath(METHYLATION);
		methylationData.setGroupingPath(METHYLATION_GROUPING);
		methylationData.setColorScheme(EDefaultColorSchemes.GREEN_WHITE_PURPLE.name());

		ArrayList<DataTypeSet> dataTypeSets = new ArrayList<DataTypeSet>();
		dataTypeSets.add(mrnaData);
		dataTypeSets.add(mirnaData);
		dataTypeSets.add(methylationData);

		DataTypeSetCollection dataTypeSetCollection = new DataTypeSetCollection();
		dataTypeSetCollection.setDataTypeSetCollection(dataTypeSets);

		JAXBContext context = null;
		try {
			Class<?>[] serializableClasses = new Class<?>[2];
			serializableClasses[0] = DataTypeSet.class;
			serializableClasses[1] = DataTypeSetCollection.class;
			context = JAXBContext.newInstance(serializableClasses);

			Marshaller marshaller;
			marshaller = context.createMarshaller();
			marshaller.marshal(dataTypeSetCollection, new File(OUTPUT_FILE_PATH));
		}
		catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContexts", ex);
		}
	}
}
