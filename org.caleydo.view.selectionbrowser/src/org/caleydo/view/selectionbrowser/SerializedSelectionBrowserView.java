package org.caleydo.view.selectionbrowser;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedTopLevelDataView;

/**
 * Serialized of the selection browser view.
 * 
 * @author Alexander Lex
 */
@XmlRootElement
@XmlType
public class SerializedSelectionBrowserView extends ASerializedTopLevelDataView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedSelectionBrowserView() {
	}

	public SerializedSelectionBrowserView(String dataDomainType) {
		super(dataDomainType);
	}

	@Override
	public String getViewType() {
		return RcpSelectionBrowserView.VIEW_TYPE;
	}
}
