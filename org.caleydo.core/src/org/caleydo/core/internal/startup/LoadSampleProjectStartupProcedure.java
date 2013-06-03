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
package org.caleydo.core.internal.startup;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import org.caleydo.core.startup.LoadProjectStartupProcedure;
import org.caleydo.core.util.system.RemoteFile;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Shell;



public class LoadSampleProjectStartupProcedure extends LoadProjectStartupProcedure {
	private final RemoteFile rFile;

	public LoadSampleProjectStartupProcedure(URL url) {
		this(RemoteFile.of(url));
	}

	public LoadSampleProjectStartupProcedure(RemoteFile rFile) {
		super(rFile.getFile().getAbsolutePath(), false);
		this.rFile = rFile;
	}

	@Override
	public void preWorkbenchOpen() {
		if (!rFile.inCache(false)) {
			try {
				new ProgressMonitorDialog(new Shell()).run(true, false, rFile);
			} catch (InvocationTargetException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		super.preWorkbenchOpen();
	}
}
