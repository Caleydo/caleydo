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
package org.caleydo.view.stratomex.tourguide;

import java.util.List;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.layout.util.multiform.MultiFormRenderer;
import org.caleydo.view.stratomex.brick.configurer.IBrickConfigurer;

import com.google.common.base.Predicate;

/**
 * view of stratomex to the {@link AAddWizardElement}
 * 
 * @author Samuel Gratzl
 * 
 */
public interface IStratomexAdapter {
	void replaceTemplate(TablePerspective with, IBrickConfigurer configurer);

	void replaceTemplate(ALayoutRenderer renderer);

	List<TablePerspective> getVisibleTablePerspectives();

	void selectStratification(Predicate<TablePerspective> filter);

	void selectGroup(Predicate<Pair<TablePerspective, Group>> filter);

	MultiFormRenderer createPreviewRenderer(TablePerspective tablePerspective);

	/**
	 * @param underlying
	 * @param numerical
	 */
	void replaceClinicalTemplate(Perspective underlying, TablePerspective numerical);
}
