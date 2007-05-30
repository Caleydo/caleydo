/**
 * PathwayElementRelation.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package keggapi;

import javax.xml.namespace.QName;

@SuppressWarnings("serial")
public class PathwayElementRelation  implements java.io.Serializable {
    private int element_id1;

    private int element_id2;

    private String type;

    private String name;

    private keggapi.Subtype[] subtypes;

    public PathwayElementRelation() {
    }

    public PathwayElementRelation(
           int element_id1,
           int element_id2,
           String type,
           String name,
           keggapi.Subtype[] subtypes) {
           this.element_id1 = element_id1;
           this.element_id2 = element_id2;
           this.type = type;
           this.name = name;
           this.subtypes = subtypes;
    }


    /**
     * Gets the element_id1 value for this PathwayElementRelation.
     * 
     * @return element_id1
     */
    public int getElement_id1() {
        return element_id1;
    }


    /**
     * Sets the element_id1 value for this PathwayElementRelation.
     * 
     * @param element_id1
     */
    public void setElement_id1(int element_id1) {
        this.element_id1 = element_id1;
    }


    /**
     * Gets the element_id2 value for this PathwayElementRelation.
     * 
     * @return element_id2
     */
    public int getElement_id2() {
        return element_id2;
    }


    /**
     * Sets the element_id2 value for this PathwayElementRelation.
     * 
     * @param element_id2
     */
    public void setElement_id2(int element_id2) {
        this.element_id2 = element_id2;
    }


    /**
     * Gets the type value for this PathwayElementRelation.
     * 
     * @return type
     */
    public String getType() {
        return type;
    }


    /**
     * Sets the type value for this PathwayElementRelation.
     * 
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }


    /**
     * Gets the name value for this PathwayElementRelation.
     * 
     * @return name
     */
    public String getName() {
        return name;
    }


    /**
     * Sets the name value for this PathwayElementRelation.
     * 
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * Gets the subtypes value for this PathwayElementRelation.
     * 
     * @return subtypes
     */
    public keggapi.Subtype[] getSubtypes() {
        return subtypes;
    }


    /**
     * Sets the subtypes value for this PathwayElementRelation.
     * 
     * @param subtypes
     */
    public void setSubtypes(keggapi.Subtype[] subtypes) {
        this.subtypes = subtypes;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PathwayElementRelation)) return false;
        PathwayElementRelation other = (PathwayElementRelation) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.element_id1 == other.getElement_id1() &&
            this.element_id2 == other.getElement_id2() &&
            ((this.type==null && other.getType()==null) || 
             (this.type!=null &&
              this.type.equals(other.getType()))) &&
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName()))) &&
            ((this.subtypes==null && other.getSubtypes()==null) || 
             (this.subtypes!=null &&
              java.util.Arrays.equals(this.subtypes, other.getSubtypes())));
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
        _hashCode += getElement_id1();
        _hashCode += getElement_id2();
        if (getType() != null) {
            _hashCode += getType().hashCode();
        }
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        if (getSubtypes() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSubtypes());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSubtypes(), i);
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
        new org.apache.axis.description.TypeDesc(PathwayElementRelation.class, true);

    static {
        typeDesc.setXmlType(new QName("SOAP/KEGG", "PathwayElementRelation"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("element_id1");
        elemField.setXmlName(new QName("", "element_id1"));
        elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("element_id2");
        elemField.setXmlName(new QName("", "element_id2"));
        elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("type");
        elemField.setXmlName(new QName("", "type"));
        elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name");
        elemField.setXmlName(new QName("", "name"));
        elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("subtypes");
        elemField.setXmlName(new QName("", "subtypes"));
        elemField.setXmlType(new QName("SOAP/KEGG", "Subtype"));
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
    @SuppressWarnings("unchecked")
    public static org.apache.axis.encoding.Serializer getSerializer(
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
    @SuppressWarnings("unchecked")
	public static org.apache.axis.encoding.Deserializer getDeserializer(
           String mechType, 
           Class _javaType,  
           QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
