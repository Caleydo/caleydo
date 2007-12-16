#!/bin/bash

jar -cvfM ../../../../org.geneview.rcp/lib/org.geneview.core_bin.jar -C ../../../bin/class/ .  
jar -cvfM ../../../../org.geneview.rcp/lib/org.geneview.core_src.jar -C ../../../src .  
