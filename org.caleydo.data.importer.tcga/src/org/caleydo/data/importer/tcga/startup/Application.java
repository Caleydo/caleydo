package org.caleydo.data.importer.tcga.startup;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.collection.table.DataTableUtils;
import org.caleydo.core.data.collection.table.LoadDataParameters;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ProjectSaver;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/**
 * This class controls all aspects of the application's execution
 */
public class Application
	implements IApplication {

	private ATableBasedDataDomain dataDomain;

	/** class which takes the parameters for parsing */
	private LoadDataParameters loadDataParameters = new LoadDataParameters();

	@Override
	public Object start(IApplicationContext context) throws Exception {

		GeneralManager.get().init();

		/*
		 
		loadDataParameters
			.setFileName("data/genome/microarray/kashofer/all_hcc_clip_greater_10_plus_refseq.csv");
		loadDataParameters.setDelimiter(";");

		// loadDataParameters.setMinDefined(true);
		// loadDataParameters.setMin(min);
		// loadDataParameters.setMaxDefined(true);
		// loadDataParameters.setMax(max);

		dataDomain =
			(ATableBasedDataDomain) DataDomainManager.get()
				.createDataDomain("org.caleydo.datadomain.genetic");
		loadDataParameters.setDataDomain(dataDomain);

		loadDataParameters.setFileIDType(dataDomain.getHumanReadableRecordIDType());
		loadDataParameters.setMathFilterMode("Normal");
		loadDataParameters.setIsDataHomogeneous(true);
		loadDataParameters.setInputPattern("SKIP;FLOAT;FLOAT;");

		List<String> dimensionLabels = new ArrayList<String>();
		dimensionLabels.add("column 1");
		dimensionLabels.add("column 2");
		loadDataParameters.setDimensionLabels(dimensionLabels);

		DataTableUtils.createDimensions(loadDataParameters);

		// the place the matrix is stored:
		DataTable table = DataTableUtils.createData(dataDomain);
		if (table == null)
			throw new IllegalStateException("Problem while creating table!");

		// the default save path is usually your home directory
		new ProjectSaver().save(System.getProperty("user.home") + System.getProperty("file.separator")
			+ "test.cal", true);
		
		*/
		
		convertGctFile( "/Users/nils/Data/Caleydo/testdata/20110728/ov/mrna_cnmf/outputprefix.expclu.gct" );

		return IApplication.EXIT_OK;
	}

	@Override
	public void stop() {
	}

	protected int convertGctFile(String fileName) throws FileNotFoundException, IOException {
		
		String delimiter = "\t";
		
		// open file to read second line to determine number of rows and columns
		BufferedReader reader = new BufferedReader( new FileReader( fileName ) );
		
		// skip header ("#1.2")
		// TODO: check if file is indeed a gct file
		reader.readLine();
		
		// read dimensions of data matrix
		String dimensionString = reader.readLine();
		
		// TODO: check if there are two numeric columns
		String[] dimensions = dimensionString.split( delimiter );
		
		int rows = new Integer( dimensions[0] );		
		int columns = new Integer( dimensions[1] );
		
		// read column headers
		String headerString = reader.readLine();
				
		// TODO: check if there are as many column headers as there are columns (+ 2)
		String[] headers = headerString.split( delimiter );

		
		LoadDataParameters loadDataParameters = new LoadDataParameters();
		GeneticDataDomain dataDomain;

		loadDataParameters.setFileName(fileName);
		loadDataParameters.setDelimiter(delimiter);
		loadDataParameters.setStartParseFileAtLine( 3 );

		// loadDataParameters.setMinDefined(true);
		// loadDataParameters.setMin(min);
		// loadDataParameters.setMaxDefined(true);
		// loadDataParameters.setMax(max);

		dataDomain =
			(GeneticDataDomain) DataDomainManager.get()
				.createDataDomain("org.caleydo.datadomain.genetic");
		loadDataParameters.setDataDomain(dataDomain);

		loadDataParameters.setFileIDType(dataDomain.getHumanReadableRecordIDType());
		loadDataParameters.setMathFilterMode("Normal");
		loadDataParameters.setIsDataHomogeneous(true);
		
		// construct input pattern string based on number of columns in file
		StringBuffer buffer = new StringBuffer( "SKIP;SKIP;" );

		// list to store column labels
		List<String> dimensionLabels = new ArrayList<String>();
		
		for ( int i = 0; i < columns; ++i )
		{
			buffer.append( "FLOAT;" );			
			dimensionLabels.add( headers[i+2]);
		}
		
		loadDataParameters.setInputPattern( buffer.toString() );
		loadDataParameters.setDimensionLabels(dimensionLabels);

		DataTableUtils.createDimensions(loadDataParameters);

		// the place the matrix is stored:
		DataTable table = DataTableUtils.createData(dataDomain);
		if (table == null)
			throw new IllegalStateException("Problem while creating table!");

		// the default save path is usually your home directory
		new ProjectSaver().save(System.getProperty("user.home") + System.getProperty("file.separator")
			+ "gct.cal", true);

		return 0;
	}

}