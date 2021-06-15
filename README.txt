
Antastic
Antastic is a simple wrapper that invokes ant.
Anything that can be done with antastic can also be done using ant on the command line.

On startup, Antastic reads metadata from the [antastic_home]/data directory.
From this metadata it extracts which projects exist and how they are grouped together.
The metadata also contains a single build file which can be used to build all the projects.
Building a project is then a simple matter of selecting it and double clicking the appropriate target.

Antastic can be invoked without any arguments and then the gui will be started.
It is also possible to run antastic in headless mode. When running headless, pass a list of jobs to run.

A job can be defined as a project name, a project and a target separated by a colon or a file name.
When a project name is specified, the default target for that project will be run.
This is the default as defined in the ant.properties, not the default target in the build file.
In the case of a file, the file must exist and must be valid antastic script files.

examples:
	antastic myproject
	antastic myproject:mytarget
	antastic /path/to/script

# Antastic scripting
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

