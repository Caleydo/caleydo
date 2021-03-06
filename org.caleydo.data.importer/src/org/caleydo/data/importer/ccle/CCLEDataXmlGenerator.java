/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.data.importer.ccle;

import org.caleydo.core.io.ColumnDescription;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.DataSetDescription.ECreateDefaultProperties;
import org.caleydo.core.io.GroupingParseSpecification;
import org.caleydo.core.io.IDSpecification;
import org.caleydo.core.io.IDTypeParsingRules;
import org.caleydo.core.io.NumericalProperties;
import org.caleydo.core.io.ParsingRule;
import org.caleydo.core.io.ProjectDescription;
import org.caleydo.data.importer.setupgenerator.DataSetDescriptionSerializer;

/**
 * Creates and parameterizes a series of {@link DataSetDescription}s for the TCGA dataset. Writes the result to an xml
 * file, which can then be loaded by the importer plugin, which creates and caleydo project file.
 *
 * @author Alexander Lex
 * @author Marc Streit
 */
public class CCLEDataXmlGenerator extends DataSetDescriptionSerializer {
	public static final String DROPBOX_CCLE_FOLDER = System.getProperty("user.home")
 + "/"
			+ "Dropbox/Caleydo/data/ccle/";

	public static final String MRNA = DROPBOX_CCLE_FOLDER + "CCLE_Expression_Entrez_2012-09-29.gct";

	public static final String SAMPLE_GROUPING = DROPBOX_CCLE_FOLDER + "CCLE_sample_info_file_2012-10-18.txt";

	public static final String COPY_NUMBER = DROPBOX_CCLE_FOLDER + "CCLE_copynumber_byGene_2012-09-29.txt";

	public static final String MUTATION = DROPBOX_CCLE_FOLDER + "CCLE_mutation.txt";

	public static final String COMPOUND_CELL_DATA = DROPBOX_CCLE_FOLDER
			+ "CCLE_NP24.2009_Drug_data_2012.02.20_changed_delimiter.csv";
	// public static final String CLINICAL = DROPBOX_CCLE_FOLDER + "clinical/clinical_patient_public_GBM.txt";

	// public static final String MUTATION = DROPBOX_CCLE_FOLDER
	// + "mutation/mut_patient_centric_table_public_transposed_01.txt";

	private IDSpecification sampleIDSpecification;

	private GroupingParseSpecification sampleGrouping;

	public static void main(String[] args) {

		CCLEDataXmlGenerator generator = new CCLEDataXmlGenerator(args);
		generator.run();
	}

	/**
	 *
	 */
	public CCLEDataXmlGenerator(String[] arguments) {
		super(arguments);
	}

	@Override
	protected void setUpDataSetDescriptions() {

		sampleIDSpecification = new IDSpecification();
		sampleIDSpecification.setIdCategory("CELL_LINE");
		sampleIDSpecification.setIdType("CELL_LINE");
		IDTypeParsingRules idTypeParsingRules = new IDTypeParsingRules();
		idTypeParsingRules.setDefault(true);
		sampleIDSpecification.setIdTypeParsingRules(idTypeParsingRules);

		sampleGrouping = new GroupingParseSpecification();
		sampleGrouping.setDataSourcePath(SAMPLE_GROUPING);
		sampleGrouping.addColum(4);
		sampleGrouping.addColum(5);
		sampleGrouping.addColum(6);
		sampleGrouping.setRowIDSpecification(sampleIDSpecification);

		projectDescription = new ProjectDescription();

		projectDescription.add(setUpMRNAData());
		projectDescription.add(setUpMutationData());
		projectDescription.add(setUpCopyNumberData());

		projectDescription.add(setUpCompoundCellData());
		// projectDescription.add(setUpClinicalData());

	}

	private DataSetDescription setUpMRNAData() {
		DataSetDescription mrnaData = new DataSetDescription(ECreateDefaultProperties.NUMERICAL);
		mrnaData.setDataSetName("mRNA Expression");

		mrnaData.setDataSourcePath(MRNA);
		mrnaData.setNumberOfHeaderLines(3);

		ParsingRule parsingRule = new ParsingRule();
		parsingRule.setFromColumn(3);
		parsingRule.setParseUntilEnd(true);
		parsingRule.setColumnDescripton(new ColumnDescription());
		mrnaData.addParsingRule(parsingRule);
		mrnaData.setTransposeMatrix(true);

		// mrnaData.getDataDescription().getNumericalProperties().setDataCenter(0d);

		IDSpecification geneIDSpecification = new IDSpecification();
		geneIDSpecification.setIDTypeGene(true);
		geneIDSpecification.setIdType("GENE_SYMBOL");
		mrnaData.setRowIDSpecification(geneIDSpecification);
		mrnaData.setRowOfColumnIDs(2);
		mrnaData.setColumnIDSpecification(sampleIDSpecification);
		mrnaData.setColumnOfRowIds(1);

		return mrnaData;
	}

	private DataSetDescription setUpMutationData() {
		DataSetDescription mutationData = new DataSetDescription(ECreateDefaultProperties.NUMERICAL);
		mutationData.setDataSetName("Mutation Status");

		mutationData.setDataSourcePath(MUTATION);
		mutationData.setNumberOfHeaderLines(1);

		ParsingRule parsingRule = new ParsingRule();
		parsingRule.setFromColumn(1);
		parsingRule.setParseUntilEnd(true);
		parsingRule.setColumnDescripton(new ColumnDescription());
		mutationData.addParsingRule(parsingRule);
		mutationData.setTransposeMatrix(true);

		// mrnaData.getDataDescription().getNumericalProperties().setDataCenter(0d);

		IDSpecification geneIDSpecification = new IDSpecification();
		geneIDSpecification.setIDTypeGene(true);
		geneIDSpecification.setIdType("GENE_SYMBOL");
		mutationData.setRowIDSpecification(geneIDSpecification);
		mutationData.setRowOfColumnIDs(0);
		mutationData.setColumnIDSpecification(sampleIDSpecification);
		mutationData.setColumnOfRowIds(0);



		return mutationData;
	}

