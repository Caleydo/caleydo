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
package org.caleydo.data.importer.jkubioinfo;

import org.caleydo.core.io.ColumnDescription;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.DataSetDescription.ECreateDefaultProperties;
import org.caleydo.core.io.GroupingParseSpecification;
import org.caleydo.core.io.IDSpecification;
import org.caleydo.core.io.NumericalProperties;
import org.caleydo.core.io.ParsingRule;
import org.caleydo.data.importer.setupgenerator.DataSetDescriptionSerializer;

/**
 * Generator class that writes the loading information of a series of
 * Johnsen&Johnsen data sets to an XML file.
 *
 * @author Marc Streit
 * @author Alexander Lex
 */
public class JJDataXMLGeneratorScreenOnco extends DataSetDescriptionSerializer {

	/**
	 * @param arguments
	 */
	public JJDataXMLGeneratorScreenOnco(String[] arguments) {
		super(arguments);

	}

	public static final String DATA_FOLDER = System.getProperty("user.home")
			+ System.getProperty("file.separator")
			+ "caleydo/data/JKU_BioInfo/ScreenOncoForMarc/dat1_1/";

	public static final String GENE_EXPRESSION_DATA = DATA_FOLDER
			+ "ws1_X_tab.csv";
	public static final String EXPERIMENT_CLUSTER_GROUPING = DATA_FOLDER
			+ "clusterAssignments.csv";

	public static final String GENE_CLUSTER_GROUPING = DATA_FOLDER
			+ "clusterAssignments_genes.csv";

	public static final String MGLU2_EXPERIMENT_BICLUSTER_DATA = DATA_FOLDER
			+ "Z.csv";

	public static final String OUTPUT_FILE_PATH = System.getProperty("user.home")
			+ System.getProperty("file.separator") + "caleydo_data.xml";

	private IDSpecification sampleIDSpecification;

	public static void main(String[] args) {

		JJDataXMLGeneratorScreenOnco generator = new JJDataXMLGeneratorScreenOnco(args);
		generator.run();
	}

	@Override
	protected void setUpDataSetDescriptions() {

		sampleIDSpecification = new IDSpecification();
		sampleIDSpecification.setIdType("SAMPLE");
		sampleIDSpecification.setIdCategory("SAMPLE");
		// sampleIDSpecification.setReplacementExpression("\\.", "-");
		// sampleIDSpecification.setSubStringExpression(TCGA_ID_SUBSTRING_REGEX);

		projectDescription.add(setUpXMLGLU2GeneExpressionData());
		// dataSetDescriptions.add(setUpXMLGLU2BiClusterData());
	}

	private DataSetDescription setUpXMLGLU2GeneExpressionData() {

		DataSetDescription mrnaData = new DataSetDescription(ECreateDefaultProperties.NUMERICAL);
		mrnaData.setDataSetName("Screen Onco Data");

		mrnaData.setDataSourcePath(GENE_EXPRESSION_DATA);
		mrnaData.setNumberOfHeaderLines(1);

		ParsingRule parsingRule = new ParsingRule();
		parsingRule.setFromColumn(1);
		parsingRule.setParseUntilEnd(true);
		parsingRule.setColumnDescripton(new ColumnDescription());
		mrnaData.addParsingRule(parsingRule);
		mrnaData.setTransposeMatrix(true);
		//mrnaData.setMathFilterMode("LOG2");
		NumericalProperties numericalProperties = new NumericalProperties();
		numericalProperties.setMin(-1.5f);
		numericalProperties.setMax(1.5f);
		mrnaData.getDataDescription().setNumericalProperties(numericalProperties);

		IDSpecification geneIDSpecification = new IDSpecification();
		geneIDSpecification.setIDTypeGene(true);
		geneIDSpecification.setIdType("GENE_SYMBOL");
		mrnaData.setRowIDSpecification(geneIDSpecification);
		mrnaData.setColumnIDSpecification(sampleIDSpecification);

		GroupingParseSpecification sampleClustering = new GroupingParseSpecification(
				EXPERIMENT_CLUSTER_GROUPING);
		sampleClustering.setContainsColumnIDs(true);
		sampleClustering.setRowIDSpecification(sampleIDSpecification);
		mrnaData.addColumnGroupingSpecification(sampleClustering);

		GroupingParseSpecification geneClustering = new GroupingParseSpecification(
				GENE_CLUSTER_GROUPING);
		geneClustering.setContainsColumnIDs(true);
		geneClustering.setRowIDSpecification(geneIDSpecification);
		mrnaData.addRowGroupingSpecification(geneClustering);

		return mrnaData;
	}
}
