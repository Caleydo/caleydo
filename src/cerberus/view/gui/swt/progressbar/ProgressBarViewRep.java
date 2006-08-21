/**
 * 
 */
package cerberus.view.gui.swt.progressbar;

import java.util.StringTokenizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ProgressBar;

import cerberus.manager.IGeneralManager;
import cerberus.manager.command.factory.CommandFactory;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.system.StringConversionTool;
import cerberus.view.gui.AViewRep;
import cerberus.view.gui.IView;
import cerberus.view.gui.swt.widget.SWTNativeWidget;

/**
 * Simple slider view that can be set between 0 and 100 percent.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class ProgressBarViewRep extends AViewRep implements IView
{
	protected int iProgressBarMinValue = 0;

	protected int iProgressBarMaxValue = 100;

	protected int iProgressBarCurrentValue = 0;

	protected Composite refSWTContainer;

	protected ProgressBar refProgressBar;

	protected int iProgressBarStyle = SWT.HORIZONTAL;

	protected int iHeight = 0;

	protected int iWidth = 0;

	
	public ProgressBarViewRep(
			IGeneralManager refGeneralManager, 
			int iViewId, 
			int iParentContainerId, 
			String sLabel)
	{
		super(refGeneralManager, iViewId, iParentContainerId, sLabel);	
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
	public final void setProgressBarStyle(int setStyle)
	{
		if (this.refProgressBar != null)
		{
			System.out
					.println("WARNING: setProgressBarStyle() has no influence, becaus ProgressBar is already created!");
		}

		if (setStyle == SWT.HORIZONTAL)
		{
			iProgressBarStyle = SWT.HORIZONTAL;
			return;
		} else if (setStyle == SWT.VERTICAL)
		{
			iProgressBarStyle = SWT.VERTICAL;
			return;
		}

		/**
		 * default..
		 */
		System.out
				.println("WARNING: set progressbar in unsupportet style; use Horizontal instead!");

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
	public final int getProgressBarStyle()
	{
		if (iProgressBarStyle == SWT.HORIZONTAL)
		{
			return SWT.HORIZONTAL;
		} else if (iProgressBarStyle == SWT.VERTICAL)
		{
			return SWT.VERTICAL;
		}

		//TODO: optimize! just return value!

		return iProgressBarStyle;
	}

	public void initView()
	{
		if (this.refProgressBar != null)
		{
			System.err.println("call initView() more than once!");
			return;
		}

		refProgressBar = new ProgressBar(refSWTContainer, iProgressBarStyle);

		refProgressBar.setMinimum(this.iProgressBarMinValue);
		refProgressBar.setMaximum(this.iProgressBarMaxValue);
		refProgressBar.setSelection(this.iProgressBarCurrentValue);
		refProgressBar.setSize(iWidth, iHeight);
	}

	public void drawView()
	{
		refProgressBar.setSelection(this.iProgressBarCurrentValue);
	}

	public void retrieveNewGUIContainer()
	{
		StringTokenizer token = new StringTokenizer(vecAttributes.get(0),
				CommandFactory.sDelimiter_CreateView_Size);

		iWidth = (StringConversionTool
				.convertStringToInt(token.nextToken(), -1));
		iHeight = (StringConversionTool.convertStringToInt(token.nextToken(),
				-1));

		SWTNativeWidget refSWTNativeWidget = (SWTNativeWidget) refGeneralManager
				.getSingelton().getSWTGUIManager().createWidget(
						ManagerObjectType.GUI_SWT_NATIVE_WIDGET,
						iParentContainerId, iWidth, iHeight);

		refSWTContainer = refSWTNativeWidget.getSWTWidget();
	}

	public void retrieveExistingGUIContainer()
	{
		// TODO Auto-generated method stub

	}


	public void setProgressBarPercentage(int iProgressPercentage)
	{
		this.iProgressBarCurrentValue = iProgressPercentage;

	}

}
