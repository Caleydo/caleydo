/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.correlation;

import java.util.List;

import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.event.EventListenerManager;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventListenerManagers;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.io.NumericalProperties;
import org.caleydo.core.util.base.ICallback;
import org.caleydo.core.util.color.Color;
import org.caleydo.view.enroute.correlation.widget.AClassificationWidget;
import org.caleydo.view.enroute.correlation.widget.CategoricalClassificationWidget;
import org.caleydo.view.enroute.correlation.widget.DataCellInfoWidget;
import org.caleydo.view.enroute.correlation.widget.NumericalClassificationWidget;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * @author Christian
 *
 */
public abstract class ASelectDataCellPage extends WizardPage implements IPageChangedListener, ICallback<IDataClassifier> {

	protected final EventListenerManager listeners = EventListenerManagers.createSWTDirect();

	protected DataCellInfoWidget dataCellInfoWidget;
	protected Group classificationGroup;
	protected DataCellInfo info;

	protected AClassificationWidget classificationWidget;
	protected List<Color> categoryColors;

	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	protected ASelectDataCellPage(String pageName, String title, ImageDescriptor titleImage, List<Color> categoryColors) {
		super(pageName, title, titleImage);
		listeners.register(this);
		this.categoryColors = categoryColors;
	}

	@Override
	public void createControl(Composite parent) {

		Composite parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		parentComposite.setLayout(new GridLayout(2, false));

		Group infoGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		infoGroup.setText("Selected Data Block:");
		infoGroup.setLayout(new GridLayout(1, true));
		infoGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		dataCellInfoWidget = new DataCellInfoWidget(infoGroup);

		classificationGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		classificationGroup.setText("Data Classification:");
		classificationGroup.setLayout(new GridLayout(3, false));
		classificationGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		classificationGroup.setVisible(false);

		setControl(parentComposite);

	}

	@Override
	public boolean isPageComplete() {
		if (classificationWidget == null)
			return false;
		return super.isPageComplete();
	}

	@ListenTo
	public void onDataCellSelected(DataCellSelectionEvent event) {
		if (isCurrentPage()) {
			info = event.getInfo();

			dataCellInfoWidget.updateInfo(info);

			classificationGroup.setVisible(true);

			Object description = info.dataDomain.getDataClassSpecificDescription(info.rowIDType, info.rowID,
					info.columnPerspective.getIdType(), info.columnPerspective.getVirtualArray().get(0));

			if (description == null || description instanceof NumericalProperties) {
				if (classificationWidget == null) {
					classificationWidget = new NumericalClassificationWidget(classificationGroup, SWT.NONE,
							categoryColors);
					initNewClassificationWidget();
				} else if (!(classificationWidget instanceof NumericalClassificationWidget)) {
					classificationWidget.dispose();
					classificationWidget = new NumericalClassificationWidget(classificationGroup, SWT.NONE,
							categoryColors);
					initNewClassificationWidget();
				}

			} else if (description instanceof CategoricalClassDescription) {
				if (classificationWidget == null) {
					classificationWidget = new CategoricalClassificationWidget(classificationGroup, SWT.NONE,
							categoryColors);
					initNewClassificationWidget();
				} else if (!(classificationWidget instanceof CategoricalClassificationWidget)) {
					classificationWidget.dispose();
					classificationWidget = new CategoricalClassificationWidget(classificationGroup, SWT.NONE,
							categoryColors);
					initNewClassificationWidget();
				}
			} else {
				throw new UnsupportedOperationException("Could not determine data type");
			}

			classificationWidget.updateData(info);
			EventPublisher.trigger(new ShowOverlayEvent(info, classificationWidget.getClassifier().getOverlayProvider(),
					getWizard().getStartingPage() == this));
		}
	}

	protected void initNewClassificationWidget() {
		classificationWidget.addCallback(this);

		classificationGroup.getShell().layout(true, true);
		classificationGroup.getShell().pack(true);
		// classificationGroup.update();
		// classificationGroup.pack(true);
		getWizard().getContainer().updateButtons();
	}

	@Override
	public void dispose() {
		listeners.unregisterAll();
		super.dispose();
	}


	@Override
	public void on(IDataClassifier data) {
		EventPublisher.trigger(new ShowOverlayEvent(info, data.getOverlayProvider(), getWizard().getStartingPage() == this));
	}

}
