#!/bin/bash

implementor="$(find .. -type f -name "Implementor.java")"
impler="../../java-advanced-2025/modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor/Impler.java"
jarImpler="../../java-advanced-2025/modules/info.kgeorgiy.java.advanced.implementor.tools/info/kgeorgiy/java/advanced/implementor/tools/JarImpler.java"
implerException="../../java-advanced-2025/modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor/ImplerException.java"


javadoc -d ../javadoc -private "$implementor" "$impler" "$jarImpler" "$implerException"
