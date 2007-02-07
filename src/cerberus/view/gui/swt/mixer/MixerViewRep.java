package cerberus.view.gui.swt.mixer;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Slider;

import cerberus.manager.IGeneralManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.view.gui.AViewRep;
import cerberus.view.gui.IView;
import cerberus.view.gui.swt.widget.SWTNativeWidget;

/**
 * Class implements a slider mixer representation.
 * The slider stack can contain arbitrary slider elements
 * that are accessible by a index.
 * Class is prepared to work together with a connected MIDI device.
 * 
 * @author Marc Streit
 *
 */
public class MixerViewRep 
extends AViewRep 
implements IView
{
	protected Composite refSWTContainer;
	
	protected ArrayList<Slider> refSliderList;
	
	protected int iNumberOfSliders = 0;
	
	public MixerViewRep(IGeneralManager refGeneralManager, 
			int iViewId, int iParentContainerId, String sLabel)
	{
		super(refGeneralManager, iViewId, iParentContainerId, sLabel);
	}
	
	/**
	 * In the init method the sliders are created.
	 * The sliders are added to the slider list.
	 * Minimum slider value = 0.
	 * Maximum slider value = 100.
	 * We use a fill layout to fill the available space optimally.
	 */
	public void initView()
	{
		refSliderList = new ArrayList<Slider>();
		refSWTContainer.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Slider tmpSlider = null;
		for (int iSliderIndex = 0; iSliderIndex < iNumberOfSliders; iSliderIndex++)
		{
			tmpSlider = new Slider(refSWTContainer, SWT.VERTICAL);
			tmpSlider.setMinimum(0);
			tmpSlider.setMaximum(100);
			tmpSlider.setSelection(50);
			//tmpSlider.setSize((int)((float)iWidth/iNumberOfSliders), iHeight);
			refSliderList.add(tmpSlider);
		}
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

	/**
	 * Method sets the slider to the value of iSliderValue by
	 * a given slider index in the matrix slider stack.
	 * If the value is smaller than the minimum value or bigger than
	 * the max value the value is clipped to min or max respectively.
	 * 
	 * @param iSliderIndex
	 * @param iSliderValue
	 */
	public void setSliderValueByIndex(int iSliderIndex, int iSliderValue) {
		
		try {
			refSliderList.get(iSliderIndex).setSelection(iSliderValue);

		}catch(Exception e) {
			throw new CerberusRuntimeException(
					"Mixer Slider with index " +iSliderIndex +" does not exist!");
		}
	}
	
	/**
	 * Method returns a slider value by a given
	 * slider index in the matrix slider stack.
	 * 
	 * @param iSliderIndex
	 * @return
	 */
	public int getSliderValueByIndex(int iSliderIndex) {

		try {
			return refSliderList.get(iSliderIndex).getSelection();

		}catch(Exception e) {
			throw new CerberusRuntimeException(
					"Mixer Slider with index " +iSliderIndex +" does not exist!");
		}	
	}
	
	/**
	 * Method return the number of sliders in the mixer view.

	 * @return Number of sliders in the mixer.
	 */
	public int getSliderDimension() {
		
		return iNumberOfSliders;
	}
	
	public void setAttributes(int iWidth, int iHeight, int iNumberOfSliders) {
		
		super.setAttributes(iWidth, iHeight);
		
		this.iNumberOfSliders = iNumberOfSliders;
	}
}
