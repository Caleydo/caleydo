/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.gui.toolbar.action;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.caleydo.core.gui.SimpleAction;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class TakeSnapshotAction extends SimpleAction {
	/**
	 * Constructor without arguments. In this case a snapshot from the whole workbench is made.
	 */
	public TakeSnapshotAction() {
		super("Take snapshot", "resources/icons/general/snapshot.png");
	}

	@Override
	public void run() {
		super.run();

		Display display = PlatformUI.getWorkbench().getDisplay();
		Shell shell = display.getActiveShell();

		String path = getFilePath(shell);
		if (path == null)
			return;

		// Image image = null;
		// try {
		// Thread.sleep(200);
		// } catch (InterruptedException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		//
		// // The default method does return empty views on windows -> use os screenshot functionality
		// if (System.getProperty("os.name").startsWith("Windows")) {
		//
		// // BufferedImage i = Screenshot.readToBufferedImage(0,0, shell.getBounds().x, shell.getBounds().y, false);
		// //
		// // ImageIO.write("ss12345", "png", tScreenCaptureImageFile);
		//
		// Robot robot;
		// // System.out.println("headless: " + GraphicsEnvironment.isHeadless());
		// try {
		// robot = new Robot();
		// } catch (AWTException e) {
		// throw new IllegalArgumentException("No robot");
		// }
		//
		// // Press Alt + PrintScreen
		// // (Windows shortcut to take a screen shot of the active window)
		// robot.keyPress(KeyEvent.VK_ALT);
		// robot.keyPress(KeyEvent.VK_PRINTSCREEN);
		// robot.keyRelease(KeyEvent.VK_PRINTSCREEN);
		// robot.keyRelease(KeyEvent.VK_ALT);
		//
		// // try {
		// // Thread.sleep(1000);
		// // } catch (InterruptedException e1) {
		// // // TODO Auto-generated catch block
		// // e1.printStackTrace();
		// // }
		//
		// Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
		//
		// try {
		// if (t != null && t.isDataFlavorSupported(DataFlavor.imageFlavor)) {
		// // image = (Image) t.getTransferData(DataFlavor.imageFlavor);
		// ImageIO.write((BufferedImage) t.getTransferData(DataFlavor.imageFlavor), "png", new File(
		// path));
		// }
		// } catch (UnsupportedFlavorException e) {
		// } catch (IOException e) {
		// }
		//
		// } else {

		Rectangle bounds = shell.getClientArea();
		GC gc = new GC(shell);

		Image image = new Image(display, bounds);
		gc.copyArea(image, 0, 0);
		gc.dispose();
		// }

		ImageLoader loader = new ImageLoader();
		loader.data = new ImageData[] { image.getImageData() };

		loader.save(path, SWT.IMAGE_PNG);

		String message = "Screenshot successfully written to\n" + path;

		Logger.log(new Status(IStatus.INFO, this.toString(), message));

		MessageDialog.openInformation(shell, "Screenshot", message);
	}

	private static String getFilePath(Shell shell) {
		String path = "screenshot_" + getDateTime() + ".png";

		FileDialog saveFileDialog = new FileDialog(shell, SWT.SAVE);
		saveFileDialog.setFilterExtensions(new String[] { "*.png" });
		saveFileDialog.setFileName(path);
		path = saveFileDialog.open();
		// check if file dialog was canceled
		if (path == null)
			return null;

		if (!path.endsWith(".png"))
			path += ".png";

		return path;
	}

	private static String getDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
		Date date = new Date();
		return dateFormat.format(date);
	}
}
