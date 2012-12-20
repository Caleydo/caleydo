/**
 *
 */
package org.caleydo.view.tourguide.internal.external.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.gui.util.AHelpButtonDialog;
import org.caleydo.core.io.gui.dataimport.PreviewTable;
import org.caleydo.core.io.gui.dataimport.PreviewTable.IPreviewCallback;
import org.caleydo.core.io.gui.dataimport.widget.ICallback;
import org.caleydo.core.io.gui.dataimport.widget.IntegerCallback;
import org.caleydo.core.io.gui.dataimport.widget.LabelWidget;
import org.caleydo.core.io.gui.dataimport.widget.LoadFileWidget;
import org.caleydo.core.util.execution.SafeCallable;
import org.caleydo.core.util.link.LinkHandler;
import org.caleydo.view.tourguide.api.score.ECombinedOperator;
import org.caleydo.view.tourguide.api.util.EnumUtils;
import org.caleydo.view.tourguide.internal.external.GroupLabelParseSpecification;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * Dialog for loading groupings for datasets.
 *
 * @author Christian Partl
 *
 */
public class ImportExternalGroupLabelScoreDialog extends AHelpButtonDialog implements
		SafeCallable<GroupLabelParseSpecification> {
	/**
	 * The row id category for which groupings should be loaded.
	 */
	private final ATableBasedDataDomain dataDomain;

	private final boolean inDimensionDirection;

	private final GroupLabelParseSpecification spec;

	/**
	 * Composite that is the parent of all gui elements of this this.
	 */
	private Composite parentComposite;

	/**
	 * Textfield for the grouping name.
	 */
	private LabelWidget label;

	private LoadFileWidget loadFile;

	private RowStratificationConfigWidget rowConfig;

	private PreviewTable previewTable;

	private Combo operator;

	private Button normalize;

	public ImportExternalGroupLabelScoreDialog(Shell parentShell,  ATableBasedDataDomain dataDomain,
			boolean inDimensionDirection) {
		this(parentShell, dataDomain, inDimensionDirection, null);
	}

	public ImportExternalGroupLabelScoreDialog(Shell parentShell, ATableBasedDataDomain dataDomain,
			boolean inDimensionDirection,
			GroupLabelParseSpecification existing) {
		super(parentShell);
		this.dataDomain = dataDomain;
		this.inDimensionDirection = inDimensionDirection;

		if (existing == null) {
			spec = new GroupLabelParseSpecification();
			spec.setDelimiter("\t");
			spec.setNumberOfHeaderLines(1);
		} else {
			this.spec = existing.clone();
		}
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Import External Score");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		int numGridCols = 2;

		parentComposite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(numGridCols, false);
		parentComposite.setLayout(layout);

		loadFile = new LoadFileWidget(parentComposite, "Open Score File", new ICallback<String>() {
			@Override
			public void on(String data) {
				onSelectFile(data);
			}
		});

		label = new LabelWidget(parentComposite, "External Score Name");

		rowConfig = new RowStratificationConfigWidget(parentComposite, dataDomain, inDimensionDirection,
				new IntegerCallback() {
					@Override
					public void on(int data) {
						previewTable.onNumHeaderRowsChanged(data);
					}
				}, new IntegerCallback() {
					@Override
					public void on(int data) {
						previewTable.onColumnOfRowIDChanged(data);
					}
				});
		rowConfig.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Group extra = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		extra.setText("Extra Settings");
		extra.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
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

		previewTable = new PreviewTable(parentComposite, this.spec, new IPreviewCallback() {
			@Override
			public void on(int numColumn, int numRow, List<? extends List<String>> dataMatrix) {
				onPreviewChanged(numColumn, numRow, dataMatrix);
			}
		});

		init();

		parentComposite.pack();

		return parent;
	}

	@Override
	protected void helpPressed() {
		LinkHandler.openLink("http://www.icg.tugraz.at/project/caleydo/help/caleydo-2.0/loading-data");
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
	 */
	public void init() {
		if (spec.getDataSourcePath() != null && new File(spec.getDataSourcePath()).exists()) {
			initWidgetsFromGroupParseSpecification();
		} else {
			initWidgetsWithDefaultValues();
		}
	}

	private void initWidgetsFromGroupParseSpecification() {
		this.loadFile.setFileName(spec.getDataSourcePath());

		this.label.setText(spec.getRankingName());
		this.label.setEnabled(true);

		this.rowConfig.setNumHeaderRows(spec.getNumberOfHeaderLines());
		this.rowConfig.setColumnOfRowIds(spec.getColumnOfRowIds() + 1);

		this.previewTable.generatePreview(spec.getColumns());

		this.operator.select(spec.getOperator().ordinal());
		this.normalize.setSelection(spec.isNormalizeScores());

		this.rowConfig.setPerspectiveKey(spec.getPerspectiveKey());
	}

	private void initWidgetsWithDefaultValues() {

		this.loadFile.setFileName("");

		this.label.setText("");
		this.label.setEnabled(false);

		this.rowConfig.setEnabled(false);
	}


	private boolean validate() {
		if (this.loadFile.getFileName().isEmpty()) {
			MessageDialog.openError(new Shell(), "Invalid Filename", "Please specify a file to load");
			return false;
		}

		if (this.rowConfig.getPerspectiveKey() == null) {
			MessageDialog.openError(new Shell(), "Invalid Row Stratification Selection", "Please select the Stratification for which scores should be imported");
			return false;
		}
		return true;
	}

	private void save() {
		List<Integer> selectedColumns = new ArrayList<Integer>(this.previewTable.getSelectedColumns());
		selectedColumns.remove(spec.getColumnOfRowIds());
		spec.setColumns(selectedColumns);
		spec.setPerspectiveKey(this.rowConfig.getPerspectiveKey());
		spec.setContainsColumnIDs(false);
		spec.setNormalizeScores(this.normalize.getSelection());
		spec.setOperator(ECombinedOperator.valueOf(this.operator.getText()));
		spec.setRankingName(this.label.getText());
	}

	@Override
	public GroupLabelParseSpecification call() {
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
		this.rowConfig.setEnabled(true);

		this.previewTable.generatePreview();
	}

	protected void onPreviewChanged(int totalNumberOfColumns, int totalNumberOfRows,
			List<? extends List<String>> dataMatrix) {
		this.rowConfig.setMaxDimension(totalNumberOfColumns, totalNumberOfRows);
		this.rowConfig.determineConfigFromPreview(dataMatrix);
		parentComposite.pack();
		parentComposite.layout(true);
	}
}
