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
package org.caleydo.testing.applications.caleydoplex;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Shell;

public class ConnectListener implements SelectionListener {

	private Shell shell;

	private DeskothequeManager deskothequeManager;

	public ConnectListener(Shell shell, DeskothequeManager deskothequeManager) {
		this.shell = shell;
		this.deskothequeManager = deskothequeManager;
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		// TODO Auto-generated method stub

		// get extents of shell window
		org.eclipse.swt.graphics.Rectangle shellRect = shell.getBounds();
		this.deskothequeManager.establishConnection(shellRect.x, shellRect.y,
				shellRect.width, shellRect.height);
		String deskoID = deskothequeManager.getDeskoID(); 
		if(deskoID == null){
			deskoID = "Unconnected"; 
		}
		shell.setText(deskoID); 
	}

}
