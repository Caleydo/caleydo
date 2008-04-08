#!/bin/bash
echo --- Killing previous instace...
killall -sKILL muddleware_XMLServer
echo --- Waiting until previous instance is closed...
sleep 1
echo --- Starting MuddleWare Server
exec ../../bin/muddleware_XMLServer
