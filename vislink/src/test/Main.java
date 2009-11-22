package test;

import VIS.Color4f;
import VIS.Selection;
import VIS.SelectionGroup;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("VisLink Daemon");

		// establish connection to VisRenderer
		RenderManager renderer = new RenderManager(); 
		renderer.connect(); 

		// register container at the renderer 
		boolean success = renderer.registerSelectionContainer
		(0, 200, 200, 400, 400, 1.0f, 0.0f, 0.0f, 1.0f);
		success &= renderer.registerSelectionContainer
		(1, 600, 200, 400, 400, -1.0f, 0.0f, 0.0f, 1.0f);
		System.out.println("success = " + success); 

		// synthesize selections  
		Selection[] selections1 = new Selection[2];  
		Selection[] selections2 = new Selection[1];  
		selections1[0] = new Selection
		(200, 200, 0, 0, new Color4f(-1, 0, 0, 0), true); 
		selections1[1] = new Selection
		(200, 300, 30, 30, new Color4f(-1, 0, 0, 0), false); 
		selections2[0] = new Selection
		(700, 300, 0, 0, new Color4f(-1, 0, 0, 0), false); 
		SelectionGroup[] groups = new SelectionGroup[2]; 
		groups[0] = new SelectionGroup(0, selections1); 
		groups[1] = new SelectionGroup(1, selections2); 
		
		// send selections to renderer 
		renderer.renderLinks(groups); 
		renderer.renderLinks(groups); 
		
		// disconnect from VisRenderer
		renderer.disconnect(); 

	}

}
