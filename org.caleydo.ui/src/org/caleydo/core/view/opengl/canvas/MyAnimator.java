/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.canvas;

import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.media.opengl.GLAnimatorControl;
import javax.media.opengl.GLAutoDrawable;

import jogamp.opengl.FPSCounterImpl;

import org.caleydo.core.util.logging.Logger;

/**
 * custom animator implementation with newer techniques
 *
 * @author Samuel Gratzl
 *
 */
public class MyAnimator implements GLAnimatorControl, Runnable {
	private static final Logger log = Logger.create(MyAnimator.class);

	private final int fps;
	/**
	 * copy on write as we change the list less than iterate over it
	 */
	private final List<GLAutoDrawable> drawables = new CopyOnWriteArrayList<>();
	private final FPSCounterImpl fpsCounter = new FPSCounterImpl();

	private Thread timerThread;

	/**
	 * use executor service
	 */
	private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

	private boolean started = false;
	private ScheduledFuture<?> animating;

	public MyAnimator(int fps) {
		this.fps = fps;
		executor.submit(new Runnable() {
			@Override
			public void run() {
				timerThread = Thread.currentThread();
				timerThread.setName("MyAnimator GL Thread");
			}
		});
	}

	@Override
	public final void setUpdateFPSFrames(int frames, PrintStream out) {
		fpsCounter.setUpdateFPSFrames(frames, out);
	}

	@Override
	public final void resetFPSCounter() {
		fpsCounter.resetFPSCounter();
	}

	@Override
	public final int getUpdateFPSFrames() {
		return fpsCounter.getUpdateFPSFrames();
	}

	@Override
	public final long getFPSStartTime() {
		return fpsCounter.getFPSStartTime();
	}

	@Override
	public final long getLastFPSUpdateTime() {
		return fpsCounter.getLastFPSUpdateTime();
	}

	@Override
	public final long getLastFPSPeriod() {
		return fpsCounter.getLastFPSPeriod();
	}

	@Override
	public final float getLastFPS() {
		return fpsCounter.getLastFPS();
	}

	@Override
	public final int getTotalFPSFrames() {
		return fpsCounter.getTotalFPSFrames();
	}

	@Override
	public final long getTotalFPSDuration() {
		return fpsCounter.getTotalFPSDuration();
	}

	@Override
	public final float getTotalFPS() {
		return fpsCounter.getTotalFPS();
	}

	@Override
	public boolean isStarted() {
		return started;
	}

	@Override
	public boolean isAnimating() {
		return isStarted() && animating != null && !animating.isCancelled();
	}

	@Override
	public boolean isPaused() {
		return isStarted() && (animating == null || animating.isCancelled());
	}

	@Override
	public Thread getThread() {
		return timerThread;
	}

	@Override
	public boolean start() {
		if (isAnimating())
			return true;
		if (isPaused())
			return resume();
		fpsCounter.resetFPSCounter();
		started = true;
		return schedule();
	}

	private boolean schedule() {
		final long period = 0 < fps ? (long) (1000.0f / fps) : 1;
		assert animating == null || animating.isCancelled();
		animating = executor.scheduleAtFixedRate(this, 0, period, TimeUnit.MILLISECONDS);
		return true;
	}

	@Override
	public boolean stop() {
		if (!isStarted())
			return true;
		animating.cancel(true);
		started = false;
		return true;
	}

	public void shutdown() {
		stop();
		executor.shutdown();
	}

	@Override
	public boolean pause() {
		return animating.cancel(true);
	}

	@Override
	public boolean resume() {
		if (!isPaused())
			return true;
		return schedule();
	}

	@Override
	public void run() {
		try {
			for (GLAutoDrawable drawable : drawables) {
				if (Thread.interrupted())
					return;
				try {
					drawable.display();
				} catch (RuntimeException e) {
					log.error("display error: " + drawable, e);
					if (Thread.interrupted() || e.getCause() instanceof InterruptedException)
						return;
				}
			}
			fpsCounter.tickFPS();
		} catch (Throwable e) {
			log.error("general display error: " + e.getMessage(), e);
		}
	}

	@Override
	public void add(GLAutoDrawable drawable) {
		drawables.add(drawable);
		drawable.setAnimator(this);
	}

	@Override
	public void remove(GLAutoDrawable drawable) {
		drawable.setAnimator(null);
		drawables.remove(drawable);
	}

}
