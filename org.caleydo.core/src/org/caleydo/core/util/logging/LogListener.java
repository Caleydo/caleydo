/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 * 
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.util.logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
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
public class LogListener implements ILogListener {

	private File logFile;

	public LogListener() {
		// String path = System.getProperty("user.dir");

		String directory = GeneralManager.CALEYDO_HOME_PATH + File.separator + "logs"
				+ File.separator;
		File dir = new File(directory);
		if (!dir.exists())
			dir.mkdir();

		Date timeNow = new Date();
		String timeStamp = (new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss.SSS"))
				.format(timeNow);
		logFile = new File(directory + timeStamp + ".log");
		try {
			logFile.createNewFile();
		} catch (IOException e) {
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
			} else if (status.getSeverity() == IStatus.CANCEL) {
				str.append("CANCEL: ");
			} else if (status.getSeverity() == IStatus.INFO) {
				str.append("INFO: ");
			} else if (status.getSeverity() == IStatus.WARNING) {
				str.append("WARNING: ");
			} else if (status.getSeverity() == IStatus.OK) {
				str.append("OK: ");
			}
			str.append(status.getPlugin() + ": ");

			str.append(status.getMessage());
			Throwable ex = status.getException();
			if (ex != null) {

				str.append(stackTraceAsTring(ex));
			}
			str.append(System.getProperty("line.separator"));
			bos.write(str.toString());
			bos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String stackTraceAsTring(Throwable throwable) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		throwable.printStackTrace(printWriter);
		return result.toString();
	}

}
