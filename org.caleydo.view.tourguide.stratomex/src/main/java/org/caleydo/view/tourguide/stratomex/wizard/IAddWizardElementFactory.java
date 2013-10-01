/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.stratomex.wizard;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.opengl.canvas.AGLView;

/**
 * @author Samuel Gratzl
 *
 */
public interface IAddWizardElementFactory {
	AAddWizardElement create(IStratomexAdapter adapter, AGLView view);

	AAddWizardElement createDependent(IStratomexAdapter adapter, AGLView view, TablePerspective tablePerspective);

	AAddWizardElement createIndepenent(IStratomexAdapter adapter, AGLView view, TablePerspective source);

	AAddWizardElement createForOther(IStratomexAdapter adapter, AGLView view);

	AAddWizardElement createForPathway(IStratomexAdapter adapter, AGLView view);

	AAddWizardElement createForStratification(IStratomexAdapter adapter, AGLView view);
}
