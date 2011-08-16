package org.caleydo.core.gui.toolbar.action;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.data.loader.ResourceLoader;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class TakeSnapshotAction
	extends AToolBarAction
	implements IToolBarItem {

	public static final String TEXT = "Take snapshot";
	public static final String ICON = "resources/icons/general/snapshot.png";

	private Composite composite;

	/**
	 * Constructor without arguments. In this case a snapshot from the whole workbench is made.
	 */
	public TakeSnapshotAction() {

		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), ICON)));
	}

	/**
	 * Constructor that takes a composite which which the screenshot is taken.
	 */
	public TakeSnapshotAction(Composite composite) {

		this();
		this.composite = composite;
	}

	@Override
	public void run() {
		super.run();

		String path = "screenshot_" + getDateTime() + ".png";

		Display display = PlatformUI.getWorkbench().getDisplay();
		Shell shell = display.getActiveShell();
		Rectangle bounds = null;
		GC gc = null;
		if (composite == null) {
			bounds = shell.getBounds();
			gc = new GC(shell);
		}
		else {
			bounds = composite.getBounds();
			gc = new GC(composite);
		}

		final Image image = new Image(display, bounds);
		gc.copyArea(image, 0, 0);
		gc.dispose();

		FileDialog saveFileDialog = new FileDialog(shell, SWT.SAVE);
		saveFileDialog.setFileName(path);
		path = saveFileDialog.open();

		ImageLoader loader = new ImageLoader();
		loader.data = new ImageData[] { image.getImageData() };

		// check if file dialog was canceled
		if (path == null)
			return;

		loader.save(path, SWT.IMAGE_PNG);

		String message = "Screenshot successfully written to\n" + path;

		Logger.log(new Status(IStatus.INFO, this.toString(), message));

		MessageBox messageBox = new MessageBox(shell, SWT.OK);
		messageBox.setText("Screenshot");
		messageBox.setMessage(message);
		messageBox.open();
	}

	private String getDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmm");
		Date date = new Date();
		return dateFormat.format(date);
	}
}
