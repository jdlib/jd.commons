# Fluent IO: Package jd.commons.io.fluent

Java has got a bad reputation for requiring lot of code for even simple problems.

Here is a quick assessment. How many lines of code do you need to achieve the following:

1. Given a `java.io.File`, read the content of the file into a byte array
2. Given a `java.nio.file.Path`, read the first n bytes of it into a byte array
3. Given a `java.io.InputStream`, decode the stream content as UTF-8 and return as string
4. Given a `java.net.URL`, read the content, decode as UTF-8, and return as a list of trimmed lines
5. Given a `java.io.File`, read the content, decode as UTF-16, encode as UTF-8 and write to a `java.io.OutputStream`
6. Given a `java.io.File`, read the content of the file into a byte array, and convert any `IOException` to a `RuntimeException`
7. Given a `java.sql.Blob`, write its content byte to `File`, converting any `SQLException` to a `IOException`
8. Given a `String`, write it as UTF-8 to a `java.io.File` and log any exception thrown.
9. Given a `java.io.Reader`, encode as UTF-8, write to a `File`, count the number of chars written.
10. Given a `String`, encode it as UTF-8, BASE64 TODO

This is how you implements these tasks using `jd.commons`:

	import static java.nio.charset.StandardCharsets.*;
	import static jd.commons.io.fluent.IO.*; // defines symbols "Bytes" and "Chars"
	
	// 1.
	byte[] result = Bytes.from(file).read().all();
	// 2.
	byte[] result = Bytes.from(path).read().first(n);
	// 3. 
	String result = Bytes.from(in).asUtf8().read().all();
	// 4.
	List<String> result = Bytes.from(url).asUtf8().read().lines().trim().toList();
	// 5.
	Bytes.from(file).as(UTF_16).write().asUtf8().to(out);
	// 6.
	byte[] result = Bytes.from(file).read().unchecked().toByteArray();
	// 7.
	Bytes.from(blob).write().to(file);
	// 8.
	Chars.from(string).write().asUtf8().silent(log:error).to(file);
	// 9.
	long count = Chars.from(reader).write().countChars().asUtf8().to(file);
10. Given a `String`, encode it as UTF-8, BASE64 TODO
	