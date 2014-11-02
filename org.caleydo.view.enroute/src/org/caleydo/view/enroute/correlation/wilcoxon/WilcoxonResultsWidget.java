/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.correlation.wilcoxon;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IIDTypeMapper;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.function.AdvancedDoubleStatistics;
import org.caleydo.view.enroute.correlation.DataCellInfo;
import org.caleydo.view.enroute.correlation.SimpleCategory;
import org.caleydo.view.enroute.correlation.wilcoxon.WilcoxonUtil.WilcoxonResult;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Christian
 *
 */
public class WilcoxonResultsWidget extends Composite {

	protected Set<org.eclipse.swt.graphics.Color> colorRegistry = new HashSet<>();

	protected DataCellInfo sourceInfo;
	protected DataCellInfo targetInfo;

	protected WilcoxonResult result;

	protected double[][] dataValues = new double[2][];

	private Label uValueLabel;
	private Label pValueLabel;

	private CLabel[] classColorLabel = new CLabel[2];

	private Label[] classLabel = new Label[2];
	private Label[] classNumElementsLabel = new Label[2];
	private Label[] classMedianLabel = new Label[2];

	/**
	 * @param parent
	 * @param style
	 */
	public WilcoxonResultsWidget(Composite parent) {
		super(parent, SWT.NONE);

		setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		setLayout(layout);
		Group summaryGroup = new Group(this, SWT.SHADOW_ETCHED_IN);
		summaryGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		summaryGroup.setLayout(new GridLayout(2, false));
		summaryGroup.setText("Summary");
		createClassSummary(summaryGroup, 0);
		createClassSummary(summaryGroup, 1);

		Group resultsGroup = new Group(this, SWT.SHADOW_ETCHED_IN);
		resultsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		resultsGroup.setLayout(new GridLayout(1, false));
		resultsGroup.setText("Results");

		uValueLabel = createLabel(resultsGroup, "U: ");
		pValueLabel = createLabel(resultsGroup, "P-Value: ");

		Button exportButton = new Button(resultsGroup, SWT.PUSH);
		exportButton.setText("Export");
		exportButton.setLayoutData(new GridData());
		exportButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDialog = new FileDialog(new Shell(), SWT.SAVE);
				fileDialog.setText("Export Results");
				String[] filterExt = { "*.csv", "*.txt", "*.*" };
				fileDialog.setFilterExtensions(filterExt);

				fileDialog.setFileName("caleydo_wilcoxon_test_"
						+ new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date()) + ".csv");
				String fileName = fileDialog.open();

				if (fileName != null) {
					exportResults(fileName);
				}
			}
		});
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

		Composite textLabelComposite = new Composite(parent, SWT.WRAP);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		gridData.widthHint = 350;
		textLabelComposite.setLayoutData(gridData);
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

	private void updateClassSummary(int classNumber, double[] values, SimpleCategory targetCategory) {
		org.eclipse.swt.graphics.Color c = targetCategory.color.getSWTColor(Display.getCurrent());
		classColorLabel[classNumber].setBackground(c);
		classColorLabel[classNumber].update();
		colorRegistry.add(c);

		classLabel[classNumber].setText(targetCategory.name);
		classLabel[classNumber].setToolTipText(targetCategory.name);
		classNumElementsLabel[classNumber].setText("Number of Elements: " + values.length);
		AdvancedDoubleStatistics stats = AdvancedDoubleStatistics.of(values);
		classMedianLabel[classNumber].setText(String.format(Locale.ENGLISH, "Median: %.6e", stats.getMedian()));

	}

	private void updateStatistics(double u, double p) {
		uValueLabel.setText(String.format(Locale.ENGLISH, "U: %.2f", u));
		pValueLabel.setText(String.format(Locale.ENGLISH, "P-Value: %.6e", p));
	}

	public void update(DataCellInfo sourceInfo, DataCellInfo targetInfo, WilcoxonResult result) {
		this.sourceInfo = sourceInfo;
		this.targetInfo = targetInfo;
		this.result = result;
		dataValues[0] = WilcoxonUtil.getSampleValuesArray(targetInfo, result.derivedClassifier.getClass1IDs());
		dataValues[1] = WilcoxonUtil.getSampleValuesArray(targetInfo, result.derivedClassifier.getClass2IDs());
		updateClassSummary(0, dataValues[0], result.derivedClassifier.getDataClasses().get(0));
		updateClassSummary(1, dataValues[1], result.derivedClassifier.getDataClasses().get(1));
		updateStatistics(result.u, result.p);
	}

	@Override
	public void dispose() {
		for (org.eclipse.swt.graphics.Color c : colorRegistry) {
			c.dispose();
		}
		colorRegistry.clear();
		super.dispose();
	}

	private void exportResults(String fileName) {

		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName),
					"UTF-16")));

			out.println("\t" + result.classifier.getDataClasses().get(0).name + "\t"
					+ result.classifier.getDataClasses().get(1).name);

			out.println("Number of Elements\t" + dataValues[0].length + "\t" + dataValues[1].length);

			AdvancedDoubleStatistics stats1 = AdvancedDoubleStatistics.of(dataValues[0]);
			AdvancedDoubleStatistics stats2 = AdvancedDoubleStatistics.of(dataValues[1]);

			out.println("Median\t" + stats1.getMedian() + "\t" + stats2.getMedian());

			out.println();

			out.println("U\t" + result.u);
			out.println("P-Value\t" + result.p);

			out.println();

			out.println(targetInfo.columnPerspective.getIdType().getIDCategory().getHumanReadableIDType().getTypeName()
					+ "\tCategory Data Block 1\tValues Data Block 2");

			printSamples(out, result.derivedClassifier.getClass1IDs(), 0);
			printSamples(out, result.derivedClassifier.getClass2IDs(), 1);

			out.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void printSamples(PrintWriter out, Set<Object> ids, int categoryIndex) {

		IDMappingManager mappingManager = IDMappingManagerRegistry.get().getIDMappingManager(
				result.derivedClassifier.getIDType());
		IIDTypeMapper<Object, Object> primaryMapper = mappingManager.getIDTypeMapper(
				result.derivedClassifier.getIDType(), result.derivedClassifier.getIDType().getIDCategory()
						.getHumanReadableIDType());
		for (Object columnID : ids) {

			Set<Object> sampleIDs = primaryMapper.apply(columnID);
			Number value = (Number) targetInfo.dataDomain.getRaw(targetInfo.columnPerspective.getIdType(),
					(Integer) columnID, targetInfo.rowIDType, targetInfo.rowID);
			if (value != null && !Double.isNaN(value.doubleValue())) {
				out.println(sampleIDs.iterator().next() + "\t"
						+ result.classifier.getDataClasses().get(categoryIndex).name
						+ "\t" + value.doubleValue());
			}

		}
	}
}
