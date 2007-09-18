package org.geneview.core.view.swt.slider;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Slider;

import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.event.mediator.IMediatorReceiver;
import org.geneview.core.manager.event.mediator.IMediatorSender;
import org.geneview.core.view.AViewRep;
import org.geneview.core.view.ViewType;

/**
 * The view representation of a slider.
 * The slider value is taken from the first 
 * selection and the first storage in the specified Set.
 * The Set is represented by the local variable setId.
 * 
 * @see org.geneview.core.view.IView
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public abstract class ASliderViewRep 
extends AViewRep 
implements IMediatorSender, IMediatorReceiver {
	
	protected Slider refSlider;
	
	protected int iCurrentSliderValue = 50; //default value (middle position)
	
	public ASliderViewRep(IGeneralManager refGeneralManager, 
			int iViewId, int iParentContainerId, String sLabel) {
		
		super(refGeneralManager, 
				iViewId, 
				iParentContainerId, 
				sLabel,
				ViewType.SWT_SLIDER);
	}
	
	/**
	 * 
	 * @see org.geneview.core.view.AViewRep#retrieveGUIContainer()
	 * @see org.geneview.core.view.IView#initView()
	 */
	protected void initViewSwtComposit(Composite swtContainer) {
		
	    refSlider = new Slider(refSWTContainer, SWT.HORIZONTAL);
	    //slider.setBounds(115, 50, 25, 15);
	    refSlider.setSize(iWidth, iHeight);
	    refSlider.setMinimum(0);
	    refSlider.setMaximum(100);
	    //refSlider.setIncrement(1);
	}

	public void drawView() {
		
	   // Check and reset minium and maximum of slider
	   if (iCurrentSliderValue > refSlider.getMaximum())
	   {
		   refSlider.setMaximum(iCurrentSliderValue);
	   }
	   else if (iCurrentSliderValue < refSlider.getMinimum())
	   {
		   refSlider.setMinimum(iCurrentSliderValue);
	   }
		
	   // Set current slider value
	   refSlider.setSelection(iCurrentSliderValue);
	}
}
