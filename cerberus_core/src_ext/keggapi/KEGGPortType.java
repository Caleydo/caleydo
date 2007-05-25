/**
 * KEGGPortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package keggapi;

public interface KEGGPortType extends java.rmi.Remote {
    public keggapi.Definition[] list_databases() throws java.rmi.RemoteException;
    public keggapi.Definition[] list_organisms() throws java.rmi.RemoteException;
    public keggapi.Definition[] list_pathways(java.lang.String org) throws java.rmi.RemoteException;
    public keggapi.Definition[] list_ko_classes(java.lang.String class_id) throws java.rmi.RemoteException;
    public java.lang.String binfo(java.lang.String db) throws java.rmi.RemoteException;
    public java.lang.String bget(java.lang.String string) throws java.rmi.RemoteException;
    public java.lang.String bfind(java.lang.String string) throws java.rmi.RemoteException;
    public java.lang.String btit(java.lang.String string) throws java.rmi.RemoteException;
    public java.lang.String bconv(java.lang.String string) throws java.rmi.RemoteException;
    public keggapi.LinkDBRelation[] get_linkdb_by_entry(java.lang.String entry_id, java.lang.String db, int start, int max_results) throws java.rmi.RemoteException;
    public keggapi.SSDBRelation[] get_best_neighbors_by_gene(java.lang.String genes_id, int start, int max_results) throws java.rmi.RemoteException;
    public keggapi.SSDBRelation[] get_best_best_neighbors_by_gene(java.lang.String genes_id, int start, int max_results) throws java.rmi.RemoteException;
    public keggapi.SSDBRelation[] get_reverse_best_neighbors_by_gene(java.lang.String genes_id, int start, int max_results) throws java.rmi.RemoteException;
    public keggapi.SSDBRelation[] get_paralogs_by_gene(java.lang.String genes_id, int start, int max_results) throws java.rmi.RemoteException;
    public keggapi.MotifResult[] get_motifs_by_gene(java.lang.String genes_id, java.lang.String db) throws java.rmi.RemoteException;
    public keggapi.Definition[] get_genes_by_motifs(java.lang.String[] motif_id_list, int start, int max_results) throws java.rmi.RemoteException;
    public java.lang.String[] get_ko_by_gene(java.lang.String genes_id) throws java.rmi.RemoteException;
    public keggapi.Definition[] get_ko_by_ko_class(java.lang.String class_id) throws java.rmi.RemoteException;
    public keggapi.Definition[] get_genes_by_ko(java.lang.String ko_id, java.lang.String org) throws java.rmi.RemoteException;
    public keggapi.Definition[] get_genes_by_ko_class(java.lang.String class_id, java.lang.String org, int start, int max_results) throws java.rmi.RemoteException;
    public java.lang.String[] get_oc_members_by_gene(java.lang.String genes_id, int start, int max_results) throws java.rmi.RemoteException;
    public java.lang.String[] get_pc_members_by_gene(java.lang.String genes_id, int start, int max_results) throws java.rmi.RemoteException;
    public keggapi.PathwayElement[] get_elements_by_pathway(java.lang.String pathway_id) throws java.rmi.RemoteException;
    public keggapi.PathwayElementRelation[] get_element_relations_by_pathway(java.lang.String pathway_id) throws java.rmi.RemoteException;
    public java.lang.String color_pathway_by_elements(java.lang.String pathway_id, int[] element_list, java.lang.String[] fg_color_list, java.lang.String[] bg_color_list) throws java.rmi.RemoteException;
    public java.lang.String get_html_of_colored_pathway_by_elements(java.lang.String pathway_id, int[] element_list, java.lang.String[] fg_color_list, java.lang.String[] bg_color_list) throws java.rmi.RemoteException;
    public java.lang.String mark_pathway_by_objects(java.lang.String pathway_id, java.lang.String[] object_id_list) throws java.rmi.RemoteException;
    public java.lang.String color_pathway_by_objects(java.lang.String pathway_id, java.lang.String[] object_id_list, java.lang.String[] fg_color_list, java.lang.String[] bg_color_list) throws java.rmi.RemoteException;
    public java.lang.String get_html_of_marked_pathway_by_objects(java.lang.String pathway_id, java.lang.String[] object_id_list) throws java.rmi.RemoteException;
    public java.lang.String get_html_of_colored_pathway_by_objects(java.lang.String pathway_id, java.lang.String[] object_id_list, java.lang.String[] fg_color_list, java.lang.String[] bg_color_list) throws java.rmi.RemoteException;
    public java.lang.String[] get_genes_by_pathway(java.lang.String pathway_id) throws java.rmi.RemoteException;
    public java.lang.String[] get_enzymes_by_pathway(java.lang.String pathway_id) throws java.rmi.RemoteException;
    public java.lang.String[] get_reactions_by_pathway(java.lang.String pathway_id) throws java.rmi.RemoteException;
    public java.lang.String[] get_pathways_by_genes(java.lang.String[] genes_id_list) throws java.rmi.RemoteException;
    public java.lang.String[] get_pathways_by_enzymes(java.lang.String[] enzyme_id_list) throws java.rmi.RemoteException;
    public java.lang.String[] get_pathways_by_reactions(java.lang.String[] reaction_id_list) throws java.rmi.RemoteException;
    public java.lang.String[] get_linked_pathways(java.lang.String pathway_id) throws java.rmi.RemoteException;
    public java.lang.String[] get_genes_by_enzyme(java.lang.String enzyme_id, java.lang.String org) throws java.rmi.RemoteException;
    public java.lang.String[] get_enzymes_by_gene(java.lang.String genes_id) throws java.rmi.RemoteException;
    public java.lang.String[] get_enzymes_by_reaction(java.lang.String reaction_id) throws java.rmi.RemoteException;
    public java.lang.String[] get_reactions_by_enzyme(java.lang.String enzyme_id) throws java.rmi.RemoteException;
    public java.lang.String[] get_genes_by_organism(java.lang.String org, int start, int max_results) throws java.rmi.RemoteException;
    public int get_number_of_genes_by_organism(java.lang.String abbr) throws java.rmi.RemoteException;
    public java.lang.String[] get_reactions_by_glycan(java.lang.String glycan_id) throws java.rmi.RemoteException;
    public java.lang.String[] get_reactions_by_compound(java.lang.String compound_id) throws java.rmi.RemoteException;
    public java.lang.String[] get_enzymes_by_glycan(java.lang.String glycan_id) throws java.rmi.RemoteException;
    public java.lang.String[] get_enzymes_by_compound(java.lang.String compound_id) throws java.rmi.RemoteException;
    public java.lang.String[] get_pathways_by_compounds(java.lang.String[] compound_id_list) throws java.rmi.RemoteException;
    public java.lang.String[] get_pathways_by_glycans(java.lang.String[] glycan_id_list) throws java.rmi.RemoteException;
    public java.lang.String[] get_compounds_by_pathway(java.lang.String pathway_id) throws java.rmi.RemoteException;
    public java.lang.String[] get_glycans_by_pathway(java.lang.String pathway_id) throws java.rmi.RemoteException;
    public java.lang.String[] get_compounds_by_reaction(java.lang.String reaction_id) throws java.rmi.RemoteException;
    public java.lang.String[] get_glycans_by_reaction(java.lang.String reaction_id) throws java.rmi.RemoteException;
    public java.lang.String[] get_compounds_by_enzyme(java.lang.String enzyme_id) throws java.rmi.RemoteException;
    public java.lang.String[] get_glycans_by_enzyme(java.lang.String enzyme_id) throws java.rmi.RemoteException;
    public java.lang.String convert_mol_to_kcf(java.lang.String mol_text) throws java.rmi.RemoteException;
    public java.lang.String[] get_kos_by_pathway(java.lang.String pathway_id) throws java.rmi.RemoteException;
    public java.lang.String[] get_pathways_by_kos(java.lang.String[] ko_id_list, java.lang.String org) throws java.rmi.RemoteException;
}
