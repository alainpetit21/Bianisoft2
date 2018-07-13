
PROJECT : Bianisoft library

DESCRIPTION : 
Remake of the Dakiisoft library for Java (and rename). It also include a stand-alone test app when run as an app, but can also be link to other game and serve as a library. 



HOW TO COMPILE

- Install latest Java JDK with Netbeans package
    http://www.oracle.com/technetwork/java/javase/downloads/jdk-netbeans-jsp-142931.html
    
- Get the Directories structure right for proper post-compile tasks
  - /Lib/Bianisoft2 : <----- This project
  - /Games/OneParticularGame
  - /Tools/OneParticulaTool
  
- Right Click on project, Properties, Librairies
  - Add library
  - Create
  - Name it LWJGL-2.9.3
  - Add Jar/Folder
  - Go into ./dependenciesLibs/lwjgl-2.9.3/jar
  - Add everything
  
- Right Click on project, Properties, Librairies
  - Add Jar/Folder
  - Go into ./dependenciesLibs
  - Add jogg__V0.0.7.jar
  - Add jorbis__V0.0.15.jar
  
  
