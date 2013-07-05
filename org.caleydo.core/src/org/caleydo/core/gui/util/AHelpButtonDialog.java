/**
 *
 */
package org.caleydo.core.gui.util;

import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * Dialog that makes a nice help button in push style.
 *
 * @author Christian Partl
 *
 */
public abstract class AHelpButtonDialog extends TrayDialog {

	/**
	 * The help button.
	 */
	protected ToolItem helpButton;

	/**
	 * @param shell
	 */
	protected AHelpButtonDialog(Shell shell) {
		super(shell);
	}

	@Override
	public boolean isHelpAvailable() {
		return true;
	}

	@Override
	protected Control createHelpControl(Composite parent) {
		Image helpImage = JFaceResources.getImage(DLG_IMG_HELP);
		if (helpImage != null) {
			return createHelpImageButton(parent, helpImage);
		}

		return super.createHelpControl(parent);
	}

	private ToolBar createHelpImageButton(Composite parent, Image image) {
		ToolBar toolBar = new ToolBar(parent, SWT.FLAT | SWT.NO_FOCUS);
		((GridLayout) parent.getLayout()).numColumns++;
		toolBar.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		final Cursor cursor = new Cursor(parent.getDisplay(), SWT.CURSOR_HAND);
		toolBar.setCursor(cursor);
		toolBar.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				cursor.dispose();
			}
		});
		helpButton = new ToolItem(toolBar, SWT.PUSH);
		helpButton.setImage(image);
		helpButton.setToolTipText(JFaceResources.getString("helpToolTip"));
		helpButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				helpPressed();
			}
		});
		return toolBar;
	}

	/**
	 * Called when the help button was pressed.
	 */
	protected abstract void helpPressed();

}
