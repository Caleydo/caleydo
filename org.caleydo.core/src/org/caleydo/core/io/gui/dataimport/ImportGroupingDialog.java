/**
 *
 */
package org.caleydo.core.io.gui.dataimport;

import org.caleydo.core.gui.util.AHelpButtonDialog;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.io.GroupingParseSpecification;
import org.caleydo.core.io.gui.dataimport.widget.BooleanCallback;
import org.caleydo.core.io.gui.dataimport.widget.DelimiterWidget;
import org.caleydo.core.io.gui.dataimport.widget.ICallback;
import org.caleydo.core.io.gui.dataimport.widget.IntegerCallback;
import org.caleydo.core.io.gui.dataimport.widget.LabelWidget;
import org.caleydo.core.io.gui.dataimport.widget.LoadFileWidget;
import org.caleydo.core.io.gui.dataimport.widget.PreviewTableWidget;
import org.caleydo.core.io.gui.dataimport.widget.RowConfigWidget;
import org.caleydo.core.io.gui.dataimport.widget.SelectAllNoneWidget;
import org.caleydo.core.util.link.LinkHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

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

	protected PreviewTableWidget previewTable;

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
		rowConfig.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		delimiterRadioGroup = new DelimiterWidget(parentComposite, new ICallback<String>() {
			@Override
			public void on(String delimiter) {
				mediator.onDelimiterChanged(delimiter);
			}
		});

		selectAllNone = new SelectAllNoneWidget(parentComposite, new BooleanCallback() {
			@Override
			public void on(boolean selectAll) {
				mediator.onSelectAllNone(selectAll);
			}
		});

		previewTable = new PreviewTableWidget(parentComposite, new BooleanCallback() {
			@Override
			public void on(boolean showAllColumns) {
				mediator.onShowAllColumns(showAllColumns);
			}
		});

		mediator.guiCreated();
	}

	/**
	 * @return the groupingParseSpecification, see
	 *         {@link #groupingParseSpecification}
	 */
	public GroupingParseSpecification getGroupingParseSpecification() {
		return mediator.getGroupingParseSpecification();
	}



	@Override
	protected void helpPressed() {
		LinkHandler
				.openLink("http://www.icg.tugraz.at/project/caleydo/help/caleydo-2.0/loading-data");
	}


}
