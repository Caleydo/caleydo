/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.data.importer.mug;

import org.caleydo.core.data.collection.table.NumericalTable;
import org.caleydo.core.io.ColumnDescription;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.DataSetDescription.ECreateDefaultProperties;
import org.caleydo.core.io.GroupingParseSpecification;
import org.caleydo.core.io.IDSpecification;
import org.caleydo.core.io.IDTypeParsingRules;
import org.caleydo.core.io.ParsingRule;
import org.caleydo.data.importer.setupgenerator.DataSetDescriptionSerializer;

/**
 * Generator class that writes the loading information of a series of TCGA data
 * sets to an XML file.
 *
 * @author Alexander Lex
 * @author Marc Streit
 */
public class MouseConfigurationGenerator extends DataSetDescriptionSerializer {

	/**
	 * @param arguments
	 */
	public MouseConfigurationGenerator(String[] arguments) {
		super(arguments);
	}

	// public static final String MRNA =
	// "data/genome/microarray/kashofer/mouse/all_mice.csv";
	public static final String MRNA = "/home/alexsb/uni/caleydo/org.caleydo.data/data/genome/microarray/kashofer/mouse/all_mice.csv";
	public static final String MRNA_SAMPLE_GROUPING = "/home/alexsb/uni/caleydo/org.caleydo.data/data/genome/microarray/kashofer/mouse/all_mice_grouping.csv";

	public static final String SEQUENCED_MRNA = "/home/alexsb/uni/caleydo/org.caleydo.data/data/genome/microarray/kashofer/mouse/20110621_TRpCSingleProbes_Mean_SD_8w_7d_con.txt";
	// public static final String MRNA_SAMPLE_GROUPING =
	// "/home/alexsb/uni/caleydo/org.caleydo.data/data/genome/microarray/kashofer/mouse/all_mice_grouping.csv";

	// public static final String OUTPUT_FILE_PATH =
	// System.getProperty("user.home")
	// + System.getProperty("file.separator") + "mouse_caleydo_data.xml";

	private IDSpecification sampleIDSpecification;

	public static void main(String[] args) {

		MouseConfigurationGenerator generator = new MouseConfigurationGenerator(args);
		generator.run();
	}

	@Override
	protected void setUpDataSetDescriptions() {

		sampleIDSpecification = new IDSpecification();
		sampleIDSpecification.setIdCategory("SAMPLE");
		sampleIDSpecification.setIdType("SAMPLE");

		projectDescription.add(setUpMRNAData());
//		dataSetDescriptionCollection.add(setUpSequencedMRNAData());

	}

	private DataSetDescription setUpMRNAData() {
		DataSetDescription mrnaData = new DataSetDescription(ECreateDefaultProperties.NUMERICAL);
		mrnaData.setDataSetName("mRNA");

		mrnaData.setDataSourcePath(MRNA);
		mrnaData.setNumberOfHeaderLines(1);
		mrnaData.getDataDescription().getNumericalProperties()
				.setDataTransformation(NumericalTable.Transformation.LOG2);

		ParsingRule parsingRule = new ParsingRule();
		parsingRule.setFromColumn(1);
		parsingRule.setParseUntilEnd(true);
		parsingRule.setColumnDescripton(new ColumnDescription());
		mrnaData.addParsingRule(parsingRule);

		IDSpecification geneIDSpecification = new IDSpecification();
		geneIDSpecification.setIDTypeGene(true);
		geneIDSpecification.setIdType("REFSEQ_MRNA");
		IDTypeParsingRules idParsingRule = new IDTypeParsingRules();
		idParsingRule.setDefault(true);
		idParsingRule.setSubStringExpression("\\.");
		geneIDSpecification.setIdTypeParsingRules(idParsingRule);
		mrnaData.setRowIDSpecification(geneIDSpecification);
		mrnaData.setColumnIDSpecification(sampleIDSpecification);

		GroupingParseSpecification sampleGrouping = new GroupingParseSpecification(
				MRNA_SAMPLE_GROUPING);
		// firehoseClustering.setContainsColumnIDs(false);
		sampleGrouping.setRowIDSpecification(sampleIDSpecification);
		mrnaData.addColumnGroupingSpecification(sampleGrouping);

		return mrnaData;
	}

	private DataSetDescription setUpSequencedMRNAData() {
		DataSetDescription mrnaData = new DataSetDescription(ECreateDefaultProperties.NUMERICAL);
		mrnaData.setDataSetName("mRNA Sequenced");

		mrnaData.setDataSourcePath(SEQUENCED_MRNA);
		mrnaData.setNumberOfHeaderLines(1);
		mrnaData.getDataDescription().getNumericalProperties()
				.setDataTransformation(NumericalTable.Transformation.LOG2);

		ParsingRule parsingRule = new ParsingRule();
		parsingRule.setFromColumn(5);
		parsingRule.setParseUntilEnd(true);
		parsingRule.setColumnDescripton(new ColumnDescription());
		mrnaData.addParsingRule(parsingRule);

		IDSpecification geneIDSpecification = new IDSpecification();
		geneIDSpecification.setIDTypeGene(true);
		geneIDSpecification.setIdType("ENSEMBL_GENE_ID");
//		mrnaData.setColumnOfRowIds(1);
//		geneIDSpecification.setIdType("GENE_SYMBOL");

		// geneIDSpecification.setSubStringExpression("\\.");
		mrnaData.setRowIDSpecification(geneIDSpecification);
		mrnaData.setColumnIDSpecification(sampleIDSpecification);

//		GroupingParseSpecification sampleGrouping = new GroupingParseSpecification(
//				MRNA_SAMPLE_GROUPING);
//		// firehoseClustering.setContainsColumnIDs(false);
//		sampleGrouping.setRowIDSpecification(sampleIDSpecification);
//		mrnaData.addColumnGroupingSpecification(sampleGrouping);

		return mrnaData;
	}

}
