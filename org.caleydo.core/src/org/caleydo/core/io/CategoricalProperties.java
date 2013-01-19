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
package org.caleydo.core.io;

/**
 * <p>
 * Properties for a homogeneous categorical dataset. In an homogeneous categorical dataset all the columns in the file
 * are of the same semantic data type and have the same value ranges. The data set can be interpreted as a unit, i.e.
 * the overall distribution, etc. is meaningful.
 * </p>
 * <p>
 * This class provides various properties to modify the dataset, such as a list of categories, and order of categories,
 * etc. You have to specify a {@link CategoricalProperties} in the {@link DataSetDescription} even if you don't
 * want to change the defaults of these parameters to convey that the dataset is indeed categorical and homogeneous.
 * </p>
 * <p>
 * See also {@link NumericalProperties}, this classes pendant for numerical data.
 * </p>
 *
 * @author Alexander Lex
 *
 */
public class CategoricalProperties {
	// category list
	// category order

	/**
	 *
	 */
	public CategoricalProperties() {
	}

}
