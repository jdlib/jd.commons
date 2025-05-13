# Fluent IO: Package jd.commons.io.fluent

Here is a coding challenge for all Java developers. How many lines of code do you need to implement the following tasks:

1. Given a `java.io.File`, read the content of the file into a byte array.
2. Given a `java.nio.file.Path`, read the first n bytes of it into a byte array.
3. Given a `java.io.InputStream`, decode the stream content as UTF-8 and append to a `StringBuilder`.
4. Given a `java.net.URL`, read the content, decode as UTF-8, and return as a list of lines.
5. Given a `java.net.Socket`, read the content, decode as ISO-8891-1 and return as a list of trimmed lines.
6. Given a `java.io.File` source and `java.io.File` target, read the source, decode as UTF-8, encode as ISO-8891-1 and write to the target.
7. Given a `java.io.Reader` and a `java.io.File`, write the Reader content as UTF-8 bytes to the file.
8. Given a `java.io.File` read all bytes and only throw RuntimeExceptions.
9. Given a `java.io.Reader` and a `java.io.File`, write the Reader content as UTF-8 bytes to the file, catch and log any exception thrown to a SLF4J logger.
10. Given a classpath resource name, read the resource, decode as UTF-8 and return as string.

This is how you implement these tasks using `jd.commons`:

	import static java.nio.charset.StandardCharsets.*;
	import static jd.commons.io.fluent.IO.*; // defines symbols "Bytes" and "Chars"
	import java.io.*;
	import java.nio.file.*;
	import org.slf4j.Logger;
	import jd.commons.io.Resource;
	
	byte[] result = Bytes.from(file).read().all(); // 1
	byte[] result = Bytes.from(path).read().first(n); // 2
	Bytes.from(in).asUtf8().write().to(sb); // 3
	List<String> result = Bytes.from(url).asUtf8().read().lines().toList(); // 4
	List<String> result = Bytes.from(socket).as(ISO_8859_1).read().lines().trim().toList(); // 5
	Bytes.from(inFile).asUtf8().write().as(ISO_8859_1).to(outFile); // 6
	Chars.from(reader).write().asUtf8().to(file) // 7
	byte[] result = Bytes.from(file).read().unchecked().all(); // 8
	Chars.from(reader).write().asUtf8().silent(e -> log.error("error", e)).to(file); // 9
	Chars.from(string).write().asUtf8().silent(log:error).to(file);
	String result = Resource.of(name).asUtf8().read().all(); // 10
