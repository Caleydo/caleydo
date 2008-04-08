/**
 * 
 */
package org.caleydo.core.view.swt.progressbar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ProgressBar;

import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.view.AViewRep;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.ViewType;

/**
 * Simple slider view that can be set between 0 and 100 percent.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class ProgressBarViewRep 
extends AViewRep 
implements IView {
	
	protected int iProgressBarMinValue = 0;

	protected int iProgressBarMaxValue = 200;

	protected int iProgressBarCurrentValue = 0;

	protected ProgressBar refProgressBar;

	protected int iProgressBarStyle = SWT.HORIZONTAL;
	
	public ProgressBarViewRep(
			IGeneralManager refGeneralManager, 
			int iViewId, 
			int iParentContainerId, 
			String sLabel) {
		super(refGeneralManager, 
				iViewId, 
				iParentContainerId, 
				sLabel,
				ViewType.SWT_PROGRESSBAR);	
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
	 * @see org.caleydo.core.view.swt.progressbar.ProgressBarViewRep#retrieveNewGUIContainer()
	 * 
	 * @param setStyle
	 */
	public final void setProgressBarStyle(int setStyle) {
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
	public final int getProgressBarStyle() {
		
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

	/**
	 * 
	 * @see org.caleydo.core.view.IView#initView()
	 */
	protected void initViewSwtComposit(Composite swtContainer) {
		
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

	public void drawView() {
		
		refProgressBar.setSelection(this.iProgressBarCurrentValue);
	}

	public void setProgressBarPercentage(int iProgressBarCurrentValue) {
		
		this.iProgressBarCurrentValue = iProgressBarCurrentValue;
	}
	
	public void setAttributes(int iProgressBarCurrentValue) {
		
		this.iProgressBarCurrentValue = iProgressBarCurrentValue;		
	}
}
