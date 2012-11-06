package org.caleydo.core.view.opengl.canvas.internal.swt;

import org.caleydo.core.util.base.ILabelProvider;
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
	private final ILabelProvider label;

	private Control control;
	private String actLabel = null;

	public SWTTooltipManager(Control control, ILabelProvider label) {
		this.label = label;
		this.control = control;
	}

	public SWTTooltipManager(Control control, final String label) {
		this(control, new ILabelProvider() {
			@Override
			public String getLabel() {
				return label;
			}

			@Override
			public String getProviderName() {
				throw new UnsupportedOperationException();
			}
		});
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