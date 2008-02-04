/**
 * 
 */
package org.geneview.core.view.jogl;

import java.util.concurrent.atomic.AtomicInteger;

import javax.media.opengl.GLAutoDrawable;

import com.sun.opengl.util.FPSAnimator;


/**
 * @author Michael Kalkusch
 *
 */
public class TriggeredAnimator extends FPSAnimator {

	protected AtomicInteger iEventCounter;
	
	/**
	 * Constructor.
	 */
	public TriggeredAnimator(final int iFrameRate) {

		super (iFrameRate);
		iEventCounter = new AtomicInteger(0);
	}


	/**
	 * Constructor.
	 * 
	 * @param drawable
	 * @param iFrameRate
	 */
	public TriggeredAnimator(final GLAutoDrawable drawable, final int iFrameRate) {

		super(drawable, iFrameRate);
		iEventCounter = new AtomicInteger(0);
	}
	
	/**
	 * Stop the animator if it has not been started yet and incremetns the event counter.
	 *
	 */
	public synchronized void stopEventCount() {
		
		if ( iEventCounter.decrementAndGet() < 1) 
		{
			this.stop();
		}
	}
	
	/**
	 * Stop the animator if no more canvas objects listen to it.
	 *
	 */
	public synchronized void startEventCount() {
		
		if ( iEventCounter.getAndIncrement() == 0 ) 
		{
			this.start();
			System.out.println("XX: TriggeredAnimator:  === START ANIMATOR === " + this.getClass().toString());
		}
	}

}
