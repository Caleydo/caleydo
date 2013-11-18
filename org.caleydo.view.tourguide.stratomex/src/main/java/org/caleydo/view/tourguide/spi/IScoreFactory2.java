/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.spi;

import java.util.List;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.view.tourguide.api.state.EWizardMode;
import org.caleydo.view.tourguide.api.state.IStateMachine;
import org.caleydo.view.tourguide.spi.IScoreFactory;

/**
 * @author Samuel Gratzl
 *
 */
public interface IScoreFactory2 extends IScoreFactory {
	void fillStateMachine(IStateMachine stateMachine, List<TablePerspective> existing, EWizardMode mode,
			TablePerspective source);
}
