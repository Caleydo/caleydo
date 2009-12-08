#!/bin/sh
srcDir="$DKTROOT/visRenderer/src"
sliceDir="$DKTROOT/visRenderer/src/vis/slice"
javaDir="$CALEYDOROOT/vislink/generated_src"
cd $srcDir
echo $srcDir
echo $sliceDir
echo $javaDir
slice2java --output-dir $javaDir -I$srcDir $sliceDir/VisCommon.ice
slice2java --output-dir $javaDir -I$srcDir $sliceDir/VisRenderer.ice

