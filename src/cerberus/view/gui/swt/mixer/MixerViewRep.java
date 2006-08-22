package cerberus.view.gui.swt.mixer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Slider;

import cerberus.manager.IGeneralManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.AViewRep;
import cerberus.view.gui.IView;
import cerberus.view.gui.swt.widget.SWTNativeWidget;

public class MixerViewRep extends AViewRep implements IView
{
	protected Composite refSWTContainer;
	
	protected Slider refSlider;
	
	public MixerViewRep(IGeneralManager refGeneralManager, 
			int iViewId, int iParentContainerId, String sLabel)
	{
		super(refGeneralManager, iViewId, iParentContainerId, sLabel);
	}
	
	public void initView()
	{
	    refSlider = new Slider(refSWTContainer, SWT.HORIZONTAL);
	    refSlider.setSize(iWidth, iHeight);
	    refSlider.setMinimum(0);
	    refSlider.setMaximum(100);
	    refSlider.setIncrement(1);
	}

	public void drawView()
	{
	   // TODO: set slider to current position
	}

	public void retrieveGUIContainer()
	{
		SWTNativeWidget refSWTNativeWidget = (SWTNativeWidget) refGeneralManager
		.getSingelton().getSWTGUIManager().createWidget(
				ManagerObjectType.GUI_SWT_NATIVE_WIDGET,
				iParentContainerId, iWidth, iHeight);

		refSWTContainer = refSWTNativeWidget.getSWTWidget();

	}
}
