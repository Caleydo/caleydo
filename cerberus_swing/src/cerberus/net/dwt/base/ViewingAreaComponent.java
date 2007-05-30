/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.net.dwt.base;

import gleem.linalg.Vec3f;

/**
 * Base getter and setter for visible areas in 3D.
 * 
 * @author Michael Kalkusch
 *
 */
public interface ViewingAreaComponent {

	/**
	 * Define the viewable area.
	 * 
	 * @param v3fPanPercent Pan in percent. Default is (0,0,0)
	 * @param v3fWindowPercent viewable area in percent. Default is (1,1,1)
	 * @param v3fRotation roation in Euler angles [0 .. 2PI]
	 */
	public void setVisibleArea( final Vec3f v3fPanPercent, 
			final Vec3f v3fWindowPercent,
			final Vec3f v3fRotation );
	
	/**
	 * Get Pan in percent.
	 * 
	 * @return pan in percent
	 */
	public Vec3f getVisibleAreaPanPercentage( );
	
	/**
	 * Get visiabel window in percent.
	 * 
	 * @return visible window in percent
	 */
	public Vec3f getVisibleAreaWindowPercentage( );
	
	/**
	 * Get roation in Euler angles from [0 .. 2PI]
	 * 
	 * @return roation in range of [0 .. 2PI]
	 */
	public Vec3f getVisibleAreaRoationXYZ( );
}
