package org.caleydo.view.treemap.layout;

import java.awt.Color;

/**
 * 
 * @author Michael
 * Interface for the layout algorithm to paint a treemap. 
 */
public interface IGlPainter {

	/**
	 * 
	 */
	public void init();
	
	
	/**
	 * 
	 */
	public void finish();

	void paintRectangle(float x, float y, float xMax, float yMax, Color c);

}