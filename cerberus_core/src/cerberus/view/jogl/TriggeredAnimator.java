/**
 * 
 */
package cerberus.view.jogl;

import java.util.concurrent.atomic.AtomicInteger;

import javax.media.opengl.GLAutoDrawable;

import com.sun.opengl.util.Animator;


/**
 * @author Michael Kalkusch
 *
 */
public class TriggeredAnimator extends Animator {

	protected AtomicInteger iEventCounter;
	
	/**
	 * 
	 */
	public TriggeredAnimator() {

		iEventCounter = new AtomicInteger(0);
	}

	/**
	 * @param drawable
	 */
	public TriggeredAnimator(GLAutoDrawable drawable) {

		super(drawable);
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
			System.out.println("X: TriggeredAnimator:  === START ANIMATOR === " + this.getClass().toString());
		}
	}

}
