$Id: README.txt 4129 2009-03-19 19:58:17Z blaine.dev $

This folder contains some test scripts for serialization compatibility, when
you are making changes to the shape classes you can use these tests to make
sure nothing has broken.

1. (optionally) run the writeShapes.js script using the existing version of
   the classes, or you can just use the pre-made <shape>.ser files here. If
   you want to run the script yourself use these commands:

       $ export RHINO_HOME=/Users/ianp/Library/Java/rhino-1.7R1
       $ export JME_HOME=/Users/ianp/Library/JME
       $ java -classpath $JME_HOME/jme.jar:$RHINO_HOME/js.jar org.mozilla.javascript.tools.shell.Main -f writeShapes.js

2. run the readShapes.js script using the modified version of the classes, if
   you've broken serialization you'll get an exception when you try to read in
   the broken class.

   $ export RHINO_HOME=/Users/ianp/Library/Java/rhino-1.7R1
   $ export JME_HOME=/Users/ianp/Workspaces/3D/jme/bin
   $ java -classpath $JME_HOME/jme.jar:$RHINO_HOME/js.jar org.mozilla.javascript.tools.shell.Main -f readShapes.js

Obviously you'll need to point the paths to the relevant locations. If you're
running on Windows the equivalent of "export" is "set", if you're running
on Unix with a csh type shell then it's also "set" :-)

