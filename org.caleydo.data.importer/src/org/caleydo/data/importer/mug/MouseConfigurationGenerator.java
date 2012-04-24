/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 * 
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.data.importer.mug;

import org.caleydo.core.io.ColumnDescription;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.GroupingParseSpecification;
import org.caleydo.core.io.IDSpecification;
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
		sampleIDSpecification.setIdType("SAMPLE");

		dataSetDescriptionCollection.add(setUpMRNAData());
		dataSetDescriptionCollection.add(setUpSequencedMRNAData());

	}

	private DataSetDescription setUpMRNAData() {
		DataSetDescription mrnaData = new DataSetDescription();
		mrnaData.setDataSetName("mRNA");

		mrnaData.setDataSourcePath(MRNA);
		mrnaData.setNumberOfHeaderLines(1);
		mrnaData.setMathFilterMode("LOG2");

		ParsingRule parsingRule = new ParsingRule();
		parsingRule.setFromColumn(1);
		parsingRule.setParseUntilEnd(true);
		parsingRule.setColumnDescripton(new ColumnDescription("FLOAT",
				ColumnDescription.CONTINUOUS));
		mrnaData.addParsingRule(parsingRule);

		IDSpecification geneIDSpecification = new IDSpecification();
		geneIDSpecification.setIDTypeGene(true);
		geneIDSpecification.setIdType("REFSEQ_MRNA");
		geneIDSpecification.setSubStringExpression("\\.");
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
		DataSetDescription mrnaData = new DataSetDescription();
		mrnaData.setDataSetName("mRNA Sequenced");

		mrnaData.setDataSourcePath(SEQUENCED_MRNA);
		mrnaData.setNumberOfHeaderLines(1);
		mrnaData.setMathFilterMode("LOG2");

		ParsingRule parsingRule = new ParsingRule();
		parsingRule.setFromColumn(5);
		parsingRule.setParseUntilEnd(true);
		parsingRule.setColumnDescripton(new ColumnDescription("FLOAT",
				ColumnDescription.CONTINUOUS));
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
