/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.model;

import java.util.Collection;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.caleydo.vis.rank.model.IRow;

/**
 * @author Samuel Gratzl
 *
 */
public interface ITablePerspectiveScoreRow extends IRow {
	TablePerspective asTablePerspective();

	IDataDomain getDataDomain();

	Pair<Collection<Integer>, IDType> getIntersection(Collection<IScore> visibleColumns, Group group);

	/**
	 * @return
	 */
	IDType getIdType();

}
