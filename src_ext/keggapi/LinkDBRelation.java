/**
 * LinkDBRelation.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package keggapi;

public class LinkDBRelation  implements java.io.Serializable {
    private java.lang.String entry_id1;

    private java.lang.String entry_id2;

    private java.lang.String type;

    private java.lang.String path;

    public LinkDBRelation() {
    }

    public LinkDBRelation(
           java.lang.String entry_id1,
           java.lang.String entry_id2,
           java.lang.String type,
           java.lang.String path) {
           this.entry_id1 = entry_id1;
           this.entry_id2 = entry_id2;
           this.type = type;
           this.path = path;
    }


    /**
     * Gets the entry_id1 value for this LinkDBRelation.
     * 
     * @return entry_id1
     */
    public java.lang.String getEntry_id1() {
        return entry_id1;
    }


    /**
     * Sets the entry_id1 value for this LinkDBRelation.
     * 
     * @param entry_id1
     */
    public void setEntry_id1(java.lang.String entry_id1) {
        this.entry_id1 = entry_id1;
    }


    /**
     * Gets the entry_id2 value for this LinkDBRelation.
     * 
     * @return entry_id2
     */
    public java.lang.String getEntry_id2() {
        return entry_id2;
    }


    /**
     * Sets the entry_id2 value for this LinkDBRelation.
     * 
     * @param entry_id2
     */
    public void setEntry_id2(java.lang.String entry_id2) {
        this.entry_id2 = entry_id2;
    }


    /**
     * Gets the type value for this LinkDBRelation.
     * 
     * @return type
     */
    public java.lang.String getType() {
        return type;
    }


    /**
     * Sets the type value for this LinkDBRelation.
     * 
     * @param type
     */
    public void setType(java.lang.String type) {
        this.type = type;
    }


    /**
     * Gets the path value for this LinkDBRelation.
     * 
     * @return path
     */
    public java.lang.String getPath() {
        return path;
    }


    /**
     * Sets the path value for this LinkDBRelation.
     * 
     * @param path
     */
    public void setPath(java.lang.String path) {
        this.path = path;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof LinkDBRelation)) return false;
        LinkDBRelation other = (LinkDBRelation) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.entry_id1==null && other.getEntry_id1()==null) || 
             (this.entry_id1!=null &&
              this.entry_id1.equals(other.getEntry_id1()))) &&
            ((this.entry_id2==null && other.getEntry_id2()==null) || 
             (this.entry_id2!=null &&
              this.entry_id2.equals(other.getEntry_id2()))) &&
            ((this.type==null && other.getType()==null) || 
             (this.type!=null &&
              this.type.equals(other.getType()))) &&
            ((this.path==null && other.getPath()==null) || 
             (this.path!=null &&
              this.path.equals(other.getPath())));
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
        if (getEntry_id1() != null) {
            _hashCode += getEntry_id1().hashCode();
        }
        if (getEntry_id2() != null) {
            _hashCode += getEntry_id2().hashCode();
        }
        if (getType() != null) {
            _hashCode += getType().hashCode();
        }
        if (getPath() != null) {
            _hashCode += getPath().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(LinkDBRelation.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("SOAP/KEGG", "LinkDBRelation"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("entry_id1");
        elemField.setXmlName(new javax.xml.namespace.QName("", "entry_id1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("entry_id2");
        elemField.setXmlName(new javax.xml.namespace.QName("", "entry_id2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("type");
        elemField.setXmlName(new javax.xml.namespace.QName("", "type"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("path");
        elemField.setXmlName(new javax.xml.namespace.QName("", "path"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
