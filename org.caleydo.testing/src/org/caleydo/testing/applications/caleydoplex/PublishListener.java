package org.caleydo.testing.applications.caleydoplex;


import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

public class PublishListener implements SelectionListener {

	public PublishListener() {
		
	}
	
	@Override
	public void widgetDefaultSelected(SelectionEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void widgetSelected(SelectionEvent arg0) {
		System.out.println("publish button pressed");
	}

}
