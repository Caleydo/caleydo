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
package org.caleydo.data.importer.tcga;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.zip.GZIPInputStream;

import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;
import org.caleydo.core.io.ColumnDescription;
import org.caleydo.core.io.DataProcessingDescription;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.GroupingParseSpecification;
import org.caleydo.core.io.IDSpecification;
import org.caleydo.core.io.IDTypeParsingRules;
import org.caleydo.core.io.ParsingRule;
import org.caleydo.core.util.clusterer.algorithm.kmeans.KMeansClusterConfiguration;
import org.caleydo.core.util.clusterer.initialization.EDistanceMeasure;
import org.caleydo.data.importer.setupgenerator.DataSetDescriptionSerializer;

/**
 * Generator class that writes the loading information of a series of TCGA data
 * sets to an XML file.
 * 
 * @author Nils Gehlenborg
 * @author Alexander Lex
 * @author Marc Streit
 */
public class TCGADataXMLGenerator extends DataSetDescriptionSerializer {
	
	protected String tumorName;
	protected String tumorAbbreviation;
	protected String runIdentifier;
	protected String baseDirectory;
	protected String tempDirectory;
	protected String archiveDirectory;
	protected String outputFilePath;
	
	
	/**
	 * @param arguments
	 */
	public TCGADataXMLGenerator(String[] arguments) {
		super(arguments);
		
		this.tumorName = "Glioblastoma Multiforme";
		this.tumorAbbreviation = "UCEC";
		this.runIdentifier = "20120525";
		this.tempDirectory = "/Users/nils/Data/StratomeX/temp";
		this.baseDirectory = "/Users/nils/Data/StratomeX/downloads/analyses__2012_05_25";
				
		// create path of archive search directory
		this.archiveDirectory = this.baseDirectory + System.getProperty("file.separator") +
				this.tumorAbbreviation + System.getProperty("file.separator") +
				this.runIdentifier;
		
		this.outputFilePath = this.baseDirectory + System.getProperty("file.separator") + this.tumorAbbreviation + "_" + this.runIdentifier + "_caleydo.xml";		
	}
	

	public static final String TCGA_ID_SUBSTRING_REGEX = "TCGA\\-|\\-...\\-";

	private IDSpecification sampleIDSpecification;

	public static void main(String[] args) {

		TCGADataXMLGenerator generator = new TCGADataXMLGenerator(args);
		generator.run( generator.getOutputFilePath() );
	}

	
	// find pipeline archive name filter (filename pattern matcher)
	// TODO: replace with PathMatcher in Java 7
	class PipelineNameFilter implements FilenameFilter
	{
	  protected String pipelineName;
		
	  public PipelineNameFilter( String pipelineName )
	  {
		  this.pipelineName = pipelineName;
	  }		
	  
	  public boolean accept( File directory, String fileName )
	  {
	    if ( fileName.contains( this.pipelineName + "." + "Level_4" ) )
	    {
		    if ( fileName.endsWith( ".tar.gz" ) )
		    {
		    	return true;
		    }
	    }
	    
	    return false;
	  }
	}		
	
