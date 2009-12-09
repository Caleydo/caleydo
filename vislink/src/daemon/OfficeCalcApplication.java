package daemon;

import java.util.Date;

import com.sun.star.awt.Point;
import com.sun.star.awt.Rectangle;
import com.sun.star.awt.Size;
import com.sun.star.awt.XUnitConversion;
import com.sun.star.awt.XWindow;
import com.sun.star.awt.XWindow2;
import com.sun.star.beans.PropertyValue;
import com.sun.star.beans.XPropertySet;
import com.sun.star.bridge.XUnoUrlResolver;
import com.sun.star.comp.helper.Bootstrap;
import com.sun.star.frame.FrameSearchFlag;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.frame.XModel;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.table.XCell;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.sun.star.uno.XInterface;

public class OfficeCalcApplication extends Application {
	
	private String docUrl = "file:///home/werner/dev/test/hw/button.ods";

	XModel xDocModel;

	public void init() {
		try {
			XComponentContext xLocalContext = Bootstrap.createInitialComponentContext(null);
		 
			XMultiComponentFactory xLocalServiceManager = xLocalContext.getServiceManager();
			Object urlResolver = xLocalServiceManager.createInstanceWithContext(
					"com.sun.star.bridge.UnoUrlResolver", xLocalContext);
		 
			XUnoUrlResolver xUrlResolver = (XUnoUrlResolver) UnoRuntime.queryInterface(XUnoUrlResolver.class, urlResolver);
			Object rInitialObject = xUrlResolver.resolve("uno:socket,host=localhost,port=2002;urp;StarOffice.ServiceManager");
		 
			XMultiServiceFactory msf = (XMultiServiceFactory) UnoRuntime.queryInterface(XMultiServiceFactory.class, rInitialObject);
			XInterface desktop = (XInterface) msf.createInstance("com.sun.star.frame.Desktop");
		  
			XComponentLoader cl = (XComponentLoader) UnoRuntime.queryInterface(XComponentLoader.class, desktop);
			XComponent component = cl.loadComponentFromURL(
					docUrl, // "private:factory/scalc", 
					"_default", 
					FrameSearchFlag.GLOBAL,
					new PropertyValue[] { } );
		  
			if (null == component) {
				throw new RuntimeException("could not establish office-calc connection");
			}
		
			xDocModel =  (XModel) UnoRuntime.queryInterface(XModel.class, component);
			
			BoundingBox bb = calcWindowBoundingBox();
			
			this.setDate(new Date());
			this.setName("calc"); // -" + this.getDate().getTime());
			this.getWindows().add(bb);
		} catch (Exception e) {
			throw (new RuntimeException(e));
		}
	}
	
	private BoundingBox calcWindowBoundingBox() {
		XWindow win = xDocModel.getCurrentController().getFrame().getContainerWindow();
		Rectangle r = win.getPosSize();
		BoundingBox bb = new BoundingBox(r.X, r.Y, r.Width, r.Height);
		return bb;
	}

	// XCell cell = (XCell) UnoRuntime.queryInterface(XCell.class, xDocModel.getCurrentSelection());

	private Rectangle calcCellBoundingBox(XCell cell, Point offset) {
		XUnitConversion conversion = (XUnitConversion) UnoRuntime.queryInterface(XUnitConversion.class, 
				xDocModel.getCurrentController().getFrame().getContainerWindow());

		XPropertySet cellProps = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, cell);
		Rectangle bb = new Rectangle();
		try {
			Size cellSize = (Size) cellProps.getPropertyValue("Size");
			Point cellPosition = (Point) cellProps.getPropertyValue("Position");
			Size bbSize = conversion.convertSizeToPixel(cellSize, (short) 0);
			Point bbPos = conversion.convertPointToPixel(cellPosition, (short) 0);
			bb.X = bbPos.X;
			bb.Y = bbPos.Y;
			bb.Height = bbSize.Height;
			bb.Width = bbSize.Width;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		bb.X = (int) (((double) bb.X) / 1.041);
		bb.Width = (int) (((double) bb.Width) / 1.041);
 
		bb.X = bb.X + offset.X;
		bb.Y = bb.Y + offset.Y;
		
		return bb;
	}
	
	private Point calcOffset() {
		XWindow win, comp;
		XWindow2 win2;
		int x, y;

		win = xDocModel.getCurrentController().getFrame().getContainerWindow();
		win2 = (XWindow2) UnoRuntime.queryInterface(XWindow2.class, win);
		comp = xDocModel.getCurrentController().getFrame().getComponentWindow();

		x = win.getPosSize().X + 40;
		y = win.getPosSize().Y + win.getPosSize().Height - win2.getOutputSize().Height + comp.getPosSize().Y + 60;

		Point offset = new Point();
		offset.X = x;
		offset.Y = y;

		return offset;
	}

	public String getDocUrl() {
		return docUrl;
	}

	public void setDocUrl(String docUrl) {
		this.docUrl = docUrl;
	}
	
}
