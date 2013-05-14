package org.caleydo.core.view.opengl.canvas.internal.swt;

import org.caleydo.core.util.base.ConstantLabelProvider;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.eclipse.swt.widgets.Control;

/**
 * using the control / canvas to show tooltips
 *
 * @author Samuel Gratzl
 *
 */
public final class SWTTooltipManager extends APickingListener {
	private final ILabeled label;

	private Control control;
	private String actLabel = null;

	public SWTTooltipManager(Control control, ILabeled label) {
		this.label = label;
		this.control = control;
	}

	public SWTTooltipManager(Control control, final String label) {
		this(control, new ConstantLabelProvider(label));
	}

	@Override
	public void mouseOver(Pick pick) {
		show();
	}

	@Override
	public void mouseOut(Pick pick) {
		hide();
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

	protected final void show() {
		control.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				actLabel = label.getLabel();
				control.setToolTipText(actLabel);
			}
		});
	}
}