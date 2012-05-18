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
package org.caleydo.core.view.opengl.picking;

import java.awt.MouseInfo;
import java.awt.PointerInfo;

import org.caleydo.core.util.base.ILabelProvider;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolTip;

/**
 * The ToolTipPickingListener automatically displays a tooltip with specified
 * text on mouse over.
 * 
 * @author Christian
 * 
 */
public class ToolTipPickingListener extends APickingListener {

	private static final int MOUSE_POSITION_TOOLTIP_SPACING_PIXELS = 20;

	private AGLView view;
	private ToolTipThread thread;

	/**
	 * Title of the tooltip. It is recommended to use the title only in
	 * conjunction with a message.
	 */
	private String toolTipTitle;
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

	/**
	 * Thread that shows the tooltip.
	 * 
	 * @author Christian
	 * 
	 */
	private class ToolTipThread implements Runnable {
		private ToolTip toolTip;
		private boolean hideToolTip = false;
		private Point toolTipPosition;

		public ToolTipThread(Point toolTipPosition) {
			super();
			this.toolTipPosition = toolTipPosition;
		}

		@Override
		public void run() {
			createToolTip();
		}

		public synchronized void hideToolTip() {
			hideToolTip = true;
			if (toolTip != null) {
				System.out.println("hide method");
				toolTip.setVisible(false);
			}
		}

		private synchronized void createToolTip() {
			if (hideToolTip)
				return;
			System.out.println("create");
			toolTip = new ToolTip(new Shell(), 0);
			toolTip.setText(toolTipTitle == null ? "" : toolTipTitle);
			toolTip.setMessage(toolTipMessage == null ? "" : toolTipMessage);

			PointerInfo pointerInfo = MouseInfo.getPointerInfo();
			toolTip.setLocation(pointerInfo.getLocation().x, pointerInfo.getLocation().y
					+ MOUSE_POSITION_TOOLTIP_SPACING_PIXELS);
			toolTip.setAutoHide(true);

			toolTip.setVisible(true);
		}

	}

	public ToolTipPickingListener(AGLView view) {
		this.view = view;
	}

	public ToolTipPickingListener(AGLView view, String toolTipMessage) {
		this.view = view;
		this.toolTipMessage = toolTipMessage;
	}

	public ToolTipPickingListener(AGLView view, ILabelProvider labelProvider) {
		this.view = view;
		this.labelProvider = labelProvider;
	}

	public ToolTipPickingListener(AGLView view, String toolTipTitle, String toolTipMessage) {
		this.view = view;
		this.toolTipTitle = toolTipTitle;
		this.toolTipMessage = toolTipMessage;
	}

	@Override
	public void mouseOver(Pick pick) {

		// JToolTip tip = new JToolTip();
		// tip.setTipText("test");
		// JButton button;
		// button.
		// tip.setLocation(10, 10);
		// tip.setVisible(true);
		if (labelProvider != null) {
			toolTipMessage = labelProvider.getLabel();
			// toolTipTitle = labelProvider.getSecondaryLabel();
		}

		System.out.println("over");

		thread = new ToolTipThread(new Point(pick.getPickedPoint().x,
				pick.getPickedPoint().y));
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				// try {
				// Thread.sleep(500);
				view.getParentComposite().getDisplay().asyncExec(thread);
				// } catch (InterruptedException e) {
				// e.printStackTrace();
				// }
			}
		};

		Thread timerThread = new Thread(runnable);
		timerThread.start();

	}

	@Override
	public void mouseOut(Pick pick) {
		System.out.println("out");
		hideToolTip();
	}

	@Override
	public void clicked(Pick pick) {
		hideToolTip();
	}

	@Override
	public void rightClicked(Pick pick) {
		hideToolTip();
	}

	private void hideToolTip() {
		System.out.println("hide picking");
		view.getParentComposite().getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				System.out.println("hide thread");
				if (thread != null)
					thread.hideToolTip();
			}
		});
	}

	/**
	 * @param toolTipTitle
	 *            setter, see {@link #toolTipTitle}
	 */
	public void setToolTipTitle(String toolTipTitle) {
		this.toolTipTitle = toolTipTitle;
	}

	/**
	 * @return the toolTipTitle, see {@link #toolTipTitle}
	 */
	public String getToolTipTitle() {
		return toolTipTitle;
	}

	/**
	 * @param toolTipMessage
	 *            setter, see {@link #toolTipMessage}
	 */
	public void setToolTipMessage(String toolTipMessage) {
		this.toolTipMessage = toolTipMessage;
	}

	/**
	 * @return the toolTipMessage, see {@link #toolTipMessage}
	 */
	public String getToolTipMessage() {
		return toolTipMessage;
	}

	/**
	 * @param labelProvider
	 *            setter, see {@link #labelProvider}
	 */
	public void setLabelProvider(ILabelProvider labelProvider) {
		this.labelProvider = labelProvider;
	}

}
