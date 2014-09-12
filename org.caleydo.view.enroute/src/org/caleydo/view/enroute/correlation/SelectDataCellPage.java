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
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

/**
 * @author Christian
 *
 */
public class SelectDataCellPage extends WizardPage implements IPageChangedListener, ICallback<IDataClassifier> {

	private final EventListenerManager listeners = EventListenerManagers.createSWTDirect();

	private Label datasetLabel;
	private Label groupLabel;
	private Label rowLabel;

	private Group classificationGroup;
	DataCellInfo info;

	private AClassificationWidget classificationWidget;
	private List<Color> categoryColors;

	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	protected SelectDataCellPage(String pageName, String title, ImageDescriptor titleImage, List<Color> categoryColors) {
		super(pageName, title, titleImage);
		listeners.register(this);
		this.categoryColors = categoryColors;
	}

	@Override
	public void createControl(Composite parent) {

		Composite parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		parentComposite.setLayout(new GridLayout(2, false));

		Group dataInfoGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		dataInfoGroup.setText("Selected Data Block:");
		dataInfoGroup.setLayout(new GridLayout(1, true));
		dataInfoGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		datasetLabel = new Label(dataInfoGroup, SWT.NONE);
		datasetLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		datasetLabel.setText("Dataset: ");

		groupLabel = new Label(dataInfoGroup, SWT.NONE);
		groupLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		groupLabel.setText("Group: ");

		rowLabel = new Label(dataInfoGroup, SWT.NONE);
		rowLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		rowLabel.setText("Row: ");

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

			datasetLabel.setText("Dataset: " + info.getDataDomainLabel());
			groupLabel.setText("Group: " + info.getGroupLabel());
			rowLabel.setText("Row: " + info.getRowLabel());

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

	private void initNewClassificationWidget() {
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
	public void pageChanged(PageChangedEvent event) {
		if (event.getSelectedPage() == getNextPage()) {

			CalculateCorrelationWizard wizard = (CalculateCorrelationWizard) getWizard();
			wizard.setPageInfo(this, info, classificationWidget.getClassifier());
		}

	}

	@Override
	public void on(IDataClassifier data) {
		EventPublisher.trigger(new ShowOverlayEvent(info, data.getOverlayProvider(), getWizard().getStartingPage() == this));
	}

}
