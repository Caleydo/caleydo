package cerberus.view.gui.swt.slider;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Slider;

import cerberus.manager.IGeneralManager;
import cerberus.manager.event.mediator.IMediatorReceiver;
import cerberus.manager.event.mediator.IMediatorSender;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.AViewRep;
import cerberus.view.gui.IView;
import cerberus.view.gui.ViewType;
import cerberus.view.gui.swt.widget.SWTNativeWidget;

/**
 * The view representation of a slider.
 * The slider value is taken from the first 
 * selection and the first storage in the specified Set.
 * The Set is represented by the local variable setId.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public abstract class ASliderViewRep 
extends AViewRep 
implements IView, IMediatorSender, IMediatorReceiver {
	
	protected Composite refSWTContainer;
	
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
	
	public void initView() {
		
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

	public void retrieveGUIContainer() {
		
		SWTNativeWidget refSWTNativeWidget = (SWTNativeWidget) refGeneralManager
		.getSingelton().getSWTGUIManager().createWidget(
				ManagerObjectType.GUI_SWT_NATIVE_WIDGET,
				iParentContainerId, iWidth, iHeight);

		refSWTContainer = refSWTNativeWidget.getSWTWidget();
	}
}
