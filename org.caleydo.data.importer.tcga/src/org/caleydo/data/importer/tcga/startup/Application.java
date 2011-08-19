package org.caleydo.data.importer.tcga.startup;

import org.caleydo.core.data.collection.dimension.ADimension;
import org.caleydo.core.data.collection.dimension.NominalDimension;
import org.caleydo.core.data.collection.dimension.NumericalDimension;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.collection.table.LoadDataParameters;
import org.caleydo.core.parser.ascii.TabularAsciiDataReader;

public class Application {

	public static void main(String[] args) {
		System.out.println("Hello World");

		// class which takes the parameters for parsing
		LoadDataParameters parameters;
		// parser
		TabularAsciiDataReader parser;
		// the place the matrix is stored:
		DataTable table;
		// Each column is stored in one of these:
		ADimension dimension;
		NumericalDimension numDimension;
		NominalDimension<String> nominalDimension;
	}

}
