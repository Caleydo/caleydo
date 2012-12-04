/**
 *
 */
package org.caleydo.view.tourguide.data.load.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.gui.util.AHelpButtonDialog;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.core.io.gui.dataimport.PreviewTable;
import org.caleydo.core.io.gui.dataimport.PreviewTable.IPreviewCallback;
import org.caleydo.core.io.gui.dataimport.widget.ICallback;
import org.caleydo.core.io.gui.dataimport.widget.IntegerCallback;
import org.caleydo.core.io.gui.dataimport.widget.LabelWidget;
import org.caleydo.core.io.gui.dataimport.widget.LoadFileWidget;
import org.caleydo.core.io.gui.dataimport.widget.RowConfigWidget;
import org.caleydo.core.util.execution.SafeCallable;
import org.caleydo.core.util.link.LinkHandler;
import org.caleydo.view.tourguide.data.load.ScoreParseSpecification;
import org.caleydo.view.tourguide.data.score.ECombinedOperator;
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
public class ImportExternalIDTypeScoreDialog extends AHelpButtonDialog implements SafeCallable<ScoreParseSpecification> {
	/**
	 * The row id category for which groupings should be loaded.
	 */
	private final IDCategory rowIDCategory;

	private final ScoreParseSpecification spec;

	/**
	 * Composite that is the parent of all gui elements of this this.
	 */
	private Composite parentComposite;

	/**
	 * Textfield for the grouping name.
	 */
	private LabelWidget label;

	private LoadFileWidget loadFile;

	private RowConfigWidget rowConfig;

	private PreviewTable previewTable;

	private Combo operator;

	private Button normalize;

	public ImportExternalIDTypeScoreDialog(Shell parentShell, IDCategory rowIDCategory) {
		this(parentShell, rowIDCategory, null);
	}

	public ImportExternalIDTypeScoreDialog(Shell parentShell, IDCategory rowIDCategory, ScoreParseSpecification existing) {
		super(parentShell);
		this.rowIDCategory = rowIDCategory;
		if (existing == null) {
			spec = new ScoreParseSpecification();
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

		rowConfig = new RowConfigWidget(parentComposite, new IntegerCallback() {
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
		this.operator.setItems(ECombinedOperator.names());
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

		this.rowConfig.setCategoryID(rowIDCategory);
		this.rowConfig.setNumHeaderRows(spec.getNumberOfHeaderLines());
		this.rowConfig.setColumnOfRowIds(spec.getColumnOfRowIds() + 1);

		this.previewTable.generatePreview(spec.getColumns());

		this.operator.select(spec.getOperator().ordinal());
		this.normalize.setSelection(spec.isNormalizeScores());

		this.rowConfig.setIDType(IDType.getIDType(spec.getRowIDSpecification().getIdType()));
	}

	private void initWidgetsWithDefaultValues() {

		this.loadFile.setFileName("");

		this.label.setText("");
		this.label.setEnabled(false);

		this.rowConfig.setCategoryID(rowIDCategory);
		this.rowConfig.setEnabled(false);
	}


	private boolean validate() {
		if (this.loadFile.getFileName().isEmpty()) {
			MessageDialog.openError(new Shell(), "Invalid Filename", "Please specify a file to load");
			return false;
		}

		if (this.rowConfig.getIDType() == null) {
			MessageDialog.openError(new Shell(), "Invalid Row ID Type", "Please select the ID type of the rows");
			return false;
		}
		return true;
	}

	private void save() {
		List<Integer> selectedColumns = new ArrayList<Integer>(this.previewTable.getSelectedColumns());
		selectedColumns.remove(spec.getColumnOfRowIds());
		spec.setColumns(selectedColumns);
		spec.setRowIDSpecification(this.rowConfig.getIDSpecification());
		spec.setContainsColumnIDs(false);
		spec.setNormalizeScores(this.normalize.getSelection());
		spec.setOperator(ECombinedOperator.valueOf(this.operator.getText()));
		spec.setRankingName(this.label.getText());
	}

	@Override
	public ScoreParseSpecification call() {
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
		this.rowConfig.determineConfigFromPreview(dataMatrix, this.rowIDCategory);
		parentComposite.pack();
		parentComposite.layout(true);
	}
}
