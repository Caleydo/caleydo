/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.view.opengl.canvas.internal.awt;

import java.awt.Color;
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
import org.caleydo.core.view.opengl.picking.APickingListener;
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
	private ILabelProvider labelProvider;

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
			panel.setBorder(BorderFactory.createLineBorder(Color.black));
			FlowLayout panelLayout = new FlowLayout(FlowLayout.CENTER);
			panelLayout.setHgap(2);
			panelLayout.setVgap(1);
			panel.setLayout(panelLayout);
			panel.add(label);
			getContentPane().add(panel);
			getContentPane().setBackground(new Color(225, 225, 225));
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

	public ToolTipPickingListener(ILabelProvider labelProvider) {
		this.labelProvider = labelProvider;
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
