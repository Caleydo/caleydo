/**
 * KEGGLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package keggapi;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Remote;
import java.util.HashSet;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;

import org.apache.axis.AxisFault;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.Service;

import keggapi.KEGGPortType;
import keggapi.KEGG;
import keggapi.KEGGBindingStub;

@SuppressWarnings("serial")
public class KEGGLocator extends Service implements KEGG {

	private HashSet <QName> ports = null;
	
	 // The WSDD service name defaults to the port name.
    private String KEGGPortWSDDServiceName = "KEGGPort";

    // Use to get a proxy class for KEGGPort
    private String KEGGPort_address = "http://soap.genome.jp/keggapi/request_v5.0.cgi";

    
    public KEGGLocator() {
    }


    public KEGGLocator(EngineConfiguration config) {
        super(config);
    }

    public KEGGLocator(String wsdlLoc, QName sName) throws ServiceException {
        super(wsdlLoc, sName);
    }

   
    public String getKEGGPortAddress() {
        return KEGGPort_address;
    }

   
    public String getKEGGPortWSDDServiceName() {
        return KEGGPortWSDDServiceName;
    }

    public void setKEGGPortWSDDServiceName(String name) {
        KEGGPortWSDDServiceName = name;
    }

    public KEGGPortType getKEGGPort() throws ServiceException {
       URL endpoint;
        try {
            endpoint = new URL(KEGGPort_address);
        }
        catch (MalformedURLException e) {
            throw new ServiceException(e);
        }
        return getKEGGPort(endpoint);
    }

    public KEGGPortType getKEGGPort(URL portAddress) throws ServiceException {
        try {
            KEGGBindingStub _stub = new KEGGBindingStub(portAddress, this);
            _stub.setPortName(getKEGGPortWSDDServiceName());
            return _stub;
        }
        catch (AxisFault e) {
            return null;
        }
    }

    public void setKEGGPortEndpointAddress(String address) {
        KEGGPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    @SuppressWarnings("unchecked")
	public Remote getPort(Class serviceEndpointInterface) throws ServiceException {
        try {
            if (KEGGPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                KEGGBindingStub _stub = new KEGGBindingStub(new URL(KEGGPort_address), this);
                _stub.setPortName(getKEGGPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new ServiceException(t);
        }
        throw new ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    @SuppressWarnings("unchecked")
    public Remote getPort(QName portName, Class serviceEndpointInterface) throws ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        String inputPortName = portName.getLocalPart();
        if ("KEGGPort".equals(inputPortName)) {
            return getKEGGPort();
        }
        else  {
            Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public QName getServiceName() {
        return new QName("SOAP/KEGG", "KEGG");
    }

    

    public Iterator <QName> getPorts() {
        if (ports == null) {
            ports = new HashSet <QName> ();
            ports.add(new QName("SOAP/KEGG", "KEGGPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(String portName, String address) throws ServiceException {
        
    	if ("KEGGPort".equals(portName)) {
            setKEGGPortEndpointAddress(address);
        }
        else 
        { // Unknown Port Name
            throw new ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(QName portName, String address) throws ServiceException {
    	if  (portName!= null) {
    		setEndpointAddress(portName.getLocalPart(), address);
    		return;
    	}
    	assert false : "setEndpointAddress( portName==null ," + address + ")";
    }

}
