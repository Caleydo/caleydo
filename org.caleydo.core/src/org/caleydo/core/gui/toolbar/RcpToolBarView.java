package org.caleydo.core.gui.toolbar;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.ISizeProvider;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * Toolbar view containing all toolbars contributed dynamically by views. This view is implemented as
 * IMediatorReceiver because it highlights the active view toolbar when an event is coming in.
 * 
 * @author Marc Streit
 */
public class RcpToolBarView
	extends ViewPart
	implements ISizeProvider {
	public static final String ID = "org.caleydo.core.gui.toolbar.RcpToolBarView";

	public static final int TOOLBAR_WIDTH = 213;
	public static final int TOOLBAR_HEIGHT = 110;

	private IToolBarRenderer toolBarRenderer;

	private Composite parentComposite;

	private ArrayList<Group> viewSpecificGroups;

	@Override
	public void createPartControl(Composite parent) {
		final Composite parentComposite = new Composite(parent, SWT.NULL);

		toolBarRenderer = new WideScreenToolBarRenderer();

		parentComposite.setLayout(toolBarRenderer.createLayout());
		this.parentComposite = parentComposite;

		viewSpecificGroups = new ArrayList<Group>();

		addGeneralToolBar();
	}

	@Override
	public void setFocus() {

	}

	@Override
	public void dispose() {
		super.dispose();
	}

	public void removeViewSpecificToolBar(int iViewID) {
		Group removedGroup = null;
		for (Group group : viewSpecificGroups) {
			if (!(group.getData("view") instanceof AGLView)) {
				continue;
			}

			if (group.getData("viewID") != null && ((Integer) group.getData("viewID")).intValue() == iViewID) {
				group.dispose();
				removedGroup = group;
				break;
			}
		}

		if (removedGroup != null) {
			viewSpecificGroups.remove(removedGroup);
		}

		// Remove toolbars of remote rendered views
		AGLView glView = GeneralManager.get().getViewGLCanvasManager().getGLView(iViewID);

		if (glView instanceof IGLRemoteRenderingView) {
			for (AGLView view : ((IGLRemoteRenderingView) glView).getRemoteRenderedViews()) {
				removeViewSpecificToolBar(view.getID());
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void removeAllViewSpecificToolBars() {
		for (Group group : viewSpecificGroups) {
			List<ToolBarManager> toolBarManagers = (List<ToolBarManager>) group.getData("toolBarManagers");
			if (toolBarManagers != null) {
				for (ToolBarManager toolBarManager : toolBarManagers) {
					toolBarManager.dispose();
				}
			}
			group.dispose();
		}
		viewSpecificGroups.clear();
	}

	private void addGeneralToolBar() {
		Group group = new Group(parentComposite, SWT.NULL);
		GridLayout layout = new GridLayout(1, false);
		layout.marginBottom =
			layout.marginTop =
				layout.marginLeft =
					layout.marginRight = layout.horizontalSpacing = layout.verticalSpacing = 0;
		layout.marginHeight = layout.marginWidth = 0;
		group.setLayout(layout);
		group.setLayoutData(toolBarRenderer.createStandardGridData());

		// group.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));

		toolBarRenderer.addGeneralToolBarActions(group);

		Label label = new Label(group, SWT.CENTER);
		label.setText("General");
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
	}

	public void highlightViewSpecificGroup(final Object eventTrigger) {
		if (eventTrigger instanceof AGLView) {
			final AGLView glView = ((AGLView) eventTrigger);

			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					// Check if toolbar is present
					for (Group group : viewSpecificGroups) {
						for (Control subControl : group.getChildren()) {
							if (subControl instanceof Label && subControl.getData() == group) {
								if (group.getData("viewID") != null
									&& ((Integer) group.getData("viewID")).intValue() == glView.getID()
									|| glView.getViewType().equals("org.caleydo.view.pathway")
									&& group.getData("viewType") == "org.caleydo.view.pathway") {
									((Label) subControl).setBackground(Display.getCurrent().getSystemColor(
										SWT.COLOR_DARK_GRAY));
								}
								else {
									((Label) subControl).setBackground(Display.getCurrent().getSystemColor(
										SWT.COLOR_GRAY));
								}
							}
						}
					}
				}
			});
		}
	}

	@Override
	public int computePreferredSize(boolean width, int availableParallel, int availablePerpendicular,
		int preferredResult) {
		// Set minimum size of the view
		if (width == true)
			return RcpToolBarView.TOOLBAR_WIDTH;

		return RcpToolBarView.TOOLBAR_HEIGHT;
	}

	@Override
	public int getSizeFlags(boolean width) {
		return SWT.MIN;
	}

	public IToolBarRenderer getToolBarRenderer() {
		return toolBarRenderer;
	}

	public void setToolBarRenderer(IToolBarRenderer toolBarRenderer) {
		this.toolBarRenderer = toolBarRenderer;
	}

	public Composite getParentComposite() {
		return parentComposite;
	}

	public void setParentComposite(Composite parentComposite) {
		this.parentComposite = parentComposite;
	}

	public ArrayList<Group> getViewSpecificGroups() {
		return viewSpecificGroups;
	}

	public void setViewSpecificGroups(ArrayList<Group> viewSpecificGroups) {
		this.viewSpecificGroups = viewSpecificGroups;
	}

}
