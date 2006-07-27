/**
 * 
 */
package cerberus.data.pathway.element;

public enum PathwayEdgeType
{
	ECel, 	// enzyme-enzyme relation, 
			// indicating two entymes catalyzing succesive reaction steps
	PPrel,	// protein-protein interactionm such as binding and modification
	GErel,	// gene expression interaction, indicating relation of 
			// transcription factor and target gene product
	PCrel	// protein-compound interaction
}