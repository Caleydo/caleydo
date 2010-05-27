<?php

header("Content-type: application/x-java-jnlp-file"); 
$class = $_GET["class"];

echo <<< EOD

<?xml version="1.0" encoding="UTF-8"?>
<jnlp spec="1.0+" 
      codebase="http://www.bethunerobotics.com/eric/"
      href="jmedemo-$class">
      
  <information>
    <title>jME Test [$class]</title>
    <vendor>Mojo Monkey Coding</vendor>
    <homepage href="http://www.mojomonkeycoding.com"/>
    <icon href="MissileAvatar.png"/>
    <description>jME JNLP Test - $class</description>
    <description kind="short">jME Technology Preview</description>
    <offline-allowed/>
  </information>

  <security>
      <all-permissions/>
  </security>
  
  <resources>
    <j2se version="1.4+"/>
    <jar href="jmetest.jar" main="true"/>
    <jar href="jmetest-data.jar"/>
    <extension name="jme" href="http://www.bethunerobotics.com/eric/jme.jnlp" />
  </resources>

  <property key="java.library.path" value="." />
  <application-desc main-class="jmetest.$class"/>
</jnlp>

EOD;

?>