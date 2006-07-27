/**
 * 
 */
package cerberus.data.pathway.element;

public enum PathwayVertexType
{
	ortholog,	// the node is a KO (ortholog group)
	enzyme,		// the node is an enzyme
	gene,		// the node is a gene product (mostly a protein)
	group,		// the node is a complex of gene products (mostly a protein complex)
	compound,	// the node is a chemical compound (including a glycan)
	map
}