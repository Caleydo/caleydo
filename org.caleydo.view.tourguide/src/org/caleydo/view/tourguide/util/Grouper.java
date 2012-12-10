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
package org.caleydo.view.tourguide.util;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.id.IDType;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimaps;

/**
 * @author Samuel Gratzl
 *
 */
public class Grouper {
	public static ImmutableListMultimap<IDType, TablePerspective> byIDType(Iterable<TablePerspective> list) {
		return Multimaps.index(list, new Function<TablePerspective, IDType>() {
			@Override
			public IDType apply(TablePerspective s) {
				return s.getRecordPerspective().getIdType();
			}
		});
	}

	public static ImmutableListMultimap<ATableBasedDataDomain, TablePerspective> byDataDomain(
			Iterable<TablePerspective> list) {
		return Multimaps.index(list, new Function<TablePerspective, ATableBasedDataDomain>() {
			@Override
			public ATableBasedDataDomain apply(TablePerspective s) {
				return s.getDataDomain();
			}
		});
	}
}
