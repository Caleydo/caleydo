package org.caleydo.core.view.swt.mixer;

import java.util.ArrayList;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.view.serialize.ASerializedView;
import org.caleydo.core.view.serialize.SerializedDummyView;
import org.caleydo.core.view.swt.ASWTView;
import org.caleydo.core.view.swt.ISWTView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Slider;

/**
 * Class implements a slider mixer representation. The slider stack can contain arbitrary slider elements that
 * are accessible by a index. Class is prepared to work together with a connected MIDI device.
 * 
 * @author Marc Streit
 * @author Michael Kalkusch
 */
public class MixerViewRep
	extends ASWTView
	implements ISWTView {
	protected ArrayList<Slider> sliderList;

	protected int iNumberOfSliders = 0;

	/**
	 * Constructor.
	 */
	public MixerViewRep(int iParentContainerId, String sLabel) {
		super(iParentContainerId, sLabel, GeneralManager.get().getIDManager().createID(
			EManagedObjectType.VIEW_SWT_MIXER));
	}

	/**
	 * In the init method the sliders are created. The sliders are added to the slider list. Minimum slider
	 * value = 0. Maximum slider value = 100. We use a fill layout to fill the available space optimally.
	 * 
	 * @see org.caleydo.core.view.IView#initView()
	 */
	@Override
	public void initViewSWTComposite(Composite parentComposite) {

		sliderList = new ArrayList<Slider>();
		parentComposite.setLayout(new FillLayout(SWT.HORIZONTAL));

		Slider tmpSlider = null;
		for (int iSliderIndex = 0; iSliderIndex < iNumberOfSliders; iSliderIndex++) {
			tmpSlider = new Slider(parentComposite, SWT.VERTICAL);
			tmpSlider.setMinimum(0);
			tmpSlider.setMaximum(100);
			tmpSlider.setSelection(50);
			// tmpSlider.setSize((int)((float)iWidth/iNumberOfSliders),
			// iHeight);
			sliderList.add(tmpSlider);
		}
	}

	@Override
	public void drawView() {

	}

	/**
	 * Method sets the slider to the value of iSliderValue by a given slider index in the matrix slider stack.
	 * If the value is smaller than the minimum value or bigger than the max value the value is clipped to min
	 * or max respectively.
	 * 
	 * @param iSliderIndex
	 * @param iSliderValue
	 */
	public void setSliderValueByIndex(int iSliderIndex, int iSliderValue) {
		sliderList.get(iSliderIndex).setSelection(iSliderValue);
	}

	/**
	 * Method returns a slider value by a given slider index in the matrix slider stack.
	 * 
	 * @param iSliderIndex
	 * @return
	 */
	public int getSliderValueByIndex(int iSliderIndex) {
		return sliderList.get(iSliderIndex).getSelection();
	}

	/**
	 * Method return the number of sliders in the mixer view.
	 * 
	 * @return Number of sliders in the mixer.
	 */
	public int getSliderDimension() {

		return iNumberOfSliders;
	}

	public void setAttributes(int iNumberOfSliders) {
		this.iNumberOfSliders = iNumberOfSliders;
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedDummyView serializedForm = new SerializedDummyView();
		serializedForm.setViewID(this.getID());
		return serializedForm; 
	}

}
