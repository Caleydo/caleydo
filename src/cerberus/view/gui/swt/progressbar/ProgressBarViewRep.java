/**
 * 
 */
package cerberus.view.gui.swt.progressbar;

import java.util.StringTokenizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

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
	protected int iProgressBarMinValue = 0;
	
	protected int iProgressBarMaxValue = 100;
	
	protected int iProgressBarCurrentValue = 0;
	
	protected Composite refSWTContainer;
	
	protected ProgressBar refProgressBar;
	
	protected int iProgressBarStyle = SWT.HORIZONTAL;
	protected int iHeight = 0;
	protected int iWidth = 0;
	
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
			iProgressBarStyle = SWT.HORIZONTAL;
			return;
		}
		else if ( setStyle == SWT.VERTICAL ) {
			iProgressBarStyle = SWT.VERTICAL;
			return;
		}
		
		/**
		 * default..
		 */
		System.out.println(
				"WARNING: set progressbar in unsupportet style; use Horizontal instead!");
		
		iProgressBarStyle = SWT.HORIZONTAL;		
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
		if ( iProgressBarStyle == SWT.HORIZONTAL )
		{
			return SWT.HORIZONTAL;			
		}
		else if ( iProgressBarStyle == SWT.VERTICAL ) 
		{
			return SWT.VERTICAL;
		}
		
		//TODO: optimize! just return value!
		
		return iProgressBarStyle;
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
			new ProgressBar( refSWTContainer, iProgressBarStyle );	
		
		refProgressBar.setMinimum( this.iProgressBarMinValue );
		refProgressBar.setMaximum( this.iProgressBarMaxValue );
		refProgressBar.setSelection( this.iProgressBarCurrentValue );
		refProgressBar.setSize(iWidth, iHeight);
	}

	/* (non-Javadoc)
	 * @see cerberus.view.gui.IView#drawView()
	 */
	public void drawView()
	{
		refProgressBar.setSelection( this.iProgressBarCurrentValue );
	}

	/* (non-Javadoc)
	 * @see cerberus.view.gui.IView#retrieveNewGUIContainer()
	 */
	public void retrieveNewGUIContainer()
	{
		StringTokenizer token = new StringTokenizer(vecAttributes.get(0),
				CommandFactory.sDelimiter_CreateView_Size );

		iWidth = (StringConversionTool.convertStringToInt( token.nextToken(), -1 ) );
		iHeight = (StringConversionTool.convertStringToInt( token.nextToken(), -1 ) );
		
		SWTNativeWidget refSWTNativeWidget = (SWTNativeWidget) refGeneralManager
				.getSingelton().getSWTGUIManager().createWidget(
						ManagerObjectType.GUI_SWT_NATIVE_WIDGET,
						iParentContainerId, iWidth , iHeight);

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

	public void setProgressBarPercentage(int iProgressPercentage)
	{
		this.iProgressBarCurrentValue = iProgressPercentage;
		
	}

}
