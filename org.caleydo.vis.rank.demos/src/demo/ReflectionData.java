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
package demo;

import java.lang.reflect.Field;

import org.caleydo.vis.rank.model.IRow;

import com.google.common.base.Function;

/**
 * @author Samuel Gratzl
 *
 */
public class ReflectionData<T> implements Function<IRow, T> {
	private final Field field;
	private final Class<T> clazz;

	public ReflectionData(Field field, Class<T> clazz) {
		this.field = field;
		this.clazz = clazz;
		field.setAccessible(true);
	}

	@Override
	public T apply(IRow in) {
		try {
			Object r = field.get(in);
			if (clazz == String.class)
				return clazz.cast(r == null ? "" : r.toString());
			if (clazz.isInstance(r))
				return clazz.cast(r);
			return null;
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
}

