package org.caleydo.rcp.command.handler;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.handlers.HandlerUtil;

public class ScreenshotHandler
	extends AbstractHandler
	implements IHandler
{

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
//		HandlerUtil.getActiveWorkbenchWindow(event).close();

		return null;
	}

//	protected void writeScreenshot()
//	{
//		String sFilePath = "screenshot_" + 
//			new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date())+ ".png";
//
//		GC gc = new GC(swtComposite.getDisplay());
//		final Image image = new Image(swtComposite.getDisplay(), swtShell.getBounds());
//		gc.copyArea(image, swtShell.getBounds().x, swtShell.getBounds().y);
//		gc.dispose();
//
//		FileDialog saveFileDialog = new FileDialog(swtShell, SWT.SAVE);
//		saveFileDialog.setFileName(sFilePath);
//		sFilePath = saveFileDialog.open();
//
//		ImageLoader loader = new ImageLoader();
//		loader.data = new ImageData[] { image.getImageData() };
//		loader.save(sFilePath, SWT.IMAGE_PNG);
//
//		// TODO: send output to status line
//		// MessageBox messageBox = new MessageBox(swtShell, SWT.OK);
//		// messageBox.setText("Message from SWT");
//		// messageBox.setMessage("Screenshot successfully written to " +
//		// sFilePath);
//		// messageBox.open();
//	}
}
