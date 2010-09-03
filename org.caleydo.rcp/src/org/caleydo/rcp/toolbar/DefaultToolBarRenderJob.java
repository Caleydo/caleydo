package org.caleydo.rcp.toolbar;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.perspective.GenomePerspective;
import org.caleydo.rcp.view.toolbar.IToolBarItem;
import org.caleydo.rcp.view.toolbar.ToolBarContainer;
import org.caleydo.rcp.view.toolbar.content.AToolBarContent;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.PlatformUI;

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
		toolBarView.removeAllViewSpecificToolBars();

		// UNCOMMENT THIS LINE TO DYNAMICALLY ADD TOOLBARS TO LEFT WORKBENCH FOLDER
		// for (AToolBarContent toolBarContent : toolBarContents) {
		// addToolBarContent(toolBarContent);
		// }
	}

	/**
	 * Adds the content of the given toolbar content to the toolbar in the default drawing style of toolbars.
	 * 
	 * @param toolBarContent
	 *            toolbar content to add to the toolbar
	 */
	private void addToolBarContent(AToolBarContent toolBarContent) {

		List<Group> viewSpecificGroups = toolBarView.getViewSpecificGroups();
		Composite parentComposite = toolBarView.getParentComposite();

		for (ToolBarContainer toolBarContainer : toolBarContent.getDefaultToolBar()) {
			Group group = new Group(parentComposite, SWT.NULL);
			GridLayout layout = new GridLayout(1, false);
			layout.marginBottom =
				layout.marginTop =
					layout.marginLeft =
						layout.marginRight = layout.horizontalSpacing = layout.verticalSpacing = 0;
			layout.marginHeight = layout.marginWidth = 0;
			group.setLayout(layout);
			group.setLayoutData(toolBarRenderer.createStandardGridData());

			viewSpecificGroups.add(group);

			// Needed to simulate toolbar wrapping which is not implemented for linux
			// See bug: https://bugs.eclipse.org/bugs/show_bug.cgi?id=46025

			addToolBarItems(group, toolBarContainer);

			ResourceLoader resourceLoader = GeneralManager.get().getResourceLoader();
			Display display = PlatformUI.getWorkbench().getDisplay();
			String path = toolBarContainer.getImagePath();
			resourceLoader.getImage(display, path);

			// TODO: write horizontal renderer
			if (!GenomePerspective.bIsWideScreen) {
				Label spacer = new Label(group, SWT.NULL);
				spacer.setLayoutData(new GridData(GridData.FILL_BOTH));
			}

			Label label = new Label(group, SWT.CENTER);
			label.setText(toolBarContainer.getTitle());
			label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			label.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));

			// Set the group as the labels data to be able to discriminate between spacer when chaning
			// background color
			label.setData(group);

			group.setData("viewType", toolBarContent.getViewClass().getName());
			group.setData("viewID", toolBarContent.getTargetViewData().getViewID());
			group.setData("resource", null);

			group.layout();
			parentComposite.layout();
		}
	}

	/**
	 * Method fills the toolbar in a given toolbar manager. Used in case of remote rendering. The array of
	 * toolbar managers is needed for simulating toolbar wrap which is not supported for linux. See bug:
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=46025
	 */
	private void addToolBarItems(Group group, ToolBarContainer toolBarContainer) {
		ArrayList<ToolBarManager> toolBarManagers = new ArrayList<ToolBarManager>();

		int itemIndex = 0;
		IToolBarManager toolBarManager = null;
		List<IToolBarItem> items = toolBarContainer.getToolBarItems();
		int wrapCount = toolBarRenderer.calcWrapCount(items.size());

		for (IToolBarItem item : items) {
			if (itemIndex % wrapCount == 0) {
				toolBarManager = createNewToolBar(group, toolBarManagers);
				itemIndex = 0;
			}
			if (item instanceof IAction) {
				toolBarManager.add((IAction) item);
				toolBarManager.update(true);
			}
			else if (item instanceof ControlContribution) {
				if (!toolBarManager.isEmpty()) {
					toolBarManager = createNewToolBar(group, toolBarManagers);
				}
				if (!toolBarManager.isEmpty()) {
					toolBarManager = createNewToolBar(group, toolBarManagers);
				}
				toolBarManager.add((ControlContribution) item);
				toolBarManager.update(true);
				toolBarManager = createNewToolBar(group, toolBarManagers);
				itemIndex = 0;
			}
			itemIndex++;
		}

		group.setData("toolBarManagers", toolBarManagers);
	}

	private ToolBarManager createNewToolBar(Group group, List<ToolBarManager> toolBarManagers) {
		final ToolBar toolBar = new ToolBar(group, SWT.WRAP | SWT.FLAT);
		ToolBarManager toolBarManager = new ToolBarManager(toolBar);
		toolBarManagers.add(toolBarManager);
		return toolBarManager;
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
