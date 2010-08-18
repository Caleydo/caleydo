package org.caleydo.core.manager.vislink;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.caleydo.core.manager.GeneralManager;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SelectionRetriever
	implements Runnable {

	private VisLinkManager visLinkManager;

	private boolean stopped = false;

	@Override
	public void run() {
		while (!stopped) {
			try {
				Thread.sleep(200);
				if (!stopped) {
					String getSelection = "propagation?name=" + visLinkManager.getAppName();
					String selectionXML = visLinkManager.doVisdaemonRequest(getSelection);
					StringReader reader = new StringReader(selectionXML);
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					DocumentBuilder db = dbf.newDocumentBuilder();
					InputSource is = new InputSource(reader);
					Document doc = db.parse(is);
					Node idNode = doc.getElementsByTagName("id").item(0);
					if (idNode != null) {
						String selectionId = null;
						selectionId = idNode.getTextContent();
						VisLinkSelectionEvent vlse = new VisLinkSelectionEvent();
						vlse.setSelectionId(selectionId);
						GeneralManager.get().getEventPublisher().triggerEvent(vlse);
					}
				}
			}
			catch (InterruptedException e) {
				e.printStackTrace();
				stopped = true;
			}
			catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean isStopped() {
		return stopped;
	}

	public void setStopped(boolean stopped) {
		this.stopped = stopped;
	}

	public VisLinkManager getVisLinkManager() {
		return visLinkManager;
	}

	public void setVisLinkManager(VisLinkManager visLinkManager) {
		this.visLinkManager = visLinkManager;
	}

}
