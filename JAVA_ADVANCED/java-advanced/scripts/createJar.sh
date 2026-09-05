#!/bin/bash

scriptDir=$(dirname "$(readlink -f "$BASH_SOURCE")")

implementor=$(find .. -type f -name "Implementor.java")
implementorJar="$(find ../.. -type f -name "info.kgeorgiy.java.advanced.implementor.jar")"
implementorToolsJar="$(find ../.. -type f -name "info.kgeorgiy.java.advanced.implementor.tools.jar")"

javac -cp "$implementorJar:$implementorToolsJar" "$implementor" -d .

jar -cfm Implementor.jar "$scriptDir/MANIFEST.MF" info

rm -rf info
