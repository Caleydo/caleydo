package org.caleydo.core.view.swt.tabular;

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

public class LabelEditorDialog
	extends Dialog
{
	String sLabel;

	/**
	 * @param parent
	 */
	public LabelEditorDialog(Shell parent)
	{
		super(parent);
	}

	public String open(String sInitialLabel)
	{
		Shell parent = getParent();
		final Shell shell = new Shell(parent, SWT.TITLE | SWT.BORDER | SWT.APPLICATION_MODAL);
		shell.setText("Change Label");

		shell.setLayout(new GridLayout(2, false));

		Label label = new Label(shell, SWT.NULL);
		label.setText("Enter caption:");

		final Text text = new Text(shell, SWT.SINGLE | SWT.BORDER);
		GridData data = new GridData(150, 20);
		text.setLayoutData(data);
		text.setText(sInitialLabel);
		text.selectAll();

		final Button buttonOK = new Button(shell, SWT.PUSH);
		buttonOK.setText("Ok");
		buttonOK.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		Button buttonCancel = new Button(shell, SWT.PUSH);
		buttonCancel.setText("Cancel");

		text.addListener(SWT.Modify, new Listener()
		{
			public void handleEvent(Event event)
			{
				try
				{
					sLabel = text.getText();
					buttonOK.setEnabled(true);
				}
				catch (Exception e)
				{
					buttonOK.setEnabled(false);
				}
			}
		});

		buttonOK.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event event)
			{
				shell.dispose();
			}
		});

		buttonCancel.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event event)
			{
				sLabel = null;
				shell.dispose();
			}
		});

		shell.addListener(SWT.Traverse, new Listener()
		{
			public void handleEvent(Event event)
			{
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

		return sLabel;
	}

	public static void main(String[] args)
	{
		Shell shell = new Shell();
		LabelEditorDialog dialog = new LabelEditorDialog(shell);
		System.out.println(dialog.open("old"));
	}
}
