# jd.commons

jd.commons is a library like [Apache Commons](https://commons.apache.org/) or [Google Guava](https://github.com/google/guava), providing utility classes to support plain Java development.

## Add to your build

jd.commons's Maven group ID is `io.github.jdlib`, and its artifact ID is `jd.commons`.
To add a dependency on `jd.commons` using Maven, use the following:

	<dependency>
		<groupId>io.github.jdlib</groupId>
		<artifactId>jd.commons</artifactId>
		<version>1.0.0</version>
	</dependency>
	
or download the [latest release](https://github.com/jdlib/jd.commons/releases/latest).

## Overview

### Check class 
[Effective argument checking](Check.md).

### Fluent IO package
[Enables](FluentIO.md) complex IO operations with one line of code.

### FilePath class
[Fuses](FilePath.md) java.io.File and java.nio.file.Path.

### FileTree class
Effective operations on a [tree of files](FileTree.md).

### Resource
Easy handling of classpath [resources](Resource.md).

### Config class
[Replaces](Config.md) java.util.Properties.

### Extended functional interfaces 
[Counterparts](XInterfaces.md) for `java.util.function` which can throw checked exceptions.

### Utility classes
...contained in the proverbial [util package](Utilities.md).


