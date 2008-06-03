package org.caleydo.core.view.swt.slider;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Slider;

import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.event.mediator.IMediatorReceiver;
import org.caleydo.core.manager.event.mediator.IMediatorSender;
import org.caleydo.core.view.AView;
import org.caleydo.core.view.ViewType;

/**
 * The view representation of a slider.
 * The slider value is taken from the first 
 * selection and the first storage in the specified Set.
 * The Set is represented by the local variable setId.
 * 
 * @see org.caleydo.core.view.IView
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public abstract class ASliderViewRep 
extends AView 
implements IMediatorSender, IMediatorReceiver {
	
	protected Slider slider;
	
	protected int iCurrentSliderValue = 50; //default value (middle position)
	
	public ASliderViewRep(IGeneralManager generalManager, 
			int iViewId, int iParentContainerId, String sLabel) {
		
		super(generalManager, 
				iViewId, 
				iParentContainerId, 
				sLabel,
				ViewType.SWT_SLIDER);
	}
	
	/**
	 * 
	 * @see org.caleydo.core.view.AView#retrieveGUIContainer()
	 * @see org.caleydo.core.view.IView#initView()
	 */
	protected void initViewSwtComposit(Composite swtContainer) {
		
	    slider = new Slider(swtContainer, SWT.HORIZONTAL);
	    //slider.setBounds(115, 50, 25, 15);
	    slider.setSize(iWidth, iHeight);
	    slider.setMinimum(0);
	    slider.setMaximum(100);
	    //slider.setIncrement(1);
	}

	public void drawView() {
		
	   // Check and reset minium and maximum of slider
	   if (iCurrentSliderValue > slider.getMaximum())
	   {
		   slider.setMaximum(iCurrentSliderValue);
	   }
	   else if (iCurrentSliderValue < slider.getMinimum())
	   {
		   slider.setMinimum(iCurrentSliderValue);
	   }
		
	   // Set current slider value
	   slider.setSelection(iCurrentSliderValue);
	}
}
