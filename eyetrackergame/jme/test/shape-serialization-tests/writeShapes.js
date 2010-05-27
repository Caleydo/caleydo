// Simple script to write out instances of the basic shapes.
// $Id: writeShapes.js 4129 2009-03-19 19:58:17Z blaine.dev $

importPackage(com.jme.math)
importPackage(com.jme.scene.shape)
importPackage(java.io)
importPackage(java.util)

name = "arrow"
print("Writing " + name + " ...")
shape = new Arrow(name, 5, 0.5)
out = new ObjectOutputStream(new FileOutputStream(name + ".ser"))
out.writeObject(shape)
out.close()

name = "axisRods"
print("Writing " + name + " ...")
shape = new AxisRods(name, true, 5, 0.5)
out = new ObjectOutputStream(new FileOutputStream(name + ".ser"))
out.writeObject(shape)
out.close()

name = "box"
print("Writing " + name + " ...")
shape = new Box(name, new Vector3f(), 1, 1, 1)
out = new ObjectOutputStream(new FileOutputStream(name + ".ser"))
out.writeObject(shape)
out.close()

name = "capsule"
print("Writing " + name + " ...")
shape = new Capsule(name, 10, 10, 10, 1, 5)
out = new ObjectOutputStream(new FileOutputStream(name + ".ser"))
out.writeObject(shape)
out.close()

name = "cone"
print("Writing " + name + " ...")
shape = new Cone(name, 10, 10, 1, 5)
out = new ObjectOutputStream(new FileOutputStream(name + ".ser"))
out.writeObject(shape)
out.close()

name = "cylinder"
print("Writing " + name + " ...")
shape = new Cylinder(name, 10, 10, 1, 5)
out = new ObjectOutputStream(new FileOutputStream(name + ".ser"))
out.writeObject(shape)
out.close()

name = "disk"
print("Writing " + name + " ...")
shape = new Disk(name, 10, 10, 2.5)
out = new ObjectOutputStream(new FileOutputStream(name + ".ser"))
out.writeObject(shape)
out.close()

name = "dodecahedron"
print("Writing " + name + " ...")
shape = new Dodecahedron(name, 5)
out = new ObjectOutputStream(new FileOutputStream(name + ".ser"))
out.writeObject(shape)
out.close()

name = "dome"
print("Writing " + name + " ...")
shape = new Dome(name, 10, 10, 3)
out = new ObjectOutputStream(new FileOutputStream(name + ".ser"))
out.writeObject(shape)
out.close()

// FIXME: get some valid values to use for extrusion and add it to the tests.
// vertices = [
//     new Vector3f(0,0,0),
//     new Vector3f(1,1,0)
// ]
// line = new com.jme.scene.Line("line", vertices, null, null, null)
// path = [
//     new Vector3f(0,0,0),
//     new Vector3f(0,0,20)
// ]
// up = Vector3f.UNIT_Z.clone()
// name = "extrusion"
// print("Writing " + name + " ...")
// shape = new Extrusion(name, line, Arrays.asList(path), up)
// out = new ObjectOutputStream(new FileOutputStream(name + ".ser"))
// out.writeObject(shape)
// out.close()

name = "geoSphere"
print("Writing " + name + " ...")
shape = new GeoSphere(name, true, 4)
out = new ObjectOutputStream(new FileOutputStream(name + ".ser"))
out.writeObject(shape)
out.close()

name = "hexagon"
print("Writing " + name + " ...")
shape = new Hexagon(name, 5)
out = new ObjectOutputStream(new FileOutputStream(name + ".ser"))
out.writeObject(shape)
out.close()

name = "icosahedron"
print("Writing " + name + " ...")
shape = new Icosahedron(name, 5)
out = new ObjectOutputStream(new FileOutputStream(name + ".ser"))
out.writeObject(shape)
out.close()

name = "multiFaceBox"
print("Writing " + name + " ...")
shape = new MultiFaceBox(name, new Vector3f(), 1, 1, 1)
out = new ObjectOutputStream(new FileOutputStream(name + ".ser"))
out.writeObject(shape)
out.close()

name = "octahedron"
print("Writing " + name + " ...")
shape = new Octahedron(name, 5)
out = new ObjectOutputStream(new FileOutputStream(name + ".ser"))
out.writeObject(shape)
out.close()

name = "orientedBox"
print("Writing " + name + " ...")
shape = new OrientedBox(name)
out = new ObjectOutputStream(new FileOutputStream(name + ".ser"))
out.writeObject(shape)
out.close()

name = "pqTorus"
print("Writing " + name + " ...")
shape = new PQTorus(name, 0.8, 0.8, 4, 2, 20, 20)
out = new ObjectOutputStream(new FileOutputStream(name + ".ser"))
out.writeObject(shape)
out.close()

name = "pyramid"
print("Writing " + name + " ...")
shape = new Pyramid(name, 4, 4)
out = new ObjectOutputStream(new FileOutputStream(name + ".ser"))
out.writeObject(shape)
out.close()

name = "quad"
print("Writing " + name + " ...")
shape = new Quad(name, 5, 5)
out = new ObjectOutputStream(new FileOutputStream(name + ".ser"))
out.writeObject(shape)
out.close()

name = "roundedBox"
print("Writing " + name + " ...")
shape = new RoundedBox(name, new Vector3f(3, 3, 3))
out = new ObjectOutputStream(new FileOutputStream(name + ".ser"))
out.writeObject(shape)
out.close()

name = "sphere"
print("Writing " + name + " ...")
shape = new Sphere(name, 10, 10, 2)
out = new ObjectOutputStream(new FileOutputStream(name + ".ser"))
out.writeObject(shape)
out.close()

name = "stripBox"
print("Writing " + name + " ...")
shape = new StripBox(name, new Vector3f(), 2, 2, 2)
out = new ObjectOutputStream(new FileOutputStream(name + ".ser"))
out.writeObject(shape)
out.close()

name = "teapot"
print("Writing " + name + " ...")
shape = new Teapot(name)
out = new ObjectOutputStream(new FileOutputStream(name + ".ser"))
out.writeObject(shape)
out.close()

name = "torus"
print("Writing " + name + " ...")
shape = new Torus(name, 10, 10, 3, 5)
out = new ObjectOutputStream(new FileOutputStream(name + ".ser"))
out.writeObject(shape)
out.close()

name = "tube"
print("Writing " + name + " ...")
shape = new Tube(name, 5, 4, 10, 10, 10)
out = new ObjectOutputStream(new FileOutputStream(name + ".ser"))
out.writeObject(shape)
out.close()
