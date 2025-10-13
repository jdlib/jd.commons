# jd.commons.io.FilePath

Java 1.0 introduced the `java.io.File` class to represent files in a local file system. 
In order to generalize to other file systems (archives, JRT) and to add address shortcomings
(limited functionality, poor error handling, performance issues, cross-platform reliability), 
Java 1.7 introduced `java.nio.file.Path` as a replacement for `File`.
The designers decided to provide much of its functionality as static methods in the new `java.nio.file.Files` 
class which makes using `Path` an non pleasant experience.

`FilePath` tries to be what `Path` could have been.
Technically it wraps a `Path` object and provides static methods of `java.nio.file.Files` as instance 
methods in an easy to use interface

	import java.nio.file.attribute.BasicFileAttributes;
	import java.nio.file.attribute.FileTime;
	jd.commons.io.FilePath;
	
	FilePath dir = FilePath.of("/some/dir");
	FilePath file = dir.resolve("test.txt");
	
Access to path properties:	
	
	boolean exists = dir.exists();
	boolean isFile = file.isRegularFile(); 
	String name = file.getName();
	
Efficient access of attributes:
	
	BasicFileAttributes basicAttrs = dir.attributes().basic();
	FileTime lastModified = basicAttrs.lastModifiedTime();
	boolean isFile = basicAttrs.isRegularFile(); 
	
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
	file.write().as(StandardCharsets.UTF_16).string("hello");
	List<String> lines = file.read().asLatin1().lines().toList();	
	
Create temporary files:

	FilePath tempDir = FilePath.tempDir(); // defined by java.io.tmpdir sys property
	// create a new temporary file in tempDir
	FilePath tempFile = tempDir.createTempFile("test", ".tmp");
	// create a new temporary file and delete when closing
	try (FilePathCloseable tempFile2 = tempDir.createTempFile("test2", ".tmp").toCloseable()) {
	    ...
	}
	