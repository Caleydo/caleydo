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

import java.util.Random;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Shell;

import DKT.ConnectionLineVertex;
import DKT.MasterApplicationIPrx;
import DKT.Point2i;

public class LinkListener implements SelectionListener{
	
	private Shell shell;

	private DeskothequeManager deskothequeManager;
	
	public LinkListener(Shell shell, DeskothequeManager deskothequeManager){
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
		
		// get master proxy and deskotheque ID from deskotheque manager
		MasterApplicationIPrx masterPrx = deskothequeManager.getMasterProxy();  
		String deskoID = deskothequeManager.getDeskoID(); 
		
		if(masterPrx != null){
			org.eclipse.swt.graphics.Rectangle shellRect = shell.getBounds();
			int num = 3; 
			int dummyNum = 6; 
			ConnectionLineVertex[] vertices = new ConnectionLineVertex[num + dummyNum]; 
			Random rand = new Random(); 
			for(int i = 0; i < num; i++){
				// create random point within widget
				Point2i p = new Point2i
						(shellRect.x + rand.nextInt(shellRect.width), 
						shellRect.y + rand.nextInt(shellRect.height)); 
				// save as connection line vertex
				vertices[i] = new ConnectionLineVertex(deskoID, p); 
			}
			
			// CAUTION: this is a testing feature which will work 
			// EXCLUSIVELY on my (manu's) machine and only for a 
			// very specific case! 
			// what it does: when called from the instance which was 
			// first registered, it pretends to draw lines to the 
			// second instance 
			String dummyTarget = "Caleydo-ServerAppI-fcggpc203-1-1"; 
			Point2i p0 = new Point2i(1500, 512); 
			Point2i p1 = new Point2i(1600, 612); 
			Point2i p2 = new Point2i(1400, 612); 
			vertices[num] = new ConnectionLineVertex(dummyTarget, p0); 
			vertices[num + 1] = new ConnectionLineVertex(dummyTarget, p1); 
			vertices[num + 2] = new ConnectionLineVertex(dummyTarget, p2); 
			
			String dummyTarget1 = "Caleydo-ServerAppI-fcggpc203-1-2"; 
			Point2i p3 = new Point2i(100, 100); 
			Point2i p4 = new Point2i(200, 200); 
			Point2i p5 = new Point2i(100, 200); 
			vertices[num + 3] = new ConnectionLineVertex(dummyTarget1, p3); 
			vertices[num + 4] = new ConnectionLineVertex(dummyTarget1, p4);
			vertices[num + 5] = new ConnectionLineVertex(dummyTarget1, p5);
			
			// send draw command: 
			// serverPrx: the server of the instance where the drawing has been 
			// invoked (might change soon) 
			// vertices: list of vertices to be connected 
			// 0: internal ID to store selection (unused) 
			masterPrx.drawConnectionLines(deskoID, vertices, 0); 
		}
		else{
			System.out.println("No Deskotheque connection"); 
		}
	}

}
