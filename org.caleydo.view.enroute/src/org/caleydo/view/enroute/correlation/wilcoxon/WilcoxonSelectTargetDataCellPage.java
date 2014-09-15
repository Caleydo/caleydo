/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.correlation.wilcoxon;

import java.util.EnumSet;

import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.data.collection.column.container.CategoricalClassDescription.ECategoryType;
import org.caleydo.core.event.EventListenerManager;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventListenerManagers;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.view.enroute.correlation.CellSelectionValidators;
import org.caleydo.view.enroute.correlation.DataCellInfo;
import org.caleydo.view.enroute.correlation.DataCellSelectionEvent;
import org.caleydo.view.enroute.correlation.ShowOverlayEvent;
import org.caleydo.view.enroute.correlation.UpdateDataCellSelectionValidatorEvent;
import org.caleydo.view.enroute.correlation.widget.DataCellInfoWidget;
import org.caleydo.view.enroute.correlation.widget.DerivedClassificationWidget;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * @author Christian
 *
 */
public class WilcoxonSelectTargetDataCellPage extends WizardPage implements IPageChangedListener {

	protected final EventListenerManager listeners = EventListenerManagers.createSWTDirect();

	protected DataCellInfoWidget dataCellInfoWidget;
	protected Group classificationGroup;
	protected DataCellInfo info;
	protected DerivedClassificationWidget classificationWidget;

	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 * @param categoryColors
	 */
	protected WilcoxonSelectTargetDataCellPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
		listeners.register(this);
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
	public void pageChanged(PageChangedEvent event) {
		WilcoxonRankSumTestWizard wizard = (WilcoxonRankSumTestWizard) getWizard();
		if (event.getSelectedPage() == this) {

			Predicate<DataCellInfo> validator = Predicates.and(
					CellSelectionValidators.nonEmptyCellValidator(),
					Predicates.or(CellSelectionValidators.numericalValuesValidator(),
							CellSelectionValidators.categoricalValuesValidator(EnumSet.of(ECategoryType.ORDINAL))));

			EventPublisher.trigger(new UpdateDataCellSelectionValidatorEvent(validator));
			if (classificationWidget != null) {
				classificationWidget.setClassifier(wizard.getDerivedIDClassifier());
				EventPublisher.trigger(new ShowOverlayEvent(info, classificationWidget.getClassifier()
						.getOverlayProvider(), getWizard().getStartingPage() == this));
			}
		} else if (event.getSelectedPage() == getNextPage()) {
			wizard.setInfo2(info);
		}
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
			Object description = info.dataDomain.getDataClassSpecificDescription(info.rowIDType, info.rowID,
					info.columnPerspective.getIdType(), info.columnPerspective.getVirtualArray().get(0));

			if (description != null && description instanceof CategoricalClassDescription) {

				CategoricalClassDescription<?> desc = (CategoricalClassDescription<?>) description;
				if (desc.getCategoryType() == ECategoryType.NOMINAL) {
					throw new UnsupportedOperationException("Wilcoxon rank-sum test cannot be applied to nominal data");
				}
			}

			dataCellInfoWidget.updateInfo(info);

			classificationGroup.setVisible(true);

			if (classificationWidget == null) {

				classificationWidget = new DerivedClassificationWidget(classificationGroup);
				classificationGroup.getShell().layout(true, true);
				classificationGroup.getShell().pack(true);
				getWizard().getContainer().updateButtons();

				WilcoxonRankSumTestWizard wizard = (WilcoxonRankSumTestWizard) getWizard();
				classificationWidget.setClassifier(wizard.getDerivedIDClassifier());
			}

			EventPublisher.trigger(new ShowOverlayEvent(info,
					classificationWidget.getClassifier().getOverlayProvider(), getWizard().getStartingPage() == this));
		}
	}

	@Override
	public void dispose() {
		listeners.unregisterAll();
		super.dispose();
	}

}
