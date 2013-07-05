/**
 *
 */
package org.caleydo.core.view.opengl.picking;

/**
 * Picking listener that calls {@link #timedMouseOut(Pick)} of subclasses a
 * specified time after {@link #mouseOut(Pick)} was called. The subclasses can
 * then easily realize UI elements showing up on {@link #mouseOver(Pick)} of an
 * object and disappearing a specified time after the
 * <code>mouseOut(Pick)</code>.
 *
 * @author Christian
 *
 */
public abstract class ATimedMouseOutPickingListener extends APickingListener {

	public static final int DEFAULT_TIMEOUT = 1500;

	/**
	 * The time in milliseconds that is waited until
	 * {@link #timedMouseOut(Pick)} is called after {@link #mouseOut(Pick)} was
	 * called.
	 */
	protected int timeout = DEFAULT_TIMEOUT;

	/**
	 * Thread that calls {@link #timedMouseOut(Pick)} after {@link #timeout}
	 * milliseconds.
	 */
	private Thread timerThread;

	/**
	 * Specifies whether {@link #mouseOver(Pick)} was called recently.
	 */
	private boolean isCurrentMouseOver = false;

	/**
	 * Method that is called after {@link #timeout} milliseconds after the last
	 * time {@link #mouseOut(Pick)} was called. If {@link #mouseOver(Pick)} is
	 * called before this method will not be called until the next
	 * <code>mouseOut(Pick)</code> resets the timer.
	 *
	 * @param pick
	 */
	protected abstract void timedMouseOut(Pick pick);

	@Override
	public void mouseOver(Pick pick) {
		if (timerThread != null && timerThread.isAlive()) {
			timerThread.interrupt();
		}
		timerThread = null;

		setCurrentMouseOver(true);
	}

	@Override
	public final void mouseOut(final Pick pick) {
		setCurrentMouseOver(false);
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(timeout);
					synchronizedTimedMouseOut(pick);
				} catch (InterruptedException e) {
					// This is ok.
				}
			}
		};

		timerThread = new Thread(runnable);
		timerThread.start();
	}

	private synchronized void synchronizedTimedMouseOut(Pick pick) {
		if (!isCurrentMouseOver) {
			timedMouseOut(pick);
		}
	}

	/**
	 * @param isCurrentMouseOver
	 *            setter, see {@link #isCurrentMouseOver}
	 */
	private synchronized void setCurrentMouseOver(boolean isCurrentMouseOver) {
		this.isCurrentMouseOver = isCurrentMouseOver;
	}

	/**
	 * @param timeout
	 *            setter, see {@link #timeout}
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * @return the timeout, see {@link #timeout}
	 */
	public int getTimeout() {
		return timeout;
	}
}
