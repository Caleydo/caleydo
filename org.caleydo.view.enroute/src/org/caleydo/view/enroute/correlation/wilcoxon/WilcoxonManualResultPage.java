/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.correlation.wilcoxon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.math3.stat.inference.MannWhitneyUTest;
import org.apache.commons.math3.stat.ranking.NaNStrategy;
import org.apache.commons.math3.stat.ranking.TiesStrategy;
import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.data.collection.column.container.CategoricalClassDescription.ECategoryType;
import org.caleydo.core.io.NumericalProperties;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.function.AdvancedDoubleStatistics;
import org.caleydo.view.enroute.correlation.DataCellInfo;
import org.caleydo.view.enroute.correlation.SimpleCategory;
import org.caleydo.view.enroute.correlation.SimpleIDClassifier;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

/**
 * @author Christian
 *
 */
public class WilcoxonManualResultPage extends WizardPage implements IPageChangedListener {

	protected Set<org.eclipse.swt.graphics.Color> colorRegistry = new HashSet<>();

	private Label uValueLabel;
	private Label pValueLabel;

	private CLabel[] classColorLabel = new CLabel[2];

	private Label[] classLabel = new Label[2];
	private Label[] classNumElementsLabel = new Label[2];
	private Label[] classMedianLabel = new Label[2];

	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	protected WilcoxonManualResultPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	@Override
	public void createControl(Composite parent) {
		Composite parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		parentComposite.setLayout(new GridLayout(1, false));
		Group summaryGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		summaryGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		summaryGroup.setLayout(new GridLayout(2, false));
		summaryGroup.setText("Summary");
		createClassSummary(summaryGroup, 0);
		createClassSummary(summaryGroup, 1);

		Group resultsGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		resultsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		resultsGroup.setLayout(new GridLayout(1, false));
		resultsGroup.setText("Results");

		uValueLabel = createLabel(resultsGroup, "U: ");
		pValueLabel = createLabel(resultsGroup, "P-Value: ");

		setControl(parentComposite);

	}

	private void createClassSummary(Composite parent, int classNumber) {
		Composite colorLabelComposite = new Composite(parent, SWT.NONE);
		colorLabelComposite.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true));
		colorLabelComposite.setLayout(new GridLayout(1, false));
		classColorLabel[classNumber] = new CLabel(colorLabelComposite, SWT.BORDER);
		GridData gridData = new GridData(SWT.LEFT, SWT.TOP, false, false);
		gridData.widthHint = 25;
		gridData.heightHint = 25;
		classColorLabel[classNumber].setLayoutData(gridData);
		org.eclipse.swt.graphics.Color c = Color.BLACK.getSWTColor(Display.getCurrent());
		classColorLabel[classNumber].setBackground(c);
		classColorLabel[classNumber].update();
		colorRegistry.add(c);

		Composite textLabelComposite = new Composite(parent, SWT.NONE);
		textLabelComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		textLabelComposite.setLayout(new GridLayout(1, false));
		classLabel[classNumber] = createLabel(textLabelComposite, "Class Name");
		classNumElementsLabel[classNumber] = createLabel(textLabelComposite, "Number of Elements: ");
		classMedianLabel[classNumber] = createLabel(textLabelComposite, "Median: ");

	}

	private Label createLabel(Composite parentComposite, String text) {
		Label resultLabel = new Label(parentComposite, SWT.NONE);
		resultLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		resultLabel.setText(text);
		return resultLabel;
	}

	@Override
	public void pageChanged(PageChangedEvent event) {
		if (event.getSelectedPage() == this) {
			WilcoxonRankSumTestWizard wizard = (WilcoxonRankSumTestWizard) getWizard();
			DataCellInfo info = wizard.getTargetInfo();
			SimpleIDClassifier derivedClassifier = wizard.getDerivedIDClassifier();
			MannWhitneyUTest test = new MannWhitneyUTest(NaNStrategy.REMOVED, TiesStrategy.AVERAGE);
			double[] values1 = getSampleValues(info, derivedClassifier.getClass1IDs());
			double[] values2 = getSampleValues(info, derivedClassifier.getClass2IDs());
			updateClassSummary(0, values1, derivedClassifier.getDataClasses().get(0));
			updateClassSummary(1, values2, derivedClassifier.getDataClasses().get(1));
			double u = test.mannWhitneyU(values1, values2);
			double p = test.mannWhitneyUTest(values1, values2);

			uValueLabel.setText(String.format(Locale.ENGLISH, "U: %.8f", u));
			pValueLabel.setText(String.format(Locale.ENGLISH, "P-Value: %.8f", p));
		}

	}

	private void updateClassSummary(int classNumber, double[] values, SimpleCategory category) {
		org.eclipse.swt.graphics.Color c = category.color.getSWTColor(Display.getCurrent());
		classColorLabel[classNumber].setBackground(c);
		classColorLabel[classNumber].update();
		colorRegistry.add(c);

		classLabel[classNumber].setText(category.name);
		classNumElementsLabel[classNumber].setText("Number of Elements: " + values.length);
		AdvancedDoubleStatistics stats = AdvancedDoubleStatistics.of(values);
		classMedianLabel[classNumber].setText(String.format(Locale.ENGLISH, "Median: %.8f", stats.getMedian()));
	}

	protected double[] getSampleValues(DataCellInfo info, Set<Object> columnIDs) {
		Object description = info.dataDomain.getDataClassSpecificDescription(info.rowIDType, info.rowID,
				info.columnPerspective.getIdType(), info.columnPerspective.getVirtualArray().get(0));

		List<Double> values = new ArrayList<>();

		if (description == null || description instanceof NumericalProperties) {

			for (Object columnID : columnIDs) {
				Number value = (Number) info.dataDomain.getRaw(info.columnPerspective.getIdType(), (Integer) columnID,
						info.rowIDType, info.rowID);
				if (value != null && !Double.isNaN(value.doubleValue())) {
					values.add(value.doubleValue());
				}
			}
			return asArray(values);

		} else if (description instanceof CategoricalClassDescription) {

			CategoricalClassDescription<?> desc = (CategoricalClassDescription<?>) description;
			if (desc.getCategoryType() == ECategoryType.NOMINAL) {
				throw new UnsupportedOperationException("Wilcoxon rank-sum test cannot be applied to nominal data");
			} else {
				for (Object columnID : columnIDs) {
					Object category = info.dataDomain.getRaw(info.columnPerspective.getIdType(), (Integer) columnID,
							info.rowIDType, info.rowID);
					if (category != null) {
						values.add((new Integer(desc.indexOf(category))).doubleValue());
					}
				}
				return asArray(values);
			}
		}

		return new double[0];
	}

	private double[] asArray(List<Double> values) {
		double[] array = new double[values.size()];
		for (int i = 0; i < values.size(); i++) {
			array[i] = values.get(i);
		}
		return array;
	}

	@Override
	public void dispose() {
		for (org.eclipse.swt.graphics.Color c : colorRegistry) {
			c.dispose();
		}
		colorRegistry.clear();
		super.dispose();
	}

	@Override
	public IWizardPage getPreviousPage() {
		return ((WilcoxonRankSumTestWizard) getWizard()).getManualTargetDataCellPage();
	}

	@Override
	public IWizardPage getNextPage() {
		return null;
	}
}
