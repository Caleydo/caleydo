// Simple script to test reading in serialized shapes.
// $Id: readShapes.js 4129 2009-03-19 19:58:17Z blaine.dev $

importPackage(com.jme.math)
importPackage(com.jme.scene.shape)
importPackage(java.io)
importPackage(java.util)

names = [
    "arrow", "axisRods", "box", "capsule", "cone", "cylinder", "disk",
    "dodecahedron", "dome" /*, "extrusion" */, "geoSphere", "hexagon",
    "icosahedron", "multiFaceBox", "octahedron", "orientedBox", "pqTorus",
    "pyramid", "quad", "roundedBox", "sphere", "stripBox","teapot",
    "torus", "tube"
]

for (var i = 0; i < names.length; ++i) {
    name = names[i]
    print("Reading " + name + " ...")
    istream = new ObjectInputStream(new FileInputStream(name + ".ser"))
    istream.readObject()
    istream.close()
}

