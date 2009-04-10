package org.caleydo.rcp.views.swt.toolbar;

import java.util.ArrayList;

import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.manager.event.EMediatorType;
import org.caleydo.core.manager.event.IEventContainer;
import org.caleydo.core.manager.event.IMediatorReceiver;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.pathway.GLPathway;
import org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering;
import org.caleydo.rcp.action.toolbar.general.ExportDataAction;
import org.caleydo.rcp.action.toolbar.general.LoadDataAction;
import org.caleydo.rcp.action.toolbar.general.OpenSearchViewAction;
import org.caleydo.rcp.action.toolbar.view.TakeSnapshotAction;
import org.caleydo.rcp.perspective.GenomePerspective;
import org.caleydo.rcp.views.opengl.GLPathwayView;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.ISizeProvider;
import org.eclipse.ui.part.ViewPart;

/**
 * Toolbar view containing all toolbars contributed dynamically by views. This view is implemented as
 * IMediatorReceiver because it highlights the active view toolbar when an event is coming in.
 * 
 * @author Marc Streit
 */
public class ToolBarView
	extends ViewPart
	implements IMediatorReceiver, ISizeProvider {
	public static final String ID = "org.caleydo.rcp.views.swt.ToolBarView";

	public static final int TOOLBAR_WIDTH = 173;
	public static final int TOOLBAR_HEIGHT = 123;

	private IToolBarRenderer toolBarRenderer;

	private Composite parentComposite;

	private ArrayList<Group> viewSpecificGroups;
	
//	private SearchView searchView;

	@Override
	public void createPartControl(Composite parent) {
		GeneralManager.get().getEventPublisher().addReceiver(EMediatorType.SELECTION_MEDIATOR, this);

		final Composite parentComposite = new Composite(parent, SWT.NULL);

		if (GenomePerspective.bIsWideScreen) {
			toolBarRenderer = new WideScreenToolBarRenderer();
		} else {
			toolBarRenderer = new StandardToolBarRenderer();
		}

		parentComposite.setLayout(toolBarRenderer.createLayout());
		this.parentComposite = parentComposite;

//		searchView = (SearchView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(SearchView.ID);
		
		viewSpecificGroups = new ArrayList<Group>();

		addGeneralToolBar();
//		addColorMappingBar();

		ToolBarMediator toolBarMediator = new ToolBarMediator();
		toolBarMediator.setToolBarView(this);
		GeneralManager.get().getEventPublisher().addReceiver(EMediatorType.VIEW_SELECTION, toolBarMediator);
	}

	@Override
	public void setFocus() {

	}

	@Override
	public void dispose() {
		super.dispose();

		GeneralManager.get().getEventPublisher().removeReceiver(EMediatorType.SELECTION_MEDIATOR, this);
	}

	public void removeViewSpecificToolBar(int iViewID) {
		Group removedGroup = null;
		for (Group group : viewSpecificGroups) {
			if (!(group.getData("view") instanceof AGLEventListener)) {
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
		AGLEventListener glView = GeneralManager.get().getViewGLCanvasManager().getGLEventListener(iViewID);

		if (glView instanceof GLRemoteRendering) {
			for (int iRemoteRenderedGLViewID : ((GLRemoteRendering) glView).getRemoteRenderedViews()) {
				removeViewSpecificToolBar(iRemoteRenderedGLViewID);
			}

			// Update search bar
			// bIsBucketViewActive = false;
//			searchView.updateSearchBar(false);
		}
	}

	public void removeAllViewSpecificToolBars() {
		for (Group group : viewSpecificGroups) {
			group.dispose();
		}

		viewSpecificGroups.clear();

		// Update search bar
		// bIsBucketViewActive = false;
//		searchView.updateSearchBar(false);
	}

	// public void highlightViewSpecificToolBar(int iViewID)
	// {
	// // Unselect old highlights
	// for (Group group : viewSpecificGroups)
	// {
	// if (item.getData("view") instanceof AGLEventListener)
	// {
	// // ((ToolBar)item.getData()).setBackground(
	// //
	// parentComposite.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
	//
	// AGLEventListener glEventListener = (AGLEventListener)
	// item.getData("view");
	//
	// // if(glEventListener.isRenderedRemote())
	// // continue;
	//				
	// item.setExpanded(false);
	// }
	// }
	//
	// for (ExpandItem item : expandBar.getItems())
	// {
	// AGLEventListener glEventListener = (AGLEventListener)
	// item.getData("view");
	//
	// if (!(glEventListener instanceof AGLEventListener))
	// continue;
	//
	// // if (!(glEventListener instanceof GLRemoteRendering))
	// // {
	// // item.setExpanded(false);
	// // // continue;
	// // }
	// // }
	//
	// if (glEventListener.getID() == iViewID
	// || (GeneralManager.get().getViewGLCanvasManager().getGLEventListener(
	// iViewID) instanceof GLPathway && glEventListener instanceof GLPathway))
	// {
	// // ((ToolBar)item.getData()).setBackground(
	// // parentComposite.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
	//
	// item.setExpanded(true);
	//
	// // Highlight also remote rendering parent of the selected sub
	// // view
	// if (glEventListener.isRenderedRemote())
	// {
	// AGLEventListener glRemoteEventListener = (AGLEventListener)
	// glEventListener
	// .getRemoteRenderingGLCanvas();
	// for (ExpandItem remoteItem : expandBar.getItems())
	// {
	// if (remoteItem.getData("view") == glRemoteEventListener)
	// {
	// remoteItem.setExpanded(true);
	// }
	// }
	// }
	// }
	// }
	// }

//	private void addColorMappingBar() {
//		Group group = new Group(parentComposite, SWT.NULL);
//		GridLayout layout = new GridLayout(1, false);
//		layout.marginBottom =
//			layout.marginTop =
//				layout.marginLeft =
//					layout.marginRight = layout.horizontalSpacing = layout.verticalSpacing = 0;
//		layout.marginHeight = layout.marginWidth = 3;
//		group.setLayout(layout);
//
//		GridData gridData;
//		if (bHorizontal) {
//			gridData = new GridData(GridData.FILL_VERTICAL);
//			gridData.minimumWidth = 110;
//			gridData.widthHint = 110;
//		}
//		else {
//			gridData = new GridData(GridData.FILL_HORIZONTAL);
//		}
//		group.setLayoutData(gridData);
//
//		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
//		store.getInt("");
//		store = GeneralManager.get().getPreferenceStore();
//		int iNumberOfMarkerPoints = store.getInt(PreferenceConstants.NUMBER_OF_COLOR_MARKER_POINTS);
//
//		Color[] alColor = new Color[iNumberOfMarkerPoints];
//		int[] iArColorMarkerPoints = new int[iNumberOfMarkerPoints - 1];
//		for (int iCount = 1; iCount <= iNumberOfMarkerPoints; iCount++) {
//			int iColorMarkerPoint =
//				(int) (100 * store.getFloat(PreferenceConstants.COLOR_MARKER_POINT_VALUE + iCount));
//
//			// Gradient label does not need the 0 point
//			if (iColorMarkerPoint != 0) {
//				iArColorMarkerPoints[iCount - 2] = iColorMarkerPoint;
//			}
//
//			String color = store.getString(PreferenceConstants.COLOR_MARKER_POINT_COLOR + iCount);
//
//			int[] iArColor = new int[3];
//			if (color.isEmpty()) {
//				iArColor[0] = 0;
//				iArColor[1] = 0;
//				iArColor[2] = 0;
//			}
//			else {
//				StringTokenizer tokenizer = new StringTokenizer(color, ",", false);
//				int iInnerCount = 0;
//				while (tokenizer.hasMoreTokens()) {
//					try {
//						String token = tokenizer.nextToken();
//						iArColor[iInnerCount] = Integer.parseInt(token);
//						System.out.println();
//					}
//					catch (Exception e) {
//
//					}
//					iInnerCount++;
//				}
//			}
//			alColor[iCount - 1] =
//				new Color(PlatformUI.getWorkbench().getDisplay(), iArColor[0], iArColor[1], iArColor[2]);
//		}
//
//		CLabel colorMappingPreviewLabel = new CLabel(group, SWT.SHADOW_NONE);
//		// colorMappingPreviewLabel.setBounds(0, 0, 200, 40);
//		colorMappingPreviewLabel.setText("");
//		// colorMappingPreviewLabel.setBackground(alColor, new int[] { 20, 100
//		// });
//
//		colorMappingPreviewLabel.setBackground(alColor, iArColorMarkerPoints);
//		colorMappingPreviewLabel.update();
//		colorMappingPreviewLabel.setLayoutData(new GridData(150, 20));
//
//		Composite colorLabelComposite = new Composite(group, SWT.NULL);
//		colorLabelComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		colorLabelComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
//		int iCompositeWidth = 110;// group.getBounds().width;
//
//		for (int iCount = 0; iCount < iArColorMarkerPoints.length; iCount++) {
//			Integer iWidthValue;
//			int iDisplayValue;
//			// iAccumulatedValue += iValue;
//			Label valueLabel = new Label(colorLabelComposite, SWT.LEFT);// |
//			// SWT.BORDER);
//			// valueLabel.setText("test");
//
//			if (iCount == 0) {
//				iDisplayValue = 0;
//				iWidthValue = iArColorMarkerPoints[iCount];
//			}
//			else {
//				iDisplayValue = iArColorMarkerPoints[iCount - 1];
//				iWidthValue = iArColorMarkerPoints[iCount] - iDisplayValue;
//			}
//
//			valueLabel.setText(Integer.toString(iDisplayValue));
//			int iWidth = (int) ((float) iWidthValue / 100 * iCompositeWidth);
//			RowData rowData = new RowData(iWidth, 15);
//			valueLabel.setLayoutData(rowData);
//		}
//
//		Label valueLabel = new Label(colorLabelComposite, SWT.RIGHT);
//		valueLabel.setText("100");
//		RowData rowData = new RowData(22, 15);
//		valueLabel.setLayoutData(rowData);
//		colorLabelComposite.pack();
//		// CLabel colorMappingPreviewLabel = new CLabel(group, SWT.SHADOW_NONE);
//		// // colorMappingPreviewLabel.setBounds(0, 0, 200, 40);
//		// colorMappingPreviewLabel.setText("");
//		//		
//		// // TODO for Alex: Read real color mapping values
//		// Color[] alColorMarkerPoints = new Color[3];
//		// alColorMarkerPoints[0] = new Color(Display.getCurrent(), 255, 0, 0);
//		// alColorMarkerPoints[1] = new Color(Display.getCurrent(), 0, 0, 0);
//		// alColorMarkerPoints[2] = new Color(Display.getCurrent(), 0, 255, 0);
//		// colorMappingPreviewLabel.setBackground(alColorMarkerPoints, new int[]
//		// { 20, 100 });
//		// colorMappingPreviewLabel.setLayoutData(new
//		// GridData(GridData.FILL_HORIZONTAL));
//		//
//		colorMappingPreviewLabel.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseDoubleClick(MouseEvent e) {
//				PreferenceDialog pref =
//					PreferencesUtil.createPreferenceDialogOn(parentComposite.getShell(),
//						"org.caleydo.rcp.preferences.ColorMappingPreferencePage", null, null);
//
//				if (pref != null) {
//					pref.open();
//				}
//			}
//		});
//
//		Label spacer = new Label(group, SWT.NULL);
//		if (bHorizontal) {
//			spacer.setLayoutData(new GridData(GridData.FILL_BOTH));
//		}
//		else {
//			GridData data = new GridData(GridData.FILL_HORIZONTAL);
//			data.minimumHeight = 10;
//			data.heightHint = 10;
//			spacer.setLayoutData(data);
//		}
//
//		Label label = new Label(group, SWT.CENTER);
//		label.setText("Color Mapping");
//		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		label.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
//	}

	private void addGeneralToolBar() {
		Group group = new Group(parentComposite, SWT.NULL);
		GridLayout layout = new GridLayout(1, false);
		layout.marginBottom =
			layout.marginTop =
				layout.marginLeft =
					layout.marginRight = layout.horizontalSpacing = layout.verticalSpacing = 0;
		layout.marginHeight = layout.marginWidth = 3;
		group.setLayout(layout);
		group.setLayoutData(toolBarRenderer.createStandardGridData());

		final ToolBar toolBar = new ToolBar(group, SWT.WRAP | SWT.FLAT);
		ToolBarManager toolBarManager = new ToolBarManager(toolBar);
		toolBarManager.add(new LoadDataAction());
		toolBarManager.add(new ExportDataAction());

		toolBarRenderer.addTakeSnapshotAction(toolBarManager, group);

		toolBarManager.update(true);

		Label label = new Label(group, SWT.CENTER);
		label.setText("General");
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
	}

	@Override
	public void handleExternalEvent(final IUniqueObject eventTrigger, IEventContainer eventContainer,
		EMediatorType eMediatorType) {
		if (eventTrigger instanceof AGLEventListener) {
			final int iViewID = ((AGLEventListener) eventTrigger).getID();

			parentComposite.getDisplay().asyncExec(new Runnable() {
				public void run() {
					// Check if toolbar is present
					for (Group group : viewSpecificGroups) {
						for (Control subControl : group.getChildren()) {
							if (subControl instanceof Label) {
								if (group.getData("viewID") != null
									&& ((Integer) group.getData("viewID")).intValue() == iViewID
									|| eventTrigger instanceof GLPathway
									&& group.getData("viewType") == GLPathwayView.ID) {
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
			return (int) ToolBarView.TOOLBAR_WIDTH;

		return (int) ToolBarView.TOOLBAR_HEIGHT;
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
