package org.caleydo.core.data.datadomain;

import java.util.Set;
import org.caleydo.core.data.collection.table.LoadDataParameters;
import org.caleydo.core.data.id.IDCategory;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;

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
	public String getDataDomainID();

	/**
	 * Set the dataDomain Type.
	 * 
	 * @param dataDomainType
	 */
	public void setDataDomainID(String dataDomainType);

	/**
	 * Returns the icon representing the data contained in this domain
	 */
	public EIconTextures getIcon();

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

	// /**
	// * @return The dimension groups that have been created for this IDataDomain object (data set).
	// */
	// public List<DataContainer> getDimensionGroups();
	//
	// /**
	// * Sets the dimension groups for this IDataDomain object (data set).
	// *
	// * @param dimensionGroups
	// */
	// public void setDimensionGroups(List<DataContainer> dimensionGroups);
	//
	// /**
	// * Adds a dimension group to this IDataDomain object (data set).
	// *
	// * @param dimensionGroup
	// */
	// public void addDimensionGroup(DataContainer dimensionGroup);

	public String getDataDomainType();

	public void setDataDomainType(String dataDomainType);

	public Color getColor();

	public void setColor(Color color);

	/**
	 * @return The amount of data, i.e. the number of data items in the data set.
	 */
	public int getDataAmount();

	/**
	 * @return The ID categories that are defined for this datadomain.
	 */
	public Set<IDCategory> getIDCategories();

	/**
	 * Adds an ID category to this datadomain.
	 * 
	 * @param category
	 */
	public void addIDCategory(IDCategory category);

	public String getLabel();
}
