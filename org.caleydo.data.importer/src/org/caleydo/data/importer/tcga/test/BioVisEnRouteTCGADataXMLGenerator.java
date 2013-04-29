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
package org.caleydo.data.importer.tcga.test;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.data.collection.column.container.CategoricalClassDescription.ECategoryType;
import org.caleydo.core.io.ColumnDescription;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.DataSetDescription.ECreateDefaultProperties;
import org.caleydo.core.io.GroupingParseSpecification;
import org.caleydo.core.io.IDSpecification;
import org.caleydo.core.io.IDTypeParsingRules;
import org.caleydo.core.io.ParsingRule;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.Colors;
import org.caleydo.data.importer.setupgenerator.DataSetDescriptionSerializer;
import org.caleydo.datadomain.genetic.TCGADefinitions;

/**
 * Generator class that writes the loading information of a the TCGA gene-referenced data sets with all genes to an XML
 * file.
 *
 * @author Alexander Lex
 * @author Marc Streit
 */
public class BioVisEnRouteTCGADataXMLGenerator extends DataSetDescriptionSerializer {

	/**
	 * @param arguments
	 */
	public BioVisEnRouteTCGADataXMLGenerator(String[] arguments) {
		super(arguments);
	}

	public static final String DROPBOX_BIOVIS_FOLDER = System.getProperty("user.home")
			+ System.getProperty("file.separator") + "Dropbox/Caleydo/data/biovis_paper_data/";

	public static final String DROPBOX_GBM_FOLDER = System.getProperty("user.home")
			+ System.getProperty("file.separator") + "Dropbox/Caleydo/data/tcga/20110728/gbm/";

	public static final String MRNA = DROPBOX_BIOVIS_FOLDER + "gbm_mrna_sampled.txt";

	public static final String MRNA_GROUPING = DROPBOX_GBM_FOLDER + "mrna_cnmf/cnmf.membership.txt";

	public static final String COPY_NUMBER = DROPBOX_BIOVIS_FOLDER + "gbm_copy_number.csv";

	public static final String GROUND_TRUTH_GROUPING = DROPBOX_GBM_FOLDER + "ground_truth/2011_exp_assignments.txt";

	public static final String MUTATION = DROPBOX_GBM_FOLDER + "mutation/gbm_mutation.csv";

	public static final String OUTPUT_FILE_PATH = System.getProperty("user.home")
			+ System.getProperty("file.separator") + "caleydo_data.xml";

	private IDSpecification sampleIDSpecification;

	public static void main(String[] args) {

		BioVisEnRouteTCGADataXMLGenerator generator = new BioVisEnRouteTCGADataXMLGenerator(args);
		generator.run();
	}

	@Override
	protected void setUpDataSetDescriptions() {

		sampleIDSpecification = new IDSpecification();
		sampleIDSpecification.setIdCategory("TCGA_SAMPLE");
		sampleIDSpecification.setIdType("TCGA_SAMPLE");
		IDTypeParsingRules idTypeParsingRules = new IDTypeParsingRules();
		idTypeParsingRules.setReplacementExpression(TCGADefinitions.TCGA_REPLACEMENT_STRING,
				TCGADefinitions.TCGA_REPLACING_EXPRESSIONS);
		idTypeParsingRules.setSubStringExpression(TCGADefinitions.TCGA_ID_SUBSTRING_REGEX);
		idTypeParsingRules.setDefault(true);
		sampleIDSpecification.setIdTypeParsingRules(idTypeParsingRules);

		projectDescription.add(setUpMRNAData());
		projectDescription.add(setUpCopyNumberData());
		projectDescription.add(setUpMutationData());

	}

	private DataSetDescription setUpMRNAData() {
		DataSetDescription mrnaData = new DataSetDescription(ECreateDefaultProperties.NUMERICAL);
		mrnaData.setDataSetName("mRNA");

		mrnaData.setDataSourcePath(MRNA);
		mrnaData.setNumberOfHeaderLines(2);
		mrnaData.setRowOfColumnIDs(0);

		ParsingRule parsingRule = new ParsingRule();
		parsingRule.setFromColumn(1);
		parsingRule.setParseUntilEnd(true);
		parsingRule.setColumnDescripton(new ColumnDescription());
		mrnaData.addParsingRule(parsingRule);
		mrnaData.setTransposeMatrix(true);

		IDSpecification geneIDSpecification = new IDSpecification();
		geneIDSpecification.setIDTypeGene(true);
		geneIDSpecification.setIdType("GENE_SYMBOL");
		mrnaData.setRowIDSpecification(geneIDSpecification);
		mrnaData.setColumnIDSpecification(sampleIDSpecification);

		GroupingParseSpecification firehoseClustering = new GroupingParseSpecification(MRNA_GROUPING);
		firehoseClustering.setContainsColumnIDs(false);
		firehoseClustering.setRowIDSpecification(sampleIDSpecification);
		mrnaData.addColumnGroupingSpecification(firehoseClustering);

		GroupingParseSpecification groundTruthGrouping = new GroupingParseSpecification();
		groundTruthGrouping.setDataSourcePath(GROUND_TRUTH_GROUPING);
		groundTruthGrouping.addColum(2);
		groundTruthGrouping.setRowIDSpecification(sampleIDSpecification);
		mrnaData.addColumnGroupingSpecification(groundTruthGrouping);

		return mrnaData;
	}

