/**
 * 
 */
package org.caleydo.datadomain.genetic;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.core.io.IDTypeParsingRules;
import org.caleydo.core.io.parser.ascii.IDMappingParser;
import org.caleydo.core.manager.GeneralManager;

/**
 * Class that triggers the creation of all genetic {@link IDType}s and mappings.
 * 
 * @author Marc Streit
 * 
 */
public class GeneticIDMappingCreator {

	public static void createIDTypesAndMapping() {

		IDCategory geneIDCategory = IDCategory.registerCategory(EGeneIDTypes.GENE.name());

		IDType david = IDType.registerType(EGeneIDTypes.DAVID.name(), geneIDCategory,
				EGeneIDTypes.DAVID.getColumnType());
		IDType.registerType(EGeneIDTypes.GENE_NAME.name(), geneIDCategory,
				EGeneIDTypes.GENE_NAME.getColumnType());
		IDType geneSymbol = IDType.registerType(EGeneIDTypes.GENE_SYMBOL.name(),
				geneIDCategory, EGeneIDTypes.GENE_SYMBOL.getColumnType());
		geneIDCategory.setHumanReadableIDType(geneSymbol);
		IDType.registerType(EGeneIDTypes.BIOCARTA_GENE_ID.name(), geneIDCategory,
				EGeneIDTypes.BIOCARTA_GENE_ID.getColumnType());
		IDType.registerType(EGeneIDTypes.REFSEQ_MRNA.name(), geneIDCategory,
				EGeneIDTypes.REFSEQ_MRNA.getColumnType());
		IDType.registerType(EGeneIDTypes.ENSEMBL_GENE_ID.name(), geneIDCategory,
				EGeneIDTypes.ENSEMBL_GENE_ID.getColumnType());
		IDType.registerType(EGeneIDTypes.ENTREZ_GENE_ID.name(), geneIDCategory,
				EGeneIDTypes.ENTREZ_GENE_ID.getColumnType());
		IDType.registerType(EGeneIDTypes.PATHWAY_VERTEX.name(), geneIDCategory,
				EGeneIDTypes.PATHWAY_VERTEX.getColumnType());
		IDType.registerType(EGeneIDTypes.PATHWAY.name(), geneIDCategory,
				EGeneIDTypes.PATHWAY.getColumnType());

		geneIDCategory.setPrimaryMappingType(david);
		geneIDCategory.setHumanReadableIDType(geneSymbol);
		geneIDCategory.setDenomination("gene");

		String fileName = "data/genome/mapping/david/"
				+ GeneralManager.get().getBasicInfo().getOrganism();

		
		IDMappingParser.loadMapping(fileName + "_DAVID2REFSEQ_MRNA.txt", 0, -1,
				IDType.getIDType("DAVID"), IDType.getIDType("REFSEQ_MRNA"), "\t",
				geneIDCategory, true, true, false, null, null);
		IDMappingParser.loadMapping(fileName + "_DAVID2ENTREZ_GENE_ID.txt", 0, -1,
				IDType.getIDType("DAVID"), IDType.getIDType("ENTREZ_GENE_ID"), "\t",
				geneIDCategory, false, true, false, null, null);
		IDMappingParser.loadMapping(fileName + "_DAVID2GENE_SYMBOL.txt", 0, -1,
				IDType.getIDType("DAVID"), IDType.getIDType("GENE_SYMBOL"), "\t",
				geneIDCategory, false, true, false, null, null);
		IDMappingParser.loadMapping(fileName + "_DAVID2GENE_NAME.txt", 0, -1,
				IDType.getIDType("DAVID"), IDType.getIDType("GENE_NAME"), "\t",
				geneIDCategory, false, true, false, null, null);
		IDMappingParser.loadMapping(fileName + "_DAVID2ENSEMBL_GENE_ID.txt", 0, -1,
				IDType.getIDType("DAVID"), IDType.getIDType("ENSEMBL_GENE_ID"), "\t",
				geneIDCategory, false, true, false, null, null);
		IDMappingParser.loadMapping("data/genome/mapping/"
				+ GeneralManager.get().getBasicInfo().getOrganism()
				+ "_BIOCARTA_GENE_ID_2_REFSEQ_MRNA.txt", 0, -1,
				IDType.getIDType("BIOCARTA_GENE_ID"), IDType.getIDType("REFSEQ_MRNA"),
				";", geneIDCategory, true, true, true,
				IDType.getIDType("BIOCARTA_GENE_ID"), IDType.getIDType("DAVID"));

		// ==== SAMPLES ======

		IDCategory sampleIDCategory = IDCategory.registerCategory("SAMPLE");

		IDType sampleID = IDType.registerType("SAMPLE", sampleIDCategory,
				EDataType.STRING);
		sampleIDCategory.setHumanReadableIDType(sampleID);

		IDCategory tcgaSampleIDCategory = IDCategory.registerCategory("TCGA_SAMPLE");

		IDType tcgaSample = IDType.registerType("TCGA_SAMPLE", tcgaSampleIDCategory,
				EDataType.STRING);
		IDTypeParsingRules tcgaIDTypeParsingRules = new IDTypeParsingRules();
		tcgaIDTypeParsingRules.setReplacementExpression("\\.", "-");
		tcgaIDTypeParsingRules.setSubStringExpression("TCGA\\-|\\-...\\-");
		tcgaSample.setIdTypeParsingRules(tcgaIDTypeParsingRules);
		tcgaSampleIDCategory.setHumanReadableIDType(tcgaSample);
	}
}