	protected String extractFileFromTarGzArchive( String archiveName, String fileName, String outputDirectoryName )
    {
		String outputFileName = null;
				
        try
        {
            byte[] buf = new byte[1024];
            TarInputStream tarInputStream = null;
            TarEntry tarEntry;
            tarInputStream = new TarInputStream(
                new GZIPInputStream( new FileInputStream( this.archiveDirectory + System.getProperty("file.separator") + archiveName ) ) );

            tarEntry = tarInputStream.getNextEntry();
            while (tarEntry != null) 
            { 
                //for each entry to be extracted
                String entryName = tarEntry.getName();
                
                // only continue if the this entry is the one we need to extract
                if ( !entryName.endsWith( fileName ) )
                {
                	tarEntry = tarInputStream.getNextEntry();
                	continue;
                }
                
                int n;
                FileOutputStream fileoutputstream;
                File newFile = new File(entryName);
                String directory = newFile.getParent();
                
                if ( directory == null )
                {
                    if( newFile.isDirectory() )
                    	
                        break;
                }
                
                outputDirectoryName += System.getProperty("file.separator") + this.runIdentifier + System.getProperty("file.separator") +
                		this.tumorAbbreviation + System.getProperty("file.separator") +
                		archiveName;
                
                if ( !(new File( outputDirectoryName ) ).exists() )
                {
                    if ( !(new File( outputDirectoryName ) ).mkdirs() )
                    {
                        // Directory creation failed
                    	throw new RuntimeException( "Unable to create output directory " + outputDirectoryName + " for " + fileName + "." );
                    }
                }
                
                outputFileName = outputDirectoryName + System.getProperty("file.separator") + fileName;
                
                fileoutputstream = new FileOutputStream(
                   outputFileName);             

                while ((n = tarInputStream.read(buf, 0, 1024)) > -1)
                    fileoutputstream.write(buf, 0, n);

                fileoutputstream.close(); 
                tarInputStream.close();
                
                break;
            }//while

        }
        catch (Exception e)
        {
        	throw new RuntimeException( "Unable to extract " + fileName + " from " + archiveName + "." );
        }
        
        if ( outputFileName == null )
        {	
        	throw new RuntimeException( "File " + fileName + " not found in " + archiveName + "." );
        }
                
        return outputFileName;
    }	
	
	
	// find Firehose archive in Firehose_get output directory and extract file from archive to temp directory
	// return path to file in temp directory
	protected String extractFile( String fileName, String pipelineName )
	{
		// check if exactly one archive exists, if not return null
		String[] archiveNames = new java.io.File( this.archiveDirectory ).list( new PipelineNameFilter( pipelineName ) );
		
		if ( archiveNames.length == 0 )
		{
			throw new RuntimeException( "No archive found for pipeline " + pipelineName + " in " + this.archiveDirectory );
		}

		if ( archiveNames.length > 1 )
		{
			throw new RuntimeException( "Multiple archives found for pipeline " + pipelineName + " in " + this.archiveDirectory );
		}
		
		
		String archiveName = archiveNames[0];
		
		// extract file to temp directory and return path to file
		return extractFileFromTarGzArchive( archiveName, fileName, this.tempDirectory );
	}
	
	
	protected int removeFile( String filePath ) 
	{
		// delete file from temp directory if it exists
		// check if this is requested before calling this file
		return 0;
	}
	

	@Override
	protected void setUpDataSetDescriptions() {

		sampleIDSpecification = new IDSpecification();
		sampleIDSpecification.setIdCategory("TCGA_SAMPLE");
		sampleIDSpecification.setIdType("TCGA_SAMPLE");
		IDTypeParsingRules idTypeParsingRules = new IDTypeParsingRules();
		idTypeParsingRules.setReplacementExpression("\\.", "-");
		idTypeParsingRules.setSubStringExpression(TCGA_ID_SUBSTRING_REGEX);
		sampleIDSpecification.setIdTypeParsingRules(idTypeParsingRules);
		try
		{
			dataSetDescriptionCollection.add(setUpClusteredMatrixData( "mRNA_Clustering_CNMF", "mRNA_Clustering_Consensus", "outputprefix.expclu.gct", "mRNA", true ));
		}
		catch( Exception e )
		{
			System.err.println( e.getMessage() );
		}

		try
		{
			dataSetDescriptionCollection.add(setUpClusteredMatrixData( "miR_Clustering_CNMF", "miR_Clustering_Consensus", "cnmf.normalized.gct", "microRNA", false ));
		}
		catch( Exception e )
		{
			System.err.println( e.getMessage() );
		}

		try
		{
			dataSetDescriptionCollection.add(setUpClusteredMatrixData( "miRseq_Clustering_CNMF", "miRseq_Clustering_Consensus", "cnmf.normalized.gct", "microRNA-seq", false ));
		}
		catch( Exception e )
		{
			System.err.println( e.getMessage() );
		}
		
		try
		{
			dataSetDescriptionCollection.add(setUpClusteredMatrixData( "Methylation_Clustering_CNMF", "Methylation_Clustering_Consensus", "cnmf.normalized.gct", "methylation", true ));
		}
		catch( Exception e )
		{
			System.err.println( e.getMessage() );
		}
		
		try
		{
			dataSetDescriptionCollection.add(setUpClusteredMatrixData( "RPPA_Clustering_CNMF", "RPPA_Clustering_Consensus", "cnmf.normalized.gct", "RPPA", false ));
		}
		catch( Exception e )
		{
			System.err.println( e.getMessage() );
		}
		
		try
		{
			dataSetDescriptionCollection.add(setUpCopyNumberData( "CopyNumber_Gistic2", "Copy Number"));
		}
		catch( Exception e )
		{
			System.err.println( e.getMessage() );
		}
		
		//dataSetDescriptionCollection.add(setUpClinicalData());
		// dataSetDescriptionCollection.add(setUpMutationData());
	}

