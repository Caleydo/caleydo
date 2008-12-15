package org.caleydo.core.view.opengl.util.wavefrontobjectloader;

import gleem.linalg.Vec3f;
import gleem.linalg.open.Vec3i;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import javax.media.opengl.GL;

/**
 * Stores and draws a wavefront object file model
 * 
 * @author Stefan Sauer
 */
public class ObjectModel
{
	ArrayList<Vec3f> verticesGeometric;
	ArrayList<Vec3f> verticesNormal;
	ArrayList<Vec3f> verticesTexture;

	HashMap<String, ObjectGroup> groups;

	HashSet<String> addFacedToGroups;

	private boolean isNormalized = false;

	public ObjectModel()
	{
		verticesGeometric = new ArrayList<Vec3f>();
		verticesNormal = new ArrayList<Vec3f>();
		verticesTexture = new ArrayList<Vec3f>();

		groups = new HashMap<String, ObjectGroup>();

		addFacedToGroups = new HashSet<String>();

	}

	public ObjectGroup getObjectGroup(String name)
	{
		if (!groups.containsKey(name))
			return null;

		return groups.get(name);
	}

	public Vec3f getGeometricVertex(int i)
	{
		if (i > verticesGeometric.size())
			return null;

		if (i < 1)
			return null;

		// index in obj file ALWAYS starts with 1
		// ArrayList starts with 0, so....
		return verticesGeometric.get(i - 1);
	}

	public Vec3f getNormalVertex(int i)
	{
		if (i >= verticesNormal.size())
			return null;

		// index in obj file ALWAYS starts with 1
		// ArrayList starts with 0, so....
		return verticesNormal.get(i - 1);
	}

	public Vec3f getTextureVertex(int i)
	{
		if (i >= verticesTexture.size())
			return null;

		// index in obj file ALWAYS starts with 1
		// ArrayList starts with 0, so....
		return verticesTexture.get(i - 1);
	}

	/**
	 * This handles the group command ("g").
	 * 
	 * @param line
	 */
	public void handleGroupCommand(String line)
	{
		ArrayList<String> lineparts = splitAndRemoveCommand(line, "g");

		// empty group name is not possible
		if (lineparts.size() <= 0)
			return;

		addFacedToGroups.clear();
		addFacedToGroups.addAll(lineparts);

		// create ObjectGroup Objects
		for (String name : lineparts)
			if (!groups.containsKey(name))
				groups.put(name, new ObjectGroup(this, name));

	}

	/**
	 * This handles the geometric vertex command ("v").
	 * 
	 * @param line
	 */
	public void handleVertexCommand(String line)
	{
		ArrayList<String> lineparts = splitAndRemoveCommand(line, "v");

		Vec3f temp = new Vec3f();
		if (lineparts.size() == 3)
			temp.set(Float.valueOf(lineparts.get(0)), Float.valueOf(lineparts.get(1)), Float
					.valueOf(lineparts.get(2)));

		verticesGeometric.add(temp);
	}

	/**
	 * This handles the normal vertex command ("vn")
	 * 
	 * @param line
	 */
	public void handleVertexNormalCommand(String line)
	{
		ArrayList<String> lineparts = splitAndRemoveCommand(line, "vn");

		Vec3f temp = new Vec3f();
		if (lineparts.size() == 3)
			temp.set(Float.valueOf(lineparts.get(0)), Float.valueOf(lineparts.get(1)), Float
					.valueOf(lineparts.get(2)));

		verticesNormal.add(temp);
	}

	/**
	 * This handles the texture vertex command ("vt")
	 * 
	 * @param line
	 */
	public void handleVertexTextureCommand(String line)
	{
		ArrayList<String> lineparts = splitAndRemoveCommand(line, "vt");

		Vec3f temp = new Vec3f();
		if (lineparts.size() == 3)
			temp.set(Float.valueOf(lineparts.get(0)), Float.valueOf(lineparts.get(1)), Float
					.valueOf(lineparts.get(2)));

		verticesTexture.add(temp);
	}

	/**
	 * This handles the face command ("f")
	 * 
	 * @param line
	 */
	public void handleFaceCommand(String line)
	{
		ArrayList<String> lineparts = splitAndRemoveCommand(line, "f");

		ArrayList<Vec3i> indices = new ArrayList<Vec3i>();

		for (String token : lineparts)
		{
			String[] tokenparts = token.split("/");

			// invalid face - ignore
			if (tokenparts.length == 0)
				continue;

			// int numSeperators = tokenparts.length;

			Vec3i temp = new Vec3i();

			temp.set(0, Integer.parseInt(tokenparts[0]));
			temp.set(1, (tokenparts.length > 1) ? Integer.parseInt(tokenparts[1]) : 0);
			temp.set(2, (tokenparts.length > 2) ? Integer.parseInt(tokenparts[2]) : 0);

			indices.add(temp);
		}

		for (String name : addFacedToGroups)
			if (groups.containsKey(name))
				groups.get(name).addFace(indices);
			else
				System.err.println("could not add face to group " + name);

	}

	private ArrayList<String> splitAndRemoveCommand(String line, String cmd)
	{
		String[] lineparts = line.split("[\\s]+");

		if (lineparts.length == 0)
			return new ArrayList<String>();

		int startat = 0;
		if (lineparts.length > 1)
			if (lineparts[0].equals(cmd))
				startat = 1;

		ArrayList<String> temp = new ArrayList<String>();

		for (int i = startat; i < lineparts.length; ++i)
			if (!lineparts[i].equals(""))
				temp.add(lineparts[i]);

		return temp;
	}

	private void normalizeScale()
	{
		if (isNormalized)
			return;

		float largest = 0;

		for (Vec3f vertex : verticesGeometric)
		{
			for (int i = 0; i < 3; ++i)
				if (largest < vertex.get(0))
					largest = vertex.get(0);
		}

		float scaleFactor = 1.0f;

		if (largest != 0)
			scaleFactor = 1.0f / largest;

		for (Vec3f vertex : verticesGeometric)
			for (int i = 0; i < 3; ++i)
				vertex.set(i, vertex.get(i) * scaleFactor);

	}

	/**
	 * This draws the complete object, defined in the file. The object is
	 * normalized inside a 1x1x1 cube.
	 * 
	 * @param gl
	 */
	public void drawObject(GL gl)
	{
		normalizeScale();

		gl.glPushMatrix();
		for (ObjectGroup group : groups.values())
		{
			group.draw(gl);
		}
		gl.glPopMatrix();
	}

	/**
	 * This draws only a part (g command) of a object file. If the group Name is
	 * not present in the object file, nothing happens. The object is normalized
	 * inside a 1x1x1 cube.
	 * 
	 * @param gl
	 * @param name
	 */
	public void drawObjectGroup(GL gl, String name)
	{
		normalizeScale();

		if (!groups.containsKey(name))
			return;

		gl.glPushMatrix();
		groups.get(name).draw(gl);
		gl.glPopMatrix();
	}

}
