package cerberus.view.gui.swt.slider;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Slider;

import cerberus.data.collection.ISelection;
import cerberus.manager.IGeneralManager;
import cerberus.manager.event.mediator.IMediatorReceiver;
import cerberus.manager.event.mediator.IMediatorSender;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.AViewRep;
import cerberus.view.gui.IView;
import cerberus.view.gui.swt.widget.SWTNativeWidget;

public class SliderViewRep 
extends AViewRep 
implements IView, IMediatorSender, IMediatorReceiver
{
	protected Composite refSWTContainer;
	
	protected Slider refSlider;
	
	protected int iCurrentSliderValue;
	
	public SliderViewRep(IGeneralManager refGeneralManager, 
			int iViewId, int iParentContainerId, String sLabel)
	{
		super(refGeneralManager, iViewId, iParentContainerId, sLabel);
	}
	
	public void initView()
	{
	    refSlider = new Slider(refSWTContainer, SWT.HORIZONTAL);
	    //slider.setBounds(115, 50, 25, 15);
	    refSlider.setSize(iWidth, iHeight);
	    refSlider.setMinimum(0);
	    refSlider.setMaximum(100);
	    refSlider.setIncrement(1);
	}

	public void drawView()
	{
	   refSlider.setSelection(iCurrentSliderValue);
	}

	public void retrieveGUIContainer()
	{
		SWTNativeWidget refSWTNativeWidget = (SWTNativeWidget) refGeneralManager
		.getSingelton().getSWTGUIManager().createWidget(
				ManagerObjectType.GUI_SWT_NATIVE_WIDGET,
				iParentContainerId, iWidth, iHeight);

		refSWTContainer = refSWTNativeWidget.getSWTWidget();
	}

	/**
	 * @deprecated
	 */
	public void extractAttributes()
	{
		super.extractAttributes();	
	}
	
	public void update(Object eventTrigger)
	{
		if (eventTrigger instanceof ISelection)
		{
			iCurrentSliderValue = ((ISelection)eventTrigger).getOffset();
			drawView();
		}
		
	}
}
