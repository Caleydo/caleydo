/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.stratomex.wizard;

import java.util.List;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.view.stratomex.brick.configurer.IBrickConfigurer;

import com.google.common.base.Predicate;

/**
 * view of stratomex to the {@link AAddWizardElement}
 *
 * @author Samuel Gratzl
 *
 */
public interface IStratomexAdapter {
	void replaceTemplate(TablePerspective with, IBrickConfigurer configurer, boolean extra, Color highlight);

	void replaceTemplate(ALayoutRenderer renderer);

	List<TablePerspective> getVisibleTablePerspectives();

	void selectStratification(Predicate<TablePerspective> filter, boolean autoSelectLeftOfMe);

	void selectGroup(Predicate<Pair<TablePerspective, Group>> filter, boolean allowSelectAll);

	ALayoutRenderer createPreviewRenderer(PathwayGraph pathway);
	ALayoutRenderer createPreviewRenderer(TablePerspective tablePerspective);

	void replaceClinicalTemplate(Perspective underlying, TablePerspective numerical, boolean extra, Color highlight);

	void replacePathwayTemplate(Perspective underlying, PathwayGraph pathway, boolean extra, Color highlight);

}