	private DataSetDescription setUpClusteredMatrixData(String cnmfArchiveName, String hierarchicalArchiveName, String matrixFileName, String dataType, boolean isGeneIdType ) {
		String mRNAFile = this.extractFile( matrixFileName, cnmfArchiveName );
		String mRNACnmfGroupingFile = this.extractFile( "cnmf.membership.txt", cnmfArchiveName );
		
		DataSetDescription matrixData = new DataSetDescription();
		matrixData.setDataSetName(dataType);

		matrixData.setDataSourcePath( mRNAFile );
		matrixData.setNumberOfHeaderLines(3);

		ParsingRule parsingRule = new ParsingRule();
		parsingRule.setFromColumn(2);
		parsingRule.setParseUntilEnd(true);
		parsingRule.setColumnDescripton(new ColumnDescription("FLOAT",
				ColumnDescription.CONTINUOUS));
		matrixData.addParsingRule(parsingRule);
		matrixData.setTransposeMatrix(true);

		if ( isGeneIdType )
		{	
			IDSpecification geneIDSpecification = new IDSpecification();
			geneIDSpecification.setIDTypeGene(true);
			geneIDSpecification.setIdType("GENE_SYMBOL");
			matrixData.setRowIDSpecification(geneIDSpecification);
		}

		matrixData.setColumnIDSpecification(sampleIDSpecification);

		GroupingParseSpecification firehoseCnmfClustering = new GroupingParseSpecification(
				mRNACnmfGroupingFile);
		firehoseCnmfClustering.setContainsColumnIDs(false);
		firehoseCnmfClustering.setRowIDSpecification(sampleIDSpecification);
		matrixData.addColumnGroupingSpecification(firehoseCnmfClustering);

		try
		{
			String mRNAHierarchicalGroupingFile = this.extractFile( this.tumorAbbreviation + ".allclusters.txt", hierarchicalArchiveName ); // e.g. GBM.allclusters.txt		
			
			GroupingParseSpecification firehoseHierarchicalClustering = new GroupingParseSpecification(
					mRNAHierarchicalGroupingFile);
			firehoseHierarchicalClustering.setContainsColumnIDs(false);
			firehoseHierarchicalClustering.setRowIDSpecification(sampleIDSpecification);
			matrixData.addColumnGroupingSpecification(firehoseHierarchicalClustering);					
		}
		catch ( RuntimeException e )
		{
			System.err.println( e.getMessage() );
		}
		
		DataProcessingDescription dataProcessingDescription = new DataProcessingDescription();
		KMeansClusterConfiguration clusterConfiguration = new KMeansClusterConfiguration();
		clusterConfiguration.setDistanceMeasure(EDistanceMeasure.EUCLIDEAN_DISTANCE);
		clusterConfiguration.setNumberOfClusters(5);
		dataProcessingDescription.addRowClusterConfiguration(clusterConfiguration);
		matrixData.setDataProcessingDescription(dataProcessingDescription);
		
		return matrixData;
	}

	
	private DataSetDescription setUpMiRNAData( String archiveName, String dataType ) {
		String miRNAFile = this.extractFile( "cnmf.normalized.gct", archiveName );
		String miRNAGroupingFile = this.extractFile( "cnmf.membership.txt", archiveName );
		
		DataSetDescription mirnaData = new DataSetDescription();
		mirnaData.setDataSetName(dataType);

		mirnaData.setDataSourcePath(miRNAFile);
		mirnaData.setNumberOfHeaderLines(3);

		ParsingRule parsingRule = new ParsingRule();
		parsingRule.setFromColumn(2);
		parsingRule.setParseUntilEnd(true);
		parsingRule.setColumnDescripton(new ColumnDescription("FLOAT",
				ColumnDescription.CONTINUOUS));
		mirnaData.addParsingRule(parsingRule);

		IDSpecification mirnaIDSpecification = new IDSpecification();
		mirnaIDSpecification.setIdType(dataType);
		mirnaData.setRowIDSpecification(mirnaIDSpecification);
		mirnaData.setTransposeMatrix(true);
		mirnaData.setColumnIDSpecification(sampleIDSpecification);

		GroupingParseSpecification firehoseClustering = new GroupingParseSpecification(
				miRNAGroupingFile);
		firehoseClustering.setContainsColumnIDs(false);
		firehoseClustering.setRowIDSpecification(sampleIDSpecification);
		mirnaData.addColumnGroupingSpecification(firehoseClustering);
		
		DataProcessingDescription dataProcessingDescription = new DataProcessingDescription();
		KMeansClusterConfiguration clusterConfiguration = new KMeansClusterConfiguration();
		clusterConfiguration.setDistanceMeasure(EDistanceMeasure.EUCLIDEAN_DISTANCE);
		clusterConfiguration.setNumberOfClusters(5);
		dataProcessingDescription.addRowClusterConfiguration(clusterConfiguration);
		mirnaData.setDataProcessingDescription(dataProcessingDescription);

		return mirnaData;
	}

