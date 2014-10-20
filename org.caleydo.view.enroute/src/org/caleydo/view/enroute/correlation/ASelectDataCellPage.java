/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.correlation;

import org.caleydo.core.event.EventListenerManager;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventListenerManagers;
import org.caleydo.view.enroute.correlation.widget.DataCellInfoWidget;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Layout;

/**
 * @author Christian
 *
 */
public abstract class ASelectDataCellPage extends WizardPage implements IPageChangedListener {

	protected final EventListenerManager listeners = EventListenerManagers.createSWTDirect();

	protected Group instructionsGroup;
	protected DataCellInfoWidget dataCellInfoWidget;
	protected DataCellInfo info;


	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	protected ASelectDataCellPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
		listeners.register(this);
	}

	@Override
	public void createControl(Composite parent) {

		Composite parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		parentComposite.setLayout(getBaseLayout());

		Group infoGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		infoGroup.setText("Selected Data Block:");
		infoGroup.setLayout(new GridLayout(1, true));
		infoGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		dataCellInfoWidget = new DataCellInfoWidget(infoGroup);

		// instructionsGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		// instructionsGroup.setText("Instructions:");
		// instructionsGroup.setLayout(new GridLayout(1, true));
		// instructionsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		createWidgets(parentComposite);

		setControl(parentComposite);

	}

	protected abstract void createWidgets(Composite parentComposite);

	protected abstract Layout getBaseLayout();

	protected abstract void dataCellChanged(DataCellInfo info);


	@ListenTo
	public void onDataCellSelected(DataCellSelectionEvent event) {
		if (isCurrentPage()) {
			info = event.getInfo();

			dataCellInfoWidget.updateInfo(info);
			dataCellChanged(info);
		}
	}


	@Override
	public void dispose() {
		listeners.unregisterAll();
		super.dispose();
	}

}
