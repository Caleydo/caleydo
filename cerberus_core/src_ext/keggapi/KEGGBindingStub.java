/**
 * KEGGBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package keggapi;

import java.util.Enumeration;
import java.util.Vector;

import org.apache.axis.utils.*;
import org.apache.axis.NoEndPointException;
import org.apache.axis.encoding.DeserializerFactory;
import org.apache.axis.encoding.SerializerFactory;
import org.apache.axis.encoding.ser.ArrayDeserializerFactory;
import org.apache.axis.encoding.ser.ArraySerializerFactory;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.encoding.ser.BeanSerializerFactory;
import org.apache.axis.encoding.ser.EnumDeserializerFactory;
import org.apache.axis.encoding.ser.EnumSerializerFactory;
import org.apache.axis.encoding.ser.SimpleDeserializerFactory;
import org.apache.axis.encoding.ser.SimpleListDeserializerFactory;
import org.apache.axis.encoding.ser.SimpleListSerializerFactory;
import org.apache.axis.encoding.ser.SimpleSerializerFactory;
import org.apache.axis.soap.*;
import javax.xml.namespace.QName;

@SuppressWarnings("unchecked")
public class KEGGBindingStub extends org.apache.axis.client.Stub implements keggapi.KEGGPortType {
    
	private Vector cachedSerClasses = new Vector();
    private Vector <QName> cachedSerQNames = new Vector <QName> ();
    private Vector cachedSerFactories = new Vector ();
    private Vector cachedDeserFactories = new Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[58];
        _initOperationDesc1();
        _initOperationDesc2();
        _initOperationDesc3();
        _initOperationDesc4();
        _initOperationDesc5();
        _initOperationDesc6();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("list_databases");
        oper.setReturnType(new QName("SOAP/KEGG", "ArrayOfDefinition"));
        oper.setReturnClass(keggapi.Definition[].class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("list_organisms");
        oper.setReturnType(new QName("SOAP/KEGG", "ArrayOfDefinition"));
        oper.setReturnClass(keggapi.Definition[].class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("list_pathways");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "org"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("SOAP/KEGG", "ArrayOfDefinition"));
        oper.setReturnClass(keggapi.Definition[].class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("list_ko_classes");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "class_id"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("SOAP/KEGG", "ArrayOfDefinition"));
        oper.setReturnClass(keggapi.Definition[].class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("binfo");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "db"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(String.class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[4] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("bget");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "string"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(String.class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[5] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("bfind");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "string"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(String.class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[6] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("btit");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "string"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(String.class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[7] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("bconv");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "string"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(String.class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[8] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_linkdb_by_entry");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "entry_id"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new QName("", "db"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new QName("", "start"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new QName("", "max_results"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("SOAP/KEGG", "ArrayOfLinkDBRelation"));
        oper.setReturnClass(keggapi.LinkDBRelation[].class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[9] = oper;

    }

    private static void _initOperationDesc2(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_best_neighbors_by_gene");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "genes_id"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new QName("", "start"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new QName("", "max_results"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("SOAP/KEGG", "ArrayOfSSDBRelation"));
        oper.setReturnClass(keggapi.SSDBRelation[].class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[10] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_best_best_neighbors_by_gene");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "genes_id"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new QName("", "start"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new QName("", "max_results"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("SOAP/KEGG", "ArrayOfSSDBRelation"));
        oper.setReturnClass(keggapi.SSDBRelation[].class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[11] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_reverse_best_neighbors_by_gene");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "genes_id"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new QName("", "start"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new QName("", "max_results"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("SOAP/KEGG", "ArrayOfSSDBRelation"));
        oper.setReturnClass(keggapi.SSDBRelation[].class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[12] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_paralogs_by_gene");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "genes_id"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new QName("", "start"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new QName("", "max_results"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("SOAP/KEGG", "ArrayOfSSDBRelation"));
        oper.setReturnClass(keggapi.SSDBRelation[].class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[13] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_motifs_by_gene");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "genes_id"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new QName("", "db"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("SOAP/KEGG", "ArrayOfMotifResult"));
        oper.setReturnClass(keggapi.MotifResult[].class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[14] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_genes_by_motifs");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "motif_id_list"), org.apache.axis.description.ParameterDesc.IN, new QName("SOAP/KEGG", "ArrayOfstring"), String[].class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new QName("", "start"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new QName("", "max_results"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("SOAP/KEGG", "ArrayOfDefinition"));
        oper.setReturnClass(keggapi.Definition[].class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[15] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_ko_by_gene");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "genes_id"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("SOAP/KEGG", "ArrayOfstring"));
        oper.setReturnClass(String[].class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[16] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_ko_by_ko_class");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "class_id"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("SOAP/KEGG", "ArrayOfDefinition"));
        oper.setReturnClass(keggapi.Definition[].class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[17] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_genes_by_ko");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "ko_id"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new QName("", "org"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("SOAP/KEGG", "ArrayOfDefinition"));
        oper.setReturnClass(keggapi.Definition[].class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[18] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_genes_by_ko_class");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "class_id"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new QName("", "org"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new QName("", "start"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new QName("", "max_results"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("SOAP/KEGG", "ArrayOfDefinition"));
        oper.setReturnClass(keggapi.Definition[].class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[19] = oper;

    }

    private static void _initOperationDesc3(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_oc_members_by_gene");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "genes_id"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new QName("", "start"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new QName("", "max_results"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("SOAP/KEGG", "ArrayOfstring"));
        oper.setReturnClass(String[].class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[20] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_pc_members_by_gene");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "genes_id"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new QName("", "start"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new QName("", "max_results"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("SOAP/KEGG", "ArrayOfstring"));
        oper.setReturnClass(String[].class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[21] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_elements_by_pathway");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "pathway_id"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("SOAP/KEGG", "ArrayOfPathwayElement"));
        oper.setReturnClass(keggapi.PathwayElement[].class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[22] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_element_relations_by_pathway");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "pathway_id"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("SOAP/KEGG", "ArrayOfPathwayElementRelation"));
        oper.setReturnClass(keggapi.PathwayElementRelation[].class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[23] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("color_pathway_by_elements");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "pathway_id"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new QName("", "element_list"), org.apache.axis.description.ParameterDesc.IN, new QName("SOAP/KEGG", "ArrayOfint"), int[].class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new QName("", "fg_color_list"), org.apache.axis.description.ParameterDesc.IN, new QName("SOAP/KEGG", "ArrayOfstring"), String[].class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new QName("", "bg_color_list"), org.apache.axis.description.ParameterDesc.IN, new QName("SOAP/KEGG", "ArrayOfstring"), String[].class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(String.class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[24] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_html_of_colored_pathway_by_elements");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "pathway_id"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new QName("", "element_list"), org.apache.axis.description.ParameterDesc.IN, new QName("SOAP/KEGG", "ArrayOfint"), int[].class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new QName("", "fg_color_list"), org.apache.axis.description.ParameterDesc.IN, new QName("SOAP/KEGG", "ArrayOfstring"), String[].class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new QName("", "bg_color_list"), org.apache.axis.description.ParameterDesc.IN, new QName("SOAP/KEGG", "ArrayOfstring"), String[].class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(String.class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[25] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("mark_pathway_by_objects");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "pathway_id"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new QName("", "object_id_list"), org.apache.axis.description.ParameterDesc.IN, new QName("SOAP/KEGG", "ArrayOfstring"), String[].class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(String.class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[26] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("color_pathway_by_objects");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "pathway_id"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new QName("", "object_id_list"), org.apache.axis.description.ParameterDesc.IN, new QName("SOAP/KEGG", "ArrayOfstring"), String[].class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new QName("", "fg_color_list"), org.apache.axis.description.ParameterDesc.IN, new QName("SOAP/KEGG", "ArrayOfstring"), String[].class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new QName("", "bg_color_list"), org.apache.axis.description.ParameterDesc.IN, new QName("SOAP/KEGG", "ArrayOfstring"), String[].class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(String.class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[27] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_html_of_marked_pathway_by_objects");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "pathway_id"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new QName("", "object_id_list"), org.apache.axis.description.ParameterDesc.IN, new QName("SOAP/KEGG", "ArrayOfstring"), String[].class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(String.class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[28] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_html_of_colored_pathway_by_objects");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "pathway_id"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new QName("", "object_id_list"), org.apache.axis.description.ParameterDesc.IN, new QName("SOAP/KEGG", "ArrayOfstring"), String[].class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new QName("", "fg_color_list"), org.apache.axis.description.ParameterDesc.IN, new QName("SOAP/KEGG", "ArrayOfstring"), String[].class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new QName("", "bg_color_list"), org.apache.axis.description.ParameterDesc.IN, new QName("SOAP/KEGG", "ArrayOfstring"), String[].class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(String.class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[29] = oper;

    }

    private static void _initOperationDesc4(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_genes_by_pathway");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "pathway_id"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("SOAP/KEGG", "ArrayOfstring"));
        oper.setReturnClass(String[].class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[30] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_enzymes_by_pathway");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "pathway_id"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("SOAP/KEGG", "ArrayOfstring"));
        oper.setReturnClass(String[].class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[31] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_reactions_by_pathway");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "pathway_id"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("SOAP/KEGG", "ArrayOfstring"));
        oper.setReturnClass(String[].class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[32] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_pathways_by_genes");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "genes_id_list"), org.apache.axis.description.ParameterDesc.IN, new QName("SOAP/KEGG", "ArrayOfstring"), String[].class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("SOAP/KEGG", "ArrayOfstring"));
        oper.setReturnClass(String[].class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[33] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_pathways_by_enzymes");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "enzyme_id_list"), org.apache.axis.description.ParameterDesc.IN, new QName("SOAP/KEGG", "ArrayOfstring"), String[].class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("SOAP/KEGG", "ArrayOfstring"));
        oper.setReturnClass(String[].class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[34] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_pathways_by_reactions");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "reaction_id_list"), org.apache.axis.description.ParameterDesc.IN, new QName("SOAP/KEGG", "ArrayOfstring"), String[].class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("SOAP/KEGG", "ArrayOfstring"));
        oper.setReturnClass(String[].class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[35] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_linked_pathways");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "pathway_id"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("SOAP/KEGG", "ArrayOfstring"));
        oper.setReturnClass(String[].class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[36] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_genes_by_enzyme");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "enzyme_id"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new QName("", "org"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("SOAP/KEGG", "ArrayOfstring"));
        oper.setReturnClass(String[].class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[37] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_enzymes_by_gene");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "genes_id"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("SOAP/KEGG", "ArrayOfstring"));
        oper.setReturnClass(String[].class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[38] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_enzymes_by_reaction");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "reaction_id"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("SOAP/KEGG", "ArrayOfstring"));
        oper.setReturnClass(String[].class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[39] = oper;

    }

    private static void _initOperationDesc5(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_reactions_by_enzyme");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "enzyme_id"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("SOAP/KEGG", "ArrayOfstring"));
        oper.setReturnClass(String[].class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[40] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_genes_by_organism");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "org"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new QName("", "start"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new QName("", "max_results"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("SOAP/KEGG", "ArrayOfstring"));
        oper.setReturnClass(String[].class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[41] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_number_of_genes_by_organism");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "abbr"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
        oper.setReturnClass(int.class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[42] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_reactions_by_glycan");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "glycan_id"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("SOAP/KEGG", "ArrayOfstring"));
        oper.setReturnClass(String[].class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[43] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_reactions_by_compound");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "compound_id"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("SOAP/KEGG", "ArrayOfstring"));
        oper.setReturnClass(String[].class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[44] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_enzymes_by_glycan");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "glycan_id"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("SOAP/KEGG", "ArrayOfstring"));
        oper.setReturnClass(String[].class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[45] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_enzymes_by_compound");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "compound_id"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("SOAP/KEGG", "ArrayOfstring"));
        oper.setReturnClass(String[].class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[46] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_pathways_by_compounds");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "compound_id_list"), org.apache.axis.description.ParameterDesc.IN, new QName("SOAP/KEGG", "ArrayOfstring"), String[].class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("SOAP/KEGG", "ArrayOfstring"));
        oper.setReturnClass(String[].class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[47] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_pathways_by_glycans");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "glycan_id_list"), org.apache.axis.description.ParameterDesc.IN, new QName("SOAP/KEGG", "ArrayOfstring"), String[].class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("SOAP/KEGG", "ArrayOfstring"));
        oper.setReturnClass(String[].class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[48] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_compounds_by_pathway");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "pathway_id"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("SOAP/KEGG", "ArrayOfstring"));
        oper.setReturnClass(String[].class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[49] = oper;

    }

    private static void _initOperationDesc6(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_glycans_by_pathway");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "pathway_id"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("SOAP/KEGG", "ArrayOfstring"));
        oper.setReturnClass(String[].class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[50] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_compounds_by_reaction");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "reaction_id"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("SOAP/KEGG", "ArrayOfstring"));
        oper.setReturnClass(String[].class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[51] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_glycans_by_reaction");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "reaction_id"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("SOAP/KEGG", "ArrayOfstring"));
        oper.setReturnClass(String[].class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[52] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_compounds_by_enzyme");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "enzyme_id"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("SOAP/KEGG", "ArrayOfstring"));
        oper.setReturnClass(String[].class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[53] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_glycans_by_enzyme");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "enzyme_id"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("SOAP/KEGG", "ArrayOfstring"));
        oper.setReturnClass(String[].class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[54] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("convert_mol_to_kcf");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "mol_text"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(String.class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[55] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_kos_by_pathway");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "pathway_id"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("SOAP/KEGG", "ArrayOfstring"));
        oper.setReturnClass(String[].class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[56] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_pathways_by_kos");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "ko_id_list"), org.apache.axis.description.ParameterDesc.IN, new QName("SOAP/KEGG", "ArrayOfstring"), String[].class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new QName("", "org"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("SOAP/KEGG", "ArrayOfstring"));
        oper.setReturnClass(String[].class);
        oper.setReturnQName(new QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[57] = oper;

    }

    public KEGGBindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public KEGGBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    @SuppressWarnings("unused")
	public KEGGBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
        ((org.apache.axis.client.Service)super.service).setTypeMappingVersion("1.2");
            Class cls;
            QName qName;
            QName qName2;
            
            
            Class beansf = BeanSerializerFactory.class;
            Class beandf = BeanDeserializerFactory.class;
			Class enumsf = EnumSerializerFactory.class;
			Class enumdf = EnumDeserializerFactory.class;
            Class arraysf = ArraySerializerFactory.class;
            Class arraydf = ArrayDeserializerFactory.class;
            Class simplesf = SimpleSerializerFactory.class;
            Class simpledf = SimpleDeserializerFactory.class;
            Class simplelistsf = SimpleListSerializerFactory.class;
            Class simplelistdf = SimpleListDeserializerFactory.class;
            qName = new QName("SOAP/KEGG", "ArrayOfDefinition");
            cachedSerQNames.add(qName);
            cls = keggapi.Definition[].class;
            cachedSerClasses.add(cls);
            qName = new QName("SOAP/KEGG", "Definition");
            qName2 = null;
            cachedSerFactories.add(new ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new ArrayDeserializerFactory());

            qName = new QName("SOAP/KEGG", "ArrayOfint");
            cachedSerQNames.add(qName);
            cls = int[].class;
            cachedSerClasses.add(cls);
            qName = new QName("http://www.w3.org/2001/XMLSchema", "int");
            qName2 = null;
            cachedSerFactories.add(new ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new ArrayDeserializerFactory());

            qName = new QName("SOAP/KEGG", "ArrayOfLinkDBRelation");
            cachedSerQNames.add(qName);
            cls = keggapi.LinkDBRelation[].class;
            cachedSerClasses.add(cls);
            qName = new QName("SOAP/KEGG", "LinkDBRelation");
            qName2 = null;
            cachedSerFactories.add(new ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new ArrayDeserializerFactory());

            qName = new QName("SOAP/KEGG", "ArrayOfMotifResult");
            cachedSerQNames.add(qName);
            cls = keggapi.MotifResult[].class;
            cachedSerClasses.add(cls);
            qName = new QName("SOAP/KEGG", "MotifResult");
            qName2 = null;
            cachedSerFactories.add(new ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new ArrayDeserializerFactory());

            qName = new QName("SOAP/KEGG", "ArrayOfPathwayElement");
            cachedSerQNames.add(qName);
            cls = keggapi.PathwayElement[].class;
            cachedSerClasses.add(cls);
            qName = new QName("SOAP/KEGG", "PathwayElement");
            qName2 = null;
            cachedSerFactories.add(new ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new ArrayDeserializerFactory());

            qName = new QName("SOAP/KEGG", "ArrayOfPathwayElementRelation");
            cachedSerQNames.add(qName);
            cls = keggapi.PathwayElementRelation[].class;
            cachedSerClasses.add(cls);
            qName = new QName("SOAP/KEGG", "PathwayElementRelation");
            qName2 = null;
            cachedSerFactories.add(new ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new ArrayDeserializerFactory());

            qName = new QName("SOAP/KEGG", "ArrayOfSSDBRelation");
            cachedSerQNames.add(qName);
            cls = keggapi.SSDBRelation[].class;
            cachedSerClasses.add(cls);
            qName = new QName("SOAP/KEGG", "SSDBRelation");
            qName2 = null;
            cachedSerFactories.add(new ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new ArrayDeserializerFactory());

            qName = new QName("SOAP/KEGG", "ArrayOfstring");
            cachedSerQNames.add(qName);
            cls = String[].class;
            cachedSerClasses.add(cls);
            qName = new QName("http://www.w3.org/2001/XMLSchema", "string");
            qName2 = null;
            cachedSerFactories.add(new ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new ArrayDeserializerFactory());

            qName = new QName("SOAP/KEGG", "ArrayOfSubtype");
            cachedSerQNames.add(qName);
            cls = keggapi.Subtype[].class;
            cachedSerClasses.add(cls);
            qName = new QName("SOAP/KEGG", "Subtype");
            qName2 = null;
            cachedSerFactories.add(new ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new ArrayDeserializerFactory());

            qName = new QName("SOAP/KEGG", "Definition");
            cachedSerQNames.add(qName);
            cls = keggapi.Definition.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new QName("SOAP/KEGG", "LinkDBRelation");
            cachedSerQNames.add(qName);
            cls = keggapi.LinkDBRelation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new QName("SOAP/KEGG", "MotifResult");
            cachedSerQNames.add(qName);
            cls = keggapi.MotifResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new QName("SOAP/KEGG", "PathwayElement");
            cachedSerQNames.add(qName);
            cls = keggapi.PathwayElement.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new QName("SOAP/KEGG", "PathwayElementRelation");
            cachedSerQNames.add(qName);
            cls = keggapi.PathwayElementRelation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new QName("SOAP/KEGG", "SSDBRelation");
            cachedSerQNames.add(qName);
            cls = keggapi.SSDBRelation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new QName("SOAP/KEGG", "Subtype");
            cachedSerQNames.add(qName);
            cls = keggapi.Subtype.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

    }

    @SuppressWarnings("unchecked")
	protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call _call = super._createCall();
            if (super.maintainSessionSet) {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null) {
                _call.setPortName(super.cachedPortName);
            }
            Enumeration <Object> keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                if (firstCall()) {
                    // must set encoding style before registering serializers
                    _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
                    _call.setEncodingStyle(org.apache.axis.Constants.URI_SOAP11_ENC);
                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
                        Class cls = (Class) cachedSerClasses.get(i);
                        QName qName = cachedSerQNames.get(i);
                        Object x = cachedSerFactories.get(i);
                        if (x instanceof Class) {
                            Class sf = (Class) cachedSerFactories.get(i);
                            Class df = (Class) cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                            SerializerFactory sf = (SerializerFactory)
                                 cachedSerFactories.get(i);
                            DeserializerFactory df = (DeserializerFactory)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        }
        catch (java.lang.Throwable _t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
        }
    }

    public keggapi.Definition[] list_databases() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#list_databases");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "list_databases"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (keggapi.Definition[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (keggapi.Definition[]) JavaUtils.convert(_resp, keggapi.Definition[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public keggapi.Definition[] list_organisms() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#list_organisms");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "list_organisms"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (keggapi.Definition[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (keggapi.Definition[]) JavaUtils.convert(_resp, keggapi.Definition[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public keggapi.Definition[] list_pathways(String org) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#list_pathways");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "list_pathways"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {org});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (keggapi.Definition[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (keggapi.Definition[]) JavaUtils.convert(_resp, keggapi.Definition[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public keggapi.Definition[] list_ko_classes(String class_id) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#list_ko_classes");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "list_ko_classes"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {class_id});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (keggapi.Definition[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (keggapi.Definition[]) JavaUtils.convert(_resp, keggapi.Definition[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public String binfo(String db) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#binfo");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "binfo"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {db});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (String) _resp;
            } catch (java.lang.Exception _exception) {
                return (String) JavaUtils.convert(_resp, String.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public String bget(String string) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[5]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#bget");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "bget"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {string});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (String) _resp;
            } catch (java.lang.Exception _exception) {
                return (String) JavaUtils.convert(_resp, String.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public String bfind(String string) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[6]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#bfind");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "bfind"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {string});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (String) _resp;
            } catch (java.lang.Exception _exception) {
                return (String) JavaUtils.convert(_resp, String.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public String btit(String string) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[7]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#btit");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "btit"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {string});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (String) _resp;
            } catch (java.lang.Exception _exception) {
                return (String) JavaUtils.convert(_resp, String.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public String bconv(String string) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[8]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#bconv");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "bconv"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {string});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (String) _resp;
            } catch (java.lang.Exception _exception) {
                return (String) JavaUtils.convert(_resp, String.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public keggapi.LinkDBRelation[] get_linkdb_by_entry(String entry_id, String db, int start, int max_results) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[9]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#get_linkdb_by_entry");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "get_linkdb_by_entry"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {entry_id, db, new java.lang.Integer(start), new java.lang.Integer(max_results)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (keggapi.LinkDBRelation[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (keggapi.LinkDBRelation[]) JavaUtils.convert(_resp, keggapi.LinkDBRelation[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public keggapi.SSDBRelation[] get_best_neighbors_by_gene(String genes_id, int start, int max_results) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[10]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#get_best_neighbors_by_gene");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "get_best_neighbors_by_gene"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {genes_id, new java.lang.Integer(start), new java.lang.Integer(max_results)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (keggapi.SSDBRelation[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (keggapi.SSDBRelation[]) JavaUtils.convert(_resp, keggapi.SSDBRelation[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public keggapi.SSDBRelation[] get_best_best_neighbors_by_gene(String genes_id, int start, int max_results) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[11]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#get_best_best_neighbors_by_gene");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "get_best_best_neighbors_by_gene"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {genes_id, new java.lang.Integer(start), new java.lang.Integer(max_results)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (keggapi.SSDBRelation[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (keggapi.SSDBRelation[]) JavaUtils.convert(_resp, keggapi.SSDBRelation[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public keggapi.SSDBRelation[] get_reverse_best_neighbors_by_gene(String genes_id, int start, int max_results) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[12]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#get_reverse_best_neighbors_by_gene");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "get_reverse_best_neighbors_by_gene"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {genes_id, new java.lang.Integer(start), new java.lang.Integer(max_results)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (keggapi.SSDBRelation[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (keggapi.SSDBRelation[]) JavaUtils.convert(_resp, keggapi.SSDBRelation[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public keggapi.SSDBRelation[] get_paralogs_by_gene(String genes_id, int start, int max_results) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[13]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#get_paralogs_by_gene");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "get_paralogs_by_gene"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {genes_id, new java.lang.Integer(start), new java.lang.Integer(max_results)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (keggapi.SSDBRelation[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (keggapi.SSDBRelation[]) JavaUtils.convert(_resp, keggapi.SSDBRelation[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public keggapi.MotifResult[] get_motifs_by_gene(String genes_id, String db) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[14]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#get_motifs_by_gene");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "get_motifs_by_gene"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {genes_id, db});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (keggapi.MotifResult[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (keggapi.MotifResult[]) JavaUtils.convert(_resp, keggapi.MotifResult[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public keggapi.Definition[] get_genes_by_motifs(String[] motif_id_list, int start, int max_results) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[15]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#get_genes_by_motifs");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "get_genes_by_motifs"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {motif_id_list, new java.lang.Integer(start), new java.lang.Integer(max_results)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (keggapi.Definition[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (keggapi.Definition[]) JavaUtils.convert(_resp, keggapi.Definition[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public String[] get_ko_by_gene(String genes_id) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[16]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#get_ko_by_gene");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "get_ko_by_gene"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {genes_id});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (String[]) JavaUtils.convert(_resp, String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public keggapi.Definition[] get_ko_by_ko_class(String class_id) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[17]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#get_ko_by_ko_class");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "get_ko_by_ko_class"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {class_id});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (keggapi.Definition[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (keggapi.Definition[]) JavaUtils.convert(_resp, keggapi.Definition[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public keggapi.Definition[] get_genes_by_ko(String ko_id, String org) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[18]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#get_genes_by_ko");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "get_genes_by_ko"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {ko_id, org});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (keggapi.Definition[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (keggapi.Definition[]) JavaUtils.convert(_resp, keggapi.Definition[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public keggapi.Definition[] get_genes_by_ko_class(String class_id, String org, int start, int max_results) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[19]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#get_genes_by_ko_class");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "get_genes_by_ko_class"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {class_id, org, new java.lang.Integer(start), new java.lang.Integer(max_results)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (keggapi.Definition[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (keggapi.Definition[]) JavaUtils.convert(_resp, keggapi.Definition[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public String[] get_oc_members_by_gene(String genes_id, int start, int max_results) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[20]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#get_oc_members_by_gene");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "get_oc_members_by_gene"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {genes_id, new java.lang.Integer(start), new java.lang.Integer(max_results)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (String[]) JavaUtils.convert(_resp, String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public String[] get_pc_members_by_gene(String genes_id, int start, int max_results) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[21]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#get_pc_members_by_gene");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "get_pc_members_by_gene"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {genes_id, new java.lang.Integer(start), new java.lang.Integer(max_results)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (String[]) JavaUtils.convert(_resp, String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public keggapi.PathwayElement[] get_elements_by_pathway(String pathway_id) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[22]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#get_elements_by_pathway");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "get_elements_by_pathway"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {pathway_id});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (keggapi.PathwayElement[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (keggapi.PathwayElement[]) JavaUtils.convert(_resp, keggapi.PathwayElement[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public keggapi.PathwayElementRelation[] get_element_relations_by_pathway(String pathway_id) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[23]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#get_element_relations_by_pathway");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "get_element_relations_by_pathway"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {pathway_id});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (keggapi.PathwayElementRelation[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (keggapi.PathwayElementRelation[]) JavaUtils.convert(_resp, keggapi.PathwayElementRelation[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public String color_pathway_by_elements(String pathway_id, int[] element_list, String[] fg_color_list, String[] bg_color_list) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[24]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#color_pathway_by_elements");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "color_pathway_by_elements"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {pathway_id, element_list, fg_color_list, bg_color_list});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (String) _resp;
            } catch (java.lang.Exception _exception) {
                return (String) JavaUtils.convert(_resp, String.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public String get_html_of_colored_pathway_by_elements(String pathway_id, int[] element_list, String[] fg_color_list, String[] bg_color_list) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[25]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#get_html_of_colored_pathway_by_elements");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "get_html_of_colored_pathway_by_elements"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {pathway_id, element_list, fg_color_list, bg_color_list});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (String) _resp;
            } catch (java.lang.Exception _exception) {
                return (String) JavaUtils.convert(_resp, String.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public String mark_pathway_by_objects(String pathway_id, String[] object_id_list) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[26]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#mark_pathway_by_objects");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "mark_pathway_by_objects"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {pathway_id, object_id_list});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (String) _resp;
            } catch (java.lang.Exception _exception) {
                return (String) JavaUtils.convert(_resp, String.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public String color_pathway_by_objects(String pathway_id, String[] object_id_list, String[] fg_color_list, String[] bg_color_list) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[27]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#color_pathway_by_objects");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "color_pathway_by_objects"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {pathway_id, object_id_list, fg_color_list, bg_color_list});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (String) _resp;
            } catch (java.lang.Exception _exception) {
                return (String) JavaUtils.convert(_resp, String.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public String get_html_of_marked_pathway_by_objects(String pathway_id, String[] object_id_list) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[28]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#get_html_of_marked_pathway_by_objects");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "get_html_of_marked_pathway_by_objects"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {pathway_id, object_id_list});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (String) _resp;
            } catch (java.lang.Exception _exception) {
                return (String) JavaUtils.convert(_resp, String.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public String get_html_of_colored_pathway_by_objects(String pathway_id, String[] object_id_list, String[] fg_color_list, String[] bg_color_list) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[29]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#get_html_of_colored_pathway_by_objects");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "get_html_of_colored_pathway_by_objects"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {pathway_id, object_id_list, fg_color_list, bg_color_list});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (String) _resp;
            } catch (java.lang.Exception _exception) {
                return (String) JavaUtils.convert(_resp, String.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public String[] get_genes_by_pathway(String pathway_id) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[30]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#get_genes_by_pathway");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "get_genes_by_pathway"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {pathway_id});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (String[]) JavaUtils.convert(_resp, String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public String[] get_enzymes_by_pathway(String pathway_id) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[31]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#get_enzymes_by_pathway");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "get_enzymes_by_pathway"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {pathway_id});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (String[]) JavaUtils.convert(_resp, String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public String[] get_reactions_by_pathway(String pathway_id) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[32]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#get_reactions_by_pathway");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "get_reactions_by_pathway"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {pathway_id});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (String[]) JavaUtils.convert(_resp, String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public String[] get_pathways_by_genes(String[] genes_id_list) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[33]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#get_pathways_by_genes");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "get_pathways_by_genes"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {genes_id_list});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (String[]) JavaUtils.convert(_resp, String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public String[] get_pathways_by_enzymes(String[] enzyme_id_list) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[34]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#get_pathways_by_enzymes");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "get_pathways_by_enzymes"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {enzyme_id_list});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (String[]) JavaUtils.convert(_resp, String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public String[] get_pathways_by_reactions(String[] reaction_id_list) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[35]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#get_pathways_by_reactions");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "get_pathways_by_reactions"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {reaction_id_list});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (String[]) JavaUtils.convert(_resp, String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public String[] get_linked_pathways(String pathway_id) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[36]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#get_linked_pathways");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "get_linked_pathways"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {pathway_id});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (String[]) JavaUtils.convert(_resp, String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public String[] get_genes_by_enzyme(String enzyme_id, String org) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[37]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#get_genes_by_enzyme");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "get_genes_by_enzyme"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {enzyme_id, org});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (String[]) JavaUtils.convert(_resp, String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public String[] get_enzymes_by_gene(String genes_id) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[38]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#get_enzymes_by_gene");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "get_enzymes_by_gene"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {genes_id});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (String[]) JavaUtils.convert(_resp, String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public String[] get_enzymes_by_reaction(String reaction_id) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[39]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#get_enzymes_by_reaction");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "get_enzymes_by_reaction"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {reaction_id});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (String[]) JavaUtils.convert(_resp, String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public String[] get_reactions_by_enzyme(String enzyme_id) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[40]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#get_reactions_by_enzyme");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "get_reactions_by_enzyme"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {enzyme_id});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (String[]) JavaUtils.convert(_resp, String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public String[] get_genes_by_organism(String org, int start, int max_results) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[41]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#get_genes_by_organism");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "get_genes_by_organism"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {org, new java.lang.Integer(start), new java.lang.Integer(max_results)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (String[]) JavaUtils.convert(_resp, String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public int get_number_of_genes_by_organism(String abbr) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[42]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#get_number_of_genes_by_organism");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "get_number_of_genes_by_organism"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {abbr});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return ((java.lang.Integer) _resp).intValue();
            } catch (java.lang.Exception _exception) {
                return ((java.lang.Integer) JavaUtils.convert(_resp, int.class)).intValue();
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public String[] get_reactions_by_glycan(String glycan_id) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[43]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#get_reactions_by_glycan");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "get_reactions_by_glycan"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {glycan_id});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (String[]) JavaUtils.convert(_resp, String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public String[] get_reactions_by_compound(String compound_id) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[44]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#get_reactions_by_compound");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "get_reactions_by_compound"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {compound_id});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (String[]) JavaUtils.convert(_resp, String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public String[] get_enzymes_by_glycan(String glycan_id) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[45]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#get_enzymes_by_glycan");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "get_enzymes_by_glycan"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {glycan_id});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (String[]) JavaUtils.convert(_resp, String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public String[] get_enzymes_by_compound(String compound_id) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[46]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#get_enzymes_by_compound");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "get_enzymes_by_compound"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {compound_id});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (String[]) JavaUtils.convert(_resp, String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public String[] get_pathways_by_compounds(String[] compound_id_list) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[47]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#get_pathways_by_compounds");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "get_pathways_by_compounds"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {compound_id_list});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (String[]) JavaUtils.convert(_resp, String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public String[] get_pathways_by_glycans(String[] glycan_id_list) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[48]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#get_pathways_by_glycans");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "get_pathways_by_glycans"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {glycan_id_list});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (String[]) JavaUtils.convert(_resp, String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public String[] get_compounds_by_pathway(String pathway_id) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[49]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#get_compounds_by_pathway");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "get_compounds_by_pathway"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {pathway_id});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (String[]) JavaUtils.convert(_resp, String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public String[] get_glycans_by_pathway(String pathway_id) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[50]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#get_glycans_by_pathway");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "get_glycans_by_pathway"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {pathway_id});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (String[]) JavaUtils.convert(_resp, String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public String[] get_compounds_by_reaction(String reaction_id) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[51]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#get_compounds_by_reaction");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "get_compounds_by_reaction"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {reaction_id});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (String[]) JavaUtils.convert(_resp, String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public String[] get_glycans_by_reaction(String reaction_id) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[52]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#get_glycans_by_reaction");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "get_glycans_by_reaction"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {reaction_id});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (String[]) JavaUtils.convert(_resp, String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public String[] get_compounds_by_enzyme(String enzyme_id) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[53]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#get_compounds_by_enzyme");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "get_compounds_by_enzyme"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {enzyme_id});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (String[]) JavaUtils.convert(_resp, String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public String[] get_glycans_by_enzyme(String enzyme_id) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[54]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#get_glycans_by_enzyme");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "get_glycans_by_enzyme"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {enzyme_id});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (String[]) JavaUtils.convert(_resp, String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public String convert_mol_to_kcf(String mol_text) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[55]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#convert_mol_to_kcf");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "convert_mol_to_kcf"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {mol_text});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (String) _resp;
            } catch (java.lang.Exception _exception) {
                return (String) JavaUtils.convert(_resp, String.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public String[] get_kos_by_pathway(String pathway_id) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[56]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#get_kos_by_pathway");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "get_kos_by_pathway"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {pathway_id});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (String[]) JavaUtils.convert(_resp, String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public String[] get_pathways_by_kos(String[] ko_id_list, String org) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[57]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SOAP/KEGG#get_pathways_by_kos");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("SOAP/KEGG", "get_pathways_by_kos"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {ko_id_list, org});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (String[]) JavaUtils.convert(_resp, String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

}