	private DataSetDescription setUpMethylationData( String archiveName, String dataType ) {
		String methylationFile = this.extractFile( "cnmf.normalized.gct", archiveName );
		String methylationGroupingFile = this.extractFile( "cnmf.membership.txt", archiveName );
		
		DataSetDescription methylationData = new DataSetDescription();
		methylationData.setDataSetName(dataType);

		methylationData.setDataSourcePath(methylationFile);
		methylationData.setNumberOfHeaderLines(3);

		ParsingRule parsingRule = new ParsingRule();
		parsingRule.setFromColumn(2);
		parsingRule.setParseUntilEnd(true);
		parsingRule.setColumnDescripton(new ColumnDescription("FLOAT",
				ColumnDescription.CONTINUOUS));
		methylationData.addParsingRule(parsingRule);
		methylationData.setTransposeMatrix(true);

		IDSpecification methylationIDSpecification = new IDSpecification();
		methylationIDSpecification.setIdType(dataType);
		methylationData.setRowIDSpecification(methylationIDSpecification);
		methylationData.setColumnIDSpecification(sampleIDSpecification);

		GroupingParseSpecification firehoseClustering = new GroupingParseSpecification(
				methylationGroupingFile);
		firehoseClustering.setContainsColumnIDs(false);
		firehoseClustering.setRowIDSpecification(sampleIDSpecification);
		methylationData.addColumnGroupingSpecification(firehoseClustering);
		
		DataProcessingDescription dataProcessingDescription = new DataProcessingDescription();
		KMeansClusterConfiguration clusterConfiguration = new KMeansClusterConfiguration();
		clusterConfiguration.setDistanceMeasure(EDistanceMeasure.EUCLIDEAN_DISTANCE);
		clusterConfiguration.setNumberOfClusters(5);
		dataProcessingDescription.addRowClusterConfiguration(clusterConfiguration);
		methylationData.setDataProcessingDescription(dataProcessingDescription);
		
		return methylationData;
	}

