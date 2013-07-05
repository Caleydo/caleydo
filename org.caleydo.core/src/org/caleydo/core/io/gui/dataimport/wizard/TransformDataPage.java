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
 * Page that offers the possibility to transform the dataset, such as data logarithmation or table transpose.
 *
 * @author Christian Partl
 *
 */
public class TransformDataPage extends AImportDataPage implements Listener {

	public static final String PAGE_NAME = "Transform Data";

	public static final String PAGE_DESCRIPTION = "Specify the data transformations to be performed.";

	/**
	 * Parent composite of all widgets in this page.
	 */
	protected Composite parentComposite;

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
	 * Button to determine whether a data center is used.
	 */
	protected Button useDataCenterButton;

	/**
	 * Text field used to define the data center.
	 */
	protected Text dataCenterTextField;

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
	protected Label warningIconLabel1;

	/**
	 * Label with a warning description.
	 */
	protected Label warningDescriptionLabel1;

	/**
	 * Label with a warning icon.
	 */
	protected Label warningIconLabel2;

	/**
	 * Label with a warning description.
	 */
	protected Label warningDescriptionLabel2;

	/**
	 * Group that contains widgets associated with data transposition.
	 */
	protected Group dataTranspositionGroup;

	/**
	 * Group that contains widgets associated with determining the data center.
	 */
	protected Group dataCenterGroup;

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

		parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		parentComposite.setLayout(new GridLayout(1, true));

		createScalingGroup(parentComposite);

		createClippingGroup(parentComposite);

		createTranspositionGroup(parentComposite);

		createDataCenterGroup(parentComposite);

		// mediator.guiCreated();

		setControl(parentComposite);
	}

	private void createTranspositionGroup(Composite parent) {
		dataTranspositionGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		dataTranspositionGroup.setText("Data Transposition");
		dataTranspositionGroup.setLayout(new GridLayout(2, false));
		dataTranspositionGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		Label transpositionExplanationLabel = new Label(dataTranspositionGroup, SWT.WRAP);
		transpositionExplanationLabel
				.setText("Caleydo assumes a limited number of dimensions and a lot of records.  You typically want to observe a variation in records over dimensions, where dimensions would be, for example points in time, and records expression values of genes. Dimensions do not necessarely map to columns in a source file and equally  records must not be the rows in your file. If you select this option you choose to show the rows in the file as dimensions and the columns in the file as records.");
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gridData.widthHint = 600;
		transpositionExplanationLabel.setLayoutData(gridData);
		swapRowsWithColumnsButton = new Button(dataTranspositionGroup, SWT.CHECK);
		swapRowsWithColumnsButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		swapRowsWithColumnsButton.setText("Swap Rows and Columns");
		swapRowsWithColumnsButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				mediator.swapRowsWithColumnsButtonSelected();
			}
		});

	}

	private void createDataCenterGroup(Composite parent) {
		dataCenterGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		dataCenterGroup.setText("Data Center");
		dataCenterGroup.setLayout(new GridLayout(2, false));
		dataCenterGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		Label dateCenterExplanationLabel = new Label(dataCenterGroup, SWT.WRAP);
		dateCenterExplanationLabel
				.setText("The data center is a balue that, if set, determines a neutral center point of the data. A common example is that 0 is the neutral value, lower values are in the negative and larger values are in the positive range. If the data center is set it is assumed that the extend into both, positive and negative direction is the same. For example, for a dataset [-0.5, 0.7] with a center set at 0, the value range will be set to -0.7 to 0.7.");
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gridData.widthHint = 600;
		dateCenterExplanationLabel.setLayoutData(gridData);
		useDataCenterButton = new Button(dataCenterGroup, SWT.CHECK);
		// useDataCenterButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
		// true, true, 1,
		// 1));
		useDataCenterButton.setText("Use data center ");
		useDataCenterButton.addListener(SWT.Selection, this);
		useDataCenterButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				mediator.useDataCenterButtonSelected();
			}
		});

		dataCenterTextField = new Text(dataCenterGroup, SWT.BORDER);
		gridData = new GridData(SWT.LEFT, SWT.FILL, false, true);
		gridData.widthHint = 70;
		dataCenterTextField.setLayoutData(gridData);
		dataCenterTextField.addListener(SWT.Modify, this);
		// dataCenterTextField.addVerifyListener(new VerifyListener() {
		// @Override
		// public void verifyText(VerifyEvent e) {
		// mediator.verifyTextField(dataCenterTextField, e);
		// }
		// });
		// dataCenterTextField.addModifyListener(new ModifyListener() {
		// @Override
		// public void modifyText(ModifyEvent e) {
		// mediator.verifyClippingTextField(dataCenterTextField.getText());
		// }
		// });

	}

	protected Label createWarningIconLabel(Composite parent) {
		Label warningLabel = new Label(parent, SWT.NONE);
		warningLabel.setImage(JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_WARNING));
		return warningLabel;
	}

	protected Label createWarningDescriptionLabel(Composite parent, String text) {
		Label warningLabel = new Label(parent, SWT.WRAP);
		warningLabel.setText(text);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.widthHint = 200;
		warningLabel.setLayoutData(gridData);
		return warningLabel;
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
		maxButton.addListener(SWT.Selection, this);
		maxButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				mediator.maxButtonSelected();
			}
		});

		maxTextField = new Text(clippingGroup, SWT.BORDER);
		maxTextField.addListener(SWT.Modify, this);
		// maxTextField.addVerifyListener(new VerifyListener() {
		// @Override
		// public void verifyText(VerifyEvent e) {
		// mediator.verifyTextField(maxTextField, e);
		// }
		// });

		minButton = new Button(clippingGroup, SWT.CHECK);
		minButton.setText("Min");
		minButton.addListener(SWT.Selection, this);
		minButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mediator.minButtonSelected();
			}
		});

		minTextField = new Text(clippingGroup, SWT.BORDER);
		minTextField.addListener(SWT.Modify, this);
		// minTextField.addVerifyListener(new VerifyListener() {
		// @Override
		// public void verifyText(VerifyEvent e) {
		// mediator.verifyTextField(minTextField, e);
		// }
		// });

	}

	private void createScalingGroup(Composite parent) {
		Group scalingGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		scalingGroup.setText("Data Scale");
		scalingGroup.setLayout(new GridLayout(2, false));
		scalingGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		Label scalingExplanationLabel = new Label(scalingGroup, SWT.WRAP);
		scalingExplanationLabel.setText("Specify the way every data point should be scaled.");
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gridData.widthHint = 200;
		scalingExplanationLabel.setLayoutData(gridData);

		Label scalingMethodLabel = new Label(scalingGroup, SWT.NONE);
		scalingMethodLabel.setText("Scaling Method");

		scalingCombo = new Combo(scalingGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		gridData = new GridData();
		gridData.widthHint = 100;
		scalingCombo.setLayoutData(gridData);
		scalingCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mediator.scalingComboSelected();
			}
		});
	}

	@Override
	public boolean isPageComplete() {

		if (mediator.isDataValid()) {
			return super.isPageComplete();
		}
		return false;
	}

	@Override
	public void fillDataSetDescription() {
		mediator.fillDataSetDescription();
	}

	@Override
	public void pageActivated() {
		mediator.pageActivated();

	}

	@Override
	public void handleEvent(Event event) {
		if (getWizard().getContainer().getCurrentPage() != null)
			getWizard().getContainer().updateButtons();
	}

}
