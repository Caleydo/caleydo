/**
 *
 */
package org.caleydo.core.io.gui.dataimport;

import java.util.ArrayList;

import org.caleydo.core.gui.util.AHelpButtonDialog;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.io.GroupingParseSpecification;
import org.caleydo.core.io.gui.dataimport.widget.BooleanCallback;
import org.caleydo.core.io.gui.dataimport.widget.DelimiterWidget;
import org.caleydo.core.io.gui.dataimport.widget.ICallback;
import org.caleydo.core.io.gui.dataimport.widget.IntegerCallback;
import org.caleydo.core.io.gui.dataimport.widget.LabelWidget;
import org.caleydo.core.io.gui.dataimport.widget.LoadFileWidget;
import org.caleydo.core.io.gui.dataimport.widget.RowConfigWidget;
import org.caleydo.core.io.gui.dataimport.widget.SelectAllNoneWidget;
import org.caleydo.core.util.link.LinkHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

/**
 * Dialog for loading groupings for datasets.
 *
 * @author Christian Partl
 *
 */
public class ImportGroupingDialog extends AHelpButtonDialog {

	/**
	 * Composite that is the parent of all gui elements of this dialog.
	 */
	protected Composite parentComposite;

	/**
	 * Table that displays a preview of the data of the file specified by
	 * {@link #inputFileName}.
	 */
	protected Table previewTable;

	/**
	 * List of buttons, each created for one column to specify whether this
	 * column should be loaded or not.
	 */
	protected ArrayList<Button> selectedColumnButtons = new ArrayList<Button>();

	/**
	 * Table editors that are associated with {@link #selectedColumnButtons}.
	 */
	protected ArrayList<TableEditor> tableEditors = new ArrayList<TableEditor>();

	/**
	 * Button to specify whether all columns of the data file should be shown in
	 * the {@link #previewTable}.
	 */
	protected Button showAllColumnsButton;

	/**
	 * Shows the total number columns in the data file and the number of
	 * displayed columns of the {@link #previewTable}.
	 */
	protected Label tableInfoLabel;

	/**
	 * Textfield for the grouping name.
	 */
	protected LabelWidget label;

	/**
	 * Mediator for this dialog.
	 */
	private ImportGroupingDialogMediator mediator;

	/**
	 * Radio group that specifies the delimiters used to parse the input file.
	 */
	protected DelimiterWidget delimiterRadioGroup;

	protected LoadFileWidget loadFile;

	protected SelectAllNoneWidget selectAllNone;

	protected RowConfigWidget rowConfig;

	/**
	 * @param parentShell
	 */
	public ImportGroupingDialog(Shell parentShell, IDCategory rowIDCategory) {
		super(parentShell);
		mediator = new ImportGroupingDialogMediator(this, rowIDCategory);
	}

	/**
	 * @param parentShell
	 * @param groupingParseSpecification
	 *            {@link GroupingParseSpecification} that will be used to
	 *            initialize the widgets of this dialog.
	 */
	public ImportGroupingDialog(Shell parentShell,
			GroupingParseSpecification groupingParseSpecification,
			IDCategory rowIDCategory) {
		super(parentShell);
		mediator = new ImportGroupingDialogMediator(this, groupingParseSpecification,
				rowIDCategory);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Import Grouping");
	}

	@Override
	protected void okPressed() {

		if (!mediator.okPressed())
			return;

		super.okPressed();
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		createGUI(parent);
		return parent;
	}

	private void createGUI(Composite parent) {

		int numGridCols = 2;

		parentComposite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(numGridCols, false);
		parentComposite.setLayout(layout);

		loadFile = new LoadFileWidget(parentComposite, "Open Grouping File", new ICallback<String>() {
			@Override
			public void on(String data) {
				mediator.onSelectFile(data);
			}
		});

		label = new LabelWidget(parentComposite, "Grouping Name");

		rowConfig = new RowConfigWidget(parentComposite, new IntegerCallback() {
			@Override
			public void on(int data) {
				mediator.onNumHeaderRowsChanged(data);
			}
		}, new IntegerCallback() {
			@Override
			public void on(int data) {
				mediator.onColumnOfRowIDChanged(data);
			}
		});

		delimiterRadioGroup = new DelimiterWidget(parentComposite, new ICallback<String>() {
			@Override
			public void on(String data) {
				mediator.onDelimiterChanged(data);
			}
		});

		selectAllNone = new SelectAllNoneWidget(parentComposite, new BooleanCallback() {
			@Override
			public void on(boolean selectAll) {
				if (selectAll)
					mediator.selectAllButtonPressed();
				else
					mediator.selectNoneButtonPressed();
			}
		});

		previewTable = new Table(parentComposite, SWT.MULTI | SWT.BORDER
				| SWT.FULL_SELECTION);
		previewTable.setLinesVisible(true);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, numGridCols, 1);
		gridData.heightHint = 300;
		gridData.widthHint = 800;
		previewTable.setLayoutData(gridData);

		createTableInfo(parentComposite);

		mediator.guiCreated();
	}

	/**
	 * @return the groupingParseSpecification, see
	 *         {@link #groupingParseSpecification}
	 */
	public GroupingParseSpecification getGroupingParseSpecification() {
		return mediator.getGroupingParseSpecification();
	}

	/**
	 * Creates a composite that contains the {@link #tableInfoLabel} and the
	 * {@link #showAllColumnsButton}.
	 *
	 * @param parent
	 */
	protected void createTableInfo(Composite parent) {
		Composite tableInfoComposite = new Composite(parent, SWT.NONE);
		tableInfoComposite.setLayout(new GridLayout(4, false));
		tableInfoComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true,
				2, 1));

		tableInfoLabel = new Label(tableInfoComposite, SWT.NONE);
		tableInfoLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label separator = new Label(tableInfoComposite, SWT.SEPARATOR | SWT.VERTICAL);
		GridData separatorGridData = new GridData(SWT.CENTER, SWT.CENTER, false, false);
		separatorGridData.heightHint = 16;
		separator.setLayoutData(separatorGridData);
		showAllColumnsButton = new Button(tableInfoComposite, SWT.CHECK);
		showAllColumnsButton.setText("Show all Columns");
		showAllColumnsButton.setSelection(false);
		showAllColumnsButton.setEnabled(false);
		showAllColumnsButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				mediator.showAllColumnsButtonPressed();
			}

		});
	}

	@Override
	protected void helpPressed() {
		LinkHandler
				.openLink("http://www.icg.tugraz.at/project/caleydo/help/caleydo-2.0/loading-data");
	}
}
