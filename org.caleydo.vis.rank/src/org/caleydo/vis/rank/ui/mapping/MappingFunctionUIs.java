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
package org.caleydo.vis.rank.ui.mapping;

import java.awt.Color;

import org.caleydo.core.io.gui.dataimport.widget.ICallback;
import org.caleydo.core.util.function.IFloatList;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.vis.rank.model.mapping.IMappingFunction;
import org.caleydo.vis.rank.model.mapping.PiecewiseLinearMapping;

/**
 * @author Samuel Gratzl
 *
 */
public class MappingFunctionUIs {
	public static GLElement create(IMappingFunction model, IFloatList data, Color color, Color bgColor,
			ICallback<IMappingFunction> callback) {
		if (model instanceof PiecewiseLinearMapping)
			return new PiecewiseLinearMappingUI((PiecewiseLinearMapping) model, data, color, bgColor, callback);
		return null;
	}

}
