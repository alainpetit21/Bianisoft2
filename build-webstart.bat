@echo off

set BIANISOFT_HOME=../..
set JAVA_HOME=/usr/lib/jvm/java-6-openjdk
set CP=$BIANISOFT_HOME/Libs/ant/ant.jar;$BIANISOFT_HOME/Libs/ant/ant-launcher.jar
set CP=%CP%;$BIANISOFT_HOME/Libs/xerces/xercesImpl.jar;$BIANISOFT_HOME/Libs/xerces/xml-apis.jar
set CP=%CP%;$BIANISOFT_HOME/Libs/proguard/proguard.jar
set CP=%CP%;%JAVA_HOME%/lib/tools.jar

java -Dant.home=$BIANISOFT_HOME/Libs/ant -cp %CP% org.apache.tools.ant.Main -buildfile build-webstart.xml %1 %2 %3 %4
