package cerberus.view.gui.swt.pathway.jgraph;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class NeighborhoodInputDialog 
extends Dialog {
	
	protected int value;

	/**
	 * @param parent
	 */
	public NeighborhoodInputDialog(Shell parent) {

		super(parent);
	}

	/**
	 * @param parent
	 * @param style
	 */
	public NeighborhoodInputDialog(Shell parent, int style) {

		super(parent, style);
	}

	/**
	 * Makes the dialog visible.
	 * 
	 * @return
	 */
	public int open() {

		Shell parent = getParent();
		final Shell shell = new Shell(parent, SWT.TITLE | SWT.BORDER
				| SWT.APPLICATION_MODAL);
		
		shell.setText("Input dialog");

		shell.setLayout(new GridLayout(2, true));
		
		Label label = new Label(shell, SWT.NULL);
		label.setText("Neighbourhood distance:");

		final Text text = new Text(shell, SWT.SINGLE | SWT.BORDER);
		text.setTextLimit(1);
		text.setText("1");
		text.setBounds(5, 5, 100, 20);
		
		final Button buttonOK = new Button(shell, SWT.PUSH);
		buttonOK.setText("Ok");
		buttonOK.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		Button buttonCancel = new Button(shell, SWT.PUSH);
		buttonCancel.setText("Cancel");

		text.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event event) {

				try
				{
					value = new Integer(text.getText());
					buttonOK.setEnabled(true);
				} catch (Exception e)
				{
					buttonOK.setEnabled(false);
				}
			}
		});

		buttonOK.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {

				shell.dispose();
			}
		});

		buttonCancel.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {

				// Default value
				value = 1;
				shell.dispose();
			}
		});

		shell.addListener(SWT.Traverse, new Listener() {
			public void handleEvent(Event event) {

				if (event.detail == SWT.TRAVERSE_ESCAPE)
					event.doit = false;
			}
		});
		
		shell.pack();
		shell.open();

		Display display = parent.getDisplay();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}

		return value;
	}
}