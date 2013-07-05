/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.genetic;

import java.util.Arrays;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomainInitialization;
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
public class GeneticDataDomainInitialization implements IDataDomainInitialization {

	private static boolean isAlreadyInitialized = false;

	@Override
	public void createIDTypesAndMapping() {

		if (isAlreadyInitialized)
			return;

		isAlreadyInitialized = true;

		IDCategory geneIDCategory = initGenes();

		loadMapping(geneIDCategory);

		// ==== SAMPLES ======

		initSamples();

		// ==== PATHWAYS ====
		initPathway(geneIDCategory);
		// Trigger pathway loading
		if (!GeneralManager.get().isDryMode())
			DataDomainManager.get().createDataDomain("org.caleydo.datadomain.pathway");
	}

	private static IDCategory initGenes() {
		IDCategory geneIDCategory = IDCategory.registerCategory(EGeneIDTypes.GENE.name());

		// create a bunch of ID Types
		for (EGeneIDTypes type : Arrays.asList(EGeneIDTypes.DAVID, EGeneIDTypes.GENE_SYMBOL, EGeneIDTypes.GENE_NAME,
				EGeneIDTypes.REFSEQ_MRNA,
				EGeneIDTypes.ENSEMBL_GENE_ID, EGeneIDTypes.ENTREZ_GENE_ID)) {
			IDType.registerType(type.name(), geneIDCategory, type.getDataType());
		}

		geneIDCategory.setPrimaryMappingType(IDType.getIDType(EGeneIDTypes.DAVID.name()));
		geneIDCategory.setHumanReadableIDType(IDType.getIDType(EGeneIDTypes.GENE_SYMBOL.name()));
		geneIDCategory.setDenomination("gene");
		return geneIDCategory;
	}

	private static void initPathway(IDCategory geneIDCategory) {
		for (EGeneIDTypes type : Arrays.asList(EGeneIDTypes.PATHWAY, EGeneIDTypes.PATHWAY_VERTEX,
				EGeneIDTypes.PATHWAY_VERTEX_REP)) {
			IDType.registerInternalType(type.name(), geneIDCategory, type.getDataType());
		}
	}

	private static void loadMapping(IDCategory geneIDCategory) {
		String basename = "data/genome/mapping/david/" + GeneticMetaData.getOrganism();

		final IDType david = IDType.getIDType(EGeneIDTypes.DAVID.name());

		IDMappingParser.loadMapping(basename + "_DAVID2REFSEQ_MRNA.txt", 0, -1, david,
				IDType.getIDType(EGeneIDTypes.REFSEQ_MRNA.name()), "\t", geneIDCategory, true, true, false, null, null);
		IDMappingParser.loadMapping(basename + "_DAVID2ENTREZ_GENE_ID.txt", 0, -1, david,
				IDType.getIDType(EGeneIDTypes.ENTREZ_GENE_ID.name()), "\t", geneIDCategory, false, true, false, null,
				null);
		IDMappingParser.loadMapping(basename + "_DAVID2GENE_SYMBOL.txt", 0, -1, david,
						IDType.getIDType(EGeneIDTypes.GENE_SYMBOL.name()), "\t", geneIDCategory, false, true, false,
						null, null);
		IDMappingParser.loadMapping(basename + "_DAVID2GENE_NAME.txt", 0, -1, david,
				IDType.getIDType(EGeneIDTypes.GENE_NAME.name()), "\t", geneIDCategory, false, true, false, null, null);

		if (GeneticMetaData.getOrganism() == Organism.MUS_MUSCULUS) {
			IDMappingParser.loadMapping(basename + "_DAVID2ENSEMBL_GENE_ID.txt", 0, -1, david,
					IDType.getIDType(EGeneIDTypes.ENSEMBL_GENE_ID.name()), "\t", geneIDCategory, false, true, false,
					null, null);
		} else {
			// This is indirection via REFSEQ_MRNA instead of DAVID is needed as
			// we currently do not have a mapping file DAVID2ENSEMBL for home
			// sapiens
			IDMappingParser.loadMapping("data/genome/mapping/" + Organism.HOMO_SAPIENS
					+ "_ENSEMBL_GENE_ID_2_REFSEQ_MRNA.txt", 0, -1,
					IDType.getIDType(EGeneIDTypes.ENSEMBL_GENE_ID.name()),
					IDType.getIDType(EGeneIDTypes.REFSEQ_MRNA.name()), ";", geneIDCategory, true, true, true,
					IDType.getIDType(EGeneIDTypes.ENSEMBL_GENE_ID.name()), david);
		}
	}

	private static void initSamples() {
		IDCategory sampleIDCategory = IDCategory.registerCategory("SAMPLE");

		IDType sampleID = IDType.registerType("SAMPLE", sampleIDCategory, EDataType.STRING);
		sampleIDCategory.setHumanReadableIDType(sampleID);

		IDCategory tcgaSampleIDCategory = IDCategory.registerCategory("TCGA_SAMPLE");

		IDType tcgaSample = IDType.registerType("TCGA_SAMPLE", tcgaSampleIDCategory, EDataType.STRING);
		IDTypeParsingRules tcgaIDTypeParsingRules = new IDTypeParsingRules();
		tcgaIDTypeParsingRules.setReplacementExpression(TCGADefinitions.TCGA_REPLACEMENT_STRING,
				TCGADefinitions.TCGA_REPLACING_EXPRESSIONS);
		tcgaIDTypeParsingRules.setSubStringExpression(TCGADefinitions.TCGA_ID_SUBSTRING_REGEX);
		tcgaIDTypeParsingRules.setDefault(true);
		tcgaSample.setIdTypeParsingRules(tcgaIDTypeParsingRules);

		tcgaSampleIDCategory.setHumanReadableIDType(tcgaSample);
	}

}
