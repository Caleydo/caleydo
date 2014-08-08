/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.correlation;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.function.DoubleFunctions;
import org.caleydo.core.util.function.IInvertableDoubleFunction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;

/**
 * Widget for classifying numerical data using a threshold.
 *
 * @author Christian
 *
 */
public class NumericalClassificationWidget extends AClassificationWidget {

	private static final int THRESHOLD_SCALE_MIN = 0;
	private static final int THRESHOLD_SCALE_MAX = 100;

	private Label maxLabel;
	private Label minLabel;

	private Text thresholdText;
	private Scale thresholdScale;

	private Composite categoryComposite;

	private IInvertableDoubleFunction mappingFunction;

	private NumericalDataClassifier classifier;

	private List<Label> classLabels;

	/**
	 * @param parent
	 * @param style
	 * @param categoryColors
	 *            Colors used for categories. Must contain at least 2 colors.
	 */
	public NumericalClassificationWidget(Composite parent, int style, List<Color> categoryColors) {
		super(parent, style, categoryColors);
		classifier = new NumericalDataClassifier(0, categoryColors.get(0), categoryColors.get(1));

		GridLayout layout = new GridLayout(2, false);
		// layout.horizontalSpacing = 0;
		// layout.verticalSpacing = 0;
		setLayout(layout);
		setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		categoryComposite = new Composite(this, SWT.NONE);
		categoryComposite.setLayout(new GridLayout(2, false));
		categoryComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		classLabels = new ArrayList<>(classifier.getDataClasses().size());

		for (SimpleCategory category : classifier.getDataClasses()) {

			CLabel preview = new CLabel(categoryComposite, SWT.SHADOW_ETCHED_IN);
			GridData gridData = new GridData(SWT.LEFT, SWT.TOP, false, false);
			gridData.widthHint = 20;
			gridData.heightHint = 20;
			preview.setLayoutData(gridData);
			org.eclipse.swt.graphics.Color c = category.color.getSWTColor(Display.getCurrent());
			preview.setBackground(c);
			preview.update();
			colorRegistry.add(c);

			Label l = new Label(categoryComposite, SWT.NONE);
			gridData = new GridData(SWT.FILL, SWT.TOP, true, false);
			gridData.widthHint = 220;
			l.setLayoutData(gridData);
			l.setText(category.name);
			classLabels.add(l);
		}

		Composite scaleComposite = new Composite(this, SWT.NONE);
		GridLayout gl = new GridLayout(1, false);
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		scaleComposite.setLayout(gl);
		scaleComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		maxLabel = new Label(scaleComposite, SWT.NONE);
		maxLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		maxLabel.setText("Max: ");

		thresholdScale = new Scale(scaleComposite, SWT.VERTICAL);

		GridData gd = new GridData(SWT.CENTER, SWT.FILL, false, true);
		gd.heightHint = 120;
		thresholdScale.setLayoutData(gd);
		thresholdScale.setMinimum(THRESHOLD_SCALE_MIN);
		thresholdScale.setMaximum(THRESHOLD_SCALE_MAX);
		thresholdScale.setPageIncrement(10);
		thresholdScale.setOrientation(SWT.RIGHT_TO_LEFT);
		thresholdScale.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				float value = (float) mappingFunction.apply(thresholdScale.getMaximum() - thresholdScale.getSelection()
						+ thresholdScale.getMinimum());
				thresholdText.setText("" + value);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});
		minLabel = new Label(scaleComposite, SWT.NONE);
		minLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		minLabel.setText("Min: ");

		Label thresholdLabel = new Label(this, SWT.NONE);
		thresholdLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, false));
		thresholdLabel.setText("Threshold: ");

		thresholdText = new Text(this, SWT.BORDER);
		thresholdText.setText("0");
		gd = new GridData(SWT.FILL, SWT.TOP, true, false);
		gd.widthHint = 100;
		thresholdText.setLayoutData(gd);

		thresholdText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				try {
					float threshold = Float.parseFloat(thresholdText.getText());
					thresholdScale.setSelection(thresholdScale.getMaximum() - (int) mappingFunction.unapply(threshold)
							- thresholdScale.getMinimum());
					updateClassifier(threshold);
				} catch (NumberFormatException ex) {
					// Do nothing
				}
			}
		});
	}

	protected void updateCategories() {
		List<SimpleCategory> categories = classifier.getDataClasses();

		for (int i = 0; i < categories.size(); i++) {
			SimpleCategory category = categories.get(i);
			Label label = classLabels.get(i);
			label.setText(category.name);
		}

	}

	protected void updateClassifier(float threshold) {
		classifier = new NumericalDataClassifier(threshold, categoryColors.get(0), categoryColors.get(1));
		updateCategories();
		notifyOfClassifierChange();
	}


	@Override
	public void updateData(DataCellInfo info) {
		float min = Float.POSITIVE_INFINITY;
		float max = Float.NEGATIVE_INFINITY;
		for (int columnID : info.columnPerspective.getVirtualArray()) {
			Number num = (Number) info.dataDomain.getRaw(info.columnPerspective.getIdType(), columnID, info.rowIDType,
					info.rowID);
			if (num.floatValue() < min)
				min = num.floatValue();
			if (num.floatValue() > max)
				max = num.floatValue();
		}

		minLabel.setText("Min: " + min);
		maxLabel.setText("Max: " + max);
		mappingFunction = DoubleFunctions.map(THRESHOLD_SCALE_MIN, THRESHOLD_SCALE_MAX, min, max);
		try {
			float threshold = Float.parseFloat(thresholdText.getText());
			thresholdScale.setSelection(thresholdScale.getMaximum() - (int) mappingFunction.unapply(threshold)
					- thresholdScale.getMinimum());
		} catch (NumberFormatException e) {
			// Do nothing
		}
	}

	@Override
	public IDataClassifier getClassifier() {
		return classifier;
	}

}
