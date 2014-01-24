/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.genetic;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomainInitialization;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.core.io.IDTypeParsingRules;
import org.caleydo.core.io.parser.ascii.IDMappingParser;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ZipUtils;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.util.system.RemoteFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * Class that triggers the creation of all genetic {@link IDType}s and mappings.
 *
 * @author Marc Streit
 *
 */
public class GeneticDataDomainInitialization implements IDataDomainInitialization, IRunnableWithProgress {
	private static final String URL_PATTERN = GeneralManager.DATA_URL_PREFIX + "mappings/%s.zip";
	private static final Logger log = Logger.create(GeneticDataDomainInitialization.class);
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
				EGeneIDTypes.REFSEQ_MRNA, EGeneIDTypes.ENSEMBL_GENE_ID, EGeneIDTypes.ENTREZ_GENE_ID
		/*
		 * , EGeneIDTypes.COMPOUND_ID, EGeneIDTypes.INTERACTION_ID, EGeneIDTypes.FINGERPRINT_ID
		 */)) {
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
		File base = prepareFile(new NullProgressMonitor());
		if (base == null)
			return;

		final IDType david = IDType.getIDType(EGeneIDTypes.DAVID.name());

		IDMappingParser.loadMapping(toFile(base, "DAVID2REFSEQ_MRNA.txt"), 0, -1, david,
				IDType.getIDType(EGeneIDTypes.REFSEQ_MRNA.name()), "\t", geneIDCategory, true, true, false, null, null);
		IDMappingParser.loadMapping(toFile(base, "DAVID2ENTREZ_GENE_ID.txt"), 0, -1, david,
				IDType.getIDType(EGeneIDTypes.ENTREZ_GENE_ID.name()), "\t", geneIDCategory, false, true, false, null,
				null);
		IDMappingParser
				.loadMapping(toFile(base, "DAVID2GENE_SYMBOL.txt"), 0, -1, david,
						IDType.getIDType(EGeneIDTypes.GENE_SYMBOL.name()), "\t", geneIDCategory, false, true, false,
						null, null);

		// IDMappingParser.loadMapping(toFile(base, "INTERACTION_ID2COMPOUND_ID.txt"), 0, -1,
		// IDType.getIDType(EGeneIDTypes.INTERACTION_ID.name()),
		// IDType.getIDType(EGeneIDTypes.COMPOUND_ID.name()), "\t", geneIDCategory, true, true, false, null, null);
		//
		// IDMappingParser.loadMapping(toFile(base, "INTERACTION_ID2ENTREZ_GENE_ID.txt"), 0, -1,
		// IDType.getIDType(EGeneIDTypes.INTERACTION_ID.name()),
		// IDType.getIDType(EGeneIDTypes.ENTREZ_GENE_ID.name()), "\t", geneIDCategory, true, true, false, null,
		// null);
		// IDMappingParser.loadMapping(toFile(base,"DAVID2GENE_NAME.txt"), 0, -1, david,
		// IDType.getIDType(EGeneIDTypes.GENE_NAME.name()), "\t", geneIDCategory, false, true, false, null, null);

		if (GeneticMetaData.getOrganism() == Organism.MUS_MUSCULUS) {
			IDMappingParser.loadMapping(toFile(base, "DAVID2ENSEMBL_GENE_ID.txt"), 0, -1, david,
					IDType.getIDType(EGeneIDTypes.ENSEMBL_GENE_ID.name()), "\t", geneIDCategory, false, true, false,
					null, null);
		} else {
			// This is indirection via REFSEQ_MRNA instead of DAVID is needed as
			// we currently do not have a mapping file DAVID2ENSEMBL for home
			// sapiens
			IDMappingParser.loadMapping(toFile(base, "ENSEMBL_GENE_ID_2_REFSEQ_MRNA.txt"), 0, -1,
					IDType.getIDType(EGeneIDTypes.ENSEMBL_GENE_ID.name()),
					IDType.getIDType(EGeneIDTypes.REFSEQ_MRNA.name()), ";", geneIDCategory, true, true, true,
					IDType.getIDType(EGeneIDTypes.ENSEMBL_GENE_ID.name()), david);
		}
	}

	private static String toFile(File base, String string) {
		return new File(base, string).getAbsolutePath();
	}

	private static File prepareFile(IProgressMonitor monitor) {
		URL url = null;
		try {
			url = new URL(String.format(URL_PATTERN, GeneticMetaData.getOrganism().name().toLowerCase()));
			RemoteFile zip = RemoteFile.of(url);
			File localZip = zip.getOrLoad(true, monitor, "Caching Mapping Data (this may take a while): Downloading "
					+ GeneticMetaData.getOrganism().getLabel() + " (%2$d MB)");
			if (localZip == null || !localZip.exists()) {
				log.error("can't download: " + url);
				return null;
			}
			File unpacked = new File(localZip.getParentFile(), localZip.getName().replaceAll("\\.zip", ""));
			if (unpacked.exists())
				return unpacked;
			ZipUtils.unzipToDirectory(localZip.getAbsolutePath(), unpacked.getAbsolutePath());
			return unpacked;
		} catch (MalformedURLException e) {
			log.error("can't download: " + url);
			return null;
		}
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		// loading zip and extracting it during initialization
		prepareFile(monitor);
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
		tcgaIDTypeParsingRules.setToUpperCase(true);
		tcgaIDTypeParsingRules.setDefault(true);
		tcgaSample.setIdTypeParsingRules(tcgaIDTypeParsingRules);

		tcgaSampleIDCategory.setHumanReadableIDType(tcgaSample);
	}

}
