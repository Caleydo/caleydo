#!/bin/bash

jar -cvfM ../../../../org.caleydo.rcp/lib/org.caleydo.core_bin.jar -C ../../../bin/class/ .  
jar -cvfM ../../../../org.caleydo.rcp/lib/org.caleydo.core_src.jar -C ../../../src .  
