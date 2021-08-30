
#h1 Antastic
Antastic is a simple wrapper that invokes ant.
Anything that can be done with antastic can also be done using ant on the command line.

On startup, Antastic reads metadata from the [antastic_home]/data directory.
From this metadata it extracts which projects exist and how they are grouped together.
The metadata also contains a single build file which can be used to build all the projects.
Building a project is then a simple matter of selecting it and double clicking the appropriate target.

Antastic can be invoked without any arguments and then the gui will be started.
It is also possible to run antastic in headless mode. When running headless, pass a list of jobs to run.




#h2 antastic cli

To run the default targets for projects simply pass the name of the projects to antastic.
The projects are run in the order in which they are declared.
Example: to run the default targets for projects p1 and p2:

`antastic p1 p2`

You can specify a target for a project using a colon (example: run target t1 on project p1)

`antastic p1:t1`

Multiple targets are possible (example: run target t1 & t2 on project p1)

`antastic p1:t1:t2`

If an entry in the list has an equals sign '=', then it is evaluated as a property:

`antastic p1:skip.test=true:t1`

All properties are applied on all targets regardless of declaration order.
It is also possible to specify global properties. 
These apply to all jobs following the global property.
So to have project p1 & p2 run with the skip.test property set to true:

`antastic skip.test=true p1 p2`

but in the following example:

`antastic skip.checkstyle p1 skip.test=true p2`

skip.test is set only for p2. skip.checkstyle is applied for both jobs

Lastly, arguments that start with a slash '/' are special.
These are references to files which will be run as antastic scripts:

antastic /path/to/script

In the case of a file, the file must exist and must be valid antastic script files.





#h2 Antastic scripting
The Antastic script format can be used to chain, building of multiple projects.
Antastic will process the targets in sequence and stop if one of them fails.
The format is as follows:

[project] [target]

All file locations are implicitly derived from the project name which is passed to the build file as a property.
This of course requires a build file that has been designed to do this.
It is also possible to assign properties from the script:

[key]=[value]

Any invocations following the property will have this property set when invoked.
It is currently not possible to unset a property once it has been set.
Start lines with a hash symbol '#' to mark them as comments.

Scripts can be passed to the application in headless mode, but can also be created in the gui.
Use the script menu item to create and run scripts interactively.

# ant home
If ant home is configured (can be done in the gui), then that ant installation will be used.
Antastic uses /usr/bin/ant by default.

# Config files
/data/antastic.ini => main config file containing build file, projects, groups
/data/filters.ini  => targets and properties that should be configured to enable these targets

