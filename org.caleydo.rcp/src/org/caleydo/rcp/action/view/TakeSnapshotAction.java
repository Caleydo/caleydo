package org.caleydo.rcp.action.view;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;


public class TakeSnapshotAction
extends AToolBarAction
{
	public static final String TEXT = "Take snapshot";
	public static final String ICON = "resources/icons/general/snapshot.png";

	/**
	 * Constructor.
	 */
	public TakeSnapshotAction(int iViewID)
	{
		super(iViewID);
		
		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromURL(this.getClass()
				.getClassLoader().getResource(ICON)));
	}
	
	@Override
	public void run()
	{
		super.run();
		
		String sFilePath = "screenshot_" + getDateTime() + ".png";

		Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
		
		GC gc = new GC(PlatformUI.getWorkbench().getDisplay());
		final Image image = new Image(shell.getDisplay(), shell.getBounds());
		gc.copyArea(image, shell.getBounds().x, shell.getBounds().y);
		gc.dispose();

		FileDialog saveFileDialog = new FileDialog(shell, SWT.SAVE);
		saveFileDialog.setFileName(sFilePath);
		sFilePath = saveFileDialog.open();

		ImageLoader loader = new ImageLoader();
		loader.data = new ImageData[] { image.getImageData() };
		loader.save(sFilePath, SWT.IMAGE_PNG);

		// MessageBox messageBox = new MessageBox(swtShell, SWT.OK);
		// messageBox.setText("Message from SWT");
		// messageBox.setMessage("Screenshot successfully written to " +
		// sFilePath);
		// messageBox.open();
	}
	
	private String getDateTime()
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmm");
		Date date = new Date();
		return dateFormat.format(date);
	}
}
