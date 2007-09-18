/**
 * KEGGPortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package keggapi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import keggapi.SSDBRelation;

import keggapi.Definition;
import keggapi.LinkDBRelation;
import keggapi.PathwayElement;

public interface KEGGPortType extends Remote {
    public Definition[] list_databases() throws RemoteException;
    public Definition[] list_organisms() throws RemoteException;
    public Definition[] list_pathways(String org) throws RemoteException;
    public Definition[] list_ko_classes(String class_id) throws RemoteException;
    public String binfo(String db) throws RemoteException;
    public String bget(String string) throws RemoteException;
    public String bfind(String string) throws RemoteException;
    public String btit(String string) throws RemoteException;
    public String bconv(String string) throws RemoteException;
    public LinkDBRelation[] get_linkdb_by_entry(String entry_id, String db, int start, int max_results) throws RemoteException;
    public SSDBRelation[] get_best_neighbors_by_gene(String genes_id, int start, int max_results) throws RemoteException;
    public SSDBRelation[] get_best_best_neighbors_by_gene(String genes_id, int start, int max_results) throws RemoteException;
    public SSDBRelation[] get_reverse_best_neighbors_by_gene(String genes_id, int start, int max_results) throws RemoteException;
    public SSDBRelation[] get_paralogs_by_gene(String genes_id, int start, int max_results) throws RemoteException;
    public keggapi.MotifResult[] get_motifs_by_gene(String genes_id, String db) throws RemoteException;
    public Definition[] get_genes_by_motifs(String[] motif_id_list, int start, int max_results) throws RemoteException;
    public String[] get_ko_by_gene(String genes_id) throws RemoteException;
    public Definition[] get_ko_by_ko_class(String class_id) throws RemoteException;
    public Definition[] get_genes_by_ko(String ko_id, String org) throws RemoteException;
    public Definition[] get_genes_by_ko_class(String class_id, String org, int start, int max_results) throws RemoteException;
    public String[] get_oc_members_by_gene(String genes_id, int start, int max_results) throws RemoteException;
    public String[] get_pc_members_by_gene(String genes_id, int start, int max_results) throws RemoteException;
    public PathwayElement[] get_elements_by_pathway(String pathway_id) throws RemoteException;
    public PathwayElementRelation[] get_element_relations_by_pathway(String pathway_id) throws RemoteException;
    public String color_pathway_by_elements(String pathway_id, int[] element_list, String[] fg_color_list, String[] bg_color_list) throws RemoteException;
    public String get_html_of_colored_pathway_by_elements(String pathway_id, int[] element_list, String[] fg_color_list, String[] bg_color_list) throws RemoteException;
    public String mark_pathway_by_objects(String pathway_id, String[] object_id_list) throws RemoteException;
    public String color_pathway_by_objects(String pathway_id, String[] object_id_list, String[] fg_color_list, String[] bg_color_list) throws RemoteException;
    public String get_html_of_marked_pathway_by_objects(String pathway_id, String[] object_id_list) throws RemoteException;
    public String get_html_of_colored_pathway_by_objects(String pathway_id, String[] object_id_list, String[] fg_color_list, String[] bg_color_list) throws RemoteException;
    public String[] get_genes_by_pathway(String pathway_id) throws RemoteException;
    public String[] get_enzymes_by_pathway(String pathway_id) throws RemoteException;
    public String[] get_reactions_by_pathway(String pathway_id) throws RemoteException;
    public String[] get_pathways_by_genes(String[] genes_id_list) throws RemoteException;
    public String[] get_pathways_by_enzymes(String[] enzyme_id_list) throws RemoteException;
    public String[] get_pathways_by_reactions(String[] reaction_id_list) throws RemoteException;
    public String[] get_linked_pathways(String pathway_id) throws RemoteException;
    public String[] get_genes_by_enzyme(String enzyme_id, String org) throws RemoteException;
    public String[] get_enzymes_by_gene(String genes_id) throws RemoteException;
    public String[] get_enzymes_by_reaction(String reaction_id) throws RemoteException;
    public String[] get_reactions_by_enzyme(String enzyme_id) throws RemoteException;
    public String[] get_genes_by_organism(String org, int start, int max_results) throws RemoteException;
    public int get_number_of_genes_by_organism(String abbr) throws RemoteException;
    public String[] get_reactions_by_glycan(String glycan_id) throws RemoteException;
    public String[] get_reactions_by_compound(String compound_id) throws RemoteException;
    public String[] get_enzymes_by_glycan(String glycan_id) throws RemoteException;
    public String[] get_enzymes_by_compound(String compound_id) throws RemoteException;
    public String[] get_pathways_by_compounds(String[] compound_id_list) throws RemoteException;
    public String[] get_pathways_by_glycans(String[] glycan_id_list) throws RemoteException;
    public String[] get_compounds_by_pathway(String pathway_id) throws RemoteException;
    public String[] get_glycans_by_pathway(String pathway_id) throws RemoteException;
    public String[] get_compounds_by_reaction(String reaction_id) throws RemoteException;
    public String[] get_glycans_by_reaction(String reaction_id) throws RemoteException;
    public String[] get_compounds_by_enzyme(String enzyme_id) throws RemoteException;
    public String[] get_glycans_by_enzyme(String enzyme_id) throws RemoteException;
    public String convert_mol_to_kcf(String mol_text) throws RemoteException;
    public String[] get_kos_by_pathway(String pathway_id) throws RemoteException;
    public String[] get_pathways_by_kos(String[] ko_id_list, String org) throws RemoteException;
}
