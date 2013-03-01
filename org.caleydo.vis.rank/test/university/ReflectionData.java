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
package university;

import java.lang.reflect.Field;
import java.util.Objects;

import org.caleydo.vis.rank.model.IRow;

import com.google.common.base.Function;

/**
 * @author Samuel Gratzl
 *
 */
public class ReflectionData implements Function<IRow, String> {
	private final Field field;

	public ReflectionData(Field field) {
		this.field = field;
		field.setAccessible(true);
	}

	@Override
	public String apply(IRow in) {
		try {
			return Objects.toString(field.get(in));
		} catch (IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}

