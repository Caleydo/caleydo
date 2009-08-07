package org.caleydo.core.serialize;

import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.selection.VirtualArray;
import org.caleydo.core.manager.usecase.AUseCase;
import org.caleydo.core.view.opengl.canvas.storagebased.EVAType;

/**
 * Message with initialization data sent from the server to the client after
 * a successful handshake. 
 * 
 * @author Werner Puff
 */
@XmlType
@XmlRootElement
public class ApplicationInitData {

	private AUseCase useCase;
	
	private byte[] setFileContent;
	
	private HashMap<EVAType, VirtualArray> virtualArrayMap;
	
	public AUseCase getUseCase() {
		return useCase;
	}

	public void setUseCase(AUseCase useCase) {
		this.useCase = useCase;
	}

	public byte[] getSetFileContent() {
		return setFileContent;
	}

	public void setSetFileContent(byte[] setFileContent) {
		this.setFileContent = setFileContent;
	}

	public HashMap<EVAType, VirtualArray> getVirtualArrayMap() {
		return virtualArrayMap;
	}

	public void setVirtualArrayMap(HashMap<EVAType, VirtualArray> virtualArrayMap) {
		this.virtualArrayMap = virtualArrayMap;
	}

}
