#!/bin/bash

GEOMJAR=build/libs/bsgeom2d.jar
TESTGUI_CLASSES=build/classes/java/testgui

java -cp $GEOMJAR:$TESTGUI_CLASSES info.bstancham.bsgeom2d.testgui.InteractiveTester
