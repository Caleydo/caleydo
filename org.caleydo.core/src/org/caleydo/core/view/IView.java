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
package org.caleydo.core.view;

import java.util.Set;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.base.IDefaultLabelHolder;
import org.caleydo.core.util.base.IUniqueObject;

/**
 * Interface for the view representations.
 *
 * @author Marc Streit
 * @author Alexander Lex
 */
public interface IView
 extends IUniqueObject, IDefaultLabelHolder {

	/**
	 * Initializes the view after setting all required parameters.
	 */
	public void initialize();

	/**
	 * Retrieves a serializable representation of the view
	 *
	 * @return serialized representation of the view
	 */
	public ASerializedView getSerializableRepresentation();

	/**
	 * Initializes the view with the values from the given {@link ASerializedView}.
	 *
	 * @param serializedView
	 *            serialized representation of the view.
	 */
	public void initFromSerializableRepresentation(ASerializedView serializedView);

	/**
	 * Get the name of the type of the view.
	 *
	 * @return
	 */
	public String getViewType();

	/**
	 * @return the instanceNumber, see {@link #instanceNumber}
	 */
	public int getInstanceNumber();

	/**
	 * @param instanceNumber
	 *            setter, see {@link #instanceNumber}
	 */
	public void setInstanceNumber(int instanceNumber);

	/**
	 * Determines whether the view displays concrete data of a data set or not.
	 *
	 * @return
	 */
	public boolean isDataView();

	/**
	 * @return A Copy of the datadomain set of this view.
	 */
	public Set<IDataDomain> getDataDomains();

}
