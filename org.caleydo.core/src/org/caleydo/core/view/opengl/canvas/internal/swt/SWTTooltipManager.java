/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.canvas.internal.swt;

import java.util.Objects;

import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.IPickingLabelProvider;
import org.caleydo.core.view.opengl.picking.Pick;
import org.eclipse.swt.widgets.Control;

/**
 * using the control / canvas to show tooltips
 *
 * @author Samuel Gratzl
 *
 */
public final class SWTTooltipManager extends APickingListener {
	private final IPickingLabelProvider label;

	private Control control;
	private String actLabel = null;
	private Pick lastShownPick = null;

	public SWTTooltipManager(Control control, IPickingLabelProvider label) {
		this.label = label;
		this.control = control;
	}

	public SWTTooltipManager(Control control, final ILabeled labeled) {
		this.label = new IPickingLabelProvider() {
			@Override
			public String getLabel(Pick pick) {
				return labeled.getLabel();
			}
		};
		this.control = control;
	}

	public SWTTooltipManager(Control control, final String label) {
		this(control, new Constant(label));
	}

	@Override
	public void mouseOver(Pick pick) {
		show(pick);
		lastShownPick = pick;
	}

	@Override
	public void mouseOut(Pick pick) {
		if (lastShownPick != null && lastShownPick.getObjectID() == pick.getObjectID())
			hide();
	}

	@Override
	protected void mouseMoved(Pick pick) {
		if (label instanceof Constant)
			return;
		show(pick);
	}

	@Override
	public void clicked(Pick pick) {
		hide();
	}

	@Override
	public void rightClicked(Pick pick) {
		hide();
	}

	private void hide() {
		control.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				String showing = control.getToolTipText();
				if (actLabel != null && actLabel.equals(showing)) {
					control.setToolTipText("");
				}
				actLabel = null;
			}
		});
	}

	protected final void show(final Pick pick) {
		final String new_ = label.getLabel(pick);
		if (Objects.equals(new_, actLabel))
			return;
		actLabel = new_;
		control.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				control.setToolTipText(new_ == null ? "" : new_);
			}
		});
	}

	private static final class Constant implements IPickingLabelProvider {
		private final String value;

		public Constant(String value) {
			this.value = value;
		}

		@Override
		public String getLabel(Pick pick) {
			return value;
		}
	}
}
