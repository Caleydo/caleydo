package org.caleydo.core.gui.toolbar;

import java.util.List;

import org.caleydo.core.gui.toolbar.content.AToolBarContent;

/**
 * Render job for toolbar contents, usually used with eclipse's Display.asyncRun()
 * 
 * @author Werner Puff
 */
public class DefaultToolBarRenderJob
	implements Runnable {

	/** list of toolbar contents to render */
	private List<AToolBarContent> toolBarContents;

	/** toolbar view to render the content into */
	private RcpToolBarView toolBarView;

	/** toolbar renderer */
	private IToolBarRenderer toolBarRenderer;

	@Override
	public void run() {
	}

	public List<AToolBarContent> getToolBarContents() {
		return toolBarContents;
	}

	public void setToolBarContents(List<AToolBarContent> toolBarContents) {
		this.toolBarContents = toolBarContents;
	}

	public RcpToolBarView getToolBarView() {
		return toolBarView;
	}

	public void setToolBarView(RcpToolBarView toolBarView) {
		this.toolBarView = toolBarView;
	}

	public IToolBarRenderer getToolBarRenderer() {
		return toolBarRenderer;
	}

	public void setToolBarRenderer(IToolBarRenderer toolBarRenderer) {
		this.toolBarRenderer = toolBarRenderer;
	}
}
