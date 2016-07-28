# Building acmeair using Eclipse

These instructions assume you 
1. Have git tools installed in Eclipse Mars (These should be included by default).
2. Have the Gradle [Buildship](https://projects.eclipse.org/projects/tools.buildship) tools installed into Eclipse Mars.

## Clone Git Repo

If the sample git repository hasn't been cloned yet, use git tools integrated into the IDE:

1.  Open the Git repositories view
    * *Window -> Show View -> Other*
    * Type "git" in the filter box, and select *Git Repositories*
2.  Copy Git repo url by finding the textbox under "HTTPS clone URL" at the top of this page, and select *Copy to clipboard*
3.  In the Git repositories view, select the hyperlink `Clone a Git repository`
4.  The git repo url should already be filled in.  Select *Next -> Next -> Finish*
5.  The "acmeair" repo should appear in the view

## Building the applications

1. In the Git Repository view, expand the acmeair repo to see the "Working Directory" folder
2. Right-click on this folder, and select *Copy path to Clipboard*
3. Select menu *File -> Import -> Gradle -> Gradle Project*
4. In the *Project root directory* folder textbox, Paste in the repository directory.
5. Click *Next* twice
6. Seven projects should be listed in the *Gradle project structure* click *Finish*
7. This will create 7 projects in Eclipse: acmeair, acmeair-as, acmeair-bs, acmeair-cs, acmeair-fs, acmeair-mainapp, acmeair-services, acmeair-services-mongo, acmeair-webapp
8. Go to the *Gradle Tasks* view in Eclipse and navigate to the *acmeair* project
9. Double click on the *eclipse* task to generate all the Eclipse files
10. In the *Enterprise Explorer* view in Eclipse right click on the seven projects mentioned in step 7 and click refresh

:star: *Note:* If you did not use Eclipse to clone the git repository, follow from step 3, but navigate to the cloned repository directory rather than pasting its name in step 4.

###### Run Gradle build

1. Go to the *Gradle Tasks* view in Eclipse and navigate to the *acmeair* project
2. Double click: build. 


This will download prerequisite jars and build four wars:  
* acmeair-as/build/libs/acmeair-as-2.0.0-SNAPSHOT.war  
* acmeair-bs/build/libs/acmeair-bs-2.0.0-SNAPSHOT.war
* acmeair-cs/build/libs/acmeair-cs-2.0.0-SNAPSHOT.war  
* acmeair-fs/build/libs/acmeair-fs-2.0.0-SNAPSHOT.war  
* acmeair-mainapp/build/libs/acmeair-webapp-2.0.0-SNAPSHOT.war
* acmeair-webapp/build/libs/acmeair-webapp-2.0.0-SNAPSHOT.war


# Building acmeair using the Command Line


## Add Java to your path and set your JAVA_HOME environment variable appropriately
Windows:
```text
set JAVA_HOME=C:\work\java\ibm-java-sdk-7.1-win-i386
set PATH=%JAVA_HOME%\bin;%PATH%
```

Linux:
```text
export JAVA_HOME=~/work/java/ibm-java-sdk-7.1-i386
export PATH=$JAVA_HOME/bin:$PATH
```

## Install the following development tools

* Git for access to the source code (http://msysgit.github.io/ for windows)
* Gradle for building the project (http://http://gradle.org/gradle-download/)
* Ensure git and gradle are in your path

### Clone Git Repo

```bash

$ git clone https://github.com/wasperf/acmeair.git

```

### Building the sample

This sample can be built using Gradle.

```bash  
$ cd <ACMEAIR_GIT_PATH>  
$ gradle build
```

This will download prerequisite jars and build four wars:  
* acmeair-as/build/libs/acmeair-as-2.0.0-SNAPSHOT.war  
* acmeair-bs/build/libs/acmeair-bs-2.0.0-SNAPSHOT.war
* acmeair-cs/build/libs/acmeair-cs-2.0.0-SNAPSHOT.war  
* acmeair-fs/build/libs/acmeair-fs-2.0.0-SNAPSHOT.war  
* acmeair-mainapp/build/libs/acmeair-webapp-2.0.0-SNAPSHOT.war
* acmeair-webapp/build/libs/acmeair-webapp-2.0.0-SNAPSHOT.war 