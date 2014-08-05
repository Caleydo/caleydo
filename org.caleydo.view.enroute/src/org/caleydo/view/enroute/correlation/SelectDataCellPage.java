/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.correlation;

import java.util.HashSet;
import java.util.Set;

import org.caleydo.core.event.EventListenerManager;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventListenerManagers;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.util.function.DoubleFunctions;
import org.caleydo.core.util.function.IInvertableDoubleFunction;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;

/**
 * @author Christian
 *
 */
public class SelectDataCellPage extends WizardPage implements IPageChangedListener {

	private final EventListenerManager listeners = EventListenerManagers.createSWTDirect();
	private static final int THRESHOLD_SCALE_MIN = 0;
	private static final int THRESHOLD_SCALE_MAX = 100;

	private Label datasetLabel;
	private Label groupLabel;
	private Label rowLabel;

	private Label maxLabel;
	private Label minLabel;

	private Text thresholdText;
	private Scale thresholdScale;

	private Group classificationGroup;
	DataCellInfo info;

	private IInvertableDoubleFunction mappingFunction;

	private Set<org.eclipse.swt.graphics.Color> colorRegistry = new HashSet<>();

	/**
	 * @param pageName
	 */
	protected SelectDataCellPage(String pageName) {
		super(pageName);
		listeners.register(this);
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


		Label thresholdLabel = new Label(classificationGroup, SWT.NONE);
		thresholdLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));
		thresholdLabel.setText("Threshold:");

		thresholdText = new Text(classificationGroup, SWT.SHADOW_ETCHED_IN);
		thresholdText.setText("0");
		thresholdText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		Composite scaleComposite = new Composite(classificationGroup, SWT.NONE);
		scaleComposite.setLayout(new GridLayout(1, false));
		scaleComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));

		maxLabel = new Label(scaleComposite, SWT.NONE);
		maxLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		maxLabel.setText("Max: ");

		thresholdScale = new Scale(scaleComposite, SWT.VERTICAL);

		GridData gd = new GridData(SWT.LEFT, SWT.FILL, false, true);
		gd.heightHint = 80;
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

		thresholdText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				float threshold = Float.parseFloat(thresholdText.getText());
				thresholdScale.setSelection(thresholdScale.getMaximum() - (int) mappingFunction.unapply(threshold)
						- thresholdScale.getMinimum());
				triggerClassificationUpdate();
			}
		});

		NumericalThresholdClassifier classifier = new NumericalThresholdClassifier(0.324f);

		for (SimpleCategory category : classifier.getDataClasses()) {

			CLabel preview = new CLabel(classificationGroup, SWT.SHADOW_ETCHED_IN);
			GridData gridData = new GridData(SWT.LEFT, SWT.TOP, false, false);
			gridData.widthHint = 20;
			gridData.heightHint = 20;
			preview.setLayoutData(gridData);
			org.eclipse.swt.graphics.Color c = category.color.getSWTColor(Display.getCurrent());
			preview.setBackground(c);
			preview.update();
			colorRegistry.add(c);

			Label l = new Label(classificationGroup, SWT.NONE);
			l.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
			l.setText(category.name);
		}

		setControl(parentComposite);
	}

	@Override
	public boolean isPageComplete() {

		return super.isPageComplete();
	}

	@ListenTo
	public void onDataCellSelected(DataCellSelectionEvent event) {
		if (isCurrentPage()) {
			info = event.getInfo();

			datasetLabel.setText("Dataset: " + info.dataDomain.getLabel());
			groupLabel.setText("Group: " + info.columnPerspective.getLabel());

			IDMappingManager mappingManager = IDMappingManagerRegistry.get().getIDMappingManager(info.rowIDType);
			Set<String> humanReadableIDs = mappingManager.getIDAsSet(info.rowIDType, info.rowIDType.getIDCategory()
					.getHumanReadableIDType(), info.rowID);
			rowLabel.setText("Row: " + humanReadableIDs.iterator().next());

			classificationGroup.setVisible(true);

			float min = Float.POSITIVE_INFINITY;
			float max = Float.NEGATIVE_INFINITY;
			for (int columnID : info.columnPerspective.getVirtualArray()) {
				Number num = (Number) info.dataDomain.getRaw(info.columnPerspective.getIdType(), columnID,
						info.rowIDType, info.rowID);
				if (num.floatValue() < min)
					min = num.floatValue();
				if (num.floatValue() > max)
					max = num.floatValue();

			}

			minLabel.setText("Min: " + min);
			maxLabel.setText("Max: " + max);
			mappingFunction = DoubleFunctions.map(THRESHOLD_SCALE_MIN, THRESHOLD_SCALE_MAX, min, max);

			triggerClassificationUpdate();
		}
	}

	private void triggerClassificationUpdate() {
		try {
			float threshold = Float.parseFloat(thresholdText.getText());
			EventPublisher.trigger(new ShowDataClassificationEvent(info.cellID, new NumericalThresholdClassifier(
					threshold)));
		} catch (NumberFormatException e) {
			// Do nothing
		}
	}

	@Override
	public void dispose() {
		listeners.unregisterAll();
		for (org.eclipse.swt.graphics.Color c : colorRegistry) {
			c.dispose();
		}
		colorRegistry.clear();
		super.dispose();
	}

	@Override
	public void pageChanged(PageChangedEvent event) {
		if (event.getSelectedPage() == getNextPage()) {

			CalculateCorrelationWizard wizard = (CalculateCorrelationWizard) getWizard();

			float threshold = Float.parseFloat(thresholdText.getText());
			wizard.setPageInfo(this, info, new NumericalThresholdClassifier(threshold));
		}

	}

}
