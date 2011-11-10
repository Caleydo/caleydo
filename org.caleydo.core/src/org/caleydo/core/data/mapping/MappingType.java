package org.caleydo.core.data.mapping;

import java.util.HashMap;

import org.caleydo.core.data.id.IDType;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * Edge of a mapping graph. It holds the the source and target ID type.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public class MappingType
	extends DefaultWeightedEdge {

	private static final long serialVersionUID = 1L;

	private IDType fromIDType;
	private IDType toIDType;

	private boolean isMultiMap = false;

	private static HashMap<String, MappingType> registeredMappingTypes = new HashMap<String, MappingType>();

	/**
	 * Constructor.
	 * 
	 * @param fromIDType
	 *            Source ID type.
	 * @param toIDType
	 *            Target ID type.
	 */
	private MappingType(IDType fromIDType, IDType toIDType, boolean isMultiMap) {
		super();
		this.fromIDType = fromIDType;
		this.toIDType = toIDType;
		this.isMultiMap = isMultiMap;
	}

	/**
	 * Factory method for a new mapping type. Creates a new mapping type or retrieves an existing mapping type
	 * if for the given parameters a mapping type was previously registered.
	 * 
	 * @param fromIDType
	 * @param toIDType
	 * @param isMultiMap
	 * @return
	 */
	public static MappingType registerMappingType(IDType fromIDType, IDType toIDType, boolean isMultiMap) {
		String key = generateKey(fromIDType, toIDType);
		MappingType type = registeredMappingTypes.get(key);
		if (type != null)
			return type;

		type = new MappingType(fromIDType, toIDType, isMultiMap);
		registeredMappingTypes.put(key, type);
		return type;
	}

	/**
	 * Get a previously registered mapping type based on iths source and target id type
	 * 
	 * @param fromIDType
	 * @param toIDType
	 * @return
	 */
	public static MappingType getType(IDType fromIDType, IDType toIDType) {
		return registeredMappingTypes.get(generateKey(fromIDType, toIDType));
	}

	private static String generateKey(IDType fromIDType, IDType toIDType) {
		return fromIDType.toString() + toIDType.toString();
	}

	public void setFromIDType(IDType fromIDType) {
		this.fromIDType = fromIDType;
	}

	public void setToIDType(IDType toIDType) {
		this.toIDType = toIDType;
	}

	public IDType getFromIDType() {
		return fromIDType;
	}

	public IDType getToIDType() {
		return toIDType;
	}

	public boolean isMultiMap() {
		return isMultiMap;
	}

	@Override
	public String toString() {
		return fromIDType + "_2_" + toIDType;
	}
}
