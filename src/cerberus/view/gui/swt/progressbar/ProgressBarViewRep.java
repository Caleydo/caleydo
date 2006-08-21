/**
 * 
 */
package cerberus.view.gui.swt.progressbar;

import java.util.StringTokenizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ProgressBar;

import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewManager;
import cerberus.manager.command.factory.CommandFactory;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.system.StringConversionTool;
import cerberus.view.gui.AViewManagedRep;
import cerberus.view.gui.IView;
import cerberus.view.gui.swt.widget.SWTNativeWidget;

import cerberus.view.gui.swt.data.DataTableViewRep;

/**
 * @author kalkusch
 *
 */
public class ProgressBarViewRep extends AViewManagedRep implements IView
{

	protected int iSWT_widht = 200;
	protected int iSWT_height = 50;
	
	protected int iProgressBar_minValue = 0;
	
	protected int iProgressBar_maxValue = 100;
	
	protected int iProgressBar_currentValue = 0;
	
	protected Composite refSWTContainer;
	
	protected ProgressBar refProgressBar;
	
	protected int iPrograssBarStyle = SWT.HORIZONTAL;
	
//	protected DataTableViewRep refDataTableViewRep;
	
	/**
	 * @param iSetCollectionId
	 * @param setGeneralManager
	 */
	public ProgressBarViewRep(int iSetCollectionId,
			IGeneralManager setGeneralManager)
	{
		super( setGeneralManager, iSetCollectionId, -1 );
		
//		IViewManager viewManager = 
//			(IViewManager) refGeneralManager.getManagerByBaseType(ManagerObjectType.VIEW);
//		//refSetTableViewRep = viewManager.createView(ManagerObjectType.VIEW_SWT_SET_TABLE);
//		refDataTableViewRep = (DataTableViewRep) 
//			viewManager.createView(ManagerObjectType.VIEW_SWT_PROGRESS_BAR);
	}

	/**
	 * Define style of progressbar. 
	 * Must be called before retrieveNewGUIContainer()
	 * 
	 * Default is SWT.HORIZONTAL
	 * Valid values are SWT.HORIZONTAL or SWT.VERTICAL
	 * 
	 * @see org.eclipse.swt.SWT
	 * @see org.eclipse.swt.widgets.ProgressBar
	 * @see cerberus.view.gui.swt.progressbar.ProgressBarViewRep#retrieveNewGUIContainer()
	 * 
	 * @param setStyle
	 */
	public final void setProgressBarStyle( int setStyle ) {
		if ( this.refProgressBar != null ) {
			System.out.println("WARNING: setProgressBarStyle() has no influence, becaus ProgressBar is already created!");
		}
		
		if ( setStyle == SWT.HORIZONTAL ) {
			iPrograssBarStyle = SWT.HORIZONTAL;
			return;
		}
		else if ( setStyle == SWT.VERTICAL ) {
			iPrograssBarStyle = SWT.VERTICAL;
			return;
		}
		
		/**
		 * default..
		 */
		System.out.println(
				"WARNING: set progressbar in unsupportet style; use Horizontal instead!");
		
		iPrograssBarStyle = SWT.HORIZONTAL;		
	}
	
	/**
	 * Get current ProgressBar style.
	 * 
	 * @see org.eclipse.swt.SWT
	 * @see org.eclipse.swt.widgets.ProgressBar	 
	 * 
	 * @return SWT.HORIZONTAL or SWT.VERTICAL
	 */
	public final int getProgressBarStyle() {
		if ( iPrograssBarStyle == SWT.HORIZONTAL )
		{
			return SWT.HORIZONTAL;			
		}
		else if ( iPrograssBarStyle == SWT.VERTICAL ) 
		{
			return SWT.VERTICAL;
		}
		
		//TODO: optimize! just return value!
		
		return iPrograssBarStyle;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.view.gui.IView#initView()
	 */
	public void initView()
	{
		if ( this.refProgressBar != null ) {
			System.err.println("call initView() more than once!");
			return;
		}
		
		refProgressBar = 
			new ProgressBar( refSWTContainer, iPrograssBarStyle );	
		
		refProgressBar.setMinimum( this.iProgressBar_minValue );
		refProgressBar.setMaximum( this.iProgressBar_maxValue );
		refProgressBar.setSelection( this.iProgressBar_currentValue );
		
		refProgressBar.setVisible( true );
		
		refSWTContainer.redraw();

	}

	/* (non-Javadoc)
	 * @see cerberus.view.gui.IView#drawView()
	 */
	public void drawView()
	{
		
		refProgressBar.setSelection( this.iProgressBar_currentValue );

		Button testButton = new Button(refSWTContainer,0);
		testButton.setText("TEST");
		testButton.setVisible( true );
		
		refSWTContainer.redraw();
		
//		 refDataTableViewRep.setExternalGUIContainer(refSWTContainer);
//			refDataTableViewRep.initTable();
	}

	/* (non-Javadoc)
	 * @see cerberus.view.gui.IView#retrieveNewGUIContainer()
	 */
	public void retrieveNewGUIContainer()
	{
		
		SWTNativeWidget refSWTNativeWidget = (SWTNativeWidget) refGeneralManager
				.getSingelton().getSWTGUIManager().createWidget(
						ManagerObjectType.GUI_SWT_NATIVE_WIDGET,
						iParentContainerId, iSWT_widht , iSWT_height);

		refSWTContainer = refSWTNativeWidget.getSWTWidget();				
		
	}

	/* (non-Javadoc)
	 * @see cerberus.view.gui.IView#retrieveExistingGUIContainer()
	 */
	public void retrieveExistingGUIContainer()
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see cerberus.data.IUniqueManagedObject#getBaseType()
	 */
	public ManagerObjectType getBaseType()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
