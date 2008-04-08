/**
 * KEGG.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package keggapi;

public interface KEGG extends javax.xml.rpc.Service {
    public String getKEGGPortAddress();

    public keggapi.KEGGPortType getKEGGPort() throws javax.xml.rpc.ServiceException;

    public keggapi.KEGGPortType getKEGGPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
