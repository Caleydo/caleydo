/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 *
 */
package org.caleydo.view.tourguide.internal.external.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.gui.util.AHelpButtonDialog;
import org.caleydo.core.io.gui.dataimport.PreviewTable;
import org.caleydo.core.io.gui.dataimport.PreviewTable.IPreviewCallback;
import org.caleydo.core.io.gui.dataimport.widget.LabelWidget;
import org.caleydo.core.io.gui.dataimport.widget.LoadFileWidget;
import org.caleydo.core.util.base.ICallback;
import org.caleydo.core.util.execution.SafeCallable;
import org.caleydo.core.util.system.BrowserUtils;
import org.caleydo.view.tourguide.api.score.ECombinedOperator;
import org.caleydo.view.tourguide.api.util.EnumUtils;
import org.caleydo.view.tourguide.internal.external.AExternalScoreParseSpecification;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Dialog for loading groupings for datasets.
 *
 * @author Christian Partl
 *
 */
public abstract class AImportExternalScoreDialog<T extends AExternalScoreParseSpecification> extends AHelpButtonDialog
		implements SafeCallable<T> {

	protected final T spec;

	/**
	 * Composite that is the parent of all gui elements of this this.
	 */
	private Composite parentComposite;

	/**
	 * Textfield for the grouping name.
	 */
	private LabelWidget label;

	private LoadFileWidget loadFile;

	protected PreviewTable previewTable;

	private Combo operator;

	private Button normalize;

	private Button colorButton;

	private Text mappingMin;

	private Text mappingMax;

	public AImportExternalScoreDialog(Shell parentShell) {
		this(parentShell, null);
	}

	@SuppressWarnings("unchecked")
	public AImportExternalScoreDialog(Shell parentShell,T existing) {
		super(parentShell);
		if (existing == null) {
			spec = createDummy();
			spec.setDelimiter("\t");
			spec.setNumberOfHeaderLines(1);
		} else {
			this.spec = (T) existing.clone();
		}
	}

	protected abstract T createDummy();

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Import External Score");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		int numGridCols = 2;

		parentComposite = new Composite(parent, SWT.BORDER);
		GridLayout layout = new GridLayout(numGridCols, false);
		parentComposite.setLayout(layout);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.widthHint = 900;
		gd.heightHint = 670;
		parentComposite.setLayoutData(gd);

		loadFile = new LoadFileWidget(parentComposite, "Open Score File", new ICallback<String>() {
			@Override
			public void on(String data) {
				onSelectFile(data);
			}
		}, new GridData(SWT.FILL, SWT.FILL, true, false));

		label = new LabelWidget(parentComposite, "External Score Name");

		createRowConfig(parentComposite);

		{
			Group extra = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
			extra.setText("Extra Settings");
			extra.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
			extra.setLayout(new GridLayout(2, false));
			Label operatorLabel = new Label(extra, SWT.TOP | SWT.LEFT);
			operatorLabel.setText("Combine Operator");
			operatorLabel.setLayoutData(new GridData(SWT.LEFT));
			this.operator = new Combo(extra, SWT.DROP_DOWN | SWT.READ_ONLY);
			this.operator.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
			this.operator
					.setToolTipText("The operator to use if multiple scores needs to be combined for a single element");
			this.operator.setItems(EnumUtils.getNames(ECombinedOperator.class));
			this.operator.setText(ECombinedOperator.MEAN.name());

			Label normalizeLabel = new Label(extra, SWT.TOP | SWT.LEFT);
			normalizeLabel.setLayoutData(new GridData(SWT.LEFT));
			this.normalize = new Button(extra, SWT.CHECK);
			this.normalize.setText("Normalize Scores");
			this.normalize.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

			Label label = new Label(extra, SWT.TOP | SWT.LEFT);
			label.setText("Score Color");
			label.setLayoutData(new GridData(SWT.LEFT));
			this.colorButton = new Button(extra, SWT.PUSH);
			this.colorButton.setText("Select Color");
			this.colorButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
			this.colorButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					// Create the color-change dialog
					ColorDialog dlg = new ColorDialog(getShell());

					// Set the selected color in the dialog from
					// user's selected color
					dlg.setRGB(colorButton.getBackground().getRGB());

					// Change the title bar text
					dlg.setText("Choose a Color");

					// Open the dialog and retrieve the selected color
					RGB rgb = dlg.open();
					if (rgb != null) {
						// Dispose the old color, create the new one, and set into the label
						Color old = colorButton.getBackground();
						Color new_ = new Color(getShell().getDisplay(), rgb);
						old.dispose();
						colorButton.setBackground(new_);
					}
				}
			});

			VerifyListener isNumber = new VerifyListener() {
				@Override
				public void verifyText(VerifyEvent e) {
					String text = e.text;
					text = text.replaceAll("[^0-9-.]", "");
					e.text = text;
				}
			};

			label = new Label(extra, SWT.TOP | SWT.LEFT);
			label.setText("Value Range Minimum");
			label.setLayoutData(new GridData(SWT.LEFT));
			this.mappingMin = new Text(extra, SWT.BORDER);
			this.mappingMin.addVerifyListener(isNumber);
			this.mappingMin.setText("0");
			this.mappingMin.setToolTipText("Leave blank for unbound");
			this.mappingMin.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

			label = new Label(extra, SWT.TOP | SWT.LEFT);
			label.setText("Value Range Maximum");
			label.setLayoutData(new GridData(SWT.LEFT));
			this.mappingMax = new Text(extra, SWT.BORDER);
			this.mappingMax.addVerifyListener(isNumber);
			this.mappingMax.setText("1");
			this.mappingMax.setToolTipText("Leave blank for unbound");
			this.mappingMax.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		}

		previewTable = new PreviewTable(parentComposite, this.spec, new IPreviewCallback() {
			@Override
			public void on(int numColumn, int numRow, List<? extends List<String>> dataMatrix) {
				onPreviewChanged(numColumn, numRow, dataMatrix);
			}
		}, false);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.minimumHeight = 300;
		gd.horizontalSpan = 2;
		previewTable.getTable().setLayoutData(gd);


		init(parent.getDisplay());

		parentComposite.pack();

		return parent;
	}

	protected abstract void createRowConfig(Composite parent);

	@Override
	protected void helpPressed() {
		BrowserUtils.openURL("http://www.icg.tugraz.at/project/caleydo/help/caleydo-2.0/loading-data");
	}

	@Override
	public void okPressed() {
		if (!validate())
			return;

		save();
		super.okPressed();
	}

	/**
	 * Initializes all widgets of the {@link #dialog}. This method should be called after all widgets of the dialog were
	 * created.
	 *
	 * @param display
	 */
	public void init(Display display) {
		if (spec.getDataSourcePath() != null && new File(spec.getDataSourcePath()).exists()) {
			initWidgetsFromGroupParseSpecification(display);
		} else {
			initWidgetsWithDefaultValues(display);
		}
	}

	protected void initWidgetsFromGroupParseSpecification(Display display) {
		this.loadFile.setFileName(spec.getDataSourcePath());

		this.label.setText(spec.getRankingName());
		this.label.setEnabled(true);
		this.previewTable.generatePreview(spec.getColumns());

		this.operator.select(spec.getOperator().ordinal());
		this.normalize.setSelection(spec.isNormalizeScores());
		this.colorButton.setBackground(spec.getColor().getSWTColor(display));
		this.mappingMin.setText(convert(spec.getMappingMin()));
		this.mappingMax.setText(convert(spec.getMappingMin()));
	}

	private String convert(float v) {
		if (Float.isNaN(v))
			return "";
		return String.format("%.2f", v);
	}

	protected void initWidgetsWithDefaultValues(Display display) {

		this.loadFile.setFileName("");

		this.label.setText("");
		this.label.setEnabled(false);

		this.colorButton.setBackground(new Color(display, 192, 192, 192));
	}


	protected boolean validate() {
		if (this.loadFile.getFileName().isEmpty()) {
			MessageDialog.openError(new Shell(), "Invalid Filename", "Please specify a file to load");
			return false;
		}
		return true;
	}

	protected void save() {
		List<Integer> selectedColumns = new ArrayList<Integer>(this.previewTable.getSelectedColumns());
		selectedColumns.remove(spec.getColumnOfRowIds());
		spec.setColumns(selectedColumns);
		spec.setContainsColumnIDs(false);
		spec.setNormalizeScores(this.normalize.getSelection());
		spec.setOperator(ECombinedOperator.valueOf(this.operator.getText()));
		spec.setRankingName(this.label.getText());
		Color c = this.colorButton.getBackground();
		spec.setColor(org.caleydo.core.util.color.Color.fromSWTColor(c));
		spec.setMappingMin(convert(this.mappingMin));
		spec.setMappingMax(convert(this.mappingMax));
	}

	private float convert(Text elem) {
		String s = elem.getText().trim();
		if (s.length() == 0)
			return Float.NaN;
		return Float.parseFloat(s);
	}

	@Override
	public T call() {
		if (this.open() == Window.OK)
			return this.spec;
		else
			return null;
	}

	public void onSelectFile(String inputFileName) {
		this.label.setText(inputFileName.substring(inputFileName.lastIndexOf(File.separator) + 1,
				inputFileName.lastIndexOf(".")));
		spec.setDataSourcePath(inputFileName);

		this.label.setEnabled(true);

		this.previewTable.generatePreview(true);
		// this.parentComposite.layout(true, true);
	}

	protected void onPreviewChanged(int totalNumberOfColumns, int totalNumberOfRows,
			List<? extends List<String>> dataMatrix) {
		// parentComposite.pack();
		parentComposite.layout(true);
	}
}
