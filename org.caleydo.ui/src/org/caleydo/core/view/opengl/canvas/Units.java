/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.canvas;

import gleem.linalg.Vec2f;

import org.apache.commons.lang.SystemUtils;
import org.caleydo.core.gui.util.DisplayUtils;
import org.caleydo.core.util.execution.SafeCallable;
import org.eclipse.swt.widgets.Display;

/**
 * conversion tools from various ones to dip (device independent pixels)
 *
 * @author Samuel Gratzl
 *
 */
public enum Units {
	PX, PT, EM, DIP;

	private static final float base = 96;
	private static final int dpi;

	private static final float pt2dip;
	private static final float px2dip;
	private static final float em2dip;
	static {
		// dip = px / (dpi / base) = px * base / dpi
		// pt = px * 72 / dpi
		// em = pt/12
		// ->
		// dip = px * base / dpi
		// dip = pt * base / 72
		// dip = em * base / 6

		dpi = DisplayUtils.syncExec(Display.getDefault(), new SafeCallable<Integer>() {
			@Override
			public Integer call() {
				Display display = Display.getCurrent();
				int dpi;
				if (SystemUtils.IS_OS_MAC_OSX) {
					dpi = 96; // FIXME for retina displays
				} else
					dpi = display.getDPI().x;

				return dpi;
			}
		});

		px2dip = base / dpi;
		pt2dip = base / 72.f;
		em2dip = base / 6.f;
	}

	public static float px(float value_px) {
		return value_px * px2dip;
	}

	public static float pt(float value_pt) {
		return value_pt * pt2dip;
	}

	public static float em(float value_em) {
		return value_em * em2dip;
	}

	public static float dip(float value_dip) {
		return value_dip;
	}

	static int dip2px(float value_dip, float scale) {
		return Math.round(value_dip / px2dip * scale);
	}

	/**
	 * converts the given value to dip
	 */
	public float apply(float value) {
		switch (this) {
		case EM:
			return em(value);
		case PT:
			return pt(value);
		case PX:
			return px(value);
		case DIP:
			return dip(value);
		}
		throw new IllegalStateException();
	}

	/**
	 * converts the given dip to the unit
	 *
	 * @param value_dip
	 * @return
	 */
	public float unapply(float value_dip) {
		switch (this) {
		case EM:
			return value_dip / em2dip;
		case PT:
			return value_dip / pt2dip;
		case PX:
			return value_dip / px2dip;
		case DIP:
			return value_dip;
		}
		throw new IllegalStateException();
	}

	public Vec2f unapply(Vec2f value_dip) {
		switch (this) {
		case EM:
			return value_dip.times(1.f / em2dip);
		case PT:
			return value_dip.times(1.f / pt2dip);
		case PX:
			return value_dip.times(1.f / px2dip);
		case DIP:
			return value_dip;
		}
		throw new IllegalStateException();
	}

	@Override
	public String toString() {
		switch (this) {
		case EM:
			return "EM";
		case PT:
			return "Point";
		case PX:
			return "Pixel";
		case DIP:
			return "Device Independent Pixel";
		}
		throw new IllegalStateException();
	}
}
