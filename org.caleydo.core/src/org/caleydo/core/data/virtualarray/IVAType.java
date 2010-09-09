package org.caleydo.core.data.virtualarray;

public class IVAType {

	protected String stringRep;

	public void setStringRep(String stringRep) {
		this.stringRep = stringRep;
	}

	public String getStringRep() {
		return stringRep;
	}

	@Override
	public String toString() {
		return stringRep;
	}

}