	// private DataSetDescription setUpCopyNumberData() {
	// DataSetDescription copyNumberData = new DataSetDescription(ECreateDefaultProperties.CATEGORICAL);
	// copyNumberData.setDataSetName("Copy number");
	//
	// copyNumberData.setDataSourcePath(COPY_NUMBER);
	// copyNumberData.setNumberOfHeaderLines(1);
	//
	// ParsingRule parsingRule = new ParsingRule();
	// parsingRule.setFromColumn(3);
	// parsingRule.setParseUntilEnd(true);
	// parsingRule.setColumnDescripton(new ColumnDescription());
	// copyNumberData.addParsingRule(parsingRule);
	// copyNumberData.setTransposeMatrix(true);
	//
	// IDSpecification geneIDSpecification = new IDSpecification();
	// geneIDSpecification.setIDTypeGene(true);
	// geneIDSpecification.setIdType("GENE_SYMBOL");
	// copyNumberData.setRowIDSpecification(geneIDSpecification);
	// copyNumberData.setColumnIDSpecification(sampleIDSpecification);
	//
	// GroupingParseSpecification groundTruthGrouping = new GroupingParseSpecification();
	// groundTruthGrouping.setDataSourcePath(GROUND_TRUTH_GROUPING);
	// groundTruthGrouping.addColum(2);
	// groundTruthGrouping.setRowIDSpecification(sampleIDSpecification);
	// copyNumberData.addColumnGroupingSpecification(groundTruthGrouping);
	//
	// return copyNumberData;
	//
	// // copyNumberData.setColorScheme(EDefaultColorSchemes.RED_YELLOW_BLUE_DIVERGING
	// // .name());
	// }

	private DataSetDescription setUpCopyNumberData() {
		DataSetDescription copyNumberData = new DataSetDescription(ECreateDefaultProperties.CATEGORICAL);
		copyNumberData.setDataSetName("Copy number");

		copyNumberData.setDataSourcePath(COPY_NUMBER);
		copyNumberData.setNumberOfHeaderLines(1);

		@SuppressWarnings("unchecked")
		CategoricalClassDescription<Integer> categoricalClassDescription = (CategoricalClassDescription<Integer>) copyNumberData
				.getDataDescription().getCategoricalClassDescription();
		categoricalClassDescription.setCategoryType(ECategoryType.ORDINAL);
		categoricalClassDescription.setRawDataType(EDataType.INTEGER);
		categoricalClassDescription.addCategoryProperty(-2, "Homozygous deletion", new Color("0571b0"));
		categoricalClassDescription.addCategoryProperty(-1, "Heterozygous deletion", new Color("5f99ba"));
		categoricalClassDescription.addCategoryProperty(0, "NORMAL", Colors.NEUTRAL_GREY);
		categoricalClassDescription.addCategoryProperty(1, "Low level amplification", new Color("c95d6e"));
		categoricalClassDescription.addCategoryProperty(2, "High level amplification", new Color("c4001f"));

		ParsingRule parsingRule = new ParsingRule();
		parsingRule.setFromColumn(3);
		parsingRule.setParseUntilEnd(true);
		parsingRule.setColumnDescripton(new ColumnDescription());
		copyNumberData.addParsingRule(parsingRule);
		copyNumberData.setTransposeMatrix(true);

		IDSpecification geneIDSpecification = new IDSpecification();
		geneIDSpecification.setIDTypeGene(true);
		geneIDSpecification.setIdType("GENE_SYMBOL");
		copyNumberData.setRowIDSpecification(geneIDSpecification);
		copyNumberData.setColumnIDSpecification(sampleIDSpecification);

		return copyNumberData;

		// copyNumberData.setColorScheme(EDefaultColorSchemes.RED_YELLOW_BLUE_DIVERGING
		// .name());
	}

	private DataSetDescription setUpMutationData() {
		DataSetDescription mutationDataDescription = new DataSetDescription(ECreateDefaultProperties.CATEGORICAL);
		mutationDataDescription.setDataSetName("Mutation Status");
		mutationDataDescription.setDataSourcePath(MUTATION);

		@SuppressWarnings("unchecked")
		CategoricalClassDescription<Integer> categoricalClassDescription = (CategoricalClassDescription<Integer>) mutationDataDescription
				.getDataDescription().getCategoricalClassDescription();
		categoricalClassDescription.addCategoryProperty(0, "Not Mutated", Colors.NEUTRAL_GREY);
		categoricalClassDescription.addCategoryProperty(1, "Mutated", Colors.RED);

		mutationDataDescription.setNumberOfHeaderLines(1);

		ParsingRule parsingRule = new ParsingRule();
		parsingRule.setFromColumn(1);
		parsingRule.setParseUntilEnd(true);
		parsingRule.setColumnDescripton(new ColumnDescription());
		mutationDataDescription.addParsingRule(parsingRule);
		mutationDataDescription.setTransposeMatrix(true);

		IDSpecification geneIDSpecification = new IDSpecification();
		geneIDSpecification.setIDTypeGene(true);
		geneIDSpecification.setIdType("GENE_SYMBOL");
		mutationDataDescription.setRowIDSpecification(geneIDSpecification);
		mutationDataDescription.setColumnIDSpecification(sampleIDSpecification);

		return mutationDataDescription;
	}

}
