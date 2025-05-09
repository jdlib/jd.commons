# jd.commons.io.FilePath

Java 1.0 introduced the `java.io.File` class to represent files in a local file system. 
In order to generalize to other file systems (archives, JRT) and to add missing features 
like file attributes, Java 1.7 introduced `java.nio.file.Path` as a replacement for `File`.
The designers decided to provide much of its functionality as static methods in the new `java.nio.file.Files` class which makes using `Path` harder and non-intuitive.

`FilePath` provides the functionality of `Path` without the added complexity.
Technically it wraps a `Path` object and provides static methods of `java.nio.file.Files` as instance 
methods in an easy to use interface

	import java.nio.file.attribute.BasicFileAttributes;
	import java.nio.file.attribute.FileTime;
	jd.commons.io.FilePath;
	
	FilePath dir = FilePath.of("/some/dir");
	FilePath file = dir.resolbe("test.txt");
	
Access to path properties:	
	
	boolean exists = dir.exists();
	boolean isFile = file.isRegularFile(); 
	String name = file.getName();
	
Access to all attributes:
	
	BasicFileAttributes attrs = dir.attributes().basic();
	FileTime lastModified = attrs.lastModifiedTime();
	
Creating directories, files and links:
	
	dir.createDirectories();
	file.createFile();
	file.createLink().to("other.txt");
	
Accessing children:

	int count = dir.children().count();
	List<FilePath> children = dir.children().toList();
	dir.children().glob("*.txt").delete();

Open streams to read or write the file content:

	InputStream in = file.open().inputStream();
	OutputStream in = file.open().append().outputStream();
	BufferedReader br = file.open().asUtf8().bufferedReader();
	
Directly read and write file content:

	byte[] bytes = file.read().all();
	file.write().as(UTF_16).string("hello");
	List<String> lines = file.read().asLatin1().lines().toList();	