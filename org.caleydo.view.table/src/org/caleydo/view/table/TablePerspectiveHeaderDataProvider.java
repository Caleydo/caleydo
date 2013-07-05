/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.table;

import java.util.Set;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IIDTypeMapper;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

/**
 * a special header {@link IDataProvider} using the human readable id types
 *
 * @author Samuel Gratzl
 *
 */
public class TablePerspectiveHeaderDataProvider implements IDataProvider {
	/**
	 * underlying {@link VirtualArray}
	 */
	private final VirtualArray va;
	/**
	 * used for the columns = isDimension or rows = records
	 */
	private final boolean isDimension;
	/**
	 * mapper from id to human readable values
	 */
	private final IIDTypeMapper<Integer, String> mapper;

	public TablePerspectiveHeaderDataProvider(TablePerspective tablePerspective, boolean isDimension) {
		this.isDimension = isDimension;
		Perspective p = isDimension ? tablePerspective.getDimensionPerspective() : tablePerspective
				.getRecordPerspective();
		va = p.getVirtualArray();
		final IDMappingManager idMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(va.getIdType());
		mapper = idMappingManager.getIDTypeMapper(va.getIdType(), va.getIdType().getIDCategory()
				.getHumanReadableIDType());
	}

	@Override
	public int getColumnCount() {
		return isDimension ? va.size() : 1;
	}

	@Override
	public Object getDataValue(int columnIndex, int rowIndex) {
		int index = isDimension ? columnIndex : rowIndex;
		int lookup = va.get(index);
		Set<String> s = mapper.apply(lookup);
		if (s == null || s.isEmpty())
			return "" + index;
		return s.iterator().next();
	}

	@Override
	public int getRowCount() {
		return isDimension ? 1 : va.size();
	}

	@Override
	public void setDataValue(int arg0, int arg1, Object arg2) {
		throw new UnsupportedOperationException();
	}

}
