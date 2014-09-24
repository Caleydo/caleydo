/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.correlation.wilcoxon;

import java.util.EnumSet;

import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.data.collection.column.container.CategoricalClassDescription.ECategoryType;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.view.enroute.correlation.ASelectDataCellPage;
import org.caleydo.view.enroute.correlation.CellSelectionValidators;
import org.caleydo.view.enroute.correlation.DataCellInfo;
import org.caleydo.view.enroute.correlation.ShowOverlayEvent;
import org.caleydo.view.enroute.correlation.UpdateDataCellSelectionValidatorEvent;
import org.caleydo.view.enroute.correlation.widget.DerivedClassificationWidget;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Layout;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * @author Christian
 *
 */
public class WilcoxonManualTargetDataCellPage extends ASelectDataCellPage {

	protected Group classificationGroup;
	protected DerivedClassificationWidget classificationWidget;

	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 * @param categoryColors
	 */
	protected WilcoxonManualTargetDataCellPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
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
						.getOverlayProvider(), false));
			}
		} else if (event.getSelectedPage() == getNextPage()) {
			wizard.setTargetInfo(info);
		}
	}

	@Override
	public boolean isPageComplete() {
		if (classificationWidget == null)
			return false;
		return super.isPageComplete();
	}


	@Override
	public void dispose() {
		listeners.unregisterAll();
		super.dispose();
	}

	@Override
	protected void createWidgets(Composite parentComposite) {
		classificationGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		classificationGroup.setText("Data Classification:");
		classificationGroup.setLayout(new GridLayout(3, false));
		classificationGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		classificationGroup.setVisible(false);

	}

	@Override
	protected Layout getBaseLayout() {
		return new GridLayout(2, false);
	}

	@Override
	protected void dataCellChanged(DataCellInfo info) {
		Object description = info.dataDomain.getDataClassSpecificDescription(info.rowIDType, info.rowID,
				info.columnPerspective.getIdType(), info.columnPerspective.getVirtualArray().get(0));

		if (description != null && description instanceof CategoricalClassDescription) {

			CategoricalClassDescription<?> desc = (CategoricalClassDescription<?>) description;
			if (desc.getCategoryType() == ECategoryType.NOMINAL) {
				throw new UnsupportedOperationException("Wilcoxon rank-sum test cannot be applied to nominal data");
			}
		}

		classificationGroup.setVisible(true);

		if (classificationWidget == null) {

			classificationWidget = new DerivedClassificationWidget(classificationGroup);
			classificationGroup.getShell().layout(true, true);
			classificationGroup.getShell().pack(true);
			getWizard().getContainer().updateButtons();

			WilcoxonRankSumTestWizard wizard = (WilcoxonRankSumTestWizard) getWizard();
			classificationWidget.setClassifier(wizard.getDerivedIDClassifier());
		}

		EventPublisher.trigger(new ShowOverlayEvent(info, classificationWidget.getClassifier().getOverlayProvider(),
				false));

	}

	@Override
	public IWizardPage getNextPage() {
		return ((WilcoxonRankSumTestWizard) getWizard()).getManualResultPage();
	}

	@Override
	public IWizardPage getPreviousPage() {
		return ((WilcoxonRankSumTestWizard) getWizard()).getManualSourceDataCellPage();
	}

}
