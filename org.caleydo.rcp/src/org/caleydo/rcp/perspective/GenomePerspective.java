package org.caleydo.rcp.perspective;


import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.PartEventAction;

public class GenomePerspective
	implements IPerspectiveFactory
{
	private IPageLayout layout;
	
	public void createInitialLayout(IPageLayout layout)
	{
		this.layout = layout;
		
		layout.setEditorAreaVisible(false);
		layout.getViewLayout("org.caleydo.rcp.views.HTMLBrowserView").setCloseable(false);
		layout.getViewLayout("org.caleydo.rcp.views.ToolBarView").setCloseable(false);
		layout.getViewLayout("org.caleydo.rcp.views.ToolBarView").setMoveable(true);
		
//		layout.setFixed(true);
		
		layout.addStandaloneView("org.caleydo.rcp.views.ToolBarView", true, IPageLayout.LEFT, 0.125f, IPageLayout.ID_EDITOR_AREA);
//		IFolderLayout folderLayoutLeft = layout.createFolder("folderLayout", IPageLayout.LEFT, 0.125f, IPageLayout.ID_EDITOR_AREA);
//		folderLayoutLeft.a(ToolBarView.ID);
		
		layout.createFolder("folderLayoutRight", IPageLayout.RIGHT, 0.875f, IPageLayout.ID_EDITOR_AREA);
//		folderLayout2.addView(ToolBarView.ID);
		
//		PlatformUI.getWorkbench().getActiveWorkbenchWindow().addPerspectiveListener(new 
//				PerspectiveListener());
		
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().addPartListener(
				new PartListener());

//		PlatformUI.getWorkbench().getActiveWorkbenchWindow().addPageListener(new IPageListener() {
//
//			@Override
//			public void pageActivated(IWorkbenchPage page)
//			{
//				System.out.println("page activated");
//				
//			}
//
//			@Override
//			public void pageClosed(IWorkbenchPage page)
//			{
//				System.out.println("page closed");
//				
//			}
//
//			@Override
//			public void pageOpened(IWorkbenchPage page)
//			{
//				System.out.println("page opended");
//				
//			}
//			
//		});
		
//		PlatformUI.getWorkbench().addWindowListener(new IWindowListener() {
//			@Override
//			public void windowActivated(IWorkbenchWindow window)
//			{
//				System.out.println(window.getActivePage());
//				System.out.println(window.getShell());
//			}
//
//			@Override
//			public void windowClosed(IWorkbenchWindow window)
//			{
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void windowDeactivated(IWorkbenchWindow window)
//			{
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void windowOpened(IWorkbenchWindow window)
//			{
//				System.out.println("hui");
//				
//			}
//		});
	}
	
    /**
     * @return Returns the layout.
     */
    public IPageLayout getLayout() {
        return layout;
    }
}
