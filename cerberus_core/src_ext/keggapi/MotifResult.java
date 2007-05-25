/**
 * MotifResult.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package keggapi;

public class MotifResult  implements java.io.Serializable {
    private java.lang.String motif_id;

    private java.lang.String definition;

    private java.lang.String genes_id;

    private int start_position;

    private int end_position;

    private float score;

    private double evalue;

    public MotifResult() {
    }

    public MotifResult(
           java.lang.String motif_id,
           java.lang.String definition,
           java.lang.String genes_id,
           int start_position,
           int end_position,
           float score,
           double evalue) {
           this.motif_id = motif_id;
           this.definition = definition;
           this.genes_id = genes_id;
           this.start_position = start_position;
           this.end_position = end_position;
           this.score = score;
           this.evalue = evalue;
    }


    /**
     * Gets the motif_id value for this MotifResult.
     * 
     * @return motif_id
     */
    public java.lang.String getMotif_id() {
        return motif_id;
    }


    /**
     * Sets the motif_id value for this MotifResult.
     * 
     * @param motif_id
     */
    public void setMotif_id(java.lang.String motif_id) {
        this.motif_id = motif_id;
    }


    /**
     * Gets the definition value for this MotifResult.
     * 
     * @return definition
     */
    public java.lang.String getDefinition() {
        return definition;
    }


    /**
     * Sets the definition value for this MotifResult.
     * 
     * @param definition
     */
    public void setDefinition(java.lang.String definition) {
        this.definition = definition;
    }


    /**
     * Gets the genes_id value for this MotifResult.
     * 
     * @return genes_id
     */
    public java.lang.String getGenes_id() {
        return genes_id;
    }


    /**
     * Sets the genes_id value for this MotifResult.
     * 
     * @param genes_id
     */
    public void setGenes_id(java.lang.String genes_id) {
        this.genes_id = genes_id;
    }


    /**
     * Gets the start_position value for this MotifResult.
     * 
     * @return start_position
     */
    public int getStart_position() {
        return start_position;
    }


    /**
     * Sets the start_position value for this MotifResult.
     * 
     * @param start_position
     */
    public void setStart_position(int start_position) {
        this.start_position = start_position;
    }


    /**
     * Gets the end_position value for this MotifResult.
     * 
     * @return end_position
     */
    public int getEnd_position() {
        return end_position;
    }


    /**
     * Sets the end_position value for this MotifResult.
     * 
     * @param end_position
     */
    public void setEnd_position(int end_position) {
        this.end_position = end_position;
    }


    /**
     * Gets the score value for this MotifResult.
     * 
     * @return score
     */
    public float getScore() {
        return score;
    }


    /**
     * Sets the score value for this MotifResult.
     * 
     * @param score
     */
    public void setScore(float score) {
        this.score = score;
    }


    /**
     * Gets the evalue value for this MotifResult.
     * 
     * @return evalue
     */
    public double getEvalue() {
        return evalue;
    }


    /**
     * Sets the evalue value for this MotifResult.
     * 
     * @param evalue
     */
    public void setEvalue(double evalue) {
        this.evalue = evalue;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MotifResult)) return false;
        MotifResult other = (MotifResult) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.motif_id==null && other.getMotif_id()==null) || 
             (this.motif_id!=null &&
              this.motif_id.equals(other.getMotif_id()))) &&
            ((this.definition==null && other.getDefinition()==null) || 
             (this.definition!=null &&
              this.definition.equals(other.getDefinition()))) &&
            ((this.genes_id==null && other.getGenes_id()==null) || 
             (this.genes_id!=null &&
              this.genes_id.equals(other.getGenes_id()))) &&
            this.start_position == other.getStart_position() &&
            this.end_position == other.getEnd_position() &&
            this.score == other.getScore() &&
            this.evalue == other.getEvalue();
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
        if (getMotif_id() != null) {
            _hashCode += getMotif_id().hashCode();
        }
        if (getDefinition() != null) {
            _hashCode += getDefinition().hashCode();
        }
        if (getGenes_id() != null) {
            _hashCode += getGenes_id().hashCode();
        }
        _hashCode += getStart_position();
        _hashCode += getEnd_position();
        _hashCode += new Float(getScore()).hashCode();
        _hashCode += new Double(getEvalue()).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MotifResult.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("SOAP/KEGG", "MotifResult"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("motif_id");
        elemField.setXmlName(new javax.xml.namespace.QName("", "motif_id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("definition");
        elemField.setXmlName(new javax.xml.namespace.QName("", "definition"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("genes_id");
        elemField.setXmlName(new javax.xml.namespace.QName("", "genes_id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("start_position");
        elemField.setXmlName(new javax.xml.namespace.QName("", "start_position"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("end_position");
        elemField.setXmlName(new javax.xml.namespace.QName("", "end_position"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("score");
        elemField.setXmlName(new javax.xml.namespace.QName("", "score"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("evalue");
        elemField.setXmlName(new javax.xml.namespace.QName("", "evalue"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
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
