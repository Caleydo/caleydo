/**
 * 
 */
package org.caleydo.core.io.gui.dataimport.wizard;

import org.caleydo.core.io.DataSetDescription;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * Page that offers the possibility to transform the dataset, such as data
 * logarithmation or table transpose.
 * 
 * @author Christian Partl
 * 
 */
public class TransformDataPage extends AImportDataPage {

	public static final String PAGE_NAME = "Transform Data";

	public static final String PAGE_DESCRIPTION = "Specify the data transformations to be performed.";

	/**
	 * Text field that specifies the minimum data clipping value.
	 */
	protected Text minTextField;
	/**
	 * Text field that specifies the minimum data clipping value.
	 */
	protected Text maxTextField;
	/**
	 * Button to determine whether the dataset should be transposed.
	 */
	protected Button swapRowsWithColumnsButton;

	/**
	 * Button to enable the {@link #maxTextField};
	 */
	protected Button maxButton;

	/**
	 * Button to enable the {@link #minTextField};
	 */
	protected Button minButton;

	/**
	 * Combo to define the scaling method that should be applied to the data.
	 */
	protected Combo scalingCombo;

	/**
	 * Label with a warning icon.
	 */
	protected Label warningIconLabel;

	/**
	 * Label with a warning description.
	 */
	protected Label warningDescriptionLabel;

	/**
	 * Mediator of this class.
	 */
	private TransformDataPageMediator mediator;

	/**
	 * @param pageName
	 * @param dataSetDescription
	 */
	// protected TransformDataPage(String pageName, DataSetDescription
	// dataSetDescription) {
	// super(pageName, dataSetDescription);
	// }

	public TransformDataPage(DataSetDescription dataSetDescription) {
		super(PAGE_NAME, dataSetDescription);
		setDescription(PAGE_DESCRIPTION);
		mediator = new TransformDataPageMediator(this, dataSetDescription);
	}

	@Override
	public void createControl(Composite parent) {

		Composite parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayout(new GridLayout(1, true));

		createScalingGroup(parentComposite);

		createClippingGroup(parentComposite);

		createTranspositionGroup(parentComposite);

		mediator.guiCreated();

		setControl(parentComposite);
	}

	private void createTranspositionGroup(Composite parent) {
		Group dataTranspositionGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		dataTranspositionGroup.setText("Data Transposition");
		dataTranspositionGroup.setLayout(new GridLayout(2, false));
		dataTranspositionGroup
				.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		Label transpositionExplanationLabel = new Label(dataTranspositionGroup, SWT.WRAP);
		transpositionExplanationLabel
				.setText("Specify whether the table should be transposed, i.e., whether the rows should become columns and vice versa.");
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gridData.widthHint = 200;
		transpositionExplanationLabel.setLayoutData(gridData);
		swapRowsWithColumnsButton = new Button(dataTranspositionGroup, SWT.CHECK);
		swapRowsWithColumnsButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 2, 1));
		swapRowsWithColumnsButton.setText("Swap Rows and Columns");
		swapRowsWithColumnsButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				mediator.swapRowsWithColumnsButtonSelected();
			}
		});

		warningIconLabel = new Label(dataTranspositionGroup, SWT.NONE);
		warningIconLabel
				.setImage(JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_WARNING));
		warningDescriptionLabel = new Label(dataTranspositionGroup, SWT.NONE);
		warningDescriptionLabel
				.setText("The number of columns is so high that it might leads to a loss in visualization quality");
	}

	private void createClippingGroup(Composite parent) {

		Group clippingGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		clippingGroup.setText("Data Clipping");
		clippingGroup.setLayout(new GridLayout(2, false));
		clippingGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		Label clippingExplanationLabel = new Label(clippingGroup, SWT.WRAP);
		clippingExplanationLabel
				.setText("Specify the value range for the dataset. Every data point exceeding this range will be clipped to the lower and upper limits respectively.");
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gridData.widthHint = 200;
		clippingExplanationLabel.setLayoutData(gridData);

		maxButton = new Button(clippingGroup, SWT.CHECK);
		maxButton.setText("Max");

		maxButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				mediator.maxButtonSelected();
			}
		});

		maxTextField = new Text(clippingGroup, SWT.BORDER);

		maxTextField.addListener(SWT.Verify, new Listener() {
			@Override
			public void handleEvent(Event e) {
				// Only allow digits
				String string = e.text;
				mediator.verifyClippingTextField(string);
			}
		});

		minButton = new Button(clippingGroup, SWT.CHECK);
		minButton.setText("Min");

		minButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mediator.minButtonSelected();
			}
		});

		minTextField = new Text(clippingGroup, SWT.BORDER);
		minTextField.addListener(SWT.Verify, new Listener() {
			@Override
			public void handleEvent(Event e) {
				// Only allow digits
				String string = e.text;
				mediator.verifyClippingTextField(string);
			}
		});

	}

	private void createScalingGroup(Composite parent) {
		Group scalingGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		scalingGroup.setText("Data Scale");
		scalingGroup.setLayout(new GridLayout(2, false));
		scalingGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		Label scalingExplanationLabel = new Label(scalingGroup, SWT.WRAP);
		scalingExplanationLabel
				.setText("Specify the way every data point should be scaled.");
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gridData.widthHint = 200;
		scalingExplanationLabel.setLayoutData(gridData);

		Label scalingMethodLabel = new Label(scalingGroup, SWT.NONE);
		scalingMethodLabel.setText("Scaling Method");

		scalingCombo = new Combo(scalingGroup, SWT.DROP_DOWN);

		scalingCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mediator.scalingComboSelected();
			}
		});
	}

	@Override
	public void fillDataSetDescription() {
		mediator.fillDataSetDescription();
	}

	@Override
	public void pageActivated() {
		mediator.pageActivated();

	}

}
