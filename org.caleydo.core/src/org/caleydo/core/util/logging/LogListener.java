package org.caleydo.core.util.logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.caleydo.core.manager.GeneralManager;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;

/**
 * Log listener that logs events to a custom location, with custom format.
 * 
 * @author Alexander Lex
 */
public class LogListener
	implements ILogListener {

	private File logFile;

	public LogListener() {
		// String path = System.getProperty("user.dir");

		String directory = GeneralManager.CALEYDO_HOME_PATH + File.separator + "logs" + File.separator;
		File dir = new File(directory);
		if (!dir.exists())
			dir.mkdir();

		Date timeNow = new Date();
		String timeStamp = (new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss.SSS")).format(timeNow);
		logFile = new File(directory + timeStamp + ".log");
		try {
			logFile.createNewFile();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void logging(IStatus status, String plugin) {
		try {
			BufferedWriter bos = new BufferedWriter(new FileWriter(logFile, true));
			StringBuffer str = new StringBuffer();
			if (status.getSeverity() == IStatus.ERROR) {
				str.append("Error: ");
			}
			else if (status.getSeverity() == IStatus.CANCEL) {
				str.append("CANCEL: ");
			}
			else if (status.getSeverity() == IStatus.INFO) {
				str.append("INFO: ");
			}
			else if (status.getSeverity() == IStatus.WARNING) {
				str.append("WARNING: ");
			}
			else if (status.getSeverity() == IStatus.OK) {
				str.append("OK: ");
			}
			str.append(status.getPlugin() + ": ");

			str.append(status.getMessage());
			Throwable ex = status.getException();
			if (ex != null) {
				String stackTrace = status.getException().getStackTrace().toString();
				str.append(stackTrace);
			}
			str.append(System.getProperty("line.separator"));
			bos.write(str.toString());
			bos.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

}
