package cerberus.pathways.element;

public class Edge extends Element
{
	public enum EdgeType
	{
		ECel, 	// enzyme-enzyme relation, 
				// indicating two entymes catalyzing succesive reaction steps
		PPrel,	// protein-protein interactionm such as binding and modification
		GErel,	// gene expression interaction, indicating relation of 
				// transcription factor and target gene product
		PCrel	// protein-compound interaction
	}
	
	protected EdgeType vertexType;
	
	//TODO: a typedef to elementID would be better here to be typesafe
	//this could be achieved by interhiting the Integer class
	protected int entry1;
	protected int entry2;

	//protected Map<Integer, EdgeRepresentation> mapEdgeRepresentation;
}