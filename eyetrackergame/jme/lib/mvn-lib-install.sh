# Version numbers here must be updated to agree with the versions specified
# in the dependency section of our "pom.xml" file.

mvn install:install-file -Dfile=lib/lwjgl/lwjgl.jar -DgroupId=org.lwjgl -DartifactId=lwjgl -Dversion=2.1 -Dpackaging=jar
mvn install:install-file -Dfile=lib/lwjgl/lwjgl_util.jar -DgroupId=org.lwjgl -DartifactId=lwjgl_util -Dversion=2.1 -Dpackaging=jar
mvn install:install-file -Dfile=lib/lwjgl/lwjgl_util_applet.jar -DgroupId=org.lwjgl -DartifactId=lwjgl_util_applet -Dversion=2.1 -Dpackaging=jar
mvn install:install-file -Dfile=lib/lwjgl/jinput.jar -DgroupId=net.java.dev.jinput -DartifactId=jinput -Dversion=SNAPSHOT -Dpackaging=jar
mvn install:install-file -Dfile=lib/jorbis/jorbis-0.0.17.jar -DgroupId=jorbis -DartifactId=jorbis -Dversion=0.0.17 -Dpackaging=jar
mvn install:install-file -Dfile=lib/jogl/jogl.jar -DgroupId=net.java.dev.jogl -DartifactId=jogl -Dversion=1.1.1 -Dpackaging=jar
mvn install:install-file -Dfile=lib/jogl/gluegen-rt.jar -DgroupId=net.java.dev.gluegen -DartifactId=gluegen-rt -Dversion=1.0b06 -Dpackaging=jar

#this is needed for windows 64
#TOTO check other OS
mvn install:install-file -Dfile=lib/swt/windows/swt_64.jar -DgroupId=org.eclipse.swt.win32.win32 -DartifactId=swt -Dversion=3.4 -Dpackaging=jar