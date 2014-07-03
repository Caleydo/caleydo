/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.filter;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.view.UseRandomSamplingEvent;
import org.caleydo.core.gui.SimpleAction;

public class UseRandomSamplingAction extends SimpleAction {

	public static final String LABEL = "Use random sampling";
	public static final String ICON = "resources/icons/view/tablebased/random_sampling.png";

	private boolean bFlag = true;

	public UseRandomSamplingAction() {
		super(LABEL, ICON);
		setChecked(bFlag);
	}

	@Override
	public void run() {
		super.run();
		bFlag = !bFlag;
		
		EventPublisher.trigger(new UseRandomSamplingEvent(bFlag));
	}
}
