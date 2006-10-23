/**
 * PathwayElement.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package keggapi;

public class PathwayElement  implements java.io.Serializable {
    private int element_id;

    private java.lang.String type;

    private java.lang.String[] names;

    private int[] components;

    public PathwayElement() {
    }

    public PathwayElement(
           int element_id,
           java.lang.String type,
           java.lang.String[] names,
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
    public java.lang.String getType() {
        return type;
    }


    /**
     * Sets the type value for this PathwayElement.
     * 
     * @param type
     */
    public void setType(java.lang.String type) {
        this.type = type;
    }


    /**
     * Gets the names value for this PathwayElement.
     * 
     * @return names
     */
    public java.lang.String[] getNames() {
        return names;
    }


    /**
     * Sets the names value for this PathwayElement.
     * 
     * @param names
     */
    public void setNames(java.lang.String[] names) {
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

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
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
                 i<java.lang.reflect.Array.getLength(getNames());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getNames(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getComponents() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getComponents());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getComponents(), i);
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
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PathwayElement.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("SOAP/KEGG", "PathwayElement"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("element_id");
        elemField.setXmlName(new javax.xml.namespace.QName("", "element_id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("type");
        elemField.setXmlName(new javax.xml.namespace.QName("", "type"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("names");
        elemField.setXmlName(new javax.xml.namespace.QName("", "names"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("components");
        elemField.setXmlName(new javax.xml.namespace.QName("", "components"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
