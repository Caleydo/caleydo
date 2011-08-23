package org.caleydo.data.importer.tcga.startup;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.collection.table.DataTableUtils;
import org.caleydo.core.data.collection.table.LoadDataParameters;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ProjectSaver;
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
		new ProjectSaver().save("test.cal");
		
		return IApplication.EXIT_OK;
	}

	@Override
	public void stop() {
	}

}