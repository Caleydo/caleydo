/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
