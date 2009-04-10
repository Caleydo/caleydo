package org.caleydo.rcp.views.swt.toolbar;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.views.swt.toolbar.content.AToolBarContent;
import org.caleydo.rcp.views.swt.toolbar.content.ToolBarContainer;
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
 * @author Werner Puff
 */
public class WideScreenToolBarRenderJob
	implements Runnable {

	/** list of toolbar contents to render */ 
	private List<AToolBarContent> toolBarContents;
	
	/** toolbar view to render the content into */
	private ToolBarView toolBarView;
	
	/** toolbar renderer */
	private IToolBarRenderer toolBarRenderer;

	@Override
	public void run() {
		toolBarView.removeAllViewSpecificToolBars();
		for (AToolBarContent toolBarContent : toolBarContents) {
			addToolBarContent(toolBarContent);
		}
	}

	public void addToolBarContent(AToolBarContent toolBarContent) {
		List<Group> viewSpecificGroups = toolBarView.getViewSpecificGroups();
		Composite parentComposite = toolBarView.getParentComposite();
		
		for (ToolBarContainer toolBarContainer : toolBarContent.getDefaultToolBar()) {
			Group group = new Group(parentComposite, SWT.NULL);
			GridLayout layout = new GridLayout(1, false);
			layout.marginBottom =
				layout.marginTop =
					layout.marginLeft =
						layout.marginRight = layout.horizontalSpacing = layout.verticalSpacing = 0;
			layout.marginHeight = layout.marginWidth = 3;
			group.setLayout(layout);
			group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			viewSpecificGroups.add(group);
		
			// Needed to simulate toolbar wrapping which is not implemented for linux
			// See bug: https://bugs.eclipse.org/bugs/show_bug.cgi?id=46025
			ArrayList<ToolBar> alToolBar = new ArrayList<ToolBar>();
			ArrayList<IToolBarManager> toolBarManagers = new ArrayList<IToolBarManager>();
		
			final ToolBar toolBar = new ToolBar(group, SWT.WRAP | SWT.FLAT);
			ToolBarManager toolBarManager = new ToolBarManager(toolBar);
			alToolBar.add(toolBar);
			toolBarManagers.add(toolBarManager);
		
			final ToolBar toolBar2 = new ToolBar(group, SWT.WRAP | SWT.FLAT);
			ToolBarManager toolBarManager2 = new ToolBarManager(toolBar2);
			alToolBar.add(toolBar2);
			toolBarManagers.add(toolBarManager2);
			
			fillToolBar(toolBarManagers, toolBarContainer);
	
			ResourceLoader resourceLoader = GeneralManager.get().getResourceLoader();
			Display display = PlatformUI.getWorkbench().getDisplay();
			String path = toolBarContainer.getImagePath(); 
			resourceLoader.getImage(display, path);

			// bIsBucketViewActive = true;
			// toolBarView.updateSearchBar(true);
			toolBarManager.update(true);
	
			if (toolBarManager2.isEmpty()) {
				toolBarManager2.dispose();
			}
			else {
				toolBarManager2.update(true);
			}
	
// 			TODO: write horizontal renderer
//			if (bHorizontal) {
//				Label spacer = new Label(group, SWT.NULL);
//				spacer.setLayoutData(new GridData(GridData.FILL_BOTH));
//			}
	
			Label label = new Label(group, SWT.CENTER);
			label.setText(toolBarContainer.getTitle());
			label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			label.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
	
			group.setData("viewType", toolBarContent.getViewClass().getName());
			group.setData("viewID", toolBarContent.getTargetViewID());
	
			group.layout();
			parentComposite.layout();
		}
	}

	/**
	 * Method fills the toolbar in a given toolbar manager. Used in case of remote rendering. The array of
	 * toolbar managers is needed for simulating toolbar wrap which is not supported for linux. See bug:
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=46025
	 */
	public void fillToolBar(ArrayList<IToolBarManager> alToolBarManager, ToolBarContainer toolBarContainer) {
		// add action items
		int iToolBarWrapCount = toolBarRenderer.calcWrapCount(toolBarContainer.size());

		for (int iToolBarItemIndex = 0; iToolBarItemIndex < toolBarContainer.size(); iToolBarItemIndex++) {
			alToolBarManager.get((int) (iToolBarItemIndex / iToolBarWrapCount)).add(
				toolBarContainer.get(iToolBarItemIndex));
		}
	}

	public List<AToolBarContent> getToolBarContents() {
		return toolBarContents;
	}

	public void setToolBarContents(List<AToolBarContent> toolBarContents) {
		this.toolBarContents = toolBarContents;
	}

	public ToolBarView getToolBarView() {
		return toolBarView;
	}

	public void setToolBarView(ToolBarView toolBarView) {
		this.toolBarView = toolBarView;
	}

	public IToolBarRenderer getToolBarRenderer() {
		return toolBarRenderer;
	}

	public void setToolBarRenderer(IToolBarRenderer toolBarRenderer) {
		this.toolBarRenderer = toolBarRenderer;
	}

}