	private DataSetDescription setUpCopyNumberData() {
		DataSetDescription copyNumberData = new DataSetDescription(ECreateDefaultProperties.NUMERICAL);
		copyNumberData.setDataSetName("Copy Number Variation");

		copyNumberData.setDataSourcePath(COPY_NUMBER);
		copyNumberData.setNumberOfHeaderLines(1);

		// @SuppressWarnings("unchecked")
		// CategoricalClassDescription<Integer> categoricalClassDescription = (CategoricalClassDescription<Integer>)
		// copyNumberData
		// .getDataDescription().getCategoricalClassDescription();
		// categoricalClassDescription.setCategoryType(ECategoryType.ORDINAL);
		// categoricalClassDescription.setRawDataType(EDataType.INTEGER);
		// categoricalClassDescription.addCategoryProperty(-2, "Homozygous deletion", Colors.BLUE);
		// categoricalClassDescription.addCategoryProperty(-1, "Heterozygous deletion",
		// Colors.BLUE.getColorWithSpecificBrighness(0.5f));
		// categoricalClassDescription.addCategoryProperty(0, "NORMAL", Colors.NEUTRAL_GREY);
		// categoricalClassDescription.addCategoryProperty(1, "Low level amplification",
		// Colors.RED.getColorWithSpecificBrighness(0.5f));
		// categoricalClassDescription.addCategoryProperty(2, "High level amplification", Colors.RED);

		ParsingRule parsingRule = new ParsingRule();
		parsingRule.setFromColumn(4);
		parsingRule.setParseUntilEnd(true);
		parsingRule.setColumnDescripton(new ColumnDescription());
		copyNumberData.addParsingRule(parsingRule);
		copyNumberData.setTransposeMatrix(true);

		IDSpecification geneIDSpecification = new IDSpecification();
		geneIDSpecification.setIDTypeGene(true);
		geneIDSpecification.setIdType("GENE_SYMBOL");
		copyNumberData.setRowIDSpecification(geneIDSpecification);
		copyNumberData.setColumnIDSpecification(sampleIDSpecification);

		NumericalProperties numProps = new NumericalProperties();
		numProps.setMax(2.5f);
		numProps.setMin(-2.5f);
		numProps.setDataCenter(0.0);
		copyNumberData.getDataDescription().setNumericalProperties(numProps);

		// copyNumberData.addColumnGroupingSpecification(sampleGrouping);

		return copyNumberData;
	}

	private DataSetDescription setUpCompoundCellData() {

		DataSetDescription compoundCellData = new DataSetDescription(ECreateDefaultProperties.NUMERICAL);
		compoundCellData.setDelimiter(";");
		compoundCellData.setDataSetName("Compound - Cell Effect");
		compoundCellData.setLinearSource(true);
		compoundCellData.setDataSourcePath(COMPOUND_CELL_DATA);
		compoundCellData.setNumberOfHeaderLines(1);

		// the cell line column
		compoundCellData.setColumnOfRowIds(0);

		// the column of the second id (compounds)
		compoundCellData.setRowOfColumnIDs(2);

		ParsingRule parsingRule = new ParsingRule();
		parsingRule.setFromColumn(10);
		parsingRule.setColumnDescripton(new ColumnDescription());
		compoundCellData.addParsingRule(parsingRule);

		IDSpecification compoundIDSpecification = new IDSpecification();
		compoundIDSpecification.setIdType("compound");

		compoundCellData.setColumnIDSpecification(compoundIDSpecification);
		compoundCellData.setRowIDSpecification(sampleIDSpecification);

		compoundCellData.addRowGroupingSpecification(sampleGrouping);

		return compoundCellData;
	}

	// private DataSetDescription setUpMutationData() {
	// DataSetDescription mutationDataMetaInfo = new DataSetDescription(ECreateDefaultProperties.CATEGORICAL);
	// mutationDataMetaInfo.setDataSetName("Mutation Status");
	// mutationDataMetaInfo.setDataSourcePath(MUTATION);
	//
	// @SuppressWarnings("unchecked")
	// CategoricalClassDescription<Integer> categoricalClassDescription = (CategoricalClassDescription<Integer>)
	// mutationDataMetaInfo
	// .getDataDescription().getCategoricalClassDescription();
	// categoricalClassDescription.addCategoryProperty(0, "Not Mutated", Colors.NEUTRAL_GREY);
	// categoricalClassDescription.addCategoryProperty(1, "Mutated", Colors.RED);
	//
	// mutationDataMetaInfo.setNumberOfHeaderLines(1);
	//
	// ParsingRule parsingRule = new ParsingRule();
	// parsingRule.setFromColumn(1);
	// parsingRule.setParseUntilEnd(true);
	// parsingRule.setColumnDescripton(new ColumnDescription());
	// mutationDataMetaInfo.addParsingRule(parsingRule);
	// mutationDataMetaInfo.setTransposeMatrix(true);
	//
	// IDSpecification geneIDSpecification = new IDSpecification();
	// geneIDSpecification.setIDTypeGene(true);
	// geneIDSpecification.setIdType("GENE_SYMBOL");
	// mutationDataMetaInfo.setRowIDSpecification(geneIDSpecification);
	// mutationDataMetaInfo.setColumnIDSpecification(sampleIDSpecification);
	//
	// return mutationDataMetaInfo;
	// }

}
