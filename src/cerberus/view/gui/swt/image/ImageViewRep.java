/**
 * 
 */
package cerberus.view.gui.swt.image;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import cerberus.manager.IGeneralManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.AViewRep;
import cerberus.view.gui.IView;
import cerberus.view.gui.swt.widget.SWTNativeWidget;

/**
 * Image view.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class ImageViewRep 
extends AViewRep 
implements IView {
	
	protected Composite refSWTContainer;
	
	protected String sImagePath;
	
	public ImageViewRep(
			IGeneralManager refGeneralManager, 
			int iViewId, 
			int iParentContainerId, 
			String sLabel) {
		
		super(refGeneralManager, iViewId, iParentContainerId, sLabel);	
	}

	public void initView() {
		
		Image image = new Image(refSWTContainer.getDisplay(), sImagePath);

		Label label = new Label(refSWTContainer, SWT.BORDER);
		label.setSize(iWidth, iHeight);
	    label.setImage(image);
	}

	public void drawView() {
		
//		refGeneralManager.getSingelton().logMsg(
//				this.getClass().getSimpleName() + 
//				": drawView(): Load "+sUrl, 
//				LoggerType.VERBOSE );		
	}

	public void retrieveGUIContainer() {
		
		SWTNativeWidget refSWTNativeWidget = (SWTNativeWidget) refGeneralManager
				.getSingelton().getSWTGUIManager().createWidget(
						ManagerObjectType.GUI_SWT_NATIVE_WIDGET,
						iParentContainerId, iWidth, iHeight);

		refSWTContainer = refSWTNativeWidget.getSWTWidget();
	}

	public void extractAttributes() {
		
		sImagePath = 
			refParameterHandler.getValueString( "sImagePath" );
	}
}
