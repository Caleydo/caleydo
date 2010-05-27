The is the redesigned jME Physics library: jME Physics 2.1 for the jME 2 development version.

More info on the homepage, board and wiki:
https://jmephysics.dev.java.net/
http://www.jmonkeyengine.com/jmeforum/index.php?board=3.0
http://wiki.jmephysics.irrisor.net/tiki-index.php

/Irrisor
   (irrisor@dev.java.net)


API features:
- no need to call any sync methods, smooth integration with jME scenegraph
- collision geometries: sphere, box, cylinder, capsule, trimesh, ray
- joints: 0 to 6 degrees of freedom, powered, stops, springs
- physics debug view
- implementation independent
- automatic generation of collision geometry (from boundings)
- materials, surface motion, material-material mappings for friction, bounce, slip etc.
- collision events
- picking (single geom collision check)
- utility: drag dynamic nodes with the mouse by applying joints (PhysicsPicker)
- some basic tutorials, design tutorial (Lesson9)

ODE-implementation features:
- supported geometries: sphere, box, capsule, cylinder, trimesh
- supported joints: 0 DOF (incl. Spring) / 1 translational / 1-2 rotational, powered / 3 rotational unpowered, spring



API TODO:
- specify mass distribution (e.g. inertia matrix)
- control step size and update rate
- simple jump and run / fps character tutorial 
- copyFrom(Node) (see http://www.jmonkeyengine.com/jmeforum/index.php?topic=3099)
- more events (thresholds etc.)
- extend .jme file format to allow storing of physics + merge nodes (by name)
- attractor (possibly with joint?) and other (custom) extensions
- plane
- visualize stops, joint/contact powers in debug view
- breakable joints
- static-static collisions (on demand)

ODE-implementation TODO:
- center of mass is poorly realized, currently, as ODE does not support changing it - should be improved...
- handle framerate drop below update rate
- debug visualization of trimesh collision geometries
- save/load for trimesh geometries
- powered ball joint (AMotor)