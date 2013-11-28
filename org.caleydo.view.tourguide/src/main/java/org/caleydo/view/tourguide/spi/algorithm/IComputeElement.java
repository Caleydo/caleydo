/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.spi.algorithm;

import java.util.Collection;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.view.tourguide.spi.compute.IComputedGroupScore;
import org.caleydo.view.tourguide.spi.compute.IComputedStratificationScore;

/**
 * a compute element is a basic unit for computing {@link IComputedGroupScore} or {@link IComputedStratificationScore}
 * on it
 *
 * @author Samuel Gratzl
 *
 */
public interface IComputeElement extends Iterable<Integer>, ILabeled {
	/**
	 * @return
	 */
	IDataDomain getDataDomain();

	/**
	 * unique id that identifiers this compute elements, which is used for caching
	 * 
	 * @return
	 */
	String getPersistentID();

	/**
	 * id type of the ids obtained by {@link #of(Group)} or {@link Iterable#iterator()}
	 *
	 * @return
	 */
	IDType getIdType();

	/**
	 * return the opposite id type to {@link #getIdType()}
	 *
	 * @return
	 */
	IDType getDimensionIdType();

	/**
	 * return all groups of this elements
	 *
	 * @return
	 */
	Collection<Group> getGroups();

	/**
	 * number of groups
	 *
	 * @return
	 */
	int getGroupSize();

	/**
	 * returns the id of a group or all ids if null is given
	 *
	 * @param group
	 *            may be null for all
	 * @return
	 */
	Collection<Integer> of(Group group);


	/**
	 * number of ids
	 *
	 * @return
	 */
	int size();

	/**
	 * @return
	 */
	Iterable<Integer> getDimensionIDs();

}
