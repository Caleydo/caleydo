/**
 * PathwayElement.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package keggapi;

import java.io.Serializable;
import java.lang.reflect.Array;
import javax.xml.namespace.QName;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.description.ElementDesc;

public class PathwayElement  
implements Serializable {

		private int element_id;

    private String type;

    private String[] names;

    private int[] components;

    public PathwayElement() {
    }

    public PathwayElement(
           int element_id,
           String type,
           String[] names,
           int[] components) {
           this.element_id = element_id;
           this.type = type;
           this.names = names;
           this.components = components;
    }


    /**
     * Gets the element_id value for this PathwayElement.
     * 
     * @return element_id
     */
    public int getElement_id() {
        return element_id;
    }


    /**
     * Sets the element_id value for this PathwayElement.
     * 
     * @param element_id
     */
    public void setElement_id(int element_id) {
        this.element_id = element_id;
    }


    /**
     * Gets the type value for this PathwayElement.
     * 
     * @return type
     */
    public String getType() {
        return type;
    }


    /**
     * Sets the type value for this PathwayElement.
     * 
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }


    /**
     * Gets the names value for this PathwayElement.
     * 
     * @return names
     */
    public String[] getNames() {
        return names;
    }


    /**
     * Sets the names value for this PathwayElement.
     * 
     * @param names
     */
    public void setNames(String[] names) {
        this.names = names;
    }


    /**
     * Gets the components value for this PathwayElement.
     * 
     * @return components
     */
    public int[] getComponents() {
        return components;
    }


    /**
     * Sets the components value for this PathwayElement.
     * 
     * @param components
     */
    public void setComponents(int[] components) {
        this.components = components;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof PathwayElement)) return false;
        PathwayElement other = (PathwayElement) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.element_id == other.getElement_id() &&
            ((this.type==null && other.getType()==null) || 
             (this.type!=null &&
              this.type.equals(other.getType()))) &&
            ((this.names==null && other.getNames()==null) || 
             (this.names!=null &&
              java.util.Arrays.equals(this.names, other.getNames()))) &&
            ((this.components==null && other.getComponents()==null) || 
             (this.components!=null &&
              java.util.Arrays.equals(this.components, other.getComponents())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        _hashCode += getElement_id();
        if (getType() != null) {
            _hashCode += getType().hashCode();
        }
        if (getNames() != null) {
            for (int i=0;
                 i<Array.getLength(getNames());
                 i++) {
                Object obj = Array.get(getNames(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getComponents() != null) {
            for (int i=0;
                 i<Array.getLength(getComponents());
                 i++) {
                Object obj = Array.get(getComponents(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static TypeDesc typeDesc =
        new TypeDesc(PathwayElement.class, true);

    static {
        typeDesc.setXmlType(new QName("SOAP/KEGG", "PathwayElement"));
        ElementDesc elemField = new ElementDesc();
        elemField.setFieldName("element_id");
        elemField.setXmlName(new QName("", "element_id"));
        elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new ElementDesc();
        elemField.setFieldName("type");
        elemField.setXmlName(new QName("", "type"));
        elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new ElementDesc();
        elemField.setFieldName("names");
        elemField.setXmlName(new QName("", "names"));
        elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new ElementDesc();
        elemField.setFieldName("components");
        elemField.setXmlName(new QName("", "components"));
        elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static Serializer getSerializer(
           String mechType, 
           Class _javaType,  
           QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static Deserializer getDeserializer(
           String mechType, 
           Class _javaType,  
           QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
