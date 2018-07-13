#!/bin/sh

BIANISOFT_HOME=../..
JAVA_HOME=/usr/lib/jvm/java-6-openjdk
CP=$BIANISOFT_HOME/Libs/ant/ant.jar:$BIANISOFT_HOME/Libs/ant/ant-launcher.jar
CP=$CP:$BIANISOFT_HOME/Libs/xerces/xercesImpl.jar:$BIANISOFT_HOME/Libs/xerces/xml-apis.jar
CP=$CP:$BIANISOFT_HOME/Libs/proguard/proguard.jar
CP=$CP:$JAVA_HOME/lib/tools.jar

java -Dant.home=$BIANISOFT_HOME/Libs/ant -cp $CP org.apache.tools.ant.Main -buildfile build-webstart.xml $@
