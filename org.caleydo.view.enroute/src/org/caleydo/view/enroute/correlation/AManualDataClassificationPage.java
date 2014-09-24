/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.correlation;

import java.util.List;

import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.io.NumericalProperties;
import org.caleydo.core.util.base.ICallback;
import org.caleydo.core.util.color.Color;
import org.caleydo.view.enroute.correlation.widget.AClassificationWidget;
import org.caleydo.view.enroute.correlation.widget.CategoricalClassificationWidget;
import org.caleydo.view.enroute.correlation.widget.NumericalClassificationWidget;
import org.eclipse.jface.resource.ImageDescriptor;
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
public abstract class AManualDataClassificationPage extends ASelectDataCellPage implements ICallback<IDataClassifier> {

	protected Group classificationGroup;
	protected AClassificationWidget classificationWidget;
	protected List<Color> categoryColors;

	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 * @param categoryColors
	 */
	protected AManualDataClassificationPage(String pageName, String title, ImageDescriptor titleImage,
			List<Color> categoryColors) {
		super(pageName, title, titleImage);
		this.categoryColors = categoryColors;
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
	public boolean isPageComplete() {
		if (classificationWidget == null)
			return false;
		return super.isPageComplete();
	}

	@Override
	protected void dataCellChanged(DataCellInfo info) {
		classificationGroup.setVisible(true);

		Object description = info.dataDomain.getDataClassSpecificDescription(info.rowIDType, info.rowID,
				info.columnPerspective.getIdType(), info.columnPerspective.getVirtualArray().get(0));

		if (description == null || description instanceof NumericalProperties) {
			if (classificationWidget == null) {
				classificationWidget = new NumericalClassificationWidget(classificationGroup, SWT.NONE, categoryColors);
				initNewClassificationWidget();
			} else if (!(classificationWidget instanceof NumericalClassificationWidget)) {
				classificationWidget.dispose();
				classificationWidget = new NumericalClassificationWidget(classificationGroup, SWT.NONE, categoryColors);
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

	}

	protected void initNewClassificationWidget() {
		classificationWidget.addCallback(this);

		classificationGroup.getShell().layout(true, true);
		classificationGroup.getShell().pack(true);
		getWizard().getContainer().updateButtons();
	}

	@Override
	public abstract void on(IDataClassifier data);

}
