/**
 * SSDBRelation.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package keggapi;

@SuppressWarnings("serial")
public class SSDBRelation  implements java.io.Serializable {
    private String genes_id1;

    private String genes_id2;

    private int sw_score;

    private float bit_score;

    private float identity;

    private int overlap;

    private int start_position1;

    private int end_position1;

    private int start_position2;

    private int end_position2;

    private boolean best_flag_1To2;

    private boolean best_flag_2To1;

    private String definition1;

    private String definition2;

    private int length1;

    private int length2;

    public SSDBRelation() {
    }

    public SSDBRelation(
           String genes_id1,
           String genes_id2,
           int sw_score,
           float bit_score,
           float identity,
           int overlap,
           int start_position1,
           int end_position1,
           int start_position2,
           int end_position2,
           boolean best_flag_1To2,
           boolean best_flag_2To1,
           String definition1,
           String definition2,
           int length1,
           int length2) {
           this.genes_id1 = genes_id1;
           this.genes_id2 = genes_id2;
           this.sw_score = sw_score;
           this.bit_score = bit_score;
           this.identity = identity;
           this.overlap = overlap;
           this.start_position1 = start_position1;
           this.end_position1 = end_position1;
           this.start_position2 = start_position2;
           this.end_position2 = end_position2;
           this.best_flag_1To2 = best_flag_1To2;
           this.best_flag_2To1 = best_flag_2To1;
           this.definition1 = definition1;
           this.definition2 = definition2;
           this.length1 = length1;
           this.length2 = length2;
    }


    /**
     * Gets the genes_id1 value for this SSDBRelation.
     * 
     * @return genes_id1
     */
    public String getGenes_id1() {
        return genes_id1;
    }


    /**
     * Sets the genes_id1 value for this SSDBRelation.
     * 
     * @param genes_id1
     */
    public void setGenes_id1(String genes_id1) {
        this.genes_id1 = genes_id1;
    }


    /**
     * Gets the genes_id2 value for this SSDBRelation.
     * 
     * @return genes_id2
     */
    public String getGenes_id2() {
        return genes_id2;
    }


    /**
     * Sets the genes_id2 value for this SSDBRelation.
     * 
     * @param genes_id2
     */
    public void setGenes_id2(String genes_id2) {
        this.genes_id2 = genes_id2;
    }


    /**
     * Gets the sw_score value for this SSDBRelation.
     * 
     * @return sw_score
     */
    public int getSw_score() {
        return sw_score;
    }


    /**
     * Sets the sw_score value for this SSDBRelation.
     * 
     * @param sw_score
     */
    public void setSw_score(int sw_score) {
        this.sw_score = sw_score;
    }


    /**
     * Gets the bit_score value for this SSDBRelation.
     * 
     * @return bit_score
     */
    public float getBit_score() {
        return bit_score;
    }


    /**
     * Sets the bit_score value for this SSDBRelation.
     * 
     * @param bit_score
     */
    public void setBit_score(float bit_score) {
        this.bit_score = bit_score;
    }


    /**
     * Gets the identity value for this SSDBRelation.
     * 
     * @return identity
     */
    public float getIdentity() {
        return identity;
    }


    /**
     * Sets the identity value for this SSDBRelation.
     * 
     * @param identity
     */
    public void setIdentity(float identity) {
        this.identity = identity;
    }


    /**
     * Gets the overlap value for this SSDBRelation.
     * 
     * @return overlap
     */
    public int getOverlap() {
        return overlap;
    }


    /**
     * Sets the overlap value for this SSDBRelation.
     * 
     * @param overlap
     */
    public void setOverlap(int overlap) {
        this.overlap = overlap;
    }


    /**
     * Gets the start_position1 value for this SSDBRelation.
     * 
     * @return start_position1
     */
    public int getStart_position1() {
        return start_position1;
    }


    /**
     * Sets the start_position1 value for this SSDBRelation.
     * 
     * @param start_position1
     */
    public void setStart_position1(int start_position1) {
        this.start_position1 = start_position1;
    }


    /**
     * Gets the end_position1 value for this SSDBRelation.
     * 
     * @return end_position1
     */
    public int getEnd_position1() {
        return end_position1;
    }


    /**
     * Sets the end_position1 value for this SSDBRelation.
     * 
     * @param end_position1
     */
    public void setEnd_position1(int end_position1) {
        this.end_position1 = end_position1;
    }


    /**
     * Gets the start_position2 value for this SSDBRelation.
     * 
     * @return start_position2
     */
    public int getStart_position2() {
        return start_position2;
    }


    /**
     * Sets the start_position2 value for this SSDBRelation.
     * 
     * @param start_position2
     */
    public void setStart_position2(int start_position2) {
        this.start_position2 = start_position2;
    }


    /**
     * Gets the end_position2 value for this SSDBRelation.
     * 
     * @return end_position2
     */
    public int getEnd_position2() {
        return end_position2;
    }


    /**
     * Sets the end_position2 value for this SSDBRelation.
     * 
     * @param end_position2
     */
    public void setEnd_position2(int end_position2) {
        this.end_position2 = end_position2;
    }


    /**
     * Gets the best_flag_1To2 value for this SSDBRelation.
     * 
     * @return best_flag_1To2
     */
    public boolean isBest_flag_1To2() {
        return best_flag_1To2;
    }


    /**
     * Sets the best_flag_1To2 value for this SSDBRelation.
     * 
     * @param best_flag_1To2
     */
    public void setBest_flag_1To2(boolean best_flag_1To2) {
        this.best_flag_1To2 = best_flag_1To2;
    }


    /**
     * Gets the best_flag_2To1 value for this SSDBRelation.
     * 
     * @return best_flag_2To1
     */
    public boolean isBest_flag_2To1() {
        return best_flag_2To1;
    }


    /**
     * Sets the best_flag_2To1 value for this SSDBRelation.
     * 
     * @param best_flag_2To1
     */
    public void setBest_flag_2To1(boolean best_flag_2To1) {
        this.best_flag_2To1 = best_flag_2To1;
    }


    /**
     * Gets the definition1 value for this SSDBRelation.
     * 
     * @return definition1
     */
    public String getDefinition1() {
        return definition1;
    }


    /**
     * Sets the definition1 value for this SSDBRelation.
     * 
     * @param definition1
     */
    public void setDefinition1(String definition1) {
        this.definition1 = definition1;
    }


    /**
     * Gets the definition2 value for this SSDBRelation.
     * 
     * @return definition2
     */
    public String getDefinition2() {
        return definition2;
    }


    /**
     * Sets the definition2 value for this SSDBRelation.
     * 
     * @param definition2
     */
    public void setDefinition2(String definition2) {
        this.definition2 = definition2;
    }


    /**
     * Gets the length1 value for this SSDBRelation.
     * 
     * @return length1
     */
    public int getLength1() {
        return length1;
    }


    /**
     * Sets the length1 value for this SSDBRelation.
     * 
     * @param length1
     */
    public void setLength1(int length1) {
        this.length1 = length1;
    }


    /**
     * Gets the length2 value for this SSDBRelation.
     * 
     * @return length2
     */
    public int getLength2() {
        return length2;
    }


    /**
     * Sets the length2 value for this SSDBRelation.
     * 
     * @param length2
     */
    public void setLength2(int length2) {
        this.length2 = length2;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SSDBRelation)) return false;
        SSDBRelation other = (SSDBRelation) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.genes_id1==null && other.getGenes_id1()==null) || 
             (this.genes_id1!=null &&
              this.genes_id1.equals(other.getGenes_id1()))) &&
            ((this.genes_id2==null && other.getGenes_id2()==null) || 
             (this.genes_id2!=null &&
              this.genes_id2.equals(other.getGenes_id2()))) &&
            this.sw_score == other.getSw_score() &&
            this.bit_score == other.getBit_score() &&
            this.identity == other.getIdentity() &&
            this.overlap == other.getOverlap() &&
            this.start_position1 == other.getStart_position1() &&
            this.end_position1 == other.getEnd_position1() &&
            this.start_position2 == other.getStart_position2() &&
            this.end_position2 == other.getEnd_position2() &&
            this.best_flag_1To2 == other.isBest_flag_1To2() &&
            this.best_flag_2To1 == other.isBest_flag_2To1() &&
            ((this.definition1==null && other.getDefinition1()==null) || 
             (this.definition1!=null &&
              this.definition1.equals(other.getDefinition1()))) &&
            ((this.definition2==null && other.getDefinition2()==null) || 
             (this.definition2!=null &&
              this.definition2.equals(other.getDefinition2()))) &&
            this.length1 == other.getLength1() &&
            this.length2 == other.getLength2();
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
        if (getGenes_id1() != null) {
            _hashCode += getGenes_id1().hashCode();
        }
        if (getGenes_id2() != null) {
            _hashCode += getGenes_id2().hashCode();
        }
        _hashCode += getSw_score();
        _hashCode += new Float(getBit_score()).hashCode();
        _hashCode += new Float(getIdentity()).hashCode();
        _hashCode += getOverlap();
        _hashCode += getStart_position1();
        _hashCode += getEnd_position1();
        _hashCode += getStart_position2();
        _hashCode += getEnd_position2();
        _hashCode += (isBest_flag_1To2() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isBest_flag_2To1() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getDefinition1() != null) {
            _hashCode += getDefinition1().hashCode();
        }
        if (getDefinition2() != null) {
            _hashCode += getDefinition2().hashCode();
        }
        _hashCode += getLength1();
        _hashCode += getLength2();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SSDBRelation.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("SOAP/KEGG", "SSDBRelation"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("genes_id1");
        elemField.setXmlName(new javax.xml.namespace.QName("", "genes_id1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("genes_id2");
        elemField.setXmlName(new javax.xml.namespace.QName("", "genes_id2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sw_score");
        elemField.setXmlName(new javax.xml.namespace.QName("", "sw_score"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bit_score");
        elemField.setXmlName(new javax.xml.namespace.QName("", "bit_score"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("identity");
        elemField.setXmlName(new javax.xml.namespace.QName("", "identity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("overlap");
        elemField.setXmlName(new javax.xml.namespace.QName("", "overlap"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("start_position1");
        elemField.setXmlName(new javax.xml.namespace.QName("", "start_position1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("end_position1");
        elemField.setXmlName(new javax.xml.namespace.QName("", "end_position1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("start_position2");
        elemField.setXmlName(new javax.xml.namespace.QName("", "start_position2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("end_position2");
        elemField.setXmlName(new javax.xml.namespace.QName("", "end_position2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("best_flag_1To2");
        elemField.setXmlName(new javax.xml.namespace.QName("", "best_flag_1to2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("best_flag_2To1");
        elemField.setXmlName(new javax.xml.namespace.QName("", "best_flag_2to1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("definition1");
        elemField.setXmlName(new javax.xml.namespace.QName("", "definition1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("definition2");
        elemField.setXmlName(new javax.xml.namespace.QName("", "definition2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("length1");
        elemField.setXmlName(new javax.xml.namespace.QName("", "length1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("length2");
        elemField.setXmlName(new javax.xml.namespace.QName("", "length2"));
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
    @SuppressWarnings("unchecked")
    public static org.apache.axis.encoding.Serializer getSerializer(
           String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
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
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
