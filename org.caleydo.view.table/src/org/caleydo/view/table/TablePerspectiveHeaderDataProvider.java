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
	private final VirtualArray va;
	private final boolean isDimension;
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