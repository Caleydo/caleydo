package org.caleydo.rcp.core.bridge;

import org.caleydo.core.bridge.gui.IGUIBridge;
import org.caleydo.rcp.command.handler.ExitHandler;
import org.eclipse.core.commands.ExecutionException;

public class RCPBridge
	implements IGUIBridge {
	private String sFileNameCurrentDataSet;

	@Override
	public void closeApplication() {
		try {
			new ExitHandler().execute(null);
		}
		catch (ExecutionException e) {
			throw new IllegalStateException("Cannot execute exit command.");
		}
	}

	@Override
	public void setShortInfo(String sMessage) {
//		PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	}

	@Override
	public void setFileNameCurrentDataSet(String sFileName) {
		this.sFileNameCurrentDataSet = sFileName;
	}

	@Override
	public String getFileNameCurrentDataSet() {
		return sFileNameCurrentDataSet;
	}
}
