/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.correlation.wilcoxon;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.function.AdvancedDoubleStatistics;
import org.caleydo.view.enroute.correlation.SimpleCategory;
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

				fileDialog.setFileName("caleydo_fishers_test_"
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

	public void updateClassSummary(int classNumber, double[] values, SimpleCategory category) {
		org.eclipse.swt.graphics.Color c = category.color.getSWTColor(Display.getCurrent());
		classColorLabel[classNumber].setBackground(c);
		classColorLabel[classNumber].update();
		colorRegistry.add(c);

		classLabel[classNumber].setText(category.name);
		classLabel[classNumber].setToolTipText(category.name);
		classNumElementsLabel[classNumber].setText("Number of Elements: " + values.length);
		AdvancedDoubleStatistics stats = AdvancedDoubleStatistics.of(values);
		classMedianLabel[classNumber].setText(String.format(Locale.ENGLISH, "Median: %.6e", stats.getMedian()));
	}

	public void updateStatistics(double u, double p) {
		uValueLabel.setText(String.format(Locale.ENGLISH, "U: %.2f", u));
		pValueLabel.setText(String.format(Locale.ENGLISH, "P-Value: %.6e", p));
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
		// FishersExactTestWizard wizard = (FishersExactTestWizard) getWizard();
		//
		// try {
		// PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName),
		// "UTF-16")));
		// IDataClassifier classifier1 = wizard.getCell1Classifier();
		// IDataClassifier classifier2 = wizard.getCell2Classifier();
		//
		// out.println("\t" + classifier1.getDataClasses().get(0).name + "\t"
		// + classifier1.getDataClasses().get(0).name);
		// out.println(classifier2.getDataClasses().get(0).name + "\t" + contingencyTable[0][0] + "\t"
		// + contingencyTable[1][0]);
		// out.println(classifier2.getDataClasses().get(1).name + "\t" + contingencyTable[0][1] + "\t"
		// + contingencyTable[1][1]);
		//
		// out.println();
		//
		// out.println("Two-Sided\t" + result[0]);
		// out.println("Left-Tail\t" + result[1]);
		// out.println("Right-Tail\t" + result[2]);
		//
		// out.println();
		//
		// DataCellInfo info1 = wizard.getInfo1();
		// DataCellInfo info2 = wizard.getInfo2();
		//
		// out.println(info2.columnPerspective.getIdType().getIDCategory().getHumanReadableIDType().getTypeName()
		// + "\tCategory Data Block 1\t Category Data Block 2");
		//
		// IDMappingManager mappingManager = IDMappingManagerRegistry.get().getIDMappingManager(
		// info1.columnPerspective.getIdType());
		// IIDTypeMapper<Object, Object> mapper = mappingManager.getIDTypeMapper(info1.columnPerspective.getIdType(),
		// info2.columnPerspective.getIdType());
		// IIDTypeMapper<Object, Object> primaryMapper = mappingManager.getIDTypeMapper(
		// info1.columnPerspective.getIdType(), info2.columnPerspective.getIdType().getIDCategory()
		// .getHumanReadableIDType());
		//
		// // We assume a 1:1 mapping between samples
		// for (int cell1ColumnID : info1.columnPerspective.getVirtualArray()) {
		//
		// Set<Object> cell2ColumnIDs = mapper.apply(cell1ColumnID);
		// if (cell2ColumnIDs != null && !cell2ColumnIDs.isEmpty()) {
		// Object sampleID = primaryMapper.apply(cell1ColumnID);
		// SimpleCategory c1 = getCategory(info1.dataDomain, info1.columnPerspective.getIdType(),
		// cell1ColumnID, info1.rowIDType, info1.rowID, wizard.getCell1Classifier());
		// SimpleCategory c2 = getCategory(info2.dataDomain, info2.columnPerspective.getIdType(),
		// (Integer) cell2ColumnIDs.iterator().next(), info2.rowIDType, info2.rowID,
		// wizard.getCell2Classifier());
		//
		// out.println(sampleID + "\t" + c1.name + "\t" + c2.name);
		// }
		//
		// }
		//
		// out.close();
		// } catch (IOException ex) {
		// ex.printStackTrace();
		// }
	}
}
