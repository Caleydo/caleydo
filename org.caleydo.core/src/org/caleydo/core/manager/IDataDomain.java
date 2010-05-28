package org.caleydo.core.manager;

import java.util.ArrayList;
import java.util.Set;

import org.caleydo.core.data.collection.set.LoadDataParameters;

/**
 * Use cases are the unique points of coordinations for views and its data. Genetic data is one example -
 * another is a more generic one where Caleydo can load arbitrary tabular data but without any special
 * features of genetic analysis.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public interface IDataDomain {

	/**
	 * Returns the qualified name of the concrete data domain
	 */
	public String getDataDomainType();

	/**
	 * Returns a list of views that can visualize the data in the domain
	 * 
	 * @return
	 */
	public ArrayList<String> getPossibleViews();

	/**
	 * Gets the parameters for loading the data-{@link Set} contained in this use case
	 * 
	 * @return parameters for loading the data-{@link Set} of this use case
	 */
	public LoadDataParameters getLoadDataParameters();

	/**
	 * Sets the parameters for loading the data-{@link Set} contained in this use case
	 * 
	 * @param loadDataParameters
	 *            parameters for loading the data-{@link Set} of this use case
	 */
	public void setLoadDataParameters(LoadDataParameters loadDataParameters);

	/** Sets the name of the boots-trap xml-file this useCase was or should be loaded */
	public String getBootstrapFileName();

	/** Gets the name of the boots-trap xml-file this useCase was or should be loaded */
	public void setBootstrapFileName(String bootstrapFileName);

}
