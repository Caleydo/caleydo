/**
 * 
 */
package org.caleydo.datadomain.genetic;

import org.caleydo.core.data.collection.EColumnType;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingCreator;
import org.caleydo.core.id.IDType;
import org.caleydo.core.manager.GeneralManager;

/**
 * Class that triggers the creation of all genetic {@link IDType}s and mappings. 
 * 
 * @author Marc Streit
 *
 */
public class GeneticIDMappingCreator {
	
	public static void createIDTypesAndMapping() {
		
		IDCategory geneIDCategory = IDCategory.registerCategory("GENE");
		IDCategory sampleIDCategory = IDCategory.registerCategory("SAMPLE");
		
		IDType.registerType("DAVID", geneIDCategory, EColumnType.INT);
		IDType.registerType("GENE_NAME", geneIDCategory, EColumnType.STRING);
		IDType geneSymbol = IDType.registerType("GENE_SYMBOL", geneIDCategory,
				EColumnType.STRING);
		geneIDCategory.setHumanReadableIDType(geneSymbol);
		IDType.registerType("BIOCARTA_GENE_ID", geneIDCategory, EColumnType.STRING);
		IDType.registerType("REFSEQ_MRNA", geneIDCategory, EColumnType.STRING);
		IDType.registerType("ENSEMBL_GENE_ID", geneIDCategory, EColumnType.STRING);
		IDType.registerType("ENTREZ_GENE_ID", geneIDCategory, EColumnType.INT);
		IDType.registerType("DAVID", geneIDCategory, EColumnType.INT);
		IDType.registerType("PATHWAY_VERTEX", geneIDCategory, EColumnType.INT);
		IDType.registerType("PATHWAY", geneIDCategory, EColumnType.INT);

		String fileName = "data/genome/mapping/david/"
				+ GeneralManager.get().getBasicInfo().getOrganism();

		IDType.registerType("SAMPLE_INT", sampleIDCategory, EColumnType.INT);
		IDType sampleID = IDType.registerType("SAMPLE", sampleIDCategory, EColumnType.STRING);
		sampleIDCategory.setHumanReadableIDType(sampleID);

		IDMappingCreator idMappingCreator = new IDMappingCreator();

		idMappingCreator.createMapping(fileName + "_DAVID2REFSEQ_MRNA.txt", 0, -1,
				IDType.getIDType("DAVID"), IDType.getIDType("REFSEQ_MRNA"), "\t",
				geneIDCategory, true, true, false, null, null);
		idMappingCreator.createMapping(fileName + "_DAVID2ENTREZ_GENE_ID.txt", 0, -1,
				IDType.getIDType("DAVID"), IDType.getIDType("ENTREZ_GENE_ID"), "\t",
				geneIDCategory, false, true, false, null, null);
		idMappingCreator.createMapping(fileName + "_DAVID2GENE_SYMBOL.txt", 0, -1,
				IDType.getIDType("DAVID"), IDType.getIDType("GENE_SYMBOL"), "\t",
				geneIDCategory, false, true, false, null, null);
		idMappingCreator.createMapping(fileName + "_DAVID2GENE_NAME.txt", 0, -1,
				IDType.getIDType("DAVID"), IDType.getIDType("GENE_NAME"), "\t",
				geneIDCategory, false, true, false, null, null);
		idMappingCreator.createMapping(fileName + "_DAVID2ENSEMBL_GENE_ID.txt", 0, -1,
				IDType.getIDType("DAVID"), IDType.getIDType("ENSEMBL_GENE_ID"), "\t",
				geneIDCategory, false, true, false, null, null);
		idMappingCreator.createMapping("data/genome/mapping/"
				+ GeneralManager.get().getBasicInfo().getOrganism()
				+ "_BIOCARTA_GENE_ID_2_REFSEQ_MRNA.txt", 0, -1,
				IDType.getIDType("BIOCARTA_GENE_ID"), IDType.getIDType("REFSEQ_MRNA"), "\t",
				geneIDCategory, true, true, true, IDType.getIDType("BIOCARTA_GENE_ID"),
				IDType.getIDType("DAVID"));
	}
}
