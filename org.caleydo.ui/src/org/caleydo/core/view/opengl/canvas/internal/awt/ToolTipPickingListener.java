/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.canvas.internal.awt;

import java.awt.FlowLayout;
import java.awt.MouseInfo;
import java.awt.PointerInfo;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

import org.caleydo.core.util.base.ILabelProvider;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.IPickingLabelProvider;
import org.caleydo.core.view.opengl.picking.Pick;

/**
 * The ToolTipPickingListener automatically displays a tooltip with specified
 * text on mouse over.
 *
 * @author Christian
 *
 */
final class ToolTipPickingListener extends APickingListener {

	private static final int MOUSE_POSITION_TOOLTIP_SPACING_PIXELS = 20;

	private ToolTipThread toolTipThread;

	/**
	 * Message of the tooltip.
	 */
	private String toolTipMessage;

	/**
	 * Provider for the text in the tooltip. The message is taken from the
	 * {@link ILabelProvider#getLabel()} and the title is taken from
	 * {@link ILabelProvider#getSecondaryLabel()}.
	 */
	private ILabeled labelProvider;

	private IPickingLabelProvider pickingLabelProvider;

	private class ToolTipp extends JWindow {

		private static final long serialVersionUID = 1L;

		public ToolTipp(String message, JFrame frame) {
			super(frame);
			FlowLayout layout = new FlowLayout(FlowLayout.CENTER);
			layout.setHgap(0);
			layout.setVgap(0);
			setLayout(layout);
			JLabel label = new JLabel(message);
			JPanel panel = new JPanel();
			panel.setBorder(BorderFactory.createLineBorder(java.awt.Color.BLACK));
			FlowLayout panelLayout = new FlowLayout(FlowLayout.CENTER);
			panelLayout.setHgap(2);
			panelLayout.setVgap(1);
			panel.setLayout(panelLayout);
			panel.add(label);
			getContentPane().add(panel);
			getContentPane().setBackground(new java.awt.Color(225, 225, 225));
			getContentPane().setFocusable(false);

			pack();
		}
	}

	/**
	 * Thread that shows the tooltip.
	 *
	 * @author Christian
	 *
	 */
	private class ToolTipThread implements Runnable {
		private ToolTipp toolTip;
		// private ToolTip toolTip;
		private boolean hideToolTip = false;

		@Override
		public void run() {
			createToolTip();
		}

		public void hide() {
			hideToolTip = true;

			if (toolTip != null) {
				toolTip.setVisible(false);
				toolTip.dispose();
			}
		}

		public void create() {

//			 System.out.println("create");

			PointerInfo pointerInfo = MouseInfo.getPointerInfo();

			toolTip = new ToolTipp(toolTipMessage, new JFrame());
			toolTip.setLocation(pointerInfo.getLocation().x, pointerInfo.getLocation().y
					+ MOUSE_POSITION_TOOLTIP_SPACING_PIXELS);
			toolTip.setVisible(true);
		}

	}

	public ToolTipPickingListener(String toolTipMessage) {
		this.toolTipMessage = toolTipMessage;
	}

	public ToolTipPickingListener(ILabeled labelProvider) {
		this.labelProvider = labelProvider;
	}

	public ToolTipPickingListener(IPickingLabelProvider labelProvider) {
		this.pickingLabelProvider = labelProvider;
	}

	private synchronized void createToolTip() {
		if (toolTipThread.hideToolTip)
			return;
		toolTipThread.create();
	}

	private synchronized void hideToolTip() {
		if (toolTipThread != null) {
			toolTipThread.hide();
		}
		toolTipThread = null;
	}

	private void triggerToolTipCreation() {

//		 System.out.println("over");
		if(toolTipThread != null)
			return;

		toolTipThread = new ToolTipThread();
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				// try {
				// Thread.sleep(500);
				SwingUtilities.invokeLater(toolTipThread);
				// } catch (InterruptedException e) {
				// e.printStackTrace();
				// }
			}
		};

		Thread timerThread = new Thread(runnable);
		timerThread.start();
	}

	@Override
	public void mouseOver(Pick pick) {

		if (labelProvider != null) {
			toolTipMessage = labelProvider.getLabel();
		}
		if (pickingLabelProvider != null) {
			toolTipMessage = pickingLabelProvider.getLabel(pick);
		}
		triggerToolTipCreation();
	}

	@Override
	public void mouseOut(Pick pick) {
		// System.out.println("out");
		triggerToolTipHide();
	}

	@Override
	public void clicked(Pick pick) {
		triggerToolTipHide();
	}

	@Override
	public void rightClicked(Pick pick) {
		triggerToolTipHide();
	}

	private void triggerToolTipHide() {
		// System.out.println("hide picking");
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				// System.out.println("hide thread");
				hideToolTip();
			}
		});
	}
}
