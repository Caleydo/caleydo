/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.datadomain.genetic;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomainInitialization;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.core.io.IDTypeParsingRules;
import org.caleydo.core.io.parser.ascii.IDMappingParser;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.specialized.Organism;

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

		IDCategory geneIDCategory = IDCategory.registerCategory(EGeneIDTypes.GENE.name());

		IDType david = IDType.registerType(EGeneIDTypes.DAVID.name(), geneIDCategory, EGeneIDTypes.DAVID.getDataType());
		IDType.registerType(EGeneIDTypes.GENE_NAME.name(), geneIDCategory, EGeneIDTypes.GENE_NAME.getDataType());
		IDType geneSymbol = IDType.registerType(EGeneIDTypes.GENE_SYMBOL.name(), geneIDCategory,
				EGeneIDTypes.GENE_SYMBOL.getDataType());
		geneIDCategory.setHumanReadableIDType(geneSymbol);
		IDType.registerType(EGeneIDTypes.REFSEQ_MRNA.name(), geneIDCategory, EGeneIDTypes.REFSEQ_MRNA.getDataType());
		IDType.registerType(EGeneIDTypes.ENSEMBL_GENE_ID.name(), geneIDCategory,
				EGeneIDTypes.ENSEMBL_GENE_ID.getDataType());
		IDType.registerType(EGeneIDTypes.ENTREZ_GENE_ID.name(), geneIDCategory,
				EGeneIDTypes.ENTREZ_GENE_ID.getDataType());
		IDType.registerType(EGeneIDTypes.PATHWAY.name(), geneIDCategory, EGeneIDTypes.PATHWAY.getDataType());
		IDType.registerType(EGeneIDTypes.PATHWAY_VERTEX.name(), geneIDCategory,
				EGeneIDTypes.PATHWAY_VERTEX.getDataType());
		IDType.registerType(EGeneIDTypes.PATHWAY_VERTEX_REP.name(), geneIDCategory,
				EGeneIDTypes.PATHWAY_VERTEX_REP.getDataType());

		geneIDCategory.setPrimaryMappingType(david);
		geneIDCategory.setHumanReadableIDType(geneSymbol);
		geneIDCategory.setDenomination("gene");

		String fileName = "data/genome/mapping/david/" + GeneralManager.get().getBasicInfo().getOrganism();

		IDMappingParser.loadMapping(fileName + "_DAVID2REFSEQ_MRNA.txt", 0, -1, IDType.getIDType("DAVID"),
				IDType.getIDType("REFSEQ_MRNA"), "\t", geneIDCategory, true, true, false, null, null);
		IDMappingParser.loadMapping(fileName + "_DAVID2ENTREZ_GENE_ID.txt", 0, -1, IDType.getIDType("DAVID"),
				IDType.getIDType("ENTREZ_GENE_ID"), "\t", geneIDCategory, false, true, false, null, null);
		IDMappingParser.loadMapping(fileName + "_DAVID2GENE_SYMBOL.txt", 0, -1, IDType.getIDType("DAVID"),
				IDType.getIDType("GENE_SYMBOL"), "\t", geneIDCategory, false, true, false, null, null);
		IDMappingParser.loadMapping(fileName + "_DAVID2GENE_NAME.txt", 0, -1, IDType.getIDType("DAVID"),
				IDType.getIDType("GENE_NAME"), "\t", geneIDCategory, false, true, false, null, null);

		if (GeneralManager.get().getBasicInfo().getOrganism() == Organism.MUS_MUSCULUS) {
			IDMappingParser.loadMapping(fileName + "_DAVID2ENSEMBL_GENE_ID.txt", 0, -1, IDType.getIDType("DAVID"),
					IDType.getIDType("ENSEMBL_GENE_ID"), "\t", geneIDCategory, false, true, false, null, null);
		} else {
			// This is indirection via REFSEQ_MRNA instead of DAVID is needed as
			// we currently do not have a mapping file DAVID2ENSEMBL for home
			// sapiens
			IDMappingParser.loadMapping("data/genome/mapping/" + Organism.HOMO_SAPIENS
					+ "_ENSEMBL_GENE_ID_2_REFSEQ_MRNA.txt", 0, -1, IDType.getIDType("ENSEMBL_GENE_ID"),
					IDType.getIDType("REFSEQ_MRNA"), ";", geneIDCategory, true, true, true,
					IDType.getIDType("ENSEMBL_GENE_ID"), IDType.getIDType("DAVID"));
		}

		// ==== SAMPLES ======

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

		// Trigger pathway loading
		if (!GeneralManager.get().isDryMode())
			DataDomainManager.get().createDataDomain("org.caleydo.datadomain.pathway");
	}

}
