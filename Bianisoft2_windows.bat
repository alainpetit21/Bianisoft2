@echo off

java -classpath "dist;dist/lib;dist/Bianisoft2.jar" -Dsun.java2d.noddraw=true -Djava.library.path="dist;dist/lib/windows;dist/Bianisoft2.jar" com.bianisoft.tests.AppTest