	private DataSetDescription setUpCopyNumberData( String archiveName, String dataType ) {
		String copyNumberFile = this.extractFile( "all_thresholded.by_genes.txt", archiveName );
		
		DataSetDescription copyNumberData = new DataSetDescription();
		copyNumberData.setDataSetName(dataType);

		copyNumberData.setDataSourcePath(copyNumberFile);
		copyNumberData.setNumberOfHeaderLines(1);

		ParsingRule parsingRule = new ParsingRule();
		parsingRule.setFromColumn(3);
		parsingRule.setParseUntilEnd(true);
		parsingRule.setColumnDescripton(new ColumnDescription("FLOAT",
				ColumnDescription.ORDINAL));
		copyNumberData.addParsingRule(parsingRule);
		copyNumberData.setTransposeMatrix(true);

		IDSpecification geneIDSpecification = new IDSpecification();
		geneIDSpecification.setIDTypeGene(true);
		geneIDSpecification.setIdType("GENE_SYMBOL");
		copyNumberData.setRowIDSpecification(geneIDSpecification);

		return copyNumberData;
	}

	//
	private DataSetDescription setUpClinicalData() {
		String CLINICAL = "";
		DataSetDescription clinicalData = new DataSetDescription();
		clinicalData.setDataSetName("Clinical");
		clinicalData.setDataHomogeneous(false);

		clinicalData.setDataSourcePath(CLINICAL);
		clinicalData.setNumberOfHeaderLines(1);

		ParsingRule parsingRule = new ParsingRule();
		parsingRule.setFromColumn(10);
		parsingRule.setToColumn(11);
		parsingRule.setColumnDescripton(new ColumnDescription());
		clinicalData.addParsingRule(parsingRule);
		parsingRule = new ParsingRule();
		parsingRule.setFromColumn(13);
		parsingRule.setToColumn(15);
		parsingRule.setColumnDescripton(new ColumnDescription());
		clinicalData.addParsingRule(parsingRule);

		IDSpecification clinicalIdSpecification = new IDSpecification();
		clinicalIdSpecification.setIdCategory("CLINICAL");
		clinicalIdSpecification.setIdType("clinical");

		clinicalData.setColumnIDSpecification(clinicalIdSpecification);
		clinicalData.setRowIDSpecification(sampleIDSpecification);

		// columnLabels.add("Days to birth");
		// columnLabels.add("Days to death");
		// columnLabels.add("Days to last followup");
		// columnLabels.add("Days to tumor progression");
		// columnLabels.add("Days to tumor recurrence");

		return clinicalData;
	}

	private DataSetDescription setUpMutationData() {
		String MUTATION = "";
		
		DataSetDescription mutationDataMetaInfo = new DataSetDescription();
		mutationDataMetaInfo.setDataSetName("Mutation Status");
		mutationDataMetaInfo.setDataSourcePath(MUTATION);

		mutationDataMetaInfo.setNumberOfHeaderLines(1);

		ParsingRule parsingRule = new ParsingRule();
		parsingRule.setFromColumn(1);
		parsingRule.setParseUntilEnd(true);
		parsingRule.setColumnDescripton(new ColumnDescription("FLOAT",
				ColumnDescription.NOMINAL));
		mutationDataMetaInfo.addParsingRule(parsingRule);
		mutationDataMetaInfo.setTransposeMatrix(true);

		IDSpecification geneIDSpecification = new IDSpecification();
		geneIDSpecification.setIDTypeGene(true);
		geneIDSpecification.setIdType("GENE_SYMBOL");
		mutationDataMetaInfo.setRowIDSpecification(geneIDSpecification);
		mutationDataMetaInfo.setColumnIDSpecification(sampleIDSpecification);

		return mutationDataMetaInfo;
	}

	
	public String getOutputFilePath() {
		return outputFilePath;
	}


	public void setOutputFilePath(String outputFilePath) {
		this.outputFilePath = outputFilePath;
	}



}
