/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi.toolbar;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.view.dvi.event.ApplySpecificGraphLayoutEvent;
import org.caleydo.view.dvi.layout.ForceDirectedGraphLayout;
import org.eclipse.jface.action.Action;

public class ApplySpringBasedLayoutAction extends Action {

	public static final String LABEL = "Apply Spring-Based Layout";

	public ApplySpringBasedLayoutAction() {
		setText(LABEL);
		setToolTipText(LABEL);
		setChecked(false);
	}

	@Override
	public void run() {
		super.run();
		ApplySpecificGraphLayoutEvent event = new ApplySpecificGraphLayoutEvent();
		event.setGraphLayoutClass(ForceDirectedGraphLayout.class);
		event.setSender(this);
		
		EventPublisher.INSTANCE.triggerEvent(event);
		setChecked(false);
	}
}